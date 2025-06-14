package com.ldb.lms.dto.mypage;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class AdminDto {
	private String adminId;
	private String adminName;
	private String adminPassword;
	private String adminEmail;
	private String adminPhone;
	private String adminImg;
}
