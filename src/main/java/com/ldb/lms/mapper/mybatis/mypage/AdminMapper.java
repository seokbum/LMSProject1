package com.ldb.lms.mapper.mybatis.mypage;

import org.apache.ibatis.annotations.Mapper;

import com.ldb.lms.dto.mypage.AdminDto;

@Mapper
public interface AdminMapper {
	
	AdminDto selectOne(String id);

}
