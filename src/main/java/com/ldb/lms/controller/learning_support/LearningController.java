package com.ldb.lms.controller.learning_support;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
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

	
	
	
	
	

	
	
	
	
	
	
}