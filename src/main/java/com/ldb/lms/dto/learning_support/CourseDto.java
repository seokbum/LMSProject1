package com.ldb.lms.dto.learning_support;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class CourseDto {
	private String courseId;
	private String deptId;
	private String professorId;
	private String professorName;
	private String courseName;
	private String creditCategory;// 전공필수여부
	private Integer courseScore;
	private String coursePlan;
	private Integer courseCurrentEnrollment;
	private Integer courseMaxCnt;
	private String timeSlot; // 시간

}