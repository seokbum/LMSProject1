package com.ldb.lms.exception;

import lombok.Getter;

@Getter
public class NotStuException  extends RuntimeException{
	
	private String errorCode;

	public NotStuException(String errorCode) {
		super(errorCode);
		this.errorCode = errorCode;
	}
	
	public NotStuException(String errorCode,String message) {
		super(message);
		this.errorCode = errorCode;
	}
	
	
	
	

}
