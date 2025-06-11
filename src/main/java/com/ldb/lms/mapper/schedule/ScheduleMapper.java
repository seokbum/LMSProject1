package com.ldb.lms.mapper.schedule;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;

import com.ldb.lms.dto.schedule.ScheduleDto;

@Mapper
public interface ScheduleMapper {

	List<ScheduleDto> listSchedules();
}
