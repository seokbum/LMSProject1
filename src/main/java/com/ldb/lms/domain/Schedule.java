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
    private Date scheduleDate; 
    private String scheduleTitle;
    private String scheduleDescription; 
}