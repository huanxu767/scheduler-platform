package com.sncfc.scheduler.server.pojo;


/**
 * 参数
 */
public class ScheduleJobParams {

    // 任务编号 —job_name
    private Long scheduleJobId;
    // 参数值
    private String value;

    public Long getScheduleJobId() {
        return scheduleJobId;
    }

    public void setScheduleJobId(Long scheduleJobId) {
        this.scheduleJobId = scheduleJobId;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }
}
