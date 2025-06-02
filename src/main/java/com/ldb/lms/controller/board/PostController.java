package com.ldb.lms.controller.board;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.ldb.lms.dto.board.post.PostPaginationDto;
import com.ldb.lms.dto.board.post.PostSearchDto;
import com.ldb.lms.service.board.PostService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;


@Slf4j
@Controller
@RequestMapping("/post")
@RequiredArgsConstructor
public class PostController {
	
	private final PostService postService;
	
	@GetMapping("getPosts")
	public String getPosts(
			@ModelAttribute PostSearchDto searchDto,
			@RequestParam(value = "pageNum", defaultValue = "1") int pageNum,
			Model model) {
		PostPaginationDto pageDto = new PostPaginationDto();
		pageDto.setCurrentPage(pageNum);
		postService.populatePostsModel(searchDto, pageDto, model);
		return "board/post/getPosts";
	}
}