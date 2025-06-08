package com.ldb.lms.controller.professor_support;

import java.util.List;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.SessionAttribute;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.ldb.lms.dto.learning_support.DeptDto;
import com.ldb.lms.dto.professor_support.RegistCourseDto;
import com.ldb.lms.exception.CourseRegistrationException;
import com.ldb.lms.service.learning_support.LearningService;
import com.ldb.lms.service.professor_support.ProfessorCourseRegisterService;

import jakarta.validation.Valid;
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
	public String getCourseForm (@SessionAttribute(name="login", required=false) String professorId,
			Model model) {
		
		String professorName = professorCourseRegisterService.getProfessorName(professorId);
        model.addAttribute("professorName", professorName);

		List<DeptDto> departments = getDepartments();
		model.addAttribute("departments", departments);
		
		if (!model.containsAttribute("registCourseDto")) {
            model.addAttribute("registCourseDto", new RegistCourseDto());
        }
		
		return "professor_support/registCourseByPro";
	}

	
	@PostMapping
	public String insertCourse (
			@SessionAttribute(name="login", required=false) String professorId,
			@Valid @ModelAttribute(name = "registCourseDto") RegistCourseDto rDto,
			BindingResult bindingResult,
			Model model,
			RedirectAttributes redirectAttributes) {
		
		log.info("등록데이터 확인 {}", rDto);
		
		if (bindingResult.hasErrors()) {
			List<DeptDto> departments = getDepartments();
			model.addAttribute("departments", departments);
			model.addAttribute("professorName", professorCourseRegisterService.getProfessorName(professorId));
			return "professor_support/registCourseByPro";
		}
		
		try {
			rDto.setProfessorId(professorId);
			professorCourseRegisterService.insertCourseAndCourseTime(rDto);
			redirectAttributes.addFlashAttribute("message", "강의가 성공적으로 등록되었습니다.");
			return "redirect:/professors/courses/register";
		} catch (CourseRegistrationException e) {
	        // 예외 메시지랑 입력값 다시 전달해서 원래 폼 보여주기
	        redirectAttributes.addFlashAttribute("errorMsg", e.getMessage());
	        redirectAttributes.addFlashAttribute("registCourseDto", rDto);
	        return "redirect:/professors/courses/register";
	    }
	}
	
	private List<DeptDto> getDepartments() {
		List<DeptDto> departments = learningService.getDepartments("");
		return departments;
	}
}











