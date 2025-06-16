package com.ldb.lms.service.professor_support;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.ldb.lms.dto.professor_support.AttendanceDataDto;
import com.ldb.lms.dto.professor_support.CourseAttendanceDto;
import com.ldb.lms.dto.professor_support.ProfessorCourseDto;
import com.ldb.lms.dto.professor_support.updateAttendanceDto;
import com.ldb.lms.mapper.mapstruct.ConvertDtoMapper;
import com.ldb.lms.mapper.mybatis.professor_support.ProfessorAttendanceMapper;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class ProfessorCourseAttendanceService {
	
	private final ProfessorAttendanceMapper attendanceMapper;
	private final ConvertDtoMapper convertoDtoMapper;
	
	public ProfessorCourseAttendanceService(ProfessorAttendanceMapper attendanceMapper,
			ConvertDtoMapper convertoDtoMapper) {
		
		this.attendanceMapper = attendanceMapper;
		this.convertoDtoMapper = convertoDtoMapper;
	}

	public List<ProfessorCourseDto> getCoursesInfoByPro(String professorId) {
		
		List<ProfessorCourseDto> result = 
				attendanceMapper.getCoursesInfoByPro(professorId);
		
		return result;
	}

	public List<CourseAttendanceDto> getAttendance(AttendanceDataDto attendanceDto) {
		List<CourseAttendanceDto> result = attendanceMapper.getAttendance(attendanceDto);
		return result;
	}
	
	@Transactional
	public void saveAttendanceInfo(List<updateAttendanceDto> attendanceList) {
		
		for (updateAttendanceDto dto : attendanceList) {
			attendanceMapper.insertAttendanceHistory(dto);
			attendanceMapper.updateAttendance(dto.getAttendanceId());
		}
		
	}
	
	
}













