package com.sncfc.scheduler.server.pojo;

import org.quartz.CronExpression;
import org.springframework.util.StringUtils;

import java.util.Date;
import java.util.List;

/**
 * 定时任务
 */
public class ScheduleJob {

    public final static Integer DELETE = 0;
    public final static Integer WAITING = 1;
    public final static Integer PAUSED = 2;
    public final static Integer DOING = 3;

    // 任务编号 —job_name
    private Long scheduleJobId;
    // 项目编号 - job_group
    private Long projectId;
    // 任务别名
    private String jobAliasName;
    // 1 异步 默认 2 同步
    private String sync = "1";
    // 链接
    private String url;
    // 1 静态方法 2 SpringBean
    private String jobType;
    // 类名
    private String className;
    // 方法名
    private String methodName;
    // 创建时间
    private Date createTime;
    // 更新时间
    private Date updateTime;
    // 0已删除1 待执行 2 暂停 3 执行中
    private int status;
    // 表达式
    private String cronExpression;
    //任务描述
    private String jobDesc;
    //参数
    private List<ScheduleJobParams> paramsList;


    public List<ScheduleJobParams> getParamsList() {
        return paramsList;
    }

    /**
     * 验证主要属性
     * @return
     */
    public boolean validate(){
        if(StringUtils.isEmpty(this.projectId)
                || StringUtils.isEmpty(this.jobAliasName)
                || StringUtils.isEmpty(this.url)
                || StringUtils.isEmpty(this.jobType)
                || StringUtils.isEmpty(this.className)
                || StringUtils.isEmpty(this.methodName)
                || StringUtils.isEmpty(this.cronExpression)){
            return false;
        }
        return CronExpression.isValidExpression(cronExpression);
    }


    public void setParamsList(List<ScheduleJobParams> paramsList) {
        this.paramsList = paramsList;
    }

    public Long getScheduleJobId() {
        return scheduleJobId;
    }

    public void setScheduleJobId(Long scheduleJobId) {
        this.scheduleJobId = scheduleJobId;
    }

    public Long getProjectId() {
        return projectId;
    }

    public void setProjectId(Long projectId) {
        this.projectId = projectId;
    }

    public String getJobAliasName() {
        return jobAliasName;
    }

    public void setJobAliasName(String jobAliasName) {
        this.jobAliasName = jobAliasName;
    }

    public String getSync() {
        return sync;
    }

    public boolean getSyncBool() {
        return "1".equals(sync) ? true : false;
    }

    public void setSync(String sync) {
        this.sync = sync;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getJobType() {
        return jobType;
    }

    public void setJobType(String jobType) {
        this.jobType = jobType;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public Date getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(Date updateTime) {
        this.updateTime = updateTime;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getCronExpression() {
        return cronExpression;
    }

    public void setCronExpression(String cronExpression) {
        this.cronExpression = cronExpression;
    }

    public String getJobName() {
        return scheduleJobId.toString();
    }

    public String getJobGroup() {
        return projectId.toString();
    }

    public String getJobDesc() {
        return jobDesc;
    }

    public void setJobDesc(String jobDesc) {
        this.jobDesc = jobDesc;
    }

    public String getClassName() {
        return className;
    }

    public void setClassName(String className) {
        this.className = className;
    }

    public String getMethodName() {
        return methodName;
    }

    public void setMethodName(String methodName) {
        this.methodName = methodName;
    }



}
