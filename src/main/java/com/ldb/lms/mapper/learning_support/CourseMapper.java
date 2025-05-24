package com.ldb.lms.mapper.learning_support;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.exceptions.PersistenceException;
import org.apache.ibatis.session.SqlSession;

import com.ldb.lms.dto.learning_support.CourseDto;
import com.ldb.lms.dto.learning_support.CoursePagingDto;
import com.ldb.lms.dto.learning_support.DeptDto;

@Mapper
public interface CourseMapper {
		 
	List<String> selectColleges();
	
	List<DeptDto> selectDepartments(String college);
	
	List<CourseDto> searchCourse(CoursePagingDto cpDto);

	Long getMaxRegistrationIdNumber(); 
	
	Map<String, Object> getCurrentEnrollments(String courseId);
	
	void updateEnrollment(Map<String, Object> map);
	
	void addCourse(Map<String, Object> param);
	
    void addAttendance(Map<String, Object> param);
    
    void addScore(Map<String, Object> param);
    
    Long getMaxAttendanceIdNumber();
    
    Long getScoreIdNumber();
    
    Map<String, String> getStudentInfo(Map<String, Object> param);
    


//	public int deleteCourse(String registrationId, String courseId, String studentId) {
//		
//		SqlSession session = MyBatisConnection.getConnection(); 
//		int num = 0;
//		Map<String, Object> map = new HashMap<String, Object>();
//		map.put("courseId", courseId);
//		map.put("studentId", studentId);
//		
//		// 현재 수강인원 조회
//		Map<String, Object> courseInfo = session.selectOne("getCurrentEnrollment", map);
//		Integer enrollment = (Integer) courseInfo.get("course_current_enrollment");
//		if (enrollment == null || enrollment <= 0) {
//		    enrollment = 0;
//		} else {
//			enrollment--;
//		}
//		
//		try {
//			map.put("enrollment",(enrollment));
//			session.update("updateEnrollment", map);
//			session.delete("course.deleteCourse", registrationId);
//			deleteAttendance(map, session);
//			deleteScore(map, session);
//			num=1;
//			MyBatisConnection.close(session);
//		} catch (Exception e) {	
//			session.close();
//			e.printStackTrace();
//			throw e;
//		}
//		
//		return num;
//		
//	}
//	
//	public void deleteAttendance(Map<String, Object> map, SqlSession session) {
//		
//		try {
//			session.delete("course.deleteAttendance", map); 
//		} catch (Exception e) {	
//			e.printStackTrace();
//			throw new RuntimeException("courseTime delete fail " + e.getMessage(), e);
//		}	
//	}
//	
//	public void deleteScore(Map<String, Object> map, SqlSession session) {
//		
//		try { 
//	        if (session.delete("course.deleteScore", map) <= 0) {
//	            throw new RuntimeException("Failed to delete ScoreTb");
//	        }
//
//	    } catch (PersistenceException e) {
//	        throw new RuntimeException("ScoreTb delete failed: " + e.getMessage(), e);
//	    }	
//	}
//	
//	public List<AttendanceDto> viewCourseTime(String studentId) {
//		
//		SqlSession session = MyBatisConnection.getConnection(); 
//		List<AttendanceDto> result = null;
//		
//		try {
//			result = session.selectList("course.viewCourseTime", studentId);
//		} catch (Exception e) {	
//			e.printStackTrace();
//		} finally {
//			MyBatisConnection.close(session);
//		}
//		
//		return result;
//	}
//
//	public Integer countCourses(SearchDto searchDto) {
//		int result = 0;
//		
//		try (SqlSession session = MyBatisConnection.getConnection()) {
//			result = session.selectOne("countCourses", searchDto);
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
//		
//		return result;
//	}

	

}
