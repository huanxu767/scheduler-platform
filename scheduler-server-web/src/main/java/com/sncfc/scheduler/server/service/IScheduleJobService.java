package com.sncfc.scheduler.server.service;


import com.sncfc.scheduler.server.pojo.Pager;
import com.sncfc.scheduler.server.pojo.Project;
import com.sncfc.scheduler.server.pojo.ScheduleJob;
import com.sncfc.scheduler.server.pojo.ScheduleLog;

import java.util.List;
import java.util.Map;

public interface IScheduleJobService {


    /**
     * 新增
     * 
     * @param ScheduleJob
     * @return
     */
    Long insert(ScheduleJob ScheduleJob);

    /**
     * 删除
     *
     * @param scheduleJobId
     */
    void delete(Long scheduleJobId);

    /**
     * 批量删除任务
     *
     * @param scheduleJobIds
     */
    void batchDelete(List<Long> scheduleJobIds);

    /**
     * 运行一次任务
     *
     * @param scheduleJobId the schedule job id
     * @return
     */
    void runOnce(Long scheduleJobId);

    /**
     * 暂停任务
     *
     * @param scheduleJobId the schedule job id
     * @return
     */
    void pauseJob(Long scheduleJobId);

    /**
     * 批量暂停
     * @param scheduleJobIds
     */
    void batchPauseJob(List<Long> scheduleJobIds);

    /**
     * 恢复任务
     *
     * @param scheduleJobId the schedule job id
     * @return
     */
    void resumeJob(Long scheduleJobId);


    /**
     * 获取任务对象
     * 
     * @param scheduleJobId
     * @return
     */
    ScheduleJob get(Long scheduleJobId);

    /**
     * 启动任务
     * @param scheduleJobId
     */
    void startJob(Long scheduleJobId);

    /**
     * 批量启动任务
     * @param scheduleJobIds
     */
    void batchStartJob(List<Long> scheduleJobIds);

    /**
     * 更新日志
     * @param fireInstanceId
     * @param responseMap
     * @return
     */
    int updateScheduleLog(String fireInstanceId, Map responseMap);
    /**
     * 执行日志增加
     * @param scheduleLog
     * @return
     */
    int addScheduleLog(ScheduleLog scheduleLog);


    /**
     * 是否存在该日志
     * @param nodeName
     * @param fireInstanceId
     * @return
     */
    boolean existedScheduleLog(String nodeName, String fireInstanceId);
    boolean existedScheduleLog(String fireInstanceId);

    /**
     * 更新
     * @param requestList
     * @return
     */
    List<String> updateScheduleLog(List requestList);

    /**
     * 新增JOB
     * @param scheduleJob
     * @return
     */
    Long addScheduleJob(ScheduleJob scheduleJob);

    /**
     * 查询任务列表 分页
     * @param paramsMap
     * @return
     */
    Pager queryJobList(Map paramsMap);

    /**
     * 查询任务主明细和参数明细
     * @param id
     * @return
     */
    ScheduleJob getScheduleJobAndParams(long id);

    /**
     * 获取项目列表
     * @return
     */
    List<Project> getProjectList();

    /**
     * 更新任务
     * @param scheduleJob
     * @return
     */
    void modifyJobDetail(ScheduleJob scheduleJob);

    /**
     * 查询 触发列表按天
     * @param days
     * @return
     */
    List countTriggersList(int days);
    /**
     * 查询触发数 按天
     * @param days
     * @return
     */
    Map countTriggers(int days);

    /**
     * 查询总触发数
     * @return
     */
    Map countAllTriggers();

    /**
     * 更新日志
     * @param scheduleLog
     */
    int updateScheduleLog(ScheduleLog scheduleLog);

    /**
     * 批量新增任务
     * @param scheduleJobList
     * @return
     */
    List batchAddScheduleJob(List<ScheduleJob> scheduleJobList);
    /**
     * 批量修改
     * @param scheduleJobs
     * @return
     */
    void batchModifyJobDetail(List<ScheduleJob> scheduleJobs);
}
