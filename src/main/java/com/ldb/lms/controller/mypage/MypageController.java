package com.ldb.lms.controller.mypage;

import java.util.Map;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.ldb.lms.service.mypage.MypageService;

import jakarta.servlet.http.HttpServletRequest;



@Controller
@RequestMapping("/mypage")
public class MypageController {
	
	private final MypageService mypageService;

	
	public MypageController(MypageService mypageService) {
		this.mypageService = mypageService;
	}
	
	@GetMapping("doLogin")
	public String callDoLogin () {
		//mypageService.loginChk();
		return "mypage/doLogin";
	}
	
	@PostMapping("login")
	public String login(@RequestParam String id , @RequestParam String password ,HttpServletRequest request) {
		Map<String,String> login = mypageService.login(id,password,request);
		if(login==null) {
			return "mypage/doLogin";
		}
		else {
			return "index";
		}
		
		
	}
	
	
	
	

}
