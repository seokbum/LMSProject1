package com.ldb.lms.dto.mypage;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class FindIdDto {

	private String name;
	private String email;
    private String professorName;
    private String professorEmail;
    private String studentName;
    private String studentEmail;
    
    public String getProfessorName() {
    	return this.name;
    }
    public String getProfessorEmail() {
    	return this.email;
    }
    public String getStudentName() {
    	return this.name;
    }
    public String getStudentEmail() {
    	return this.email;
    }
}
