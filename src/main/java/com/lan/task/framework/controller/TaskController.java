package com.lan.task.framework.controller;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;

import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.lan.task.framework.model.Page;
import com.lan.task.framework.model.ResultMessage;
import com.lan.task.framework.model.SchedulerJob;
import com.lan.task.framework.service.FrameworkTaskService;

@RestController
@RequestMapping("framework")
public class TaskController {

	@Resource
	private FrameworkTaskService taskService;
	
	@PostConstruct
	public void initAllJobIntoScheduler(){
		taskService.initAllJobIntoScheduler();
	}

	@RequestMapping("initCreateTable")
	public ResultMessage initCreateTable() {
		boolean success = taskService.initCreateTable();
		return ResultMessage.success(success);
	}
	
	@PostMapping("createTask")
	public ResultMessage createTask(SchedulerJob job) {
		if (StringUtils.isEmpty(job.getJobName())) {
			return ResultMessage.error("name不能为空");
		}
		if (StringUtils.isEmpty(job.getJobGroup())) {
		//	return ResultMessage.error("group不能为空");
			job.setJobGroup("default");
		}
		if (StringUtils.isEmpty(job.getClassName())) {
			return ResultMessage.error("ClassName不能为空");
		}
		if (StringUtils.isEmpty(job.getCron())) {
			return ResultMessage.error("Cron不能为空");

		}
		if (StringUtils.isEmpty(job.getDescription())) {
			return ResultMessage.error("Description不能为空");
		}
		boolean checkCron = taskService.checkCron(job.getCron());
		if(!checkCron){
			return ResultMessage.error("cron表达式错误");
		}
		boolean checkJob = taskService.checkJobClass(job.getClassName());
		if(!checkJob){
			return ResultMessage.error(job.getClassName()+"类错误,该类必须是继承BaseJob类的最终类");
		}
		SchedulerJob dbJob = taskService.getByNameAndGroup(job.getJobName(), job.getJobGroup());;
		if(dbJob!=null){
			return ResultMessage.error("系统中已存在同名同组的，请确认name和group。");
		}
		boolean success = taskService.createJob(job);
		return new ResultMessage(success);
	}
	
	@PostMapping("changeStatus")
	public ResultMessage changeStatus(Integer jobId, String status) {
		if (jobId==null || jobId<0) {
			return ResultMessage.error("JobId不能为空");
		}
		if (StringUtils.isEmpty(status) || (!"01".equals(status) && !"02".equals(status))) {
			return ResultMessage.error("Status错误");
		}
		boolean success = taskService.changeStatus(jobId, status);
		return new ResultMessage(success);
	}
	
	@PostMapping("removeJob")
	public ResultMessage removeJob(Integer jobId) {
		if (jobId==null || jobId<0) {
			return ResultMessage.error("jobId不能为空");
		}
		boolean success = taskService.removeJob(jobId);
		return new ResultMessage(success);
	}

	@PostMapping("updateCron")
	public ResultMessage updateCron(Integer jobId, String cron) {
		if (jobId==null || jobId<0) {
			return ResultMessage.error("jobId不能为空");
		}
		if(StringUtils.isEmpty(cron)){
			return ResultMessage.error("cron不能为空");
		}
		if(!taskService.checkCron(cron)){
			return ResultMessage.error("cron表达式错误，请检查");
		}
		
		boolean success = taskService.updateCron(jobId, cron);
		return new ResultMessage(success);
	}

	@PostMapping("runNow")
	public ResultMessage runNow(Integer jobId) {
		if (jobId==null || jobId<0) {
			return ResultMessage.error("jobId不能为空");
		}
		boolean success = taskService.runNow(jobId);
		if(!success){
			return ResultMessage.error("运行失败，请检查是否是生效中的任务，仅生效中的任务可运行");
		}
		return new ResultMessage(success);
	}
	
	@PostMapping("findAllTask")
	public ResultMessage findAllTask(Page page){
		page = taskService.findTaskByStatusPage(new String[]{"01", "02"}, page);
		return ResultMessage.success(page);
	}
	
	@PostMapping("findRunningTask")
	public ResultMessage findRunningTask(Page page){
		page = taskService.findTaskByStatusPage(new String[]{"01"}, page);
		return ResultMessage.success(page);
	}
	
	@PostMapping("findSuspendTask")
	public ResultMessage findSuspendTask(Page page){
		page = taskService.findTaskByStatusPage(new String[]{"02"}, page);
		return ResultMessage.success(page);
	}
	
	@PostMapping("findDeleteTask")
	public ResultMessage findDeleteTask(Page page){
		page = taskService.findTaskByStatusPage(new String[]{"-1"}, page);
		return ResultMessage.success(page);
	}
	
	@PostMapping("findHistory")
	public ResultMessage findHistory(Page page, Integer jobId, String type){
		page = taskService.findHistoryPage(jobId, type, page);
		return ResultMessage.success(page);
	}
}
