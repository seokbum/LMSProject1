package com.ldb.lms.controller.professor_support;

import java.util.List;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.SessionAttribute;

import com.ldb.lms.dto.professor_support.PaginationDto;
import com.ldb.lms.dto.professor_support.RegistCourseDto;
import com.ldb.lms.service.learning_support.LearningService;
import com.ldb.lms.service.professor_support.ProfessorCourseManagementService;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Controller
@RequestMapping("/professors/courses/management")
public class ProfessorCourseManagementController {

    private final LearningService learningService;
	
	private final ProfessorCourseManagementService professorCourseManagementService;
	
	public ProfessorCourseManagementController(ProfessorCourseManagementService professorCourseManagementService, LearningService learningService) {
		this.professorCourseManagementService = professorCourseManagementService;
		this.learningService = learningService;
	}

	@GetMapping
	public String getCourses (
			@ModelAttribute PaginationDto paginationDto,
			@SessionAttribute(name="login", required=false) String professorId,
			Model model) {
		// 임시 하드코딩
		professorId = "P001";
		
		if (StringUtils.hasText(professorId)) {
			paginationDto.setProfessorId(professorId);
			professorCourseManagementService.calcPage(paginationDto);
			List<RegistCourseDto> courses = 
					professorCourseManagementService.getCourses(paginationDto);
			model.addAttribute("courses", courses);
		} else {
			throw new RuntimeException("로그인정보가 확인되지 않습니다.");
		}

		return "professor_support/manageCourse";
	}
	
//	@PutMapping("/{courseId}")
//	public String insertCourse (
//			@SessionAttribute(name="login", required=false) String professorId,
//			@ModelAttribute(name = "RegistCourseDto") RegistCourseDto rDto,
//			Model model
//			) {
//		
//		if (!StringUtils.hasText(professorId)) {
//			professorId = "P001";
//        }
//		
//		rDto.setProfessorId(professorId);
//		professorCourseManagementService.insertCourseAndCourseTime(rDto);
//		
//		return "redirect:courses";
//	}
	
}











