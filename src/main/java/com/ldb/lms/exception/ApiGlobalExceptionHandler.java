package com.ldb.lms.exception;

import java.util.HashMap;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.NoHandlerFoundException;

@RestControllerAdvice
public class ApiGlobalExceptionHandler {

	@ExceptionHandler(NoHandlerFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public Map<String, Object> handle404(NoHandlerFoundException ex) {
        
		Map<String, Object> error = new HashMap<>();
        error.put("success", false);
        error.put("status", 404);
        error.put("message", "요청한 URL이 존재하지 않습니다.");
        
        return error;
    }
	
	@ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public Map<String, Object> handleGeneral(Exception ex) {
		
        Map<String, Object> error = new HashMap<>();
        error.put("success", false);
        error.put("message", "서버 오류가 발생했습니다.");
        
        return error;
    }
	
}
