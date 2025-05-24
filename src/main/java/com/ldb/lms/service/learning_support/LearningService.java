package com.ldb.lms.service.learning_support;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.ldb.lms.dto.learning_support.CourseDto;
import com.ldb.lms.dto.learning_support.CoursePagingDto;
import com.ldb.lms.dto.learning_support.DeptDto;
import com.ldb.lms.dto.learning_support.RegistrationDto;
import com.ldb.lms.dto.learning_support.SearchDto;
import com.ldb.lms.dto.professor_support.PaginationDto;
import com.ldb.lms.mapper.learning_support.CourseMapper;

@Service
public class LearningService {
	
	private final CourseMapper courseMapper;

	public LearningService(CourseMapper courseMapper) {
		this.courseMapper = courseMapper;
	}
	
	public List<String> getColleges() {
        return courseMapper.getColleges();
    }

    public List<DeptDto> getDepartments(String college) {
        return courseMapper.getDepartments(college);
    }

    public Map<String, Object> searchCourse(SearchDto searchDto, PaginationDto pageDto) {
        Integer pageSize = pageDto.getItemsPerPage() != null ? pageDto.getItemsPerPage() : 10;
        Integer currentPage = pageDto.getCurrentPage() != null ? pageDto.getCurrentPage() : 1;
        Integer offset = (currentPage - 1) * pageSize;
        Integer totalRows = courseMapper.countCourses(searchDto);
        Integer totalPages = (int) Math.ceil((double) totalRows / pageSize);

        pageDto.setCurrentPage(currentPage);
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
	
	
	@Transactional
    public void registerCourse(Map<String, Object> map) {
        // 수강신청 ID 생성
        Long maxId = courseMapper.getMaxRegistrationIdNumber();
        String registrationId = "R" + (maxId + 1);
        map.put("registrationId", registrationId);

        // 수강 인원 체크
        Map<String, Object> courseInfo = courseMapper.getCurrentEnrollments((String) map.get("courseId"));
        Integer enrollment = (Integer) courseInfo.get("course_current_enrollment");
        Integer maxCnt = (Integer) courseInfo.get("course_max_cnt");

        if (enrollment >= maxCnt) {
            throw new RuntimeException("course is full");
        }

        // 수강 인원 업데이트
        map.put("enrollment", enrollment + 1);
        courseMapper.updateEnrollment(map);

        // 수강신청 등록
        courseMapper.addCourse(map);
        map.remove("registrationId");

        // 출석 데이터 추가
        Long maxAttendanceId = courseMapper.getMaxAttendanceIdNumber();
        String attendanceId = "A" + (maxAttendanceId + 1);
        map.put("attendanceId", attendanceId);
        courseMapper.addAttendance(map);
        map.remove("attendanceId");

        // 성적 데이터 추가
        Long maxScoreId = courseMapper.getScoreIdNumber();
        String scoreId = "SC" + (maxScoreId + 1);
        Map<String, String> studentInfo = courseMapper.getStudentInfo(map);
        String studentName = studentInfo.get("studentName");
        String deptId = studentInfo.get("dept_id");
        map.put("scoreId", scoreId);
        map.put("studentName", studentName);
        map.put("deptId", deptId);
        courseMapper.addScore(map);
    }

	
	
}
