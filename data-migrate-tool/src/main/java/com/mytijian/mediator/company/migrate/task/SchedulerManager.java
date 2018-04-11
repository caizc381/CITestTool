package com.mytijian.mediator.company.migrate.task;

import javax.annotation.Resource;

import org.quartz.CronScheduleBuilder;
import org.quartz.JobBuilder;
import org.quartz.JobDetail;
import org.quartz.JobKey;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.Trigger;
import org.quartz.Trigger.TriggerState;
import org.quartz.TriggerBuilder;
import org.quartz.TriggerKey;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service("schedulerManager")
public class SchedulerManager {
	private Logger logger = LoggerFactory.getLogger(SchedulerManager.class);

	@Resource(name = "schedulerFactoryBean")
	private Scheduler scheduler;

	@Value("${companyCronExp}")
	private String companyCronExp;
	
	@Value("${reportCronExp}")
	private String reportCronExp;

	public void startJob() {
		JobKey jobKey = new JobKey(MigrateCompanyDataJob.class.getSimpleName());
		JobDetail job = JobBuilder.newJob(MigrateCompanyDataJob.class)
				.withIdentity(jobKey).build();
		TriggerKey triggerKey = new TriggerKey(
				MigrateCompanyDataJob.class.getSimpleName());
		
		Trigger trigger = getTrigger(triggerKey, jobKey, companyCronExp);
		try {
			
			if (!scheduler.checkExists(jobKey)) {
				scheduler.scheduleJob(job, trigger);
			}
			if (!scheduler.isStarted()) {
				scheduler.start();
			}
			
		} catch (SchedulerException e) {
			logger.error("scheduler error", e);
		}
	}

	public String getTaskStatus() {
		TriggerKey triggerKey = new TriggerKey(
				MigrateCompanyDataJob.class.getSimpleName());
		try {
			TriggerState state = scheduler.getTriggerState(triggerKey);
			return "task status:" + state.name();
		} catch (SchedulerException e) {
			logger.error("查询任务状态错误", e);
			return "error";
		}
	}
	
	public void stopJob(){
		try {
			if (!scheduler.isShutdown()) {
				scheduler.shutdown();
			}
		} catch (SchedulerException e) {
			logger.error("stop job error", e);
		}
	}

	private Trigger getTrigger(TriggerKey triggerKey, JobKey jobKey,
			String cronExp) {
		Trigger newTrigger = TriggerBuilder.newTrigger()
				.withIdentity(triggerKey).startNow().forJob(jobKey)
				.withSchedule(CronScheduleBuilder.cronSchedule(cronExp))
				.build();
		return newTrigger;
	}

}
