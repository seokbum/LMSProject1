package com.ldb.lms.controller.mypage;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.ldb.lms.service.mypage.MypageService;

@Controller
@RequestMapping("/mypage")
public class MypageController {
	
	private final MypageService mypageService;

	
	public MypageController(MypageService mypageService) {
		this.mypageService = mypageService;
	}
	
	@GetMapping("doLogin")
	public String callDoLogin () {
		System.out.println("안녕하세요 doLogin");
		return "mypage/doLogin";
	}
	
	
	
	

}
