package com.ldb.lms.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import com.ldb.lms.interceptor.LoginCheckInterceptor;

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
                .excludePathPatterns("/", "/login", "/css/**", "/js/**");
    }
}
