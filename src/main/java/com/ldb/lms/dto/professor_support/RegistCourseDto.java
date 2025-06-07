package com.ldb.lms.dto.professor_support;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class RegistCourseDto {
	
    private String courseId;         			// 강의 ID
	
	@NotBlank(message="{course.deptId.required}")
	private String deptId;           			// 학과 ID
    
	private String professorId;     			// 교수 ID
    
    @NotBlank(message="{course.courseName.required}")
    private String courseName;      		    // 강의명
    
    private String courseStatus;     			// 강의 상태
    
    private Integer courseCurrentEnrollment;    // 현재 인원수
    
    @NotNull(message = "{course.courseMaxCnt.required}")
    @Min(value = 1, message = "{course.courseMaxCnt.min}")
    @Max(value = 100, message = "{course.courseMaxCnt.max}")
    private Integer courseMaxCnt;    			// 최대 수강 인원
    
    @NotNull(message = "{course.courseScore.required}")
    @Min(value = 1, message = "{course.courseScore.min}")
    @Max(value = 6, message = "{course.courseScore.max}")
    private Integer courseScore;     			// 학점
    
    @NotBlank(message = "{course.creditCategory.required}")
    @Pattern(regexp = "MajorRequired|MajorElective|LiberalArts",
    		 message = "{course.creditCategory.invalid}")
    private String creditCategory;   			// 이수구분
    
    @NotBlank(message = "{course.coursePeriod.required}")
    @Pattern(regexp = "1학기|2학기", message = "{course.coursePeriod.invalid}")
    private String coursePeriod;     			// 강의 학기
    
    @Size(max = 1000, message = "{course.coursePlan.size}")
    private String coursePlan;       			// 강의 계획
    
    @NotBlank(message = "{course.courseEmail.required}")
    @Email(message = "{course.courseEmail.invalid}")
    private String courseEmail;					// 강의 이메일
    
    private String courseTimeId;     			// 강의 시간 ID
    
    @NotBlank(message = "{course.courseTimeLoc.required}")
    @Size(max = 50, message = "{course.courseTimeLoc.size}")
    private String courseTimeLoc;    			// 강의 장소
    
    @NotBlank(message = "{course.courseTimeYoil.required}")
    @Pattern(regexp = "월|화|수|목|금", message = "{course.courseTimeYoil.invalid}")
    private String courseTimeYoil;   			// 요일
    
    @NotBlank(message = "{course.courseTimeStart.required}")
    @Pattern(regexp = "(0[9]|1[0-7]):00", message = "{course.courseTimeStart.invalid}")
    private String courseTimeStart;  			// 시작 시간 (HH:00 형식)
    
    @NotBlank(message = "{course.courseTimeEnd.required}")
    @Pattern(regexp = "(0[9]|1[0-7]):50", message = "{course.courseTimeEnd.invalid}")
    private String courseTimeEnd;    			// 종료 시간 (HH:50 형식)

}