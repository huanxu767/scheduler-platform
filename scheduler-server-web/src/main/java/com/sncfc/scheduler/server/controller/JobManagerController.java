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
@RequestMapping("/admin")
public class JobManagerController extends BaseActionController{
    private static final Logger logger = LoggerFactory.getLogger(JobManagerController.class);

    @Autowired
    private IScheduleJobService scheduleJobService;

    /**
     * 主框架
     * @return
     */
    @RequestMapping(value = "main")
    public ModelAndView testAddJob(){
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName("main");
        return modelAndView;
    }
    /**
     * 头页面
     * @return
     */
    @RequestMapping(value = "head")
    public ModelAndView top(){
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName("head");
        return modelAndView;
    }
    /**
     * 菜单
     * @return
     */
    @RequestMapping(value = "menu")
    public ModelAndView left(){
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName("menu");
        modelAndView.getModel().put("activeMenu","10000");
        return modelAndView;
    }
    /**
     * 首页
     * @return
     */
    @RequestMapping(value = "dashboard")
    public ModelAndView dashboard(){
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName("jobManager/dashboard");
        return modelAndView;
    }
    /**
     * 内容
     * @return
     */
    @RequestMapping(value = "content")
    public ModelAndView right(){
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName("content");
        return modelAndView;
    }

    /**
     * 新增任务初始化
     * @return
     */
    @RequestMapping(value = "addJob")
    public ModelAndView addJob(){
        ModelAndView modelAndView = new ModelAndView();
        List<Project> projects = scheduleJobService.getProjectList();
        modelAndView.getModel().put("projects",projects);
        modelAndView.setViewName("jobManager/addJob");
        return modelAndView;
    }



    /**
     * 新增任务
     * @return
     */
    @ResponseBody
    @RequestMapping(value = "addJobDetail")
    public BaseResultBean addJobDetail(ScheduleJob scheduleJob){
        BaseResultBean resultBean = new BaseResultBean();
        try {
            Map resultMap = new HashMap();
            Long id = scheduleJobService.addScheduleJob(scheduleJob);
            resultMap.put("id",id);
            resultBean.setResult(resultMap);
            resultBean.success();
        }catch (ResultException e){
            logger.error("新增任务失败",e);
            resultBean.failure();
            resultBean.setErrorMsg(e.getResultMsg());
        }catch (Exception e){
            logger.error("新增任务失败",e);
            resultBean.failure();
        }
        return resultBean;
    }


    /**
     * 更新任务
     * @return
     */
    @ResponseBody
    @RequestMapping(value = "modifyJobDetail")
    public BaseResultBean modifyJobDetail(ScheduleJob scheduleJob){
        BaseResultBean resultBean = new BaseResultBean();
        try {
            scheduleJobService.modifyJobDetail(scheduleJob);
            resultBean.success();
        }catch (ResultException e){
            logger.error("更新任务失败",e);
            resultBean.failure();
            resultBean.setErrorMsg(e.getResultMsg());
        }catch (Exception e){
            logger.error("更新任务失败",e);
            resultBean.failure();
        }
        return resultBean;
    }

    /**
     * 查询任务初始化
     * @return
     */
    @ResponseBody
    @RequestMapping(value = "queryJobInit")
    public ModelAndView queryJobInit(){
        ModelAndView modelAndView = new ModelAndView();
        List<Project> projects = scheduleJobService.getProjectList();
        modelAndView.getModel().put("projects",projects);
        modelAndView.setViewName("jobManager/queryJob");
        return modelAndView;
    }

    /**
     * 查询任务
     * @return
     */
    @ResponseBody
    @RequestMapping(value = "queryJob")
    public BaseResultBean queryJob(HttpServletRequest request){
        Map paramsMap = getParameterMap(request);
        BaseResultBean resultBean = new BaseResultBean();
        try {
            Pager pager = scheduleJobService.queryJobList(paramsMap);
            resultBean.success();
            resultBean.setResult(pager);
        }catch (Exception e){
            logger.error("分页查询任务失败",e);
            resultBean.failure();
        }
        return resultBean;
    }

    /**
     * 删除任务
     * @return
     */
    @ResponseBody
    @RequestMapping(value = "deleteJob")
    public BaseResultBean deleteJob(Long scheduleJobId){
        BaseResultBean resultBean = new BaseResultBean();
        try {
            scheduleJobService.delete(scheduleJobId);
            resultBean.success();
        }catch (ResultException e){
            logger.error("删除任务失败",e);
            resultBean.failure();
            resultBean.setErrorMsg(e.getResultMsg());
        }catch (Exception e){
            logger.error("删除任务失败",e);
            resultBean.failure();
        }
        return resultBean;
    }

