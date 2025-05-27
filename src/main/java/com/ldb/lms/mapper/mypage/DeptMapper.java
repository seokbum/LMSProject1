package com.ldb.lms.mapper.mypage;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;

import com.ldb.lms.dto.mypage.Dept;

@Mapper
public interface DeptMapper {
	
	//학과이름으로 아이디추출
	String selectId(String deptName);
	
	//dept의 모든 컬럼조회
	List<Dept> selectAll();
	
	//dept_id에 맞는 학과이름 반환
	String selectName(String deptId);

}
