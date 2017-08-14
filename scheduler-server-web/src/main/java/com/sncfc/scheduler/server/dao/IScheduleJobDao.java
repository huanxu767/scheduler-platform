package com.sncfc.scheduler.server.dao;


import com.sncfc.scheduler.server.pojo.Project;
import com.sncfc.scheduler.server.pojo.ScheduleJob;
import com.sncfc.scheduler.server.pojo.ScheduleJobParams;
import com.sncfc.scheduler.server.pojo.ScheduleLog;

import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Created by 123 on 2017/1/19.
 */
public interface IScheduleJobDao {
    /**
     * 新增任务
     * @param job
     * @return
     */
    Long insertScheduleJob(final ScheduleJob job);

    /**
     * 添加任务参数表
     * @param paramsList
     * @return
     */
    void insertScheduleJobParams(final Long id, final List<ScheduleJobParams> paramsList);

    /**
     * 获取任务明细
     * @param scheduleJobId
     * @return
     */
    ScheduleJob queryScheduleJob(Long scheduleJobId);

    /**
     * 更新任务状态
     * @param scheduleJobId
     * @return
     */
    int updateJobStatus(Long scheduleJobId, int status);

    /**
     * 更新
     * @param scheduleJob
     * @return
     */
    int updateJob(ScheduleJob scheduleJob);

    /**
     * 添加执行记录
     * @return
     */
    int insertScheduleLog(ScheduleLog scheduleLog);

    /**
     * 查询日志
     * @param nodeName
     * @param fireInstanceId
     * @return
     */
    int existedScheduleLog(String nodeName, String fireInstanceId);

    /**
     * 更新执行状态
     * @param fireInstanceId
     * @param executed
     * @param errorMsg
     */
    void updateLogStatus(String fireInstanceId, boolean executed, String errorMsg);

    /**
     * 查询节点下面未完成的任务
     * @param nodeSet
     * @return
     */
    List<String> queryUnCompleteJob(Set<String> nodeSet);

    /**
     * 查询任务总数
     * @param paramsMap
     * @return
     */
    Integer countScheduleJob(Map paramsMap);

    /**
     * 查询任务列表
     * @param curPage
     * @param pageSize
     * @param paramsMap
     * @return
     */
    List queryScheduleJobList(int curPage, int pageSize, Map paramsMap);

    /**
     * 查询任务参数明细
     * @param scheduleJobId
     * @return
     */
    List<ScheduleJobParams> queryScheduleJobParams(long scheduleJobId);

    /**
     * 获取项目列表
     * @return
     */
    List<Project> queryProjectList();

    /**
     * 删除参数表
     * @param scheduleJobId
     * @return
     */
    int deleteScheduleJobParams(Long scheduleJobId);

    Map countTriggers(int days);

    List countTriggersList(int days);

    List countJobs();

    List countAllTriggers();

    int updateScheduleLog(ScheduleLog scheduleLog);

    int existedScheduleLog(String fireInstanceId);
}
