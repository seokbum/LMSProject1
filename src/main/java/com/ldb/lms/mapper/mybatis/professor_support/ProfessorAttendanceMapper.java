package com.ldb.lms.mapper.mybatis.professor_support;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;

import com.ldb.lms.dto.professor_support.AttendanceDataDto;
import com.ldb.lms.dto.professor_support.CourseAttendanceDto;
import com.ldb.lms.dto.professor_support.ProfessorCourseDto;
import com.ldb.lms.dto.professor_support.updateAttendanceDto;

@Mapper
public interface ProfessorAttendanceMapper {

	List<ProfessorCourseDto> getCoursesInfoByPro(String professorId);

	List<CourseAttendanceDto> getAttendance(AttendanceDataDto attendanceDto);

	int insertAttendanceHistory(updateAttendanceDto updateAttendanceDto);

	int updateAttendance(String attendanceId);
	
}
