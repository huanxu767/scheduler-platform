package com.sncfc.scheduler.server.pojo;

import java.io.Serializable;
import java.util.Date;

/**
 * 定时任务
 */
public class ScheduleLog implements Serializable {
    public final static String AUTO_TRIGGER = "1";
    public final static String CUSTOMER_TRIGGER = "2";

    public final static String TRIGGER_SUCCESS = "1";
    public final static String TRIGGER_FAILURE = "2";
    public final static String JOB_SUCCESS = "3";
    public final static String JOB_FAILURE = "4";

    public final static String CLIENT_TIGGERED = "1";
    public final static String CLIENT_DOING = "2";
    public final static String CLIENT_END = "3";


    /**
     * 任务名称-- 关联 SCHEDULE_JOB.SCHEDULE_JOB_ID
     */
    private String jobName;
    /** 任务组-- 关联 SCHEDULE_JOB.PROJECT_ID*/
    private String jobGroup;
    private String fireInstanceId;
    /** 1触发成功 2触发失败 3执行成功 4 执行失败 */
    private String success;
    /** 1已触发 2 客户端已执行 3已结束 */
    private String status;
    private String nodeName;
    private String errorMessage;
    private Date createTime;
    private Date endTime;
    /** 1自动 2手动 */
    private String triggerType;

    private String jobAliasName;
    private String jobGroupName;


    public ScheduleLog() {

    }

    public ScheduleLog(String jobName, String jobGroup, String fireInstanceId) {
        this.jobName = jobName;
        this.jobGroup = jobGroup;
        this.fireInstanceId = fireInstanceId;
    }

    public String getJobName() {
        return jobName;
    }

    public void setJobName(String jobName) {
        this.jobName = jobName;
    }

    public String getJobGroup() {
        return jobGroup;
    }

    public void setJobGroup(String jobGroup) {
        this.jobGroup = jobGroup;
    }

    public String getFireInstanceId() {
        return fireInstanceId;
    }

    public void setFireInstanceId(String fireInstanceId) {
        this.fireInstanceId = fireInstanceId;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getNodeName() {
        return nodeName;
    }

    public void setNodeName(String nodeName) {
        this.nodeName = nodeName;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public String getSuccess() {
        return success;
    }

    public void setSuccess(String success) {
        this.success = success;
    }

    public Date getEndTime() {
        return endTime;
    }

    public void setEndTime(Date endTime) {
        this.endTime = endTime;
    }

    public String getJobAliasName() {
        return jobAliasName;
    }

    public void setJobAliasName(String jobAliasName) {
        this.jobAliasName = jobAliasName;
    }

    public String getJobGroupName() {
        return jobGroupName;
    }

    public void setJobGroupName(String jobGroupName) {
        this.jobGroupName = jobGroupName;
    }

    public String getTriggerType() {
        return triggerType;
    }

    public void setTriggerType(String triggerType) {
        this.triggerType = triggerType;
    }
}
