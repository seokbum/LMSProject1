package com.ldb.lms.controller.professor_support;

import java.util.List;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.SessionAttribute;

import com.ldb.lms.dto.learning_support.DeptDto;
import com.ldb.lms.service.learning_support.LearningService;
import com.ldb.lms.service.professor_support.ProfessorService;


@Controller
public class ProfessorController {

    private final LearningService learningService;
	
	private final ProfessorService professorService;
	
	public ProfessorController(ProfessorService professorService, LearningService learningService) {
		this.professorService = professorService;
		this.learningService = learningService;
	}

	@GetMapping("/professors/courses")
	public String getCourses (
			@SessionAttribute(value="login", required=false) String professorId,
			Model model
			) {
		List<DeptDto> departments = learningService.getDepartments("");
		// TODO: 여기부터 작업하렴~~~~부서목록 가져와서 강의등록페이지에 뿌려줘야한다
		return "professor_support/registerCourse";
	}
	
	
}











