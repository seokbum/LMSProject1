package com.ldb.lms.mapper.mybatis.mypage;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;

import com.ldb.lms.dto.mypage.GetScoresDto;

@Mapper
public interface ScoreMapper {
	
	List<GetScoresDto> getScore(String id);

}
