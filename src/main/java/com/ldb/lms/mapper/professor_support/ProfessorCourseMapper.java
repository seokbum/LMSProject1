package com.ldb.lms.mapper.professor_support;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Mapper;

import com.ldb.lms.domain.Course;
import com.ldb.lms.domain.CourseTime;
import com.ldb.lms.dto.professor_support.PaginationDto;
import com.ldb.lms.dto.professor_support.RegistCourseDto;

@Mapper
public interface ProfessorCourseMapper {

	void insertCourseInfo(Course course);

	void insertCourseTime(CourseTime courseTime);

	long getMaxcourseIdNumber();

	long getMaxcourseTimeIdNumber();

	Integer getCourseCountRows(Map<String, String> map);

	List<RegistCourseDto> searchCourseInfo(PaginationDto paginationDto);
	
	
}
