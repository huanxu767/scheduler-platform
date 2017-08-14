package com.sncfc.scheduler.server.controller;

import com.sncfc.scheduler.server.Exception.ResultException;
import com.sncfc.scheduler.server.pojo.BaseResultBean;
import com.sncfc.scheduler.server.pojo.Pager;
import com.sncfc.scheduler.server.pojo.Project;
import com.sncfc.scheduler.server.pojo.ScheduleJob;
import com.sncfc.scheduler.server.service.IScheduleJobService;
import org.quartz.CronExpression;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by 123 on 2017/1/19.
 */
@Controller
@RequestMapping("/api")
public class ApiController extends BaseActionController{
    private static final Logger logger = LoggerFactory.getLogger(ApiController.class);

    @Autowired
    private IScheduleJobService scheduleJobService;

    /**
     * 批量新增任务
     * @param scheduleJobList
     * @return
     */
    @ResponseBody
    @RequestMapping(value = "batchAddJobDetail")
    public BaseResultBean batchAddJobDetail(@RequestBody List<ScheduleJob> scheduleJobList){
        BaseResultBean resultBean = new BaseResultBean();
        Map resultMap = new HashMap();
        try {
            List ids = scheduleJobService.batchAddScheduleJob(scheduleJobList);
            resultMap.put("ids",ids);
            resultBean.success();
            resultBean.setResult(resultMap);
        }catch (ResultException e){
            logger.error("批量新增任务失败");
            resultBean.failure();
            resultBean.setErrorMsg(e.getResultMsg());
        }catch (Exception e){
            logger.error("批量新增任务失败",e);
            resultBean.failure();
        }
        return resultBean;
    }

    /**
     * 批量暂停任务
     * @return
     */
    @ResponseBody
    @RequestMapping(value = "batchPauseJob")
    public BaseResultBean batchPauseJob(@RequestBody List<Long> scheduleJobIds){
        BaseResultBean resultBean = new BaseResultBean();
        try {
            scheduleJobService.batchPauseJob(scheduleJobIds);
            resultBean.success();
        }catch (ResultException e){
            logger.error("批量暂停任务失败");
            resultBean.failure();
            resultBean.setErrorMsg(e.getResultMsg());
        }catch (Exception e){
            logger.error("批量暂停任务失败",e);
            resultBean.failure();
        }
        return resultBean;
    }

    /**
     * 批量 删除任务
     * @return
     */
    @ResponseBody
    @RequestMapping(value = "batchDeleteJob")
    public BaseResultBean batchDeleteJob(@RequestBody List<Long> scheduleJobIds){
        BaseResultBean resultBean = new BaseResultBean();
        try {
            scheduleJobService.batchDelete(scheduleJobIds);
            resultBean.success();
        }catch (ResultException e){
            logger.error("批量删除任务失败");
            resultBean.failure();
            resultBean.setErrorMsg(e.getResultMsg());
        }catch (Exception e){
            logger.error("批量删除任务失败",e);
            resultBean.failure();
        }
        return resultBean;
    }

    /**
     * 批量恢复执行
     * @return
     */
    @ResponseBody
    @RequestMapping(value = "batchResumeJob")
    public BaseResultBean batchResumeJob(@RequestBody List<Long> scheduleJobIds){
        BaseResultBean resultBean = new BaseResultBean();
        try {
            scheduleJobService.batchStartJob(scheduleJobIds);
            resultBean.success();
        }catch (ResultException e){
            logger.error("批量恢复执行失败");
            resultBean.failure();
            resultBean.setErrorMsg(e.getResultMsg());
        }catch (Exception e){
            logger.error("批量恢复执行失败",e);
            resultBean.failure();
        }
        return resultBean;
    }

    /**
     * 批量 更新任务
     * @return
     */
    @ResponseBody
    @RequestMapping(value = "batchModifyJob")
    public BaseResultBean batchModifyJob(@RequestBody List<ScheduleJob> scheduleJobs){
        BaseResultBean resultBean = new BaseResultBean();
        try {
            scheduleJobService.batchModifyJobDetail(scheduleJobs);
            resultBean.success();
        }catch (ResultException e){
            logger.error("批量更新执行失败");
            resultBean.failure();
            resultBean.setErrorMsg(e.getResultMsg());
        }catch (Exception e){
            logger.error("批量更新执行失败",e);
            resultBean.failure();
        }
        return resultBean;
    }

}
