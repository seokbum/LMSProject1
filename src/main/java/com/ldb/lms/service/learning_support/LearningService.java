package com.ldb.lms.service.learning_support;

import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.ldb.lms.mapper.learning_support.CourseMapper;

@Service
public class LearningService {
	
	private final CourseMapper courseMapper;

	public LearningService(CourseMapper courseMapper) {
		this.courseMapper = courseMapper;
	}
	
	public List<String> getColleges() {
		
		return null;
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
