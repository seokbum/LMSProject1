package com.ldb.lms.controller.learning_support;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.SessionAttribute;

import com.ldb.lms.dto.learning_support.StudentRegistrationSummaryDto;
import com.ldb.lms.service.learning_support.LearningService;



@Controller
@RequestMapping("/learning_support")
public class LearningController {
	
	private final LearningService learningService;
	
	public LearningController(LearningService learningService) {
        this.learningService = learningService;
    }
	
	@GetMapping("registerCourse")
	public String callRegisterCourse () {
		return "learning_support/registerCourse";
	}
	
	@GetMapping("viewCourse")
	public String callViewCourse (
			@SessionAttribute(value = "login", required = false) String studentId,
			Model model
	) {
		
		StudentRegistrationSummaryDto registList = learningService.getStudentRegistrationSummary(studentId);
		
		model.addAttribute("registration", registList.getRegistrations());
		model.addAttribute("totalScore", registList.getTotalScore());
		
		return "learning_support/viewCourse";
	}
}











