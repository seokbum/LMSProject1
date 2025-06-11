package com.ldb.lms.service.learning_support;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import com.ldb.lms.dto.learning_support.AttendanceDto;
import com.ldb.lms.dto.learning_support.CourseDto;
import com.ldb.lms.dto.learning_support.CoursePagingDto;
import com.ldb.lms.dto.learning_support.DeptDto;
import com.ldb.lms.dto.learning_support.RegistrationDto;
import com.ldb.lms.dto.learning_support.SearchDto;
import com.ldb.lms.dto.learning_support.StudentRegistrationSummaryDto;
import com.ldb.lms.dto.professor_support.PaginationDto;
import com.ldb.lms.mapper.mybatis.learning_support.CourseMapper;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class LearningService {
	
	private final CourseMapper courseMapper;
	
	public List<String> getColleges() {
        return courseMapper.getColleges();
    }

    public List<DeptDto> getDepartments(String college) {
        return courseMapper.getDepartments(college);
    }

    public Map<String, Object> searchCourse(SearchDto searchDto, PaginationDto pageDto) {
        Integer pageSize = pageDto.getItemsPerPage();
        Integer page = pageDto.getPage();
        Integer offset = (page - 1) * pageSize;
        Integer totalRows = courseMapper.countCourses(searchDto);
        Integer totalPages = (int) Math.ceil((double) totalRows / pageSize);

        pageDto.setPage(page);
        pageDto.setTotalRows(totalRows);
        pageDto.setTotalPages(totalPages);
        pageDto.setOffset(offset);

        CoursePagingDto cpDto = new CoursePagingDto();
        cpDto.setSearchDto(searchDto);
        cpDto.setPaginationDto(pageDto);

        List<CourseDto> courses = courseMapper.searchCourse(cpDto);

        Map<String, Object> response = new HashMap<>();
        response.put("courses", courses);
        response.put("pagination", pageDto);
        return response;
    }
    
    public List<RegistrationDto> searchRegistrationCourses(String studentId) {

        return courseMapper.searchRegistrationCourses(studentId);
    }
    
    public StudentRegistrationSummaryDto getStudentRegistrationSummary(String studentId) {
    	List<RegistrationDto> regList = courseMapper.searchRegistrationCourses(studentId);
    	Integer totalScore = calcTotalScore(regList);
    	
        return new StudentRegistrationSummaryDto(regList, totalScore);
    }
	
    private Integer calcTotalScore(List<RegistrationDto> registList) {
    	int totalScore = 0;
		
		for (RegistrationDto r : registList) {
			totalScore += r.getCourseScore();
		}
		
		return totalScore;
	}
    
	@Transactional
    public void registerCourse(Map<String, Object> map) {
		
		String courseId = (String) map.get("courseId");
	    String studentId = (String) map.get("studentId");
	    String professorId = (String) map.get("professorId");

	    // 필수 매개변수 유효성 검사
	    if (!StringUtils.hasText(courseId)) {
	        log.error("registerCourse: courseId가 유효하지 않습니다.");
	        throw new IllegalArgumentException("과목 ID는 필수입니다.");
	    }
	    
	    if (!StringUtils.hasText(studentId)) {
	        log.error("registerCourse: 학생 ID는 필수입니다. 로그인 정보를 확인해주세요.");
	        throw new SecurityException("사용자 정보를 확인할 수 없습니다.");
	    }
	    
	    if (!StringUtils.hasText(professorId)) {
	        log.error("registerCourse: 교수 ID는 필수입니다. professorId가 유효하지 않습니다.");
	        throw new IllegalArgumentException("교수ID 정보를 확인할 수 없습니다.");
	    }
	    
	    // 수강 인원 증가 및 정원 체크 
        int updatedRows = courseMapper.increaseEnrollment(courseId);
        if (updatedRows == 0) {
        	log.warn("registerCourse: 과목 {}이(가) 정원 초과로 수강 신청 실패.", courseId);
            throw new RuntimeException("course is full");
        }
        
	    try {
	        // 수강신청 ID 생성 및 등록
	    	Map<String, Object> registrationData = new HashMap<>();
	        Long maxId = courseMapper.getMaxRegistrationIdNumber();
	        String registrationId = "R" + (maxId + 1);
	        
	        registrationData.put("registrationId", registrationId);
	        registrationData.put("studentId", studentId);
	        registrationData.put("courseId", courseId);
	        registrationData.put("professorId", professorId);
	        courseMapper.addCourse(registrationData);
	        
	        // 출석 데이터 추가
	        Map<String, Object> attendanceData = new HashMap<>();
	        Long maxAttendanceId = courseMapper.getMaxAttendanceIdNumber();
	        String attendanceId = "A" + (maxAttendanceId + 1);
	        
	        attendanceData.put("attendanceId", attendanceId);
	        attendanceData.put("courseId", courseId);
	        attendanceData.put("studentId", studentId);
	        attendanceData.put("professorId", professorId);
	        courseMapper.addAttendance(attendanceData);
	
	        // 성적 데이터 추가
	        Map<String, Object> scoreData = new HashMap<>();
	        Long maxScoreId = courseMapper.getScoreIdNumber();
	        String scoreId = "SC" + (maxScoreId + 1);
	        
	        scoreData.put("scoreId", scoreId);
            scoreData.put("courseId", courseId);
            scoreData.put("studentId", studentId);
            scoreData.put("professorId", professorId);

	        Map<String, String> studentInfo = courseMapper.getStudentInfo(studentId);
            if (studentInfo != null) {
                scoreData.put("studentName", studentInfo.get("student_name"));
                scoreData.put("deptId", studentInfo.get("dept_id"));
            } else {
                log.warn("registerCourse: 학생 정보(ID: {})를 찾을 수 없습니다. 성적 데이터에 이름/학과ID가 누락될 수 있습니다.", studentId);
            }
	        courseMapper.addScore(scoreData);
	        
	    } catch(Exception e) {
	    	log.error("registerCourse: 수강 신청 중 예상치 못한 시스템 오류 발생. courseId: {}, studentId: {}.",
                    courseId, studentId, e); 
    		throw new RuntimeException("수강 신청 처리 중 알 수 없는 오류가 발생했습니다. 잠시 후 다시 시도해주세요.", e);
	    }
    }
	
	@Transactional
	public void deleteCourse(Map<String, Object> map) {
		
		String courseId = (String) map.get("courseId");
        String registrationId = (String) map.get("registrationId");
        String studentId = (String) map.get("studentId");

        // 매개변수 유효성 검사
        if (!StringUtils.hasText(courseId)) {
        	log.error("deleteCourse: courseId가 유효하지 않습니다.");
            throw new IllegalArgumentException("과목 ID는 필수입니다.");
        }
        if (!StringUtils.hasText(registrationId)) {
        	log.error("deleteCourse: registrationId가 유효하지 않습니다.");
            throw new IllegalArgumentException("수강 신청 ID는 필수입니다.");
        }
        if (!StringUtils.hasText(studentId)) {
            log.error("deleteCourse: 현재 사용자 ID(studentId)가 유효하지 않습니다.");
            throw new SecurityException("사용자 정보를 확인할 수 없습니다.");
        }
		
		
		// 현재 수강인원 조회
		Map<String, Object> courseInfo = courseMapper.getCurrentEnrollments(courseId);
		Integer enrollment = null;
		
        if (courseInfo != null && courseInfo.containsKey("course_current_enrollment")) {
            enrollment = (Integer) courseInfo.get("course_current_enrollment");
        }
		
		if (enrollment == null || enrollment <= 0) {
			log.warn("deleteCourse: 비정상적인 수강 인원 값 감지. courseId: {}, enrollment: {}. 0으로 초기화합니다.", 
					courseId, enrollment);
		    enrollment = 0;
		} else {
			enrollment--;
		}
		
		// 과목 인원수정보 갱신, 학생수강신청목록 삭제, 성적테이블 삭제
		try {
			map.put("enrollment", enrollment);
			courseMapper.decrementEnrollment(courseId);
			courseMapper.deleteRegistration(registrationId);
			courseMapper.deleteAttendance(map);
			courseMapper.deleteScore(map);
		} catch (Exception e) {
			// 오류 로그 기록
            log.error("수강 신청 삭제 중 데이터베이스 작업 오류 발생. registrationId: {}, courseId: {}",
                         map.get("registrationId"), map.get("courseId"), e);
            // 런타임 예외를 다시 던져서 트랜잭션 롤백을 유도하고, 컨트롤러로 전달
            throw new RuntimeException("수강 신청 삭제 실패: " + e.getMessage(), e);
		}
	}

	public List<AttendanceDto> viewCourseTime(String studentId) {
		List<AttendanceDto> timetable = null;
		
		try {
			timetable = courseMapper.viewCourseTime(studentId);
			
		} catch (Exception e) {
			// 오류 로그 기록
            log.error("시간표조회 에러 ",e);
            // 런타임 예외를 다시 던져서 트랜잭션 롤백을 유도하고, 컨트롤러로 전달
            throw new RuntimeException("시간표조회 실패: " + e.getMessage(), e);
		}
		
		return timetable;
	}

	
	
}
