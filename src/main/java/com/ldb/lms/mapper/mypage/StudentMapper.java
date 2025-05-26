package com.ldb.lms.mapper.mypage;

import org.apache.ibatis.annotations.Mapper;

import com.ldb.lms.domain.Student;
import com.ldb.lms.dto.mypage.DeleteUserDto;
import com.ldb.lms.dto.mypage.UpdateStuPwDto;

@Mapper
public interface StudentMapper {
	
	int cnt();
	
	Student list();
	
	void insert(Student st);
	
	Student selectOne(String student_id);
	
	void deleteUser(DeleteUserDto dto);
	
	String selectStatus(String student_id);
	
	void tempPw(UpdateStuPwDto dto);

}
