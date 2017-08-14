package com.sncfc.scheduler.client.core;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.locks.ReentrantLock;


public class JobExecutionContext {

    private String fireInstanceId;

    private String jobGroup;

    private String jobName;

    private String triggerGroup;

    private String triggerName;

    private JobBroker jobBroker;

    private Map<String, Object> data = new HashMap<String, Object>();

    private boolean validated = false;

    private ReentrantLock executingLock = new ReentrantLock();

    private boolean executed = false;

    private String exceptionStackTrace = null;

    private String rootExceptionClassName = null;

    private String rootExceptionMsg = null;

    private Long receivedTime;

    private boolean concurrentExectionDisallowed = false;

    public JobExecutionContext(String fireInstanceId, String jobGroup, String jobName,
                               String triggerGroup, String triggerName,
                               JobBroker jobBroker, Map<String, Object> data,
                               boolean concurrentExectionDisallowed) {
        this.fireInstanceId = fireInstanceId;
        this.jobGroup = jobGroup;
        this.jobName = jobName;
        this.triggerGroup = triggerGroup;
        this.triggerName = triggerName;
        this.jobBroker = jobBroker;
        this.data = data;
        this.receivedTime = System.currentTimeMillis();
        this.concurrentExectionDisallowed = concurrentExectionDisallowed;
    }

    public String getFireInstanceId() {
        return fireInstanceId;
    }

    public String getJobGroup() {
        return jobGroup;
    }

    public String getJobName() {
        return jobName;
    }

    public String getTriggerGroup() {
        return triggerGroup;
    }

    public String getTriggerName() {
        return triggerName;
    }

    public Object getData(String key) {
        return data.get(key);
    }

    public Set<String> getDataKeys() {
        return data.keySet();
    }

    protected boolean isExecuted() {
        return executed;
    }

    protected void setExecuted(boolean executed) {
        this.executed = executed;
    }

    protected String getExceptionStackTrace() {
        return exceptionStackTrace;
    }

    protected void setExceptionStackTrace(String exceptionStackTrace) {
        this.exceptionStackTrace = exceptionStackTrace;
    }

    protected String getRootExceptionClassName() {
        return rootExceptionClassName;
    }

    protected void setRootExceptionClassName(String rootExceptionClassName) {
        this.rootExceptionClassName = rootExceptionClassName;
    }

    protected boolean hasValidated() {
        return validated;
    }

    protected void setValidated(boolean validated) {
        this.validated = validated;
    }

    protected Long getReceivedTime() {
        return receivedTime;
    }

    protected JobBroker getJobBroker() {
        return jobBroker;
    }

    protected void setRootExceptionMsg(String rootExceptionMsg) {
        this.rootExceptionMsg = rootExceptionMsg;
    }

    protected String getRootExceptionMsg() {
        return rootExceptionMsg;
    }

    protected boolean acquireExecutingLock() {
        return executingLock.tryLock();
    }

    protected void releaseExecutingLock() {
        executingLock.unlock();
    }

    protected boolean isConcurrentExectionDisallowed() {
        return concurrentExectionDisallowed;
    }
}
