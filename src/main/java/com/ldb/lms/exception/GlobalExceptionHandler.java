package com.ldb.lms.exception;

import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.NoHandlerFoundException;

@ControllerAdvice
public class GlobalExceptionHandler {

	@ExceptionHandler(ProfessorNotFoundException.class)
    public String handleProfessorNotFound(ProfessorNotFoundException e, Model model) {
        model.addAttribute("error", e.getMessage());
        return "error"; 
    }
	
	@ExceptionHandler(NoHandlerFoundException.class)
    public String handle404(NoHandlerFoundException e, Model model) {
        model.addAttribute("error", "페이지를 찾을 수 없습니다.");
        return "error";
    }
	
	@ExceptionHandler(Exception.class)
    public String handleEtc(Exception e, Model model) {
        model.addAttribute("error", "알 수 없는 오류가 발생했습니다.");
        return "error";
    }
	// 다른 예외들도 여기서 처리 가능
}
