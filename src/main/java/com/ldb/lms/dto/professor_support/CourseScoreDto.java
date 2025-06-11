package com.ldb.lms.dto.professor_support;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter @Setter
@AllArgsConstructor
@NoArgsConstructor
public class CourseScoreDto {
    private String courseId;
    private String courseName;
    private String coursePeriod;
    private String professorName;
    private List<ScoreDto> grades;
}