package com.ldb.lms.dto.professor_support;

import java.time.LocalDate;
import java.util.Arrays;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class AttendanceDataDto {

	private String attendanceId;
	private String courseId;
	private String studentId;
	private String professorId;
	private Integer attendanceLate; 
	private Integer attendanceAbsent; 
	private String attendanceRemarks;
	private LocalDate attendanceDate; // String -> LocalDate
	private String attendanceStatus;
	private String studentName;

}
