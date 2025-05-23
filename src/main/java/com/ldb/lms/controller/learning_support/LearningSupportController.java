package com.ldb.lms.controller.learning_support;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.ldb.lms.mapper.TestMapper;

import jakarta.servlet.http.HttpServletRequest;

@RequestMapping("/learning_support")
@Controller
public class LearningSupportController {
	private final TestMapper testMapper;

    public LearningSupportController(TestMapper testMapper) {
        this.testMapper = testMapper;
    }

    
    
    @GetMapping("/viewCourse")
    public String registerCourse(HttpServletRequest request,  Model model) {
    	String contextPath = request.getContextPath();
        System.out.println("컨트롤러에서 확인한 contextPath: " + contextPath); // 이 값을 로그에서 확인
        model.addAttribute("pathFromController", contextPath); // JSP로 전달하여 확인
        
    	String testResult = testMapper.selectTest();
        model.addAttribute("testResult", testResult);
        return "test";
    }
}