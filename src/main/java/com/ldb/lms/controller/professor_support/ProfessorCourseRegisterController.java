package com.ldb.lms.controller.professor_support;

import java.util.List;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.SessionAttribute;

import com.ldb.lms.dto.learning_support.DeptDto;
import com.ldb.lms.dto.professor_support.RegistCourseDto;
import com.ldb.lms.service.learning_support.LearningService;
import com.ldb.lms.service.professor_support.ProfessorCourseRegisterService;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Controller
@RequestMapping("/professors/courses/register")
public class ProfessorCourseRegisterController {

    private final LearningService learningService;
	
	private final ProfessorCourseRegisterService professorCourseRegisterService;
	
	public ProfessorCourseRegisterController(ProfessorCourseRegisterService professorCourseRegisterService, LearningService learningService) {
		this.professorCourseRegisterService = professorCourseRegisterService;
		this.learningService = learningService;
	}

	@GetMapping
	public String getCourseForm (Model model) {
		List<DeptDto> departments = learningService.getDepartments("");
		model.addAttribute("departments", departments);
		return "professor_support/registCourseByPro";
	}
	
	@PostMapping
	public String insertCourse (
			@SessionAttribute(name="login", required=false) String professorId,
			@ModelAttribute(name = "RegistCourseDto") RegistCourseDto rDto,
			Model model
			) {
		
		if (!StringUtils.hasText(professorId)) {
			professorId = "P001";
        }
		
		rDto.setProfessorId(professorId);
		professorCourseRegisterService.insertCourseAndCourseTime(rDto);
		
		return "redirect:courses";
	}
	
}











