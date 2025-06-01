package com.ldb.lms.service.professor_support;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.ldb.lms.domain.Course;
import com.ldb.lms.domain.CourseTime;
import com.ldb.lms.dto.professor_support.RegistCourseDto;
import com.ldb.lms.mapper.learning_support.CourseMapper;
import com.ldb.lms.mapper.professor_support.ConvertDtoMapper;
import com.ldb.lms.mapper.professor_support.ProfessorCourseMapper;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class ProfessorCourseRegisterService {
	
	private final CourseMapper courseMapper;
	private final ConvertDtoMapper convertMapper;
	private final ProfessorCourseMapper professorCourseMapper;
	
	public ProfessorCourseRegisterService(
			CourseMapper courseMapper,
			@Qualifier("convertDtoMapperImpl") ConvertDtoMapper convertMapper,
			ProfessorCourseMapper professorCourseMapper
			) {
			this.courseMapper = courseMapper;
			this.convertMapper = convertMapper;
			this.professorCourseMapper = professorCourseMapper;
		}
	
	@Transactional
	public void insertCourseAndCourseTime(RegistCourseDto rDto) {
		
		try {
			
			long maxCourseId = professorCourseMapper.getMaxcourseIdNumber();
			long maxCtId = professorCourseMapper.getMaxcourseTimeIdNumber();
			String courseId = "C" + (++maxCourseId);
			String courseTimeId = "CT" + (++maxCtId);
			rDto.setCourseId(courseId);
			rDto.setCourseTimeId(courseTimeId);
			
			Course course =  convertMapper.toCourse(rDto);
			CourseTime courseTime =  convertMapper.toCourseTime(rDto);
			
			professorCourseMapper.insertCourseInfo(course);
			professorCourseMapper.insertCourseTime(courseTime);
		} catch(Exception e) {
			log.error("강의 생성중 오류 발생. {}", rDto, e);
			throw new RuntimeException("Course insert failed", e);
		}
    }

	

	

   

	
	
}