    /**
     * 暂停任务
     * @return
     */
    @ResponseBody
    @RequestMapping(value = "pauseJob")
    public BaseResultBean pauseJob(Long scheduleJobId){
        BaseResultBean resultBean = new BaseResultBean();
        try {
            scheduleJobService.pauseJob(scheduleJobId);
            resultBean.success();
        }catch (ResultException e){
            logger.error("暂停任务失败",e);
            resultBean.failure();
            resultBean.setErrorMsg(e.getResultMsg());
        }catch (Exception e){
            logger.error("暂停任务失败",e);
            resultBean.failure();
        }
        return resultBean;
    }

    /**
     * 恢复 执行
     * @return
     */
    @ResponseBody
    @RequestMapping(value = "resumeJob")
    public BaseResultBean resumeJob(Long scheduleJobId){
        BaseResultBean resultBean = new BaseResultBean();
        try {
            scheduleJobService.startJob(scheduleJobId);
            resultBean.success();
        }catch (ResultException e){
            logger.error("恢复任务失败",e);
            resultBean.failure();
            resultBean.setErrorMsg(e.getResultMsg());
        }catch (Exception e){
            logger.error("恢复任务失败",e);
            resultBean.failure();
        }
        return resultBean;
    }

    /**
     * 运行一次
     * @return
     */
    @ResponseBody
    @RequestMapping(value = "runOnce")
    public BaseResultBean runOnce(Long scheduleJobId){
        BaseResultBean resultBean = new BaseResultBean();
        try {
            scheduleJobService.runOnce(scheduleJobId);
            resultBean.success();
        }catch (ResultException e){
            logger.error("运行一次任务失败",e);
            resultBean.failure();
            resultBean.setErrorMsg(e.getResultMsg());
        }catch (Exception e){
            logger.error("运行一次任务失败",e);
            resultBean.failure();
        }
        return resultBean;
    }

    /**
     * 修改任务初始化
     * @return
     */
    @ResponseBody
    @RequestMapping(value = "modifyJobInit")
    public ModelAndView modifyJobInit(Long scheduleJobId){
        ModelAndView modelAndView = new ModelAndView();
        ScheduleJob scheduleJob = scheduleJobService.getScheduleJobAndParams(scheduleJobId);
        List<Project> projects = scheduleJobService.getProjectList();
        modelAndView.getModel().put("scheduleJob",scheduleJob);
        modelAndView.getModel().put("projects",projects);
        modelAndView.setViewName("jobManager/modifyJob");
        return modelAndView;
    }

    /**
     * 任务初始化
     * @return
     */
    @ResponseBody
    @RequestMapping(value = "jobDetail")
    public ModelAndView jobDetail(Long scheduleJobId){
        ModelAndView modelAndView = new ModelAndView();
        ScheduleJob scheduleJob = scheduleJobService.getScheduleJobAndParams(scheduleJobId);
        List<Project> projects = scheduleJobService.getProjectList();
        modelAndView.getModel().put("scheduleJob",scheduleJob);
        modelAndView.getModel().put("projects",projects);
        modelAndView.setViewName("jobManager/jobDetail");
        return modelAndView;
    }

    /**
     * 最近10天记录
     * @return
     */
    @ResponseBody
    @RequestMapping(value = "countTriggers")
    public Map countTriggers(int days){
        Map resultMap = new HashMap();
        List list = scheduleJobService.countTriggersList(days);
        Map map = scheduleJobService.countTriggers(days);
        resultMap.put("list",list);
        resultMap.put("map",map);
        return resultMap;
    }

    /**
     * 最近10天记录
     * @return
     */
    @ResponseBody
    @RequestMapping(value = "countAllTriggers")
    public Map countTriggers(){
        Map map = scheduleJobService.countAllTriggers();
        return map;
    }

    /**
     * 验证cron表达式
     * @param cronExpression
     * @return
     */
    @ResponseBody
    @RequestMapping(value = "validExpression")
    public boolean validExpression(String cronExpression){
        return CronExpression.isValidExpression(cronExpression);
    }

    public static void main(String[] args) {
        boolean flag = CronExpression.isValidExpression("0/3 * * * * ?");
        System.out.println(flag);
    }
}
