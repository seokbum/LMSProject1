package com.ldb.lms.service.mypage;

import java.util.Map;

import org.springframework.stereotype.Service;

import com.ldb.lms.dto.mypage.LoginDto;
import com.ldb.lms.mapper.learning_support.CourseMapper;
import com.ldb.lms.mapper.mypage.ProStuMapper;
import com.ldb.lms.mapper.mypage.ProfessorMapper;
import com.ldb.lms.mapper.mypage.StudentMapper;

@Service
public class MypageService {

	//private final StudentMapper studentMapper;
	//private final ProfessorMapper professroMapper;
	//private final ProStuMapper proStuMapper; 
	
	public Map<String,String> login(LoginDto dto) {
		//Map<String, String> loginChk = proStuMapper.loginChk(dto);
		return null;
	}

}
