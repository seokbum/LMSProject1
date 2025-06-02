package com.ldb.lms.controller.mypage;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Random;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.ldb.lms.dto.mypage.FindIdDto;
import com.ldb.lms.dto.mypage.FindPwDto;
import com.ldb.lms.dto.mypage.RegisterUserDto;
import com.ldb.lms.dto.mypage.UpdatePwDto;
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
		//로그인 상태로 doLogin에 접근 불가능
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
		return login==null?"mypage/doLogin":"redirect:/";
	}
	
	@GetMapping("findId")
	public String callFindId() {
		return "mypage/findId";
	}
	
	@PostMapping("findIdProcess")
	public String findIdProcess(@ModelAttribute FindIdDto dto,HttpServletRequest request) {
		mypageService.findId(dto,request);
		return "mypage/findId";
	}
	
	@GetMapping("findPw")
	public String callFindPw() {
		return "mypage/findPw";
	}
	
	@PostMapping("findPwProcess")
	public String findPwProcess(@ModelAttribute FindPwDto dto,HttpServletRequest request) {
		if(mypageService.findPwProcess(dto,request)) {
			return "mypage/updatePw";
		}
		return "mypage/findPw";
	}
	
	@GetMapping("updatePw")
	public String callUpdatePw() {
		return "mypage/updatePw";
	}
	
	@PostMapping("changePw")
	public String callChangePw(@ModelAttribute UpdatePwDto dto,HttpServletRequest request) {
		mypageService.changePw(dto,request);
		return "mypage/updatePw";
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
		//어차피 회원가입성공 시 모든세션정보를 서비스 내에서 날림
		if(mypageService.registerSuccess(request)) {
			return "mypage/doLogin";
		}
		return "mypage/registerUser";
	}
	
	
	
	
	
	

}
