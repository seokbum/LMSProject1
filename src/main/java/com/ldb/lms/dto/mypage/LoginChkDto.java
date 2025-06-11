package com.ldb.lms.dto.mypage;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class LoginChkDto {
	
	private String id;
	private String password;
	private String studentId;
	private String studentPassword;
	
    private String professorId;
    private String professorPassword;
    
	public String getId() {
		if(this.studentId==null) {
			return getProfessorId();
		}
		else {
			return getStudentId();
		}
	}
	
	public String getPassword() {
		if(this.studentPassword==null) {
			return getProfessorPassword();
		}
		else {
			return getStudentPassword();
		}
	}
	
	
}
