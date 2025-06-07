package com.ldb.lms.mapper.professor_support;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

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

	int updateCourseInfo(Course course);

	int getCurrentEnrollmentById(String courseId);

	int updateCourseStatus(@Param("courseId") String courseId, 
			@Param("courseStatus") String courseStatus);

	int updateCourseTimeInfo(CourseTime ct);

	int deleteCourse(String courseId);

	int deleteCourseTime(String courseId);
	
	
}
