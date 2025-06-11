package com.ldb.lms.mapper.mybatis.learning_support;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Mapper;

import com.ldb.lms.dto.learning_support.AttendanceDto;
import com.ldb.lms.dto.learning_support.CourseDto;
import com.ldb.lms.dto.learning_support.CoursePagingDto;
import com.ldb.lms.dto.learning_support.DeptDto;
import com.ldb.lms.dto.learning_support.RegistrationDto;
import com.ldb.lms.dto.learning_support.SearchDto;

@Mapper
public interface CourseMapper {
		 
	List<String> getColleges();
	
	List<DeptDto> getDepartments(String college);
	
	List<CourseDto> searchCourse(CoursePagingDto cpDto);

	Long getMaxRegistrationIdNumber(); 
	
	Map<String, Object> getCurrentEnrollments(String courseId);
	
	int increaseEnrollment(String courseId);
	
	int decrementEnrollment(String courseId);
	
	void addCourse(Map<String, Object> param);
	
    void addAttendance(Map<String, Object> param);
    
    void addScore(Map<String, Object> param);
    
    Long getMaxAttendanceIdNumber();
    
    Long getScoreIdNumber();
    
    Map<String, String> getStudentInfo(String studentId);

	Integer countCourses(SearchDto searchDto);

	List<RegistrationDto> searchRegistrationCourses(String studentId);

	int deleteRegistration(String string);

	void deleteAttendance(Map<String, Object> map);

	void deleteScore(Map<String, Object> map);

	List<AttendanceDto> viewCourseTime(String studentId); 

	

}
