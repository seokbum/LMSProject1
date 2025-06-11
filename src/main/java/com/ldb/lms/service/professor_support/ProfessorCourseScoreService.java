package com.ldb.lms.service.professor_support;

import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;

import com.ldb.lms.dto.professor_support.CourseScoreDto;
import com.ldb.lms.dto.professor_support.ProfessorCourseDto;
import com.ldb.lms.dto.professor_support.ProfessorInfoDto;
import com.ldb.lms.dto.professor_support.ScoreDto;
import com.ldb.lms.dto.professor_support.ScoreMngDto;
import com.ldb.lms.dto.professor_support.ScoreUpdateDto;
import com.ldb.lms.exception.NoDataFoundException;
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
		
		if (scoreResult == null || scoreResult.isEmpty()) {
	        throw new NoDataFoundException("해당 과목의 성적 정보가 없습니다.");
	    }
		
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

	public void updateScores(List<ScoreUpdateDto> scoreList) {
		
		if (scoreList == null || scoreList.isEmpty()) {
            throw new IllegalArgumentException("성적 정보가 비어 있습니다.");
        }
		
		
		for (ScoreUpdateDto score : scoreList) {
            try {
                professorScoreMapper.updateScore(score);
            } catch (Exception e) {
                // 실패한 항목을 로깅하거나, 나중에 실패 목록 반환 등도 가능
                throw new RuntimeException("성적 업데이트 중 오류 발생: " + score.getStudentId(), e);
            }
        }
		
		
	}

}
