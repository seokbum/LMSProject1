package com.ldb.lms.interceptor;

import jakarta.servlet.http.HttpServletRequest;

import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

@Component
public class LoginCheckInterceptor implements HandlerInterceptor {
    @Override
    public boolean preHandle(HttpServletRequest request,
                             HttpServletResponse response,
                             Object handler) throws Exception {
    	
        HttpSession session = request.getSession(false);
        
        // TODO: 인덱스로 갔을때 alert로 로그인안되어있는거 알려줘야함
        if (session == null || session.getAttribute("login") == null) {
            response.sendRedirect("/?isLogin=false");  // 로그인 안되었으면 홈으로 리다이렉트
            return false; // 컨트롤러 실행 안 함
        }
        return true; // 로그인 되어 있으면 정상 진행
    }
}