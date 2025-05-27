package com.ldb.lms.dto.learning_support;

import java.util.List;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class StudentRegistrationSummaryDto {
    private List<RegistrationDto> registrations;
    private Integer totalScore;
    
    public StudentRegistrationSummaryDto(List<RegistrationDto> registrations, Integer totalScore) {
        this.registrations = registrations;
        this.totalScore = totalScore;
    }
}