package com.ldb.lms.domain;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class Score {
	
	private String scoreId;
    private String studentId;
    private String courseId;
    private String professorId;
    private String deptId;
    
    private Integer scoreMid;
    private Integer scoreFinal;
    private Integer scoreTotal;
    private String scoreGrade;
    private String scoreEtc;
	   
}