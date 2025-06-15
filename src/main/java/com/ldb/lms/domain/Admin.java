package com.ldb.lms.domain;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class Admin {
	private String adminId;
	private String adminName;
	private String adminPassword;
	private String adminEmail;
	private String adminPhone;
	private Integer scheduleId;
	private String studentId;
	private String professorId;
}
