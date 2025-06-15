package com.ldb.lms.dto.professor_support;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class updateAttendanceDto {

	private String attendanceDate;
	private String attendanceId;
	private String attendanceStatus;
	private String courseId;
	private String studentId;
	private String studentName;

}
