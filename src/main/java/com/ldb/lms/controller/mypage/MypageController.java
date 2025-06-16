package com.ldb.lms.controller.mypage;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.ldb.lms.dto.mypage.DeleteUserDto;
import com.ldb.lms.dto.mypage.FindIdDto;
import com.ldb.lms.dto.mypage.FindPwDto;
import com.ldb.lms.dto.mypage.RegisterUserDto;
import com.ldb.lms.dto.mypage.UpdateInfoDto;
import com.ldb.lms.dto.mypage.UpdatePwDto;
import com.ldb.lms.service.mypage.MypageService;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;


@Controller
@RequestMapping("/mypage")
@RequiredArgsConstructor // final이 붙은 필드이용 생성자 자동생성
public class MypageController {

	private final MypageService mypageService;

	@GetMapping("doLogin")
	public String callDoLogin(HttpServletRequest request) {
		// 로그인 상태로 doLogin에 접근 불가능
		if (mypageService.loginChk(request)) {
			return "error";
		}
		return "mypage/doLogin";
	}

	@PostMapping("login")
	public String login(@RequestParam String id, @RequestParam String password, HttpServletRequest request) {

		if (mypageService.login(id, password, request)) {
			// 로그인성공시 메인페이지로
			return "redirect:/";
		} else {
			return "mypage/doLogin";
		}

	}

	@GetMapping("logout")
	public String callLogout(HttpServletRequest request) {
		mypageService.logout(request);
		// 주소를 모두초기화하고 doLogin 페이지로보냄
		return "redirect:/mypage/doLogin";
	}

	@GetMapping("findId")
	public String callFindId() {
		return "mypage/findId";
	}

	@PostMapping("findIdProcess")
	public String findIdProcess(@ModelAttribute FindIdDto dto, HttpServletRequest request) {
		mypageService.findId(dto, request);
		return "mypage/findId";
	}

	@GetMapping("findPw")
	public String callFindPw() {
		return "mypage/findPw";
	}

	@PostMapping("findPwProcess")
	public String findPwProcess(@ModelAttribute FindPwDto dto, HttpServletRequest request) {
		/*
		 * if(mypageService.findPwProcess(dto,request)) { return "mypage/updatePw"; }
		 */
		mypageService.findPwProcess(dto, request);
		return "mypage/findPw";
	}

	@PostMapping("updatePw")
	public String callUpdatePw() {
		return "mypage/updatePw";
	}

	@PostMapping("changePw")
	public String callChangePw(@ModelAttribute UpdatePwDto dto, HttpServletRequest request) {
		System.out.println("dto :::" + dto);
		mypageService.changePw(dto, request);
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
	public String picture(HttpServletRequest request) {
		mypageService.picture(request);
		return "mypage/picture";
	}

	@PostMapping("registerNumChk")
	public String registerNumChk(HttpServletRequest request, @ModelAttribute RegisterUserDto dto) {
		System.out.println("registerNumChk(DTO) : " + dto);
		mypageService.registerNumChk(dto, request);
		return "mypage/registerNumChk";
	}

	@PostMapping("registerSuccess")
	public String registerSuccess(HttpServletRequest request) {	
		mypageService.registerSuccess(request);
		return "mypage/registerUser";
	}

	@GetMapping("userInfo")
	public String getUserInfo(HttpServletRequest request) {
		return "mypage/userInfo";
	}

	@GetMapping("updateEmail")
	public String updateEmail(HttpServletRequest request) {
		return "mypage/updateEmail";
	}

	@GetMapping("updatePhone")
	public String updatePhone(HttpServletRequest request) {
		return "mypage/updatePhone";
	}

	@PostMapping("userUpdate")
	public String userUpdate(HttpServletRequest request, @ModelAttribute UpdateInfoDto dto) {
		String id = (String) request.getSession().getAttribute("login");
		dto.setId(id);
		if (!mypageService.userUpdate(dto, request)) {
			request.setAttribute("msg", "변경 실패");
		} else {
			request.setAttribute("msg", "변경 성공");
		}
		return "mypage/userInfo";
	}

	@GetMapping("deleteUser")
	public String callDeleteUser(HttpServletRequest request) {
		mypageService.getDeptAll(request);
		return "mypage/deleteUser";
	}

	@PostMapping("deleteUser")
	public String postDeleteUser(HttpServletRequest request, @ModelAttribute DeleteUserDto dto) {
		mypageService.deleteUser(request, dto);
		return "mypage/deleteUser";
	}

	@GetMapping("getCourseTimetable")
	public String postDeleteUser() {
		return "mypage/getCourseTimetable";
	}
	
	@GetMapping("getCourseScores")
	public String getCourseScores (HttpServletRequest request) {
		String id = (String)request.getSession().getAttribute("login");
		if(mypageService.getScore(id,request)) {
			return "mypage/getCourseScores";	
		}
		else {
			return "error";
		}
		
	}
	

}
