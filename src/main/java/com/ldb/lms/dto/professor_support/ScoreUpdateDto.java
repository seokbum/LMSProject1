package com.ldb.lms.dto.professor_support;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ScoreUpdateDto {
    private String studentId;
    private String courseId;
    private String scoreMid;
    private String scoreFinal;
    private String scoreTotal;
    private String scoreGrade;
}
