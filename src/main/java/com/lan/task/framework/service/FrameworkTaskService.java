package com.lan.task.framework.service;

import java.util.List;

import com.lan.task.framework.model.Page;
import com.lan.task.framework.model.SchedulerJob;

public interface FrameworkTaskService {

	boolean createJob(SchedulerJob job);

	boolean changeStatus(Integer jobId, String status);

	boolean addTask(SchedulerJob taskJob);

	boolean checkCron(String cron);

	boolean checkJobClass(String className);

	boolean removeJob(Integer jobId);

	SchedulerJob getByNameAndGroup(String name, String group);

	boolean updateCron(Integer jobId, String cron);

	boolean runNow(Integer jobId);

	boolean initCreateTable();

	void initAllJobIntoScheduler();

	List<SchedulerJob> findTaskByStatus(String[] status);

	Page findTaskByStatusPage(String[] status, Page page);

	Page findHistoryPage(Integer jobId, String type, Page page);

	
}
