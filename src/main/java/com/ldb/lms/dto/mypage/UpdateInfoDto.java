package com.ldb.lms.dto.mypage;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class UpdateInfoDto {
	private String id;
	private String email;
    private String phone;
    private String picture;
}
