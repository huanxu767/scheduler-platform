package com.sncfc.scheduler.server.pojo;

import org.quartz.DisallowConcurrentExecution;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.quartz.PersistJobDataAfterExecution;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.quartz.QuartzJobBean;

/**
 *同步任务工厂
 */
@PersistJobDataAfterExecution
@DisallowConcurrentExecution
public class SyncJobFactory extends QuartzJobBean {

    /* 日志对象 */
    private static final Logger logger = LoggerFactory.getLogger(SyncJobFactory.class);

    protected void executeInternal(JobExecutionContext context) throws JobExecutionException {
        /**
        IScheduleJobService scheduleJobService = (IScheduleJobService) SpringContextUtil.getBean("scheduleJobService");
        JobKey jobKey = context.getJobDetail().getKey();
        ScheduleJob scheduleJob = scheduleJobService.get(Long.parseLong(jobKey.getName()));
        System.out.println(scheduleJob.getJobAliasName());
        String fireInstanceId = context.getFireInstanceId();
        Map map = new HashMap();
        map.put("fireInstanceId",context.getFireInstanceId());
        map.put("brokerClassName","com.suning.framework.uts.client.broker.SpringBeanJobBroker");
        map.put("beanId","springBeanTest");
        map.put("methodName","test");
        System.out.println("end");
//        Class[] obsTypes = new Class[]{String.class,String.class};
//        Object[] obsValues = new Object[]{"许","欢"};
//        map.put("argumentTypes",obsTypes);
//        map.put("arguments",obsValues);

        Map responseMap = new HttpClientUtil(scheduleJob.getUrl()).sendReq(map);
//        errMsg flag errMsg
        System.out.println(responseMap);
        ScheduleLog scheduleLog = new ScheduleLog();
        scheduleLog.setJobName(scheduleJob.getJobName());
        scheduleLog.setJobGroup(scheduleJob.getJobGroup());
        scheduleLog.setFireInstanceId(fireInstanceId);
        scheduleLog.setSuccess("success".equals(responseMap.get("flag").toString())? "1":"2");
        scheduleLog.setNodeName(responseMap.get("nodeName").toString());
        scheduleLog.setErrorMessage(responseMap.get("errMsg").toString());
        scheduleJobService.addSchedule(scheduleLog);
        **/
    }
}
