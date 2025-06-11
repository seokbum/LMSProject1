package com.ldb.lms.service.professor_support;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.ldb.lms.domain.Course;
import com.ldb.lms.domain.CourseTime;
import com.ldb.lms.dto.professor_support.PaginationDto;
import com.ldb.lms.dto.professor_support.RegistCourseDto;
import com.ldb.lms.mapper.mapstruct.ConvertDtoMapper;
import com.ldb.lms.mapper.mybatis.professor_support.ProfessorCourseMapper;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class ProfessorCourseManagementService {
	
	private final ProfessorCourseMapper professorCourseMapper;
	private final ConvertDtoMapper convertMapper;
	
	public ProfessorCourseManagementService(
			@Qualifier("convertDtoMapperImpl") ConvertDtoMapper convertMapper,
			ProfessorCourseMapper professorCourseMapper) {
		
		this.convertMapper = convertMapper;
		this.professorCourseMapper = professorCourseMapper;
	}

	public void calcPage(PaginationDto paginationDto) {
		log.info("paginationDto: {}", paginationDto.toString());
		Map<String, String> map = new HashMap<>();
		map.put("professorId", paginationDto.getProfessorId());
		map.put("search", paginationDto.getSearch());
		
		Integer itemPerPage = paginationDto.getItemsPerPage();
		Integer page = paginationDto.getPage();
		Integer totalRows = professorCourseMapper.getCourseCountRows(map);
		Integer totalPages = (int) Math.ceil((double) totalRows / itemPerPage);
		Integer startPage = ((page - 1) / itemPerPage) * itemPerPage + 1;
		Integer endPage = Math.min((startPage + itemPerPage -1), totalPages);
		Integer offset = (page - 1) * itemPerPage;
		
		paginationDto.setTotalRows(totalRows);
		paginationDto.setTotalPages(totalPages);
		paginationDto.setStartPage(startPage);
		paginationDto.setEndPage(endPage);
		paginationDto.setOffset(offset);
		log.info("paginationDto: {}", paginationDto.toString());
	}
	
	public List<RegistCourseDto> getCourses(PaginationDto paginationDto) {
		List<RegistCourseDto> courses = professorCourseMapper.searchCourseInfo(paginationDto);
		log.info("list: {}", courses);
		return courses;
	}
	
	@Transactional
	public void updateCourse(RegistCourseDto rDto) {
		Course course = convertMapper.toCourse(rDto);
		CourseTime ct = convertMapper.toCourseTime(rDto);
		try {
			updateCourseInfo(course);
			updateCourseTimeInfo(ct);
		} catch (Exception e) {
			log.warn("update course fail {}", rDto, e);
			throw e;
		}
		
	}

	public void updateCourseStatus(String courseId, String courseStatus) {
		
		int currentEnrollment = getCurrentEnrollment(courseId);
		
		if (courseStatus.equals("OPEN") && currentEnrollment > 0) {
			throw new IllegalStateException("수강생이 있는 강의는 종료할수 없습니다.");
		}
		
		courseStatus = courseStatus.equals("OPEN") ? "CLOSED" : "OPEN";
		
		
		try {
			professorCourseMapper.updateCourseStatus(courseId, courseStatus);
		} catch (Exception e) {
			log.warn("update course status fail {}", courseId);
			throw e;
		}
	}
	
	@Transactional
	public void deleteCourse(String courseId) {
		
		int currentEnrollment = getCurrentEnrollment(courseId);
		
		if (currentEnrollment > 0) {
			throw new IllegalStateException("수강생이 있는 강의는 종료할수 없습니다.");
		}
		
		try {
			deleteCourseTimeInfo(courseId);
			deleteCourseInfo(courseId);
		} catch (Exception e) {
			log.warn("delete course fail {}", courseId, e);
			throw e;
		}
	}

	private int getCurrentEnrollment(String courseId) {
		return professorCourseMapper.getCurrentEnrollmentById(courseId);
	}
	
	private void updateCourseInfo(Course course) {
		if (professorCourseMapper.updateCourseInfo(course) < 1) {
			throw new RuntimeException("강의상세정보 수정 실패");
		}
	}
	
	private void updateCourseTimeInfo(CourseTime ct) {
		if (professorCourseMapper.updateCourseTimeInfo(ct) < 1) {
			throw new RuntimeException("강의시간정보 수정 실패");
		}
	}
	
	private void deleteCourseInfo(String courseId) {
		if (professorCourseMapper.deleteCourse(courseId) < 1) {
			throw new RuntimeException("강의상세정보 수정 실패");
		}
	}
	
	private void deleteCourseTimeInfo(String courseId) {
		if (professorCourseMapper.deleteCourseTime(courseId) < 1) {
			throw new RuntimeException("강의시간정보 수정 실패");
		}
	}

	
	
}









