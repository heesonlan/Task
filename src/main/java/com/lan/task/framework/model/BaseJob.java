package com.lan.task.framework.model;

import java.util.Date;

import org.quartz.Job;
import org.quartz.JobDataMap;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.lan.task.framework.mapper.SchedulerJobLogMapper;
import com.lan.task.framework.mapper.SchedulerJobMapper;

public abstract class BaseJob implements Job {
	
	public Logger log = LoggerFactory.getLogger(this.getClass());

	@Override
	public void execute(JobExecutionContext context) throws JobExecutionException {
		beforeExecute(context);
		Date startTime = new Date();
		try {
			String result = run(context);
			afterExecute(context, startTime, result);
		} catch (Exception e) {
			log.error("定时任务执行失败", e);
			afterErrorExecute(context, startTime, e);
			throw e;
		}
	}
	
	private void beforeExecute(JobExecutionContext context){
		JobDataMap jobDataMap = context.getMergedJobDataMap();
		SchedulerJob taskJob = (SchedulerJob)jobDataMap.get("taskJob");
		SchedulerJobMapper schedulerJobMapper = (SchedulerJobMapper)jobDataMap.get("schedulerJobMapper");
		log.info("启动任务["+taskJob.getJobName()+"]================>>");
		schedulerJobMapper.beforeRun(taskJob.getJobId(), SimpleTriggerContext.getNextRunTime(taskJob.getCron()));
	}

	private void afterErrorExecute(JobExecutionContext context, Date startTime, Throwable e) {
		JobDataMap jobDataMap = context.getMergedJobDataMap();
		SchedulerJob taskJob = (SchedulerJob)jobDataMap.get("taskJob");
		SchedulerJobMapper schedulerJobMapper = (SchedulerJobMapper)jobDataMap.get("schedulerJobMapper");
		SchedulerJobLogMapper schedulerJobLogMapper = (SchedulerJobLogMapper)jobDataMap.get("schedulerJobLogMapper");
		log.error("任务["+taskJob.getJobName()+"]运行失败================<<", e);
		
		schedulerJobMapper.afterError(taskJob.getJobId());
		
		SchedulerJobLog jobLog = new SchedulerJobLog();
		jobLog.setJobId(taskJob.getJobId());
		jobLog.setJobName(taskJob.getJobName());
		jobLog.setRemark(e.getMessage());
		jobLog.setStartTime(startTime);
		jobLog.setEndTime(new Date());
		jobLog.setCreateTime(new Date());
		jobLog.setFailTime(new Date());
		schedulerJobLogMapper.insertSelective(jobLog);
	}

	private void afterExecute(JobExecutionContext context, Date startTime, String result){
		JobDataMap jobDataMap = context.getMergedJobDataMap();
		SchedulerJob taskJob = (SchedulerJob)jobDataMap.get("taskJob");
		SchedulerJobMapper schedulerJobMapper = (SchedulerJobMapper)jobDataMap.get("schedulerJobMapper");
		SchedulerJobLogMapper schedulerJobLogMapper = (SchedulerJobLogMapper)jobDataMap.get("schedulerJobLogMapper");
		log.info("任务["+taskJob.getJobName()+"]运行结束,结果："+result+"================<<");
		
		schedulerJobMapper.afterRun(taskJob.getJobId());
		
		SchedulerJobLog jobLog = new SchedulerJobLog();
		jobLog.setJobId(taskJob.getJobId());
		jobLog.setJobName(taskJob.getJobName());
		jobLog.setRemark(result);
		jobLog.setStartTime(startTime);
		jobLog.setEndTime(new Date());
		jobLog.setCreateTime(new Date());
		jobLog.setSuccessTime(new Date());
		schedulerJobLogMapper.insertSelective(jobLog);
	}
	
	public abstract String run(JobExecutionContext context) throws JobExecutionException ;
	
}
