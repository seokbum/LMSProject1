package com.ldb.lms.service.professor_support;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import com.ldb.lms.domain.Course;
import com.ldb.lms.domain.CourseTime;
import com.ldb.lms.dto.professor_support.RegistCourseDto;
import com.ldb.lms.mapper.learning_support.CourseMapper;
import com.ldb.lms.mapper.professor_support.ConvertDtoMapper;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service

public class ProfessorService {
	
	private final CourseMapper courseMapper;
	private final ConvertDtoMapper convertMapper;
	
	public ProfessorService(
			CourseMapper courseMapper,
			@Qualifier("convertDtoMapperImpl") ConvertDtoMapper convertMapper) {
			this.courseMapper = courseMapper;
			this.convertMapper = convertMapper;
		}
	
	
	public Course test(RegistCourseDto rDto) {
		Course c =  convertMapper.toCourse(rDto);
		CourseTime ct =  convertMapper.toCourseTime(rDto);
		log.info("RegistCourseDto: {}", rDto);
		log.info("course: {}", c.toString());
		log.info("coursetime: {}", ct.toString());
		return c;
    }



	

   

	
	
}
