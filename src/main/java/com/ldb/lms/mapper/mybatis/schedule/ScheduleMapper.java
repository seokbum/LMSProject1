package com.ldb.lms.mapper.mybatis.schedule;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import com.ldb.lms.dto.schedule.ScheduleDto;

@Mapper
public interface ScheduleMapper {

    List<ScheduleDto> listSchedules(@Param("semesterType") String semesterType);

    void insertSchedule(ScheduleDto schedule);

    void updateSchedule(ScheduleDto schedule);

    void deleteSchedule(@Param("scheduleId") Integer scheduleId);

    ScheduleDto getScheduleById(@Param("scheduleId") Integer scheduleId);
}