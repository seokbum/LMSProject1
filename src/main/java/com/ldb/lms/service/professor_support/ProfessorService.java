package com.ldb.lms.service.professor_support;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import com.ldb.lms.domain.Course;
import com.ldb.lms.domain.CourseTime;
import com.ldb.lms.dto.professor_support.RegistCourseDto;
import com.ldb.lms.mapper.learning_support.CourseMapper;
import com.ldb.lms.mapper.professor_support.ConvertDtoMapper;
import com.ldb.lms.mapper.professor_support.ProfessorCourseMapper;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service

public class ProfessorService {
	
	private final CourseMapper courseMapper;
	private final ConvertDtoMapper convertMapper;
	private final ProfessorCourseMapper professorCourseMapper;
	
	public ProfessorService(
			CourseMapper courseMapper,
			@Qualifier("convertDtoMapperImpl") ConvertDtoMapper convertMapper,
			ProfessorCourseMapper professorCourseMapper
			) {
			this.courseMapper = courseMapper;
			this.convertMapper = convertMapper;
			this.professorCourseMapper = professorCourseMapper;
		}
	
	
	public void insertCourseAndCourseTime(RegistCourseDto rDto) {
		Course course =  convertMapper.toCourse(rDto);
		CourseTime courseTime =  convertMapper.toCourseTime(rDto);
		
		professorCourseMapper.insertCourseInfo(course);
		professorCourseMapper.insertCourseTime(courseTime);
		
		
    }

	

	

   

	
	
}
