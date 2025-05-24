package com.ldb.lms.dto.learning_support;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class RegistrationDto {
    private String registrationId;
    private String creditCategory;
    private String courseId;
    private String courseName;
    private Integer courseScore;
    private String professorName;
    private String courseTimeLoc;
    private String timeSlot;
 
}