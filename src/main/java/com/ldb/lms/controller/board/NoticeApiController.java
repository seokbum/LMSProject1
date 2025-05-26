package com.ldb.lms.controller.board;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.ldb.lms.service.board.NoticeService;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequestMapping("/api/notice")
public class NoticeApiController {

    private final NoticeService noticeService;

	public NoticeApiController(NoticeService noticeService) {
		this.noticeService = noticeService;
	}
	
	@PostMapping("getNotice")
	
	
	
}
