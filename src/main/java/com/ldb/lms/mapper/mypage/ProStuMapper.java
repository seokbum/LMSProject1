package com.ldb.lms.mapper.mypage;

import java.util.Map;


import org.apache.ibatis.annotations.Mapper;

import com.ldb.lms.dto.mypage.FindIdDto;
import com.ldb.lms.dto.mypage.FindPwDto;
import com.ldb.lms.dto.mypage.LoginDto;
import com.ldb.lms.dto.mypage.UpdateProInfoDto;
import com.ldb.lms.dto.mypage.UpdatePwDto;
import com.ldb.lms.dto.mypage.UpdateStuInfoDto;


@Mapper 
public interface ProStuMapper {
	//이름과 이메일로 교수 또는 학생의 id를 조회(union을 이용해 두개의select문을 한번에) 
	String findId(FindIdDto findIdDto);
	
	//넘어온 id와 맞는  학생의id,name,pawssword를 추출
	Map<String,String> loginChk(LoginDto loginDto);
	
	//id와이메일을 이용해 학생의 비밀번호 반환
	String findPw(FindPwDto findPwDto);
	
	//넘겨받은 학생아이디가 일치한다면 넘겨받은 비밀번호로 업데이트
	int updateStuPw(UpdatePwDto dto);
	
	//넘겨받은 교수아이디가 일치한다면 넘겨받은 비밀번호로 업데이트
	int updateProPw(UpdatePwDto dto);
	
	//학생or교수의 아이디를 넘겨받아 학과명 추출 
	String selectDeptName(String studentId);
	
	void updateProInfo(UpdateProInfoDto updateProInfoDto);
	
	//넘겨받은 id에 해당하는 학생의 img , email , phone 업데이트
	void updateStuInfo(UpdateStuInfoDto updateStuInfoDto);
	
	
	
	
	
	
	
	
}
