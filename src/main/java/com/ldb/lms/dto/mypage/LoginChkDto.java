package com.ldb.lms.dto.mypage;

import org.springframework.util.StringUtils;

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
    
    private String adminId;
    private String adminPassword;
    
	public String getId() {
		if(StringUtils.hasText(this.professorId)) {
			return getProfessorId();
		}
		else if(StringUtils.hasText(this.studentId)){
			return getStudentId();
		}
		else {
			return getAdminId();
		}
	}
	
	public String getPassword() {
		if(this.studentPassword!=null) {
			return this.studentPassword;
		}
		else if(this.professorPassword!=null) {
			return this.professorPassword;
		}
		else {
			return getAdminPassword();
		}
	}
	
	
}
