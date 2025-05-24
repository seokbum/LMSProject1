package com.ldb.lms.dto.learning_support;

import java.sql.Time;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class AttendanceDto {
	private String studentName; // 학생 이름
	private String courseName; // 강의명
	private String professorName; // 교수명
	private String courseTimeYoil; // 요일
	private Time courseTimeStart; // 시작 시간 (TIME 타입)
	private Time courseTimeEnd; // 종료 시간 (TIME 타입)
	private String courseTimeLoc; // 강의실

}