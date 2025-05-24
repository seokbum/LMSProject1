package com.ldb.lms.dto.professor_support;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class ScoreMngDto {
	
	private String studentId;
	private String studentName;
	private String studentNum;
	private String deptName;
	private String professorName;
	private String courseName;
	private String courseId;
	private String coursePeriod;
	private Integer courseCurrentEnrollment;
	private Integer scoreMid;
	private Integer scoreFinal;
	private Integer scoreTotal;
	private String scoreGrade;
	private String scoreEtc;
	
}
