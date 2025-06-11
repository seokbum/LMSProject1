package com.ldb.lms.mapper.mybatis.professor_support;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Mapper;

import com.ldb.lms.dto.professor_support.ProfessorCourseDto;
import com.ldb.lms.dto.professor_support.ProfessorInfoDto;
import com.ldb.lms.dto.professor_support.ScoreMngDto;
import com.ldb.lms.dto.professor_support.ScoreUpdateDto;

@Mapper
public interface ProfessorScoreMapper {

	ProfessorInfoDto getProfessorInfo(String professorId);

	List<ProfessorCourseDto> getCoursesInfo(String professorId);

	List<ScoreMngDto> getScoreInfo(Map<String, String> of);

	int updateScore(ScoreUpdateDto score);
	
}
