package com.ldb.lms.mapper.mypage;

import org.apache.ibatis.annotations.Mapper;

import com.ldb.lms.dto.mypage.Professor;
import com.ldb.lms.dto.mypage.UpdateProPwDto;

@Mapper
public interface ProfessorMapper {
	
	int idchk(String professorId);
	void insert(Professor p);
	Professor selectone(String professorId);
	void tempPw(UpdateProPwDto pwDto);
	
}
