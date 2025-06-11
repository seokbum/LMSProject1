package com.ldb.lms.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import com.ldb.lms.dto.board.notice.NoticeDto;
import com.ldb.lms.service.board.NoticeService;
import com.ldb.lms.service.mypage.MypageService;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;

import java.util.List;

@Controller
@RequiredArgsConstructor
public class HomeController {

    private final MypageService mypageService;
    private final NoticeService noticeService;

    @GetMapping("/")
    public String home(HttpServletRequest request, Model model) {
        if (mypageService.index(request)) {
            List<NoticeDto> recentNotices = noticeService.getRecentNotices();
            model.addAttribute("recentNotices", recentNotices);

            return "index";
        } else {
            request.setAttribute("msg", "로그인하세요");
            request.setAttribute("url", "mypage/doLogin");
            return "alert";
        }
    }
}