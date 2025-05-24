package com.ldb.lms.domain;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class Course {

    private String courseId;
    private String deptId;
    private String professorId;
    private String courseName;
    private String courseStatus;
    private Integer courseCurrentEnrollment;  // NULL 가능성이 있어서 Integer로 선언
    private Integer courseMaxCnt;             // NULL 가능성이 있어서 Integer로 선언
    private Integer courseScore;
    private String creditCategory;
    private String coursePeriod;
    private String coursePlan;
}
