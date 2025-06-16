package com.ldb.lms.dto.professor_support;

import java.time.LocalDate;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class CourseAttendanceDto {
	
	private String studentId;
	private String studentName;
	private String attendanceId;
	private Integer attendanceLate;
	private Integer attendanceAbsent;
	private String attendanceRemarks;
	private LocalDate attendanceHistoryDate;
	private String attendanceHistoryStatus;
	
}
