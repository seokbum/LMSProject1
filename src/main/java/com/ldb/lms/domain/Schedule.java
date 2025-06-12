package com.ldb.lms.domain;

import java.util.Date;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class Schedule {
    private Integer scheduleId;
    private String scheduleTitle;
    private String scheduleDescription;
    private Date scheduleStartDate; 
    private Date scheduleEndDate;   
    private String semesterType;    
}