package com.ldb.lms.controller.board;

import java.util.Map;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.ldb.lms.dto.board.notice.NoticeDto;
import com.ldb.lms.dto.board.notice.NoticePaginationDto;
import com.ldb.lms.dto.board.notice.NoticeSearchDto;

import com.ldb.lms.service.board.NoticeService;

@Controller
@RequestMapping("/notice")
public class NoticeController {

    private final NoticeService noticeService;
	
	public NoticeController(NoticeService noticeService) {
		this.noticeService = noticeService; 
	}
	
	@GetMapping("getNotices")
	public String listNotice(
			@ModelAttribute NoticeSearchDto searchDto,
			@ModelAttribute NoticePaginationDto pageDto,
			@RequestParam(value = "pageNum", defaultValue = "1") int pageNum,
			Model model) {
		pageDto.setCurrentPage(pageNum);
		Map<String, Object> response = noticeService.listNotice(searchDto, pageDto);
		model.addAttribute("notices",response.get("notices"));
		model.addAttribute("pagination",response.get("pagination"));
		model.addAttribute("searchDto",searchDto);
		model.addAttribute("today", new java.util.Date());
		return "board/notice/getNotices";
	}
	
	@GetMapping("createNotice")
	public String createNotice() {
		return "board/notice/createNotice";
	}
	
	@PostMapping("write")
	public String wrtieNotice(
			@ModelAttribute NoticeDto noticeDto) {
		return null;
	}
}
