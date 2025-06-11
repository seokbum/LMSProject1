package com.ldb.lms.interceptor;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

@Component
public class ProCheckInterceptor implements HandlerInterceptor{

	@Override
    public boolean preHandle(HttpServletRequest request,
                             HttpServletResponse response,
                             Object handler) throws Exception {
    	/*인자 false의 의미:
    	false를 전달하면, 기존 세션이 존재하지 않는 경우 새 세션을 생성하지 않고 null을 반환*/
       HttpSession session = request.getSession(false);        
       //세션null검증은 LogincheckInterceptor에서 진행하므로 굳이 로그인세션이 존재하는지 검증하지않는다
       
       String position = (String)session.getAttribute("login");
       if(!position.contains("P")) {    	   
    	   String msg = "교수만 접근 가능합니다";
    	   
    	   //한국어메시지를get방식으로 그냥넘기려하면 문제가생김! URLEncoder를 이용해 UTF-8로 인코딩
    	   response.sendRedirect("/alert?"
    	   		+ "url=/"
    	   		+ "&"
    	   		+ "msg="+URLEncoder.encode(msg, StandardCharsets.UTF_8));
    	   return false;
       }
        return true; // 교수면 정상실행
    }
	

}
