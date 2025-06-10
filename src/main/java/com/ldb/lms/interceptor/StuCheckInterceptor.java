package com.ldb.lms.interceptor;

import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

@Component
public class StuCheckInterceptor implements HandlerInterceptor{

	@Override
    public boolean preHandle(HttpServletRequest request,
                             HttpServletResponse response,
                             Object handler) throws Exception {
    	
    	/*인자 false의 의미:
    	false를 전달하면, 기존 세션이 존재하지 않는 경우 새 세션을 생성하지 않고 null을 반환*/
        HttpSession session = request.getSession(false);
        
        
        if (session == null || session.getAttribute("login") == null) {
            response.sendRedirect("/?isLogin=false");  // 로그인 안되었으면 홈으로 리다이렉트
            return false; // 컨트롤러 실행 안 함
        }
        String position = (String)session.getAttribute("login");
       if(position.contains("P")) {
    	   response.sendRedirect("/?isLogin=false");
    	   return false;
       }
        return true; // 학생이라면 정상실행
    }
	

}
