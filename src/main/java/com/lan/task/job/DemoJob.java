package com.lan.task.job;

import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

import com.lan.task.framework.model.BaseJob;

public class DemoJob extends BaseJob{

	@Override
	public String run(JobExecutionContext context) throws JobExecutionException {
		System.out.println("DemoJob-------------------------------------run");
		return "success";
	}
	
}
