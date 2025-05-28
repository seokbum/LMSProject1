package com.ldb.lms.dto.mypage;

import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.Date;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class RegisterUserDto {
	private String picture;
	private String name;
	private LocalDate birth;
	private String position;
	private String deptId;
	private String password;
	private String confirmPassword;
	private String phone;
	private String email;
	
	
	
	
	
	
	

}
