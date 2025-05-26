package com.ldb.lms.controller.board;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.ldb.lms.service.board.NoticeService;

@Controller
@RequestMapping("/notice")
public class NoticeController {

    private final NoticeService noticeService;
	
	public NoticeController(NoticeService noticeService) {
		this.noticeService = noticeService; 
	}
	
	@GetMapping("getNotice")
	public String callGetNotice() {
		return "board/notice/getNotice";
	}
}
