package com.lan.task.framework.mapper;

import java.util.Date;
import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import com.lan.task.framework.model.Page;
import com.lan.task.framework.model.SchedulerJob;

@Mapper
public interface SchedulerJobMapper {
    int deleteByPrimaryKey(Integer jobId);

    int insert(SchedulerJob record);

    int insertSelective(SchedulerJob record);

    SchedulerJob selectByPrimaryKey(Integer jobId);

    int updateByPrimaryKeySelective(SchedulerJob record);

    int updateByPrimaryKey(SchedulerJob record);

	void beforeRun(@Param("jobId") Integer jobId, @Param("nextRunTime") Date nextRunTime);

	void afterRun(@Param("jobId") Integer jobId);

	void afterError(@Param("jobId") Integer jobId);

	SchedulerJob getByNameAndGroup(@Param("jobName") String jobName, @Param("jobGroup") String jobGroup);

	void createTableIfNotExist();

	List<SchedulerJob> findByStatus(@Param("jobStatus") String[] jobStatus);

	List<SchedulerJob> findByStatus(@Param("jobStatus") String[] status, @Param("page")Page page);
}