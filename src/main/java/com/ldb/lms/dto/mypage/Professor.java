package com.ldb.lms.dto.mypage;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class Professor {
    private String professorId;
    private String professorName;
    private String professorEmail;
    private LocalDate professorBirthday;
    private String professorPhone;
    private String professorImg;
    private String professorPassword;
    private String deptId;
    
    public Date getProfessorBirthday() {
    	return Date.from(this.professorBirthday.atStartOfDay(ZoneId.systemDefault()).toInstant());
 
    	
    }
    
}
