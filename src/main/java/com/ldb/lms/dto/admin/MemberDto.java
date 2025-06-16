package com.ldb.lms.dto.admin;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class MemberDto {
    private String id; 
    private String name;
    private String email;
    private String phone;
    private String type; 
}