package com.sncfc.scheduler.server.controller;

import com.sncfc.scheduler.server.pojo.BaseResultBean;
import com.sncfc.scheduler.server.pojo.Pager;
import com.sncfc.scheduler.server.pojo.Project;
import com.sncfc.scheduler.server.service.ILoggerService;
import com.sncfc.scheduler.server.service.IScheduleJobService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Map;


/**
 * Created by 123 on 2017/1/19.
 */
@Controller
@RequestMapping("/admin")
public class LoggerManagerController extends BaseActionController{
    private static final Logger logger = LoggerFactory.getLogger(LoggerManagerController.class);

    @Autowired
    private IScheduleJobService scheduleJobService;

    @Autowired
    private ILoggerService loggerService;


    /**
     * 查询日志初始化
     * @return
     */
    @ResponseBody
    @RequestMapping(value = "queryLoggerInit")
    public ModelAndView queryLoggerInit(){
        ModelAndView modelAndView = new ModelAndView();
        List<Project> projects = scheduleJobService.getProjectList();
        modelAndView.getModel().put("projects",projects);
        modelAndView.setViewName("loggerManager/queryLogger");
        return modelAndView;
    }

    /**
     * 查询任务
     * @return
     */
    @ResponseBody
    @RequestMapping(value = "queryLogger")
    public BaseResultBean queryLogger(HttpServletRequest request){
        Map paramsMap = getParameterMap(request);
        BaseResultBean resultBean = new BaseResultBean();
        try {
            Pager pager = loggerService.queryJobList(paramsMap);
            resultBean.success();
            resultBean.setResult(pager);
        }catch (Exception e){
            logger.error("分页查询日志失败",e);
            resultBean.failure();
        }
        return resultBean;
    }
}
