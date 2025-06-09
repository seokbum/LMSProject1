package com.ldb.lms.controller.mypage;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.ldb.lms.dto.ApiResponseDto;
import com.ldb.lms.dto.learning_support.AttendanceDto;
import com.ldb.lms.dto.mypage.Student;
import com.ldb.lms.service.learning_support.LearningService;
import com.ldb.lms.service.mypage.MypageService;

import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequestMapping("/api/mypage")
@RequiredArgsConstructor
public class MypageApiController {

	
    private final MypageService mypageService;
    private final LearningService learningService;

    
    
    @GetMapping("viewCourseTimetable")
	public ResponseEntity<ApiResponseDto<List<AttendanceDto>>> viewCourseTimetable(HttpSession session) {
		try {
			Student stu = (Student)session.getAttribute("m");
			List<AttendanceDto> timetable =  learningService.viewCourseTime(stu.getStudentId());
			return ResponseEntity.ok(new ApiResponseDto<List<AttendanceDto>>(true, "조회 성공", timetable));
		} catch(Exception e) {
			return ResponseEntity
					.status(HttpStatus.INTERNAL_SERVER_ERROR)
					.body(new ApiResponseDto<>(false, "조회 실패: " + e.getMessage(), null));
		}
	}

	

}
