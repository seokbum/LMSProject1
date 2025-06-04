package com.ldb.lms.controller;




import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import com.ldb.lms.service.mypage.MypageService;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;


@Controller
@RequiredArgsConstructor
public class HomeController {
 
	private final MypageService mypageService;
	
    @GetMapping("/")
    public String home(HttpServletRequest request,  Model model) {
    	/*mypageService.index(request);
    	return "index";*/
    	if(mypageService.index(request)) {
    		return "index";
    	}
    	else {
    		return "redirect:mypage/doLogin";
    	}
        
    }

}