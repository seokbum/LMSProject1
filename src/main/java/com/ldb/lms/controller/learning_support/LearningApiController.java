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
            @ModelAttribute SearchDto searchDto,
            @ModelAttribute PaginationDto pageDto) {
    	
        String studentId = "S001"; // 테스트용 하드코딩
        searchDto.setStudentId(studentId);

        return ResponseEntity.ok(learningService.searchCourse(searchDto, pageDto));
    }

    @GetMapping("searchRegistrationCourses")
    public ResponseEntity<List<RegistrationDto>> searchRegistrationCourses() {
        String studentId = "S001"; // 테스트용 하드코딩
        return ResponseEntity.ok(learningService.searchRegistrationCourses(studentId));
    }
	
	@PostMapping("registerCourse")
	@ResponseBody
	public ResponseEntity<ApiResponseDto<Void>> registerCourse (
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
			return ResponseEntity.ok(new ApiResponseDto<>(true, "등록처리 성공", null));
		} catch(Exception e) {
			e.printStackTrace();
			return ResponseEntity
					.status(HttpStatus.INTERNAL_SERVER_ERROR)
					.body(new ApiResponseDto<>(false, "수정 실패: " + e.getMessage(), null));
		}
	}
	
	@PostMapping("deleteCourse")
	public ResponseEntity<ApiResponseDto<Void>> deleteCourse (
			@RequestParam Map<String, Object> map,
			@SessionAttribute(value = "login", required = false) String studentId
			) {

		if (studentId == null) {
			// login 처리전까지 하드코딩
//            response.put("success", false);
//            response.put("errorMsg", "Login required");
//            return ResponseEntity.badRequest().body(response);
			studentId = "S001";
			map.put("studentId", studentId);
        }
		
		try {
			learningService.deleteCourse(map);
			return ResponseEntity.ok(new ApiResponseDto<>(true, "삭제처리 성공", null));
		} catch(Exception e) {
			e.printStackTrace();
			return ResponseEntity
					.status(HttpStatus.INTERNAL_SERVER_ERROR)
					.body(new ApiResponseDto<>(false, "수정 실패: " + e.getMessage(), null));
		}
		
	}
	
	@GetMapping("viewCourseTime")
	public ResponseEntity<ApiResponseDto<List<AttendanceDto>>> viewCourseTime(
			@SessionAttribute(value = "login", required = false) String studentId
			) {
		
		if (studentId == null) {
			// login 처리전까지 하드코딩
//            response.put("success", false);
//            response.put("errorMsg", "Login required");
//            return ResponseEntity.badRequest().body(response);
			studentId = "S001";
        }
		
		try {
			List<AttendanceDto> timetable =  learningService.viewCourseTime(studentId);
			return ResponseEntity.ok(new ApiResponseDto<List<AttendanceDto>>(true, "조회 성공", timetable));
		} catch(Exception e) {
			return ResponseEntity
					.status(HttpStatus.INTERNAL_SERVER_ERROR)
					.body(new ApiResponseDto<>(false, "조회 실패: " + e.getMessage(), null));
		}
		
	}
	
	
}


























