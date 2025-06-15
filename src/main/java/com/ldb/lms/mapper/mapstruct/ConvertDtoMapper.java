package com.ldb.lms.mapper.mapstruct;

import java.sql.Time;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

import org.mapstruct.Mapper;

import com.ldb.lms.domain.Course;
import com.ldb.lms.domain.CourseTime;
import com.ldb.lms.dto.professor_support.PaginationDto;
import com.ldb.lms.dto.professor_support.RegistCourseDto;
import com.ldb.lms.dto.professor_support.ScoreDto;
import com.ldb.lms.dto.professor_support.ScoreMngDto;

@Mapper(componentModel = "spring")
public interface ConvertDtoMapper {
	Course toCourse(RegistCourseDto rDto);
	CourseTime toCourseTime(RegistCourseDto rDto);
	PaginationDto toPageDto(PaginationDto paginationDto);
	ScoreDto toScoreDto(ScoreMngDto scoreMngDto);
	List<ScoreDto> toScoreDtoList(List<ScoreMngDto> scoreMngDtos);
	

	
	default Time map(String timeStr) {
	    if (timeStr == null) return null;
    	// "10:00" → "10:00:00"
    	LocalTime time = LocalTime.parse(timeStr, DateTimeFormatter.ofPattern("H:mm[:ss]"));
	    	 
	    return Time.valueOf(time);
	}
	
}
