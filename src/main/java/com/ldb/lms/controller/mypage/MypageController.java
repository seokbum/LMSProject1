package com.ldb.lms.controller.mypage;

import java.util.Map;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.ldb.lms.dto.mypage.RegisterUserDto;
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
	public String callDoLogin (HttpServletRequest request) {
		if(mypageService.loginChk(request)) {
			return "error";
		}
		return "mypage/doLogin";
	}
	
	//너무간단한데 이 1문장도 서비스에서 처리??
	@GetMapping("logout")
	public String callLogout(HttpServletRequest request) {
		mypageService.logout(request);
		return "mypage/doLogin";
	}
	
	
	@PostMapping("login")
	public String login(@RequestParam String id , @RequestParam String password ,HttpServletRequest request) {
		Map<String,String> login = mypageService.login(id,password,request);
		System.out.println("login"+login);
		return login==null?"redirect:mypage/doLogin":"redirect:/";
	}
	
	
	@GetMapping("registerUser")
	public String callRegisterUser(HttpServletRequest request) {
		mypageService.getDeptAll(request);
		return "mypage/registerUser";
	}
	@GetMapping("registerImg")
	public String callRegisterImg(HttpServletRequest request) {
		return "mypage/registerImg";
	}
	
	@PostMapping("picture")
	public String picture(HttpServletRequest request){
		mypageService.picture(request);
		return "mypage/picture";
	}
	
	@PostMapping("registerNumChk")
	public String registerNumChk(HttpServletRequest request,@ModelAttribute RegisterUserDto dto){
		System.out.println("registerNumChk(DTO) : "+dto);
		mypageService.registerNumChk(dto,request);
		return "mypage/registerNumChk";
	}
	
	@PostMapping("registerSuccess")
	public String registerSuccess(HttpServletRequest request){
		if(mypageService.registerSuccess(request)) {
			return "mypage/doLogin";
		}
		return "mypage/registerUser";
	}
	
	
	
	
	
	

}
