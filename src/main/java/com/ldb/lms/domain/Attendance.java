package com.ldb.lms.domain;

import java.util.Date;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class Attendance {
	
    private String attendanceId;
    private String courseId;
    private String studentId;
    private String professorId;
    
    private Integer attendanceLate;
    private Integer attendanceAbsent;
    private String attendanceRemarks;
    private Date createdDate;

}