package com.lan.task.framework.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import com.lan.task.framework.model.Page;
import com.lan.task.framework.model.SchedulerJobLog;

@Mapper
public interface SchedulerJobLogMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(SchedulerJobLog record);

    int insertSelective(SchedulerJobLog record);

    SchedulerJobLog selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(SchedulerJobLog record);

    int updateByPrimaryKey(SchedulerJobLog record);

	void createTableIfNotExist();

	List<SchedulerJobLog> findAllHistoryPage(@Param("jobId") Integer jobId, @Param("type") String type, @Param("page") Page page);
}