package com.ldb.lms.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import com.ldb.lms.mapper.TestMapper;

import jakarta.servlet.http.HttpServletRequest;

@Controller
public class HomeController {
	private final TestMapper testMapper;

    public HomeController(TestMapper testMapper) {
        this.testMapper = testMapper;
    }

    @GetMapping("/")
    public String home(HttpServletRequest request,  Model model) {
    	String contextPath = request.getContextPath();
        System.out.println("컨트롤러에서 확인한 contextPath: " + contextPath); // 이 값을 로그에서 확인
        model.addAttribute("pathFromController", contextPath); // JSP로 전달하여 확인
        
    	String testResult = testMapper.selectTest();
        model.addAttribute("testResult", testResult);
        return "index";
    }
    
    @GetMapping("/registerCourse")
    public String registerCourse(HttpServletRequest request,  Model model) {
    	String contextPath = request.getContextPath();
        System.out.println("컨트롤러에서 확인한 contextPath: " + contextPath); // 이 값을 로그에서 확인
        model.addAttribute("pathFromController", contextPath); // JSP로 전달하여 확인
        
    	String testResult = testMapper.selectTest();
        model.addAttribute("testResult", testResult);
        return "test";
    }
}