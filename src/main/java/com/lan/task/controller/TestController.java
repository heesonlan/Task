package com.lan.task.controller;

import javax.annotation.Resource;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.lan.task.framework.model.ResultMessage;
import com.lan.task.framework.model.SchedulerJob;
import com.lan.task.framework.service.FrameworkTaskService;

@RestController
public class TestController {
	
	@Resource
	private FrameworkTaskService frameworkTaskService;

	@RequestMapping("test")
	public ResultMessage test() {
		SchedulerJob taskJob = new SchedulerJob();
		taskJob.setJobName("test");
		taskJob.setJobGroup("default");
		taskJob.setCron("0/5 * * * * ?");
		taskJob.setClassName("com.lan.task.job.DemoJob");
		boolean seccess = frameworkTaskService.addTask(taskJob);
		return ResultMessage.success(seccess);
	}
}
