package com.ldb.lms.domain;

import java.sql.Time;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class CourseTime {
    private String courseTimeId;
    private String professorId;
    private String courseId;
    private String courseTimeYoil;
    private String courseTimeLoc;
    private Time courseTimeStart;
    private Time courseTimeEnd;

}