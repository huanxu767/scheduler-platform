package com.sncfc.scheduler.server.pojo;

import com.sncfc.scheduler.server.service.IScheduleJobService;
import com.sncfc.scheduler.server.util.HttpClientUtil;
import com.sncfc.scheduler.server.util.SpringContextUtil;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.quartz.JobKey;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.quartz.QuartzJobBean;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 异步任务工厂
 */
public class AsyncJobFactory extends QuartzJobBean {

    /* 日志对象 */
    private static final Logger logger = LoggerFactory.getLogger(AsyncJobFactory.class);
    @Override
    protected void executeInternal(JobExecutionContext context) throws JobExecutionException {
        JobKey jobKey = context.getJobDetail().getKey();
        trigger(Long.parseLong(jobKey.getName()),context.getFireInstanceId(),ScheduleLog.AUTO_TRIGGER);
    }

    public static void trigger(Long name,String fireInstanceId,String triggerType){
        /**
         * 1秒钟内无法执行2次相同的任务
         *
         */
        if(Constant.JOB_TRIGGER_AUTO.equals(triggerType) && triggerOnceMore(name.toString())){
            logger.error("重复执行："+name +" ,fireInstanceId:"+fireInstanceId);
            return;
        }
        IScheduleJobService scheduleJobService = (IScheduleJobService) SpringContextUtil.getBean("scheduleJobService");
        ScheduleJob scheduleJob = scheduleJobService.getScheduleJobAndParams(name);
        logger.info(scheduleJob.getJobAliasName()+"fireInstanceId:"+fireInstanceId);
        String jobType = scheduleJob.getJobType();
        Map map = new HashMap();
        map.put("fireInstanceId",fireInstanceId);
        map.put("methodName",scheduleJob.getMethodName());
        List<ScheduleJobParams> paramsList = scheduleJob.getParamsList();
        List<String> values = new ArrayList<String>();
        List<Class> types = new ArrayList<Class>();
        for (ScheduleJobParams params : paramsList) {
            values.add(params.getValue());
            types.add(String.class);
        }
        map.put("argumentTypes",types.toArray(new Class[0]));
        map.put("arguments",values.toArray());
        if(Constant.JOB_TYPE_STATIC_METHOD.equals(jobType)){
            map.put("brokerClassName","com.sncfc.scheduler.client.broker.StaticMethodJobBroker");
            map.put("staticClassName",scheduleJob.getClassName());

        }else if(Constant.JOB_TYPE_SPRING_BEAN.equals(jobType)){
            map.put("beanId",scheduleJob.getClassName());
            map.put("brokerClassName","com.sncfc.scheduler.client.broker.SpringBeanJobBroker");
        }
        Map responseMap;
        ScheduleLog scheduleLog = new ScheduleLog();
        scheduleLog.setTriggerType(triggerType);
        scheduleLog.setJobName(scheduleJob.getJobName());
        scheduleLog.setJobGroup(scheduleJob.getJobGroup());
        scheduleLog.setFireInstanceId(fireInstanceId);
        //插入执行记录
        scheduleJobService.addScheduleLog(scheduleLog);
        try {
            responseMap = new HttpClientUtil(scheduleJob.getUrl()).sendReq(map);
            logger.info("responseMap:"+responseMap);
            if(responseMap == null){
                scheduleLog.setSuccess(ScheduleLog.TRIGGER_FAILURE);
                scheduleLog.setStatus(ScheduleLog.CLIENT_END);
                scheduleLog.setErrorMessage("response为空");
            }else{
                scheduleLog.setSuccess("success".equals(responseMap.get("flag").toString())? ScheduleLog.TRIGGER_SUCCESS:ScheduleLog.TRIGGER_FAILURE);
                scheduleLog.setNodeName(responseMap.get("nodeName").toString());
                scheduleLog.setErrorMessage(responseMap.get("errMsg").toString());
                scheduleLog.setStatus(ScheduleLog.CLIENT_TIGGERED);
            }
        }catch (Exception e) {
            scheduleLog.setSuccess(ScheduleLog.TRIGGER_FAILURE);
            scheduleLog.setStatus(ScheduleLog.CLIENT_END);
            scheduleLog.setErrorMessage(e.getMessage());
            logger.error("触发失败",e);
        }
        //更新执行记录
        scheduleJobService.updateScheduleLog(scheduleLog);
    }


    /**
     * 判断同一个任务触发多次
     * @param name
     * @return true 触发多次 false 未触发多次
     */
    private synchronized static boolean triggerOnceMore(String name){
        if(Constant.JOB_MAP.containsKey(name)){
            Long lastTriggerTime = Constant.JOB_MAP.get(name);
            Long now = System.currentTimeMillis();
            System.out.println(now - lastTriggerTime);
            if(now - lastTriggerTime < 1000){
                //重复执行
                logger.error("间隔时间："+(now - lastTriggerTime));
                return true;
            }
        }
        Constant.JOB_MAP.put(name,System.currentTimeMillis());
        return false;
    }

    public static void main(String[] args) {
        Long beginTime = System.currentTimeMillis();
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        Long endTime = System.currentTimeMillis();
        System.out.println(endTime - beginTime);

    }
}
