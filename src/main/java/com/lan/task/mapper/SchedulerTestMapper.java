package com.lan.task.mapper;

import org.apache.ibatis.annotations.Mapper;

import com.lan.task.model.SchedulerTest;

@Mapper
public interface SchedulerTestMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(SchedulerTest record);

    int insertSelective(SchedulerTest record);

    SchedulerTest selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(SchedulerTest record);

    int updateByPrimaryKey(SchedulerTest record);
}