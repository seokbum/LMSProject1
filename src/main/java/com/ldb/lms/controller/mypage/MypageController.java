package com.ldb.lms.controller.mypage;

import java.util.Map;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.ldb.lms.dto.mypage.FindIdDto;
import com.ldb.lms.dto.mypage.FindPwDto;
import com.ldb.lms.dto.mypage.RegisterUserDto;
import com.ldb.lms.dto.mypage.UpdateInfoDto;
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
	

	@GetMapping("logout")
	public String callLogout(HttpServletRequest request) {
		mypageService.logout(request);
		//String path = request.getContextPath();
		System.out.println("Redirecting to /mypage/doLogin");
		return "redirect:/mypage/doLogin";		
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
		/*if(mypageService.findPwProcess(dto,request)) {
			return "mypage/updatePw";
		}*/
		mypageService.findPwProcess(dto,request);
		return "mypage/findPw";
	}
	
	@PostMapping("updatePw")
	public String callUpdatePw() {
		return "mypage/updatePw";
	}
	
	@PostMapping("changePw")
	public String callChangePw(@ModelAttribute UpdatePwDto dto,HttpServletRequest request) {
		System.out.println("dto :::"+dto);
		request.getSession().invalidate();//세션초기화
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
		/*
		if(mypageService.registerSuccess(request)) {
			return "mypage/doLogin";}
		return "mypage/registerUser";
		*/
		mypageService.registerSuccess(request);
		return "mypage/registerUser";
	}
	
	@GetMapping("userInfo")
	public String getUserInfo(HttpServletRequest request){
		return "mypage/userInfo";
	}
	
	@GetMapping("updateEmail")
	public String updateEmail(HttpServletRequest request){
		return "mypage/updateEmail";
	}
	
	@GetMapping("updatePhone")
	public String updatePhone(HttpServletRequest request){
		return "mypage/updatePhone";
	}
	
	@PostMapping("userUpdate")
	public String userUpdate(HttpServletRequest request
			, @ModelAttribute UpdateInfoDto dto){
		String id = (String)request.getSession().getAttribute("login");
		dto.setId(id);
		if(!mypageService.userUpdate(dto,request)) {
			request.setAttribute("msg", "변경 실패");
		}
		else {
			request.setAttribute("msg", "변경 성공");
		}
		return "mypage/userInfo";
	}
	
	
	
	
	

}
