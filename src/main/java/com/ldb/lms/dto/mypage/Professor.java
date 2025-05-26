package com.ldb.lms.dto.mypage;

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
    private Date professorBirthday;
    private String professorPhone;
    private String professorImg;
    private String professorPassword;
    private String deptId;
    
}
