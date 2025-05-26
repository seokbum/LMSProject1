package com.ldb.lms.dto.mypage;

import java.util.Date;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class Student {
	
	private String studentId;
    private String studentName;
    private String studentNum;
    private String deptId;
    private String studentEmail;
    private String studentPassword;
    private String studentStatus;
    private Date studentBirthday;
    private String studentPhone;
    private String studentImg;

}
