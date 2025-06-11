package com.ldb.lms.mapper.mybatis.mypage;

import org.apache.ibatis.annotations.Mapper;


import com.ldb.lms.dto.mypage.DeleteUserDto;
import com.ldb.lms.dto.mypage.Student;

@Mapper
public interface StudentMapper {
	
	int idchk(String id);
	
	Student list();
	
	int insert(Student st);
	
	Student selectOne(String student_id);
	
	int deleteUser(DeleteUserDto dto);
	
	String selectStatus(String student_id);

}
