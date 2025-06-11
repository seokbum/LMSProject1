package com.ldb.lms.controller.professor_support;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.SessionAttribute;

import com.ldb.lms.dto.ApiResponseDto;
import com.ldb.lms.dto.professor_support.CourseScoreDto;
import com.ldb.lms.dto.professor_support.ProfessorCourseDto;
import com.ldb.lms.dto.professor_support.ProfessorInfoDto;
import com.ldb.lms.exception.NoDataFoundException;
import com.ldb.lms.service.professor_support.ProfessorCourseScoreService;

@Controller
@RequestMapping("/professors/courses/score")
public class ProfessorCourseScoreController {
	
	private final ProfessorCourseScoreService professorCourseScoreService;
	
	public ProfessorCourseScoreController(ProfessorCourseScoreService professorCourseScoreService) {
		this.professorCourseScoreService = professorCourseScoreService;
	}
	
	@GetMapping
	public String getScoreForm(
			@SessionAttribute(name="login", required=false) String professorId,
			Model model) {
		
			ProfessorInfoDto professorInfo = 
					professorCourseScoreService.getProfessorInfo(professorId);
			model.addAttribute("professorInfo",professorInfo);
		
		
		return "professor_support/manageScore";
	}
	
	@GetMapping("/getCourseList")
	@ResponseBody
	public ResponseEntity<ApiResponseDto<List<ProfessorCourseDto>>> getCourseList(
			@SessionAttribute(value = "login", required = false) String professorId
			) {
		
		try {
	        List<ProfessorCourseDto> courses = professorCourseScoreService.getCoursesInfo(professorId);
	        if (courses.isEmpty()) {
	            return ApiResponseDto.fail("등록된 과목이 없습니다.");
	        }
	        return ApiResponseDto.ok(courses);
	    } catch (Exception e) {
	        return ApiResponseDto.error("과목데이터 조회 실패: " + e.getMessage());
	    }
	}
	
	@GetMapping("/getScoreInfo")
	@ResponseBody
	public ResponseEntity<ApiResponseDto<CourseScoreDto>> getScoreInfo(
			@RequestParam String courseId,
			@RequestParam String courseName,
			@SessionAttribute(value = "login", required = false) String professorId
			) {
		
		try {
	        CourseScoreDto dto = professorCourseScoreService.getScoreInfo(professorId, courseId, courseName);
	        return ApiResponseDto.ok(dto);
	    } catch (NoDataFoundException e) {
	        return ApiResponseDto.fail(e.getMessage()); // 200 OK + 실패 메시지
	    } catch (IllegalArgumentException e) {
	        return ApiResponseDto.fail("요청 파라미터 오류: " + e.getMessage());
	    } catch (Exception e) {
	        return ApiResponseDto.error("성적 정보 조회 중 오류 발생: " + e.getMessage());
	    }
	    
	}
	
}




























