package com.ldb.lms.mapper.professor_support;

import java.sql.Time;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

import org.mapstruct.Mapper;

import com.ldb.lms.domain.Course;
import com.ldb.lms.domain.CourseTime;
import com.ldb.lms.dto.professor_support.PaginationDto;
import com.ldb.lms.dto.professor_support.RegistCourseDto;

@Mapper(componentModel = "spring")
public interface ConvertDtoMapper {
	Course toCourse(RegistCourseDto rDto);
	CourseTime toCourseTime(RegistCourseDto rDto);
	PaginationDto toPageDto(PaginationDto paginationDto);
	
	default Time map(String timeStr) {
	    if (timeStr == null) return null;
    	// "10:00" → "10:00:00"
    	LocalTime time = LocalTime.parse(timeStr, DateTimeFormatter.ofPattern("H:mm[:ss]"));
	    	 
	    return Time.valueOf(time);
	}
	
}
