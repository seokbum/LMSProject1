package com.ldb.lms.service.professor_support;

import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;

import com.ldb.lms.dto.professor_support.CourseScoreDto;
import com.ldb.lms.dto.professor_support.ProfessorCourseDto;
import com.ldb.lms.dto.professor_support.ProfessorInfoDto;
import com.ldb.lms.dto.professor_support.ScoreDto;
import com.ldb.lms.dto.professor_support.ScoreMngDto;
import com.ldb.lms.exception.ProfessorNotFoundException;
import com.ldb.lms.mapper.mapstruct.ConvertDtoMapper;
import com.ldb.lms.mapper.mybatis.professor_support.ProfessorScoreMapper;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class ProfessorCourseScoreService {

	private final ProfessorScoreMapper professorScoreMapper;
	private final ConvertDtoMapper convertDtoMapper;
	
	public ProfessorInfoDto getProfessorInfo(String professorId) {

		ProfessorInfoDto dto = professorScoreMapper.getProfessorInfo(professorId);

		if (dto == null) {
			throw new ProfessorNotFoundException("교수 정보를 찾을 수 없습니다. ID: " + professorId);
		}

		return dto;
	}

	public List<ProfessorCourseDto> getCoursesInfo(String professorId) {
		return professorScoreMapper.getCoursesInfo(professorId);

	}

	public CourseScoreDto getScoreInfo(String professorId, String courseId, String courseName) {

		List<ScoreMngDto> scoreResult = professorScoreMapper
				.getScoreInfo(Map.of("professorId", professorId, "courseId", courseId));
		
		return buildCourseScoreDto(scoreResult, courseId, courseName);
	}

	private CourseScoreDto buildCourseScoreDto(List<ScoreMngDto> result, String courseId, String courseName) {
		
		CourseScoreDto dto = new CourseScoreDto();
	    dto.setCourseId(courseId);
	    dto.setCourseName(courseName);

	    result.stream().findFirst().ifPresent(first -> {
	        dto.setCoursePeriod(first.getCoursePeriod());
	        dto.setProfessorName(first.getProfessorName());
	    });

	    List<ScoreDto> grades = convertDtoMapper.toScoreDtoList(result);
	    dto.setGrades(grades);

	    return dto;
	}

}
