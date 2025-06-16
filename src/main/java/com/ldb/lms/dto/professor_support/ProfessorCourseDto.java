package com.ldb.lms.dto.professor_support;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class ProfessorCourseDto {
	
	private String courseName;
	private String courseId;
	private String coursePeriod;
	private String courseCurrentEnrollment;
}
