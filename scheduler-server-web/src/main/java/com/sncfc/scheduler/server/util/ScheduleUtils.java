package com.sncfc.scheduler.server.util;

import com.sncfc.scheduler.server.Exception.ResultException;
import com.sncfc.scheduler.server.pojo.AsyncJobFactory;
import com.sncfc.scheduler.server.pojo.ScheduleJob;
import com.sncfc.scheduler.server.pojo.SyncJobFactory;
import org.quartz.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ScheduleUtils {

    /**
     * 日志对象
     */
    private static final Logger LOG = LoggerFactory.getLogger(ScheduleUtils.class);

    /**
     * 获取触发器key
     *
     * @param jobName
     * @param jobGroup
     * @return
     */
    public static TriggerKey getTriggerKey(String jobName, String jobGroup) {

        return TriggerKey.triggerKey(jobName, jobGroup);
    }

    /**
     * 获取表达式触发器
     *
     * @param scheduler the scheduler
     * @param jobName   the job name
     * @param jobGroup  the job group
     * @return cron trigger
     */
    public static CronTrigger getCronTrigger(Scheduler scheduler, String jobName, String jobGroup) throws SchedulerException {
        TriggerKey triggerKey = TriggerKey.triggerKey(jobName, jobGroup);
        return (CronTrigger) scheduler.getTrigger(triggerKey);
    }

    /**
     * 创建任务
     *
     * @param scheduler   the scheduler
     * @param scheduleJob the schedule job
     */
    public static void createScheduleJob(Scheduler scheduler, ScheduleJob scheduleJob) {
        createScheduleJob(scheduler, scheduleJob.getJobName(), scheduleJob.getJobGroup(), scheduleJob.getCronExpression(), scheduleJob.getSyncBool());
    }

    /**
     * 创建定时任务
     *
     * @param scheduler      the scheduler
     * @param jobName        the job name
     * @param jobGroup       the job group
     * @param cronExpression the cron expression
     * @param isSync         the is sync
     */
    public static void createScheduleJob(Scheduler scheduler, String jobName, String jobGroup, String cronExpression, boolean isSync) {
        //同步或异步
        Class<? extends Job> jobClass = isSync ? AsyncJobFactory.class : SyncJobFactory.class;
        //构建job信息
        JobDetail jobDetail = JobBuilder.newJob(jobClass).withIdentity(jobName, jobGroup).build();
        //表达式调度构建器

        //        withMisfireHandlingInstructionDoNothing
        //——不触发立即执行
        //——等待下次Cron触发频率到达时刻开始按照Cron频率依次执行
        //       withMisfireHandlingInstructionIgnoreMisfires
        //——以错过的第一个频率时间立刻开始执行
        //——重做错过的所有频率周期后
        //——当下一次触发频率发生时间大于当前时间后，再按照正常的Cron频率依次执行
        //
        //    withMisfireHandlingInstructionFireAndProceed
        //——以当前时间为触发频率立刻触发一次执行
        //——然后按照Cron频率依次执行

        CronScheduleBuilder scheduleBuilder = CronScheduleBuilder.cronSchedule(cronExpression);
//        CronScheduleBuilder scheduleBuilder = CronScheduleBuilder.cronSchedule(cronExpression).withMisfireHandlingInstructionDoNothing();
        //按新的cronExpression表达式构建一个新的trigger
        CronTrigger trigger = TriggerBuilder.newTrigger().withIdentity(jobName, jobGroup).withSchedule(scheduleBuilder).build();
        try {
            scheduler.scheduleJob(jobDetail, trigger);
        } catch (SchedulerException e) {
            LOG.error("创建定时任务失败", e);
            throw new ResultException("创建定时任务失败");
        }
    }

    /**
     * 运行一次任务
     *
     * @param scheduler
     * @param jobName
     * @param jobGroup
     */
    public static void runOnce(Scheduler scheduler, String jobName, String jobGroup) {
        JobKey jobKey = JobKey.jobKey(jobName, jobGroup);
        try {
            scheduler.triggerJob(jobKey);
        } catch (SchedulerException e) {
            LOG.error("运行一次定时任务失败", e);
            throw new ResultException("运行一次定时任务失败");
        }
    }

    /**
     * 暂停任务
     *
     * @param scheduler
     * @param jobName
     * @param jobGroup
     */
    public static void pauseJob(Scheduler scheduler, String jobName, String jobGroup) {
        JobKey jobKey = JobKey.jobKey(jobName, jobGroup);
        try {
            scheduler.pauseJob(jobKey);
        } catch (SchedulerException e) {
            LOG.error("暂停定时任务失败", e);
             throw new ResultException("暂停定时任务失败");
        }
    }

    /**
     * 恢复任务
     *
     * @param scheduler
     * @param jobName
     * @param jobGroup
     */
    public static void resumeJob(Scheduler scheduler, String jobName, String jobGroup) {

        JobKey jobKey = JobKey.jobKey(jobName, jobGroup);
        try {
            scheduler.resumeJob(jobKey);
        } catch (SchedulerException e) {
            LOG.error("恢复定时任务失败", e);
            throw new ResultException("恢复定时任务失败");
        }
    }

    /**
     * 获取jobKey
     *
     * @param jobName  the job name
     * @param jobGroup the job group
     * @return the job key
     */
    public static JobKey getJobKey(String jobName, String jobGroup) {
        return JobKey.jobKey(jobName, jobGroup);
    }

    /**
     * 更新定时任务
     *
     * @param scheduler   the scheduler
     * @param scheduleJob the schedule job
     */
    public static void updateScheduleJob(Scheduler scheduler, ScheduleJob scheduleJob) {
        updateScheduleJob(scheduler, scheduleJob.getJobName(), scheduleJob.getJobGroup(), scheduleJob.getCronExpression(), scheduleJob.getSyncBool());
    }

    /**
     * 更新定时任务
     *
     * @param scheduler      the scheduler
     * @param jobName        the job name
     * @param jobGroup       the job group
     * @param cronExpression the cron expression
     * @param isSync         the is sync
     */
    public static void updateScheduleJob(Scheduler scheduler, String jobName, String jobGroup, String cronExpression, boolean isSync) {
        //同步或异步
        Class<? extends Job> jobClass = isSync ? AsyncJobFactory.class : SyncJobFactory.class;
        try {
            JobDetail jobDetail = scheduler.getJobDetail(getJobKey(jobName, jobGroup));
            jobDetail.getJobBuilder().ofType(jobClass).build();
            TriggerKey triggerKey = ScheduleUtils.getTriggerKey(jobName, jobGroup);
            //表达式调度构建器
            CronScheduleBuilder scheduleBuilder = CronScheduleBuilder.cronSchedule(cronExpression);
            CronTrigger trigger = (CronTrigger) scheduler.getTrigger(triggerKey);
            //按新的cronExpression表达式重新构建trigger
            trigger = trigger.getTriggerBuilder().withIdentity(triggerKey).withSchedule(scheduleBuilder).build();
            // 忽略状态为PAUSED的任务，解决集群环境中在其他机器设置定时任务为PAUSED状态后，集群环境启动另一台主机时定时任务全被唤醒的bug
//            Trigger.TriggerState triggerState = scheduler.getTriggerState(trigger.getKey());
//            if(!triggerState.name().equalsIgnoreCase("PAUSED")){
//                //按新的trigger重新设置job执行
//            }
            scheduler.rescheduleJob(triggerKey, trigger);
        } catch (SchedulerException e) {
            LOG.error("更新定时任务失败", e);
             throw new ResultException("更新定时任务失败");
        }
    }

    /**
     * 删除定时任务
     *
     * @param scheduler
     * @param jobName
     * @param jobGroup
     */
    public static void deleteScheduleJob(Scheduler scheduler, String jobName, String jobGroup) {
        try {
            boolean flag = scheduler.deleteJob(getJobKey(jobName, jobGroup));
            if(!flag){
                throw new ResultException("删除定时任务失败");
            }
        } catch (SchedulerException e) {
            LOG.error("删除定时任务异常", e);
             throw new ResultException("删除定时任务异常");
        }
    }
}
