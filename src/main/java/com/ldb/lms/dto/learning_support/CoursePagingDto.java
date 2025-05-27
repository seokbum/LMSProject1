package com.ldb.lms.dto.learning_support;

import com.ldb.lms.dto.professor_support.PaginationDto;

import lombok.Getter;
import lombok.Setter;


@Getter
@Setter
public class CoursePagingDto {
	
	private SearchDto searchDto;
	private PaginationDto paginationDto;
	
}