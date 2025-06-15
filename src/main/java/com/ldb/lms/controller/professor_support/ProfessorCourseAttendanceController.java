package com.ldb.lms.controller.professor_support;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.SessionAttribute;

import com.ldb.lms.dto.ApiResponseDto;
import com.ldb.lms.dto.professor_support.AttendanceDataDto;
import com.ldb.lms.dto.professor_support.CourseAttendanceDto;
import com.ldb.lms.dto.professor_support.ProfessorCourseDto;
import com.ldb.lms.dto.professor_support.updateAttendanceDto;
import com.ldb.lms.service.professor_support.ProfessorCourseAttendanceService;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequestMapping("/professors/courses/attendance")
@Controller
public class ProfessorCourseAttendanceController {
	
	private final ProfessorCourseAttendanceService attendanceService;
	
	public ProfessorCourseAttendanceController(ProfessorCourseAttendanceService attendanceService) {
		this.attendanceService = attendanceService;
	}
	
	@GetMapping
	public String getAttendanceForm(
			@SessionAttribute(name="login", required=false) String professorId,
			Model model) {
		
		List<ProfessorCourseDto> result = 
				attendanceService.getCoursesInfoByPro(professorId);
		model.addAttribute("courses", result);
		
		return "professor_support/manageAttendance";
	}
	
	@GetMapping("/{courseId}")
	@ResponseBody
	public ResponseEntity<ApiResponseDto<List<CourseAttendanceDto>>> getCourseAttendanceInfo(
			@SessionAttribute(name="login", required=false) String professorId,
			@PathVariable String courseId,
			@ModelAttribute AttendanceDataDto attendanceDto) {
		
		attendanceDto.setCourseId(courseId);
		
		try {
			List<CourseAttendanceDto> result = attendanceService.getAttendance(attendanceDto);
			return ResponseEntity.ok(new ApiResponseDto<>(true, "출석데이터 조회 성공", result));
		} catch(Exception e) {
			return ResponseEntity
					.status(HttpStatus.INTERNAL_SERVER_ERROR)
					.body(new ApiResponseDto<>(false, "출석데이터 조회 실패: " + e.getMessage(), null));
		}
	}
	
	@PostMapping("/{courseId}")
	@ResponseBody
	public ResponseEntity<ApiResponseDto<Void>> updateAttendanceInfo(
			@SessionAttribute(name="login", required=false) String professorId,
			@RequestBody List<updateAttendanceDto> attendanceList) {
		
		try {
			attendanceService.saveAttendanceInfo(attendanceList);
			return ResponseEntity.ok(new ApiResponseDto<>(true, "출석데이터 수정 성공", null));
		} catch (IllegalArgumentException e) {
	        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
	                .body(new ApiResponseDto<>(false, "잘못된 요청: " + e.getMessage(), null));
	    } catch(Exception e) {
			return ResponseEntity
					.status(HttpStatus.INTERNAL_SERVER_ERROR)
					.body(new ApiResponseDto<>(false, "출석데이터 수정 실패: " + e.getMessage(), null));
		}
	}
	
}
