package com.ldb.lms.dto.professor_support;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter @Setter
@AllArgsConstructor
@NoArgsConstructor
public class ScoreDto {
    private String studentName;
    private String studentId;
    private String deptName;
    private Integer scoreMid;
    private Integer scoreFinal;
    private Integer scoreTotal;
    private String scoreGrade;
}
