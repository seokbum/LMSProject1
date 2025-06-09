package com.ldb.lms.dto.mypage;

import lombok.Data;

@Data
public class DeleteUserDto {
	private String studentId;
	private String deptId;
	private String studentName;
	private String studentStatus;
	private String studentEmail;
	private String pw;
}
