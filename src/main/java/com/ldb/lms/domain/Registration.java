package com.ldb.lms.domain;

import java.util.Date;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class Registration {
    private String registrationId;
    private String studentId;
    private String courseId;
    private String professorId;
    private String registrationStatus;
    private Date registrationCreate;
    private Date registrationUpdate;
}