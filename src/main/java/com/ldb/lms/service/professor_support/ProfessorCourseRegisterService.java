package com.ldb.lms.service.professor_support;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import com.ldb.lms.domain.Course;
import com.ldb.lms.domain.CourseTime;
import com.ldb.lms.dto.professor_support.RegistCourseDto;
import com.ldb.lms.exception.CourseRegistrationException;
import com.ldb.lms.mapper.mapstruct.ConvertDtoMapper;
import com.ldb.lms.mapper.mybatis.learning_support.CourseMapper;
import com.ldb.lms.mapper.mybatis.professor_support.ProfessorCourseMapper;

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
	
	public String getProfessorName(String professorId) {
		String professorName = professorCourseMapper.getProfessorNameById(professorId);
        if (!StringUtils.hasText(professorName)) {
            log.warn("사용자 정보가 존재하지 않습니다. {}", professorId);
            throw new CourseRegistrationException("user.not.found");
        }
        return professorName; 
	}	
	
	@Transactional
	public void insertCourseAndCourseTime(RegistCourseDto rDto) {
		
		// 시간 검증
        String[] startTimeParts = rDto.getCourseTimeStart().split(":");
        String[] endTimeParts = rDto.getCourseTimeEnd().split(":");
        int startHour = Integer.parseInt(startTimeParts[0]);
        int endHour = Integer.parseInt(endTimeParts[0]);
        
        if (startHour < 9 || startHour > 17 || endHour < 9 || endHour > 17) {
            throw new CourseRegistrationException("course.time.invalid");
        }
        if (startHour >= endHour) {
            throw new CourseRegistrationException("course.time.invalid");
        }
		
	
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
    }

	
}














