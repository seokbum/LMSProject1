package com.ldb.lms.controller.professor_support;

import java.util.List;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.SessionAttribute;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.ldb.lms.dto.ApiResponseDto;
import com.ldb.lms.dto.professor_support.PaginationDto;
import com.ldb.lms.dto.professor_support.RegistCourseDto;
import com.ldb.lms.service.learning_support.LearningService;
import com.ldb.lms.service.professor_support.ProfessorCourseManagementService;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Controller
@RequestMapping("/professors/courses/management")
public class ProfessorCourseManagementController {

    private final ProfessorCourseManagementService professorCourseManagementService;
	
	public ProfessorCourseManagementController(ProfessorCourseManagementService professorCourseManagementService, LearningService learningService) {
		this.professorCourseManagementService = professorCourseManagementService;
	}

	@GetMapping
	public String getCourses (
			@ModelAttribute PaginationDto paginationDto,
			@SessionAttribute(name="login", required=false) String professorId,
			Model model) {
		
			paginationDto.setProfessorId(professorId);
			professorCourseManagementService.calcPage(paginationDto);
			List<RegistCourseDto> courses = 
					professorCourseManagementService.getCourses(paginationDto);
			model.addAttribute("courses", courses);
		

		return "professor_support/manageCourse";
	}
	
	@PutMapping("/{courseId}")
	public String updateCourse (
			@RequestParam Map<String, Object> paramMap,
			@SessionAttribute(name="login", required=false) String professorId,
			@ModelAttribute(name = "RegistCourseDto") RegistCourseDto rDto,
			RedirectAttributes redirectAttributes) {
		
		String page = (String) paramMap.get("page");
		String search = (String) paramMap.get("search");
		String sortDirection = (String) paramMap.get("sortDirection");
		
		rDto.setProfessorId(professorId);
		log.info("==RegistcourseDto : {}", rDto.toString());
		
		// 강의 상세정보 수정
		try {
			professorCourseManagementService.updateCourse(rDto);
		} catch(Exception e) {
			redirectAttributes.addFlashAttribute("message", e.getMessage());
		}
		
		return "redirect:/professors/courses/management?page="
			+ page + "&search=" + search + "&sortDirection=" + sortDirection;
	}
	
	@PutMapping("/{courseId}/status")
	@ResponseBody
	public ResponseEntity<ApiResponseDto<Void>> updateCourseStatus(
			@PathVariable String courseId,
			@RequestParam String courseStatus) {
		
		try {
			professorCourseManagementService.updateCourseStatus(courseId, courseStatus);
			return ResponseEntity.ok(new ApiResponseDto<>(true, "변경처리 성공", null));
		} catch(Exception e) {
			return ResponseEntity
					.status(HttpStatus.INTERNAL_SERVER_ERROR)
					.body(new ApiResponseDto<>(false, "강의상태 변경 실패: " + e.getMessage(), null));
		}
	}
	
	@DeleteMapping("/{courseId}")
	public String deleteCourse(
			@PathVariable String courseId,
			@RequestParam Map<String, Object> paramMap,
			RedirectAttributes redirectAttributes) {

		String page = (String) paramMap.get("page");
		String search = (String) paramMap.get("search");
		String sortDirection = (String) paramMap.get("sortDirection");
		
		try {
			professorCourseManagementService.deleteCourse(courseId);
		} catch(Exception e) {
			redirectAttributes.addFlashAttribute("message", "강의 삭제처리 실패");
		}
		
		return "redirect:/professors/courses/management?page="
				+ page + "&search=" + search + "&sortDirection=" + sortDirection;
	}
}











