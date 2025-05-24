package com.ldb.lms.controller.learning_support;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.SessionAttribute;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ldb.lms.dto.learning_support.DeptDto;
import com.ldb.lms.dto.learning_support.RegistrationDto;
import com.ldb.lms.dto.learning_support.SearchDto;
import com.ldb.lms.dto.professor_support.PaginationDto;
import com.ldb.lms.mapper.TestMapper;
import com.ldb.lms.service.learning_support.LearningService;

import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;


@Slf4j
@RestController
@RequestMapping("/api/learning_support")
public class LearningApiController {
	
private final LearningService learningService;
	
	public LearningApiController(LearningService learningService) {
        this.learningService = learningService;
    }
	
	@GetMapping("colleges")
    public ResponseEntity<List<String>> getColleges() {
        return ResponseEntity.ok(learningService.getColleges());
    }

    @GetMapping("departments")
    public ResponseEntity<List<DeptDto>> getDepartments(@RequestParam String college) {
        return ResponseEntity.ok(learningService.getDepartments(college));
    }

    @GetMapping("searchCourse")
    public ResponseEntity<Map<String, Object>> searchCourse(
            @ModelAttribute SearchDto searchDto,
            @ModelAttribute PaginationDto pageDto) {
    	
        String studentId = "S001"; // 테스트용 하드코딩
        searchDto.setStudentId(studentId);
        System.out.println("dto확인: " + searchDto.toString());
        System.out.println("dto확인: " + pageDto.toString());
        return ResponseEntity.ok(learningService.searchCourse(searchDto, pageDto));
    }

    @GetMapping("searchRegistrationCourses")
    public ResponseEntity<List<RegistrationDto>> searchRegistrationCourses() {
        String studentId = "S001"; // 테스트용 하드코딩
        return ResponseEntity.ok(learningService.searchRegistrationCourses(studentId));
    }
	
	@PostMapping("registerCourse")
	@ResponseBody
	public ResponseEntity<Map<String, Object>> registerCourse (
			@RequestParam Map<String, Object> map,
            @SessionAttribute(value = "login", required = false) String studentId) {
		
		Map<String, Object> response = new HashMap<>();
		
		if (studentId == null) {
			// login 처리전까지 하드코딩
//            response.put("success", false);
//            response.put("errorMsg", "Login required");
//            return ResponseEntity.badRequest().body(response);
			studentId = "S001";
        }

        map.put("studentId", studentId);
        
        try {
            learningService.registerCourse(map);
            response.put("success", true);
            response.put("message", "Course registered successfully");
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            response.put("success", false);
            response.put("errorMsg", e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
	}
	
//	@PostMapping("deleteCourse")
//	public String deleteCourse (HttpServletRequest request, HttpServletResponse response) {
//
//		String studentId = (String) request.getSession().getAttribute("login");
//		
//		String registrationId = request.getParameter("registrationId");
//		String courseId = request.getParameter("courseId");
//		
//		try {
//			courseDao.deleteCourse(registrationId,courseId, studentId);
//		} catch(Exception e) {
//			e.printStackTrace();
//		}
//		
//		return "/pages/dummy";
//	}
}