package com.ldb.lms.aop;

import org.aspectj.lang.annotation.AfterThrowing;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;

import com.ldb.lms.exception.CourseRegistrationException;

import lombok.extern.slf4j.Slf4j;

@Aspect
@Component
@Slf4j
public class ExceptionHandlingAspect {
	
	@AfterThrowing(
			pointcut = "execution(* com.ldb.lms..*(..))",
			throwing = "ex"
	)
	public void logCourseRegistrationException(CourseRegistrationException ex) {
        log.error("CourseRegistrationException 발생: {}", ex.getMessage());
    }
}























