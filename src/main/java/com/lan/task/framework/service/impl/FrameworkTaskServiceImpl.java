package com.lan.task.framework.service.impl;

import java.lang.reflect.Modifier;
import java.text.ParseException;
import java.util.List;

import javax.annotation.Resource;

import org.quartz.CronExpression;
import org.quartz.CronScheduleBuilder;
import org.quartz.CronTrigger;
import org.quartz.Job;
import org.quartz.JobBuilder;
import org.quartz.JobDataMap;
import org.quartz.JobDetail;
import org.quartz.JobKey;
import org.quartz.ScheduleBuilder;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.Trigger;
import org.quartz.TriggerBuilder;
import org.quartz.TriggerKey;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.quartz.SchedulerFactoryBean;
import org.springframework.stereotype.Service;

import com.lan.task.framework.mapper.SchedulerJobLogMapper;
import com.lan.task.framework.mapper.SchedulerJobMapper;
import com.lan.task.framework.model.BaseJob;
import com.lan.task.framework.model.Page;
import com.lan.task.framework.model.SchedulerJob;
import com.lan.task.framework.model.SchedulerJobLog;
import com.lan.task.framework.service.FrameworkTaskService;

/**
 * 
 * @author LAN
 * @date 2018年9月7日
 */
@Service("frameworkTaskService")
public class FrameworkTaskServiceImpl implements FrameworkTaskService{

	private Logger log = LoggerFactory.getLogger(this.getClass());
	@Resource
	private SchedulerFactoryBean schedulerFactoryBean;//该类在springboot加载时已自动加载
	
	@Resource
	private SchedulerJobMapper schedulerJobMapper;
	
	@Resource
	private SchedulerJobLogMapper schedulerJobLogMapper;
	
	@Override
	@SuppressWarnings("unchecked")
	public boolean addTask(SchedulerJob taskJob){
		Scheduler scheduler = schedulerFactoryBean.getScheduler();
		
		try {
			Class<? extends Job> clazz = (Class<? extends Job>)Class.forName(taskJob.getClassName());
			
			JobDetail jobDetail = JobBuilder.newJob(clazz).withIdentity(taskJob.getJobName(), taskJob.getJobGroup()).build();
			JobDataMap jobDataMap = jobDetail.getJobDataMap();
			jobDataMap.put("taskJob", taskJob);
			jobDataMap.put("schedulerJobMapper", schedulerJobMapper);
			jobDataMap.put("schedulerJobLogMapper", schedulerJobLogMapper);
			//创建Trigger
			ScheduleBuilder<?> cronScheduleBuilder = CronScheduleBuilder.cronSchedule(taskJob.getCron());
			Trigger trigger = TriggerBuilder.newTrigger().withIdentity(taskJob.getJobName(), taskJob.getJobGroup()).withSchedule(cronScheduleBuilder).build();
			//启动定时任务，并不是立即运行任务
			scheduler.scheduleJob(jobDetail, trigger);
		} catch (SchedulerException e) {
			log.error("添加定时任务失败", e);
			return false;
		} catch (ClassNotFoundException e) {
			log.error("添加定时任务失败", e);
			return false;
		}
		return true;
	}
	
	private boolean updateTaskCron(SchedulerJob taskJob, String cron) {  
        try {
            Scheduler scheduler = schedulerFactoryBean.getScheduler();  
            TriggerKey triggerKey = TriggerKey.triggerKey(taskJob.getJobName(), taskJob.getJobGroup());
            CronTrigger trigger = (CronTrigger) scheduler.getTrigger(triggerKey);  
            if (trigger == null) {  
                return false;  
            }  
            String oldTime = trigger.getCronExpression();  
            if (!oldTime.equalsIgnoreCase(cron)) { 
                // 触发器  
                TriggerBuilder<Trigger> triggerBuilder = TriggerBuilder.newTrigger();
                // 触发器名,触发器组  
                triggerBuilder.withIdentity(taskJob.getJobName(), taskJob.getJobGroup());
                triggerBuilder.startNow();
                // 触发器时间设定  
                triggerBuilder.withSchedule(CronScheduleBuilder.cronSchedule(cron));
                // 创建Trigger对象
                trigger = (CronTrigger) triggerBuilder.build();
                // 方式一 ：修改一个任务的触发时间
                scheduler.rescheduleJob(triggerKey, trigger);
            } 
            return true;
        } catch (Exception e) {
        	log.error("更新cron失败", e);
        	return false;
        }  
    }  

	
	private boolean removeTask(String name, String group){
		Scheduler scheduler = schedulerFactoryBean.getScheduler();
		JobKey jobKey = new JobKey(name, group);
		try {
			scheduler.deleteJob(jobKey);
		} catch (SchedulerException e) {
			log.error("移除定时任务失败", e);
			return false;
		}
		return true;
	}

	private boolean exist(String name, String group) {
		Scheduler scheduler = schedulerFactoryBean.getScheduler();
		JobKey jobKey = new JobKey(name, group);
		boolean exist = false;
		try {
			exist = scheduler.checkExists(jobKey);
		} catch (SchedulerException e) {
			e.printStackTrace();
		}
		return exist;
	}
	
	private boolean runTaskNow(SchedulerJob taskJob){
		Scheduler scheduler = schedulerFactoryBean.getScheduler();
		JobKey jobKey = new JobKey(taskJob.getJobName(), taskJob.getJobGroup());
		try {
			scheduler.triggerJob(jobKey);
		} catch (SchedulerException e) {
			log.error("立即运行定时任务["+taskJob.getJobName()+"]失败", e);
			return false;
		}
		return true;
	}
	
	@Override
	public boolean checkCron(String cron){
		try {
			new CronExpression(cron);
		} catch (ParseException e) {
			log.info("cron表达式错误", e);
			return false;
		}
        return true;
	}
	
	@Override
	public boolean checkJobClass(String className){
		try {
			Class<?> clazz = Class.forName(className);
			boolean isAbs = Modifier.isAbstract(clazz.getModifiers());
			if(isAbs){
				return false;
			}
			boolean isExtendsJob = BaseJob.class.isAssignableFrom(clazz);
			if(!isExtendsJob){
				return false;
			}
		} catch (ClassNotFoundException e) {
			return false;
		}
        return true;
	}
	
	@Override
	public boolean createJob(SchedulerJob job){
		job.setJobStatus("02");
		schedulerJobMapper.insertSelective(job);
		return true;
	}

	@Override
	public boolean changeStatus(Integer jobId, String status) {
		SchedulerJob dbJob = schedulerJobMapper.selectByPrimaryKey(jobId);
		if("01".equals(status)){//生效
			addTask(dbJob);
		}else{//失效
			removeTask(dbJob.getJobName(), dbJob.getJobGroup());
		}
		dbJob.setJobStatus(status);
		schedulerJobMapper.updateByPrimaryKeySelective(dbJob);
		return true;
	}

	@Override
	public boolean removeJob(Integer jobId) {
		SchedulerJob dbJob = schedulerJobMapper.selectByPrimaryKey(jobId);
		if(this.exist(dbJob.getJobName(), dbJob.getJobGroup())){
			removeTask(dbJob.getJobName(), dbJob.getJobGroup());
		}
		dbJob.setJobStatus("-1");
		schedulerJobMapper.updateByPrimaryKeySelective(dbJob);
		return true;
	}

	@Override
	public SchedulerJob getByNameAndGroup(String name, String group) {
		return schedulerJobMapper.getByNameAndGroup(name, group);
	}
	
	@Override
	public boolean updateCron(Integer jobId, String cron) {
		SchedulerJob dbJob = schedulerJobMapper.selectByPrimaryKey(jobId);
		if("01".equals(dbJob.getJobStatus())){
			updateTaskCron(dbJob, cron);
		}
		dbJob.setCron(cron);
		schedulerJobMapper.updateByPrimaryKeySelective(dbJob);
		return true;
	}
	
	@Override
	public boolean runNow(Integer jobId) {
		SchedulerJob dbJob = schedulerJobMapper.selectByPrimaryKey(jobId);
		if(!"01".equals(dbJob.getJobStatus())){
			return false;
		}
		runTaskNow(dbJob);
		return true;
	}

	@Override
	public boolean initCreateTable() {
		schedulerJobMapper.createTableIfNotExist();
		schedulerJobLogMapper.createTableIfNotExist();
		return true;
	}
	
	@Override
	public void initAllJobIntoScheduler() {
		List<SchedulerJob> list = schedulerJobMapper.findByStatus(new String[]{"01"});
		if(list==null || list.size()==0){
			log.info("没有任务需要初始化运行");
			return;
		}
		for(SchedulerJob job:list){
			addTask(job);
			log.info("任务["+job.getJobName()+"]初始化成功！");
		}
		log.info("***所有任务初始化完成***");
	}
	
	@Override
	public List<SchedulerJob> findTaskByStatus(String[] status) {
		return schedulerJobMapper.findByStatus(status);
	}

	@Override
	public Page findTaskByStatusPage(String[] status, Page page) {
		List<SchedulerJob> list= schedulerJobMapper.findByStatus(status, page);
		page.setData(list);
		return page;
	}
	
	@Override
	public Page findHistoryPage(Integer jobId, String type, Page page) {
		List<SchedulerJobLog> list = schedulerJobLogMapper.findAllHistoryPage(jobId, type, page);
		page.setData(list);
		return page;
	}
}
