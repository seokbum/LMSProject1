package com.ldb.lms.controller.learning_support;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.commons.beanutils.BeanUtils;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.SessionAttribute;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ldb.lms.mapper.TestMapper;
import com.ldb.lms.service.learning_support.LearningService;

import jakarta.servlet.http.HttpServletRequest;


@RestController
@RequestMapping("/api/learning_support")
public class LearningApiController {
	
private final LearningService learningService;
	
	public LearningApiController(LearningService learningService) {
        this.learningService = learningService;
    }
	
	@GetMapping("colleges")
	public String getColleges (HttpServletRequest request, HttpServletResponse response) {
		
		List<String> colleges = learningService.getColleges();
		
        ObjectMapper mapper = new ObjectMapper();
        String json;
        
		try {
			json = mapper.writeValueAsString(colleges);
			request.setAttribute("json", json);
			
		} catch (IOException e) {
			e.printStackTrace();

		}
		return "/pages/learning_support/ajax_learning_support";
	}
	
	@GetMapping("departments")
	public String getDepartments (HttpServletRequest request, HttpServletResponse response) {
		
		String college = request.getParameter("college");
		System.out.println("college: " + college);
		List<DeptDto> departments = courseDao.getDepartments(college);
		
        ObjectMapper mapper = new ObjectMapper();
        String json;
        
		try {
			json = mapper.writeValueAsString(departments);
			request.setAttribute("json", json);
		} catch (IOException e) {
			e.printStackTrace();

		}
		return "/pages/learning_support/ajax_learning_support";
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
	
	@GetMapping("searchCourse")
	public String searchCourse (HttpServletRequest request, HttpServletResponse response) {
		//세션 불러오기
		String studentId = (String) request.getSession().getAttribute("login");
		
		SearchDto searchDto = new SearchDto();
		PaginationDto pageDto = new PaginationDto();
		CoursePagingDto cpDto = new CoursePagingDto();
		
		try {
			BeanUtils.populate(searchDto, request.getParameterMap());
			searchDto.setStudentId(studentId);
			BeanUtils.populate(pageDto, request.getParameterMap());
		} catch(Exception e) {
			e.printStackTrace();
		}
		
		// 페이징처리 
		Integer pageSize = pageDto.getItemsPerPage(); 
		Integer currentPage = pageDto.getCurrentPage();
		currentPage = currentPage != null ? currentPage : 1;
		Integer offset = (currentPage - 1) * pageSize;
		Integer totalRows = courseDao.countCourses(searchDto);
		Integer totalPages = (int) Math.ceil((double)totalRows / pageSize); 
		
		pageDto.setCurrentPage(currentPage);
		pageDto.setTotalRows(totalRows);
		pageDto.setTotalPages(totalPages);
		pageDto.setOffset(offset);

		cpDto.setPaginationDto(pageDto);
		cpDto.setSearchDto(searchDto);

		List<CourseDto> courses = courseDao.searchCourse(cpDto);
		
		ObjectMapper mapper = new ObjectMapper();
        
		Map<String, Object> responseMap = new HashMap<String, Object>();
		responseMap.put("courses", courses);
		responseMap.put("pagination", pageDto);
		
		try {
			String json = mapper.writeValueAsString(responseMap);
			request.setAttribute("json", json);
			
		} catch (IOException e) {
			e.printStackTrace();
		}
		
        return "/pages/learning_support/ajax_learning_support";
	}
	
	@GetMapping("searchRegistrationCourses")
	public String searchRegistrationCourses (HttpServletRequest request, HttpServletResponse response) {
		
		String studentId = (String) request.getSession().getAttribute("login");
		
		List<RegistrationDto> result = courseDao.searchRegistrationCourses(studentId);
		ObjectMapper mapper = new ObjectMapper();
        String json;
        
		try {
			json = mapper.writeValueAsString(result);
			request.setAttribute("json", json);
		} catch (IOException e) {
			e.printStackTrace();
		}
		
        return "/pages/learning_support/ajax_learning_support";
	}
	
	
	@PostMapping("deleteCourse")
	public String deleteCourse (HttpServletRequest request, HttpServletResponse response) {

		String studentId = (String) request.getSession().getAttribute("login");
		
		String registrationId = request.getParameter("registrationId");
		String courseId = request.getParameter("courseId");
		
		try {
			courseDao.deleteCourse(registrationId,courseId, studentId);
		} catch(Exception e) {
			e.printStackTrace();
		}
		
		return "/pages/dummy";
	}
}