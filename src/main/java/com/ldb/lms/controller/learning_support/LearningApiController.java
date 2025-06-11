package com.ldb.lms.controller.learning_support;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.SessionAttribute;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ldb.lms.dto.ApiResponseDto;
import com.ldb.lms.dto.learning_support.AttendanceDto;
import com.ldb.lms.dto.learning_support.DeptDto;
import com.ldb.lms.dto.learning_support.RegistrationDto;
import com.ldb.lms.dto.learning_support.SearchDto;
import com.ldb.lms.dto.professor_support.PaginationDto;
import com.ldb.lms.service.learning_support.LearningService;

import lombok.extern.slf4j.Slf4j;


@Slf4j
@RestController
@RequestMapping("/api/learning_support")
public class LearningApiController {
	
	private final LearningService learningService;
	
	private final ObjectMapper objectMapper;
	
	public LearningApiController(LearningService learningService) {
        this.learningService = learningService;
		this.objectMapper = new ObjectMapper();
    }
	
	@GetMapping("colleges")
    public ResponseEntity<List<String>> getColleges() {
        return ResponseEntity.ok(learningService.getColleges());
    }

    @GetMapping("departments")
    public ResponseEntity<List<DeptDto>> getDepartments(@RequestParam String college) {
    	List<DeptDto> depts = learningService.getDepartments(college);	
    	return ResponseEntity.ok(depts);
    }

    @GetMapping("searchCourse")
    public ResponseEntity<Map<String, Object>> searchCourse(
    		@SessionAttribute(value = "login", required = false) String studentId,
            @ModelAttribute SearchDto searchDto,
            @ModelAttribute PaginationDto pageDto) {
    	
        searchDto.setStudentId(studentId);

        return ResponseEntity.ok(learningService.searchCourse(searchDto, pageDto));
    }

    @GetMapping("searchRegistrationCourses")
    public ResponseEntity<List<RegistrationDto>> searchRegistrationCourses(
    		@SessionAttribute(value = "login", required = false) String studentId) {

        return ResponseEntity.ok(learningService.searchRegistrationCourses(studentId));
        
    }
	
	@PostMapping("registerCourse")
	@ResponseBody
	public ResponseEntity<ApiResponseDto<Void>> registerCourse (
			@RequestParam Map<String, Object> map,
            @SessionAttribute(value = "login", required = false) String studentId) {
		
		Map<String, Object> response = new HashMap<>();
        map.put("studentId", studentId);
        
        try {
			learningService.registerCourse(map);
			return ApiResponseDto.ok(null);
		} catch(Exception e) {
			e.printStackTrace();
			return ApiResponseDto.error("강의수정 실패: " + e.getMessage());
		}
	}
	
	@PostMapping("deleteCourse")
	public ResponseEntity<ApiResponseDto<Void>> deleteCourse (
			@RequestParam Map<String, Object> map,
			@SessionAttribute(value = "login", required = false) String studentId
			) {
		
		map.put("studentId", studentId);
		
		try {
			learningService.deleteCourse(map);
			return ApiResponseDto.ok(null);
		} catch(Exception e) {
			e.printStackTrace();
			return ApiResponseDto.error("강의삭제 실패: " + e.getMessage());
		}
		
	}
	
	@GetMapping("viewCourseTime")
	public ResponseEntity<ApiResponseDto<List<AttendanceDto>>> viewCourseTime(
			@SessionAttribute(value = "login", required = false) String studentId
			) {

		try {
			List<AttendanceDto> timetable =  learningService.viewCourseTime(studentId);
			if (timetable.isEmpty()) {
	            return ApiResponseDto.fail("등록된 시간표가 없습니다.");
	        }
			return ApiResponseDto.ok(timetable);
		} catch(Exception e) {
			return ApiResponseDto.error("시간표 조회 실패: " + e.getMessage());
			
		}
	}
	
	
}


























