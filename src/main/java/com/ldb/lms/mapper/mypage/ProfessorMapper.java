package com.ldb.lms.mapper.mypage;

import org.apache.ibatis.annotations.Mapper;



import com.ldb.lms.dto.mypage.Professor;

@Mapper
public interface ProfessorMapper {
	
	int idchk(String professorId);
	int insert(Professor p);
	Professor selectOne(String professorId);
	
	
}
