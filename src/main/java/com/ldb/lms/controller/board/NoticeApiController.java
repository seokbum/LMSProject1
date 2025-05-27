package com.ldb.lms.controller.board;

import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.ldb.lms.dto.board.notice.NoticePaginationDto;
import com.ldb.lms.dto.board.notice.NoticeSearchDto;
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
	
	@GetMapping("getNotices")
	public ResponseEntity<Map<String, Object>> listNotice(
			@ModelAttribute NoticeSearchDto searchDto,
			@ModelAttribute NoticePaginationDto pageDto
			){
		return ResponseEntity.ok(noticeService.listNotice(searchDto,pageDto));
	}
	
	
	
	
	
	
	
}
