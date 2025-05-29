package com.ldb.lms.dto.professor_support;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class RegistCourseDto {
    private String courseId;         			// 강의 ID
    private String deptId;           			// 학과 ID
    private String professorId;     			// 교수 ID
    private String courseName;      		    // 강의명
    private String courseStatus;     			// 강의 상태
    private Integer courseCurrentEnrollment;    // 현재 인원수
    private Integer courseMaxCnt;    			// 최대 수강 인원
    private Integer courseScore;     			// 학점
    private String creditCategory;   			// 이수구분
    private String coursePeriod;     			// 강의 학기
    private String coursePlan;       			// 강의 계획

    private String courseTimeId;     			// 강의 시간 ID
    private String courseTimeLoc;    			// 강의 장소
    private String courseTimeYoil;   			// 요일
    private String courseTimeStart;  			// 시작 시간 (HH:00 형식)
    private String courseTimeEnd;    			// 종료 시간 (HH:50 형식)

}