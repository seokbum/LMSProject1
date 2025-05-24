package com.ldb.lms.dto.learning_support;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class SearchDto {
	private String college;
	private String deptId;
	private String courseId;
    private String courseName;
    private String studentId;
    
}