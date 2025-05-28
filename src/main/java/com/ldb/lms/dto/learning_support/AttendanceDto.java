package com.ldb.lms.dto.learning_support;

import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

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
	private LocalTime courseTimeStart; // 시작 시간 (TIME 타입)
	private LocalTime courseTimeEnd; // 종료 시간 (TIME 타입)
	private String courseTimeLoc; // 강의실
	
	// 포맷팅된 시간 필드
	private String courseTimeStartFormatted;
	private String courseTimeEndFormatted;
	
	public String getCourseTimeStartFormatted() {
        if (this.courseTimeStart != null) {
            return this.courseTimeStart.format(DateTimeFormatter.ofPattern("HH:mm"));
        }
        return null;
    }

    public String getCourseTimeEndFormatted() {
        if (this.courseTimeEnd != null) {
            return this.courseTimeEnd.format(DateTimeFormatter.ofPattern("HH:mm"));
        }
        return null;
    }
    
}