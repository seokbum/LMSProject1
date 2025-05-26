package com.ldb.lms.service.mypage;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.ldb.lms.dto.mypage.LoginDto;
import com.ldb.lms.mapper.learning_support.CourseMapper;
import com.ldb.lms.mapper.mypage.ProStuMapper;
import com.ldb.lms.mapper.mypage.ProfessorMapper;
import com.ldb.lms.mapper.mypage.StudentMapper;

import lombok.RequiredArgsConstructor;


@Service
@RequiredArgsConstructor
public class MypageService {
	
	@Autowired
	private final StudentMapper studentMapper;
	@Autowired
	private final ProfessorMapper professroMapper;
	@Autowired
	private final ProStuMapper proStuMapper;
	
	public Map<String,String> login(LoginDto dto) {
		Map<String, String> loginChk = proStuMapper.loginChk(dto);
		if(loginChk == null) {
			return null;
		}
		else {
			return loginChk;
		}
		
	}

}
