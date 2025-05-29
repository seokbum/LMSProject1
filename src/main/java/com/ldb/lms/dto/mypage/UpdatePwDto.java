package com.ldb.lms.dto.mypage;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Setter
@Getter
@ToString
public class UpdatePwDto {
	//임시비밀번호 업데이트용 (id,newPw)
	private String id;
	private String newPw;
	
	
	//비밀번호변경폼을 이용한다면 1개의필드 더 사용
	private String email;
	private String pw;
	
}
