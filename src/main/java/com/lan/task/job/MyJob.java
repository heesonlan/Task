package com.lan.task.job;

import javax.annotation.Resource;

import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.springframework.stereotype.Component;

import com.lan.task.framework.model.BaseJob;
import com.lan.task.mapper.SchedulerTestMapper;
import com.lan.task.model.SchedulerTest;

@Component
public class MyJob extends BaseJob{
	
	@Resource
	private SchedulerTestMapper schedulerTestMapper;
	
	@Override
	public String run(JobExecutionContext context) throws JobExecutionException {
		SchedulerTest record = new SchedulerTest();
		record.setRandom((int)Math.random()*1000);
		schedulerTestMapper.insert(record);
		return "ok";
	}

}
