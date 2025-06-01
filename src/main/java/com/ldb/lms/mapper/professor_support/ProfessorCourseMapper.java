package com.ldb.lms.mapper.professor_support;

import org.apache.ibatis.annotations.Mapper;

import com.ldb.lms.domain.Course;
import com.ldb.lms.domain.CourseTime;

@Mapper
public interface ProfessorCourseMapper {

	void insertCourseInfo(Course course);

	void insertCourseTime(CourseTime courseTime);

	long getMaxcourseIdNumber();

	long getMaxcourseTimeIdNumber();
	
	
}
