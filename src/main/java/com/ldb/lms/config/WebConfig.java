package com.ldb.lms.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import com.ldb.lms.interceptor.LoginCheckInterceptor;
import com.ldb.lms.interceptor.ProCheckInterceptor;
import com.ldb.lms.interceptor.StuCheckInterceptor;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(new LoginCheckInterceptor())
                .addPathPatterns("/professors/**")
                .addPathPatterns("/api/learning_support/**")
                .addPathPatterns("/learning_support/**")
                .addPathPatterns("/mypage/userInfo")
                .addPathPatterns("/mypage/updatePhone")
                .addPathPatterns("/mypage/updateEmail")
                .addPathPatterns("/mypage/deleteUser")
                .addPathPatterns("/mypage/getCourseTimetable")
                .excludePathPatterns("/", "/login", "/css/**", "/js/**");
        
       
        //학생이 아니라면 false반환(학생만 접근가능)
        registry.addInterceptor(new StuCheckInterceptor())
        .addPathPatterns("/mypage/getCourseTimetable")
        .addPathPatterns("/learning_support/**")
        .addPathPatterns("/api/learning_support/**")
        .excludePathPatterns("/", "/login", "/css/**", "/js/**");
        
        //교수가 아니라면 false반환(교수만 접근가능)
        registry.addInterceptor(new ProCheckInterceptor())
        .addPathPatterns("/professors/**")
        .addPathPatterns("/notice/createNotice");
        
    }
}
