package com.ldb.lms.service.board;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;

import com.ldb.lms.dto.board.post.PostDto;
import com.ldb.lms.dto.board.post.PostPaginationDto;
import com.ldb.lms.dto.board.post.PostSearchDto;
import com.ldb.lms.mapper.board.PostMapper;

import org.springframework.ui.Model;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class PostService {
	
	private final PostMapper postMapper;

	public void populatePostsModel(PostSearchDto searchDto, PostPaginationDto pageDto, Model model) {
		// 공지사항 조회
		Map<String, Object> noticeParams = new HashMap<>();
		noticeParams.put("searchType", searchDto.getSearchType());
		noticeParams.put("searchKeyword", searchDto.getSearchKeyword());
		List<PostDto> notices = postMapper.listNotices(noticeParams);

		// 일반 게시물 조회 및 페이징
		Map<String, Object> result = listPost(searchDto, pageDto);
		
		model.addAttribute("notices", notices);
		model.addAttribute("posts", result.get("posts"));
		model.addAttribute("pagination", result.get("pagination"));
		model.addAttribute("searchDto", searchDto);
	}

	private Map<String, Object> listPost(PostSearchDto searchDto, PostPaginationDto pageDto) {
		Map<String, Object> response = new HashMap<>();
		
		Integer pageSize = pageDto.getItemsPerPage() != null ? pageDto.getItemsPerPage() : 10;
		Integer currentPage = pageDto.getCurrentPage() != null && pageDto.getCurrentPage() > 0 ? pageDto.getCurrentPage() : 1;
		Integer totalRows = postMapper.countPosts(searchDto);
		Integer totalPages = (int) Math.ceil((double) totalRows / pageSize);
		Integer offset = (currentPage - 1) * pageSize;
		
		if (totalPages == 0 && totalRows > 0) {
			totalPages = 1;
		} else if (totalPages == 0 && totalRows == 0) {
			totalPages = 1;
		}
		
		Integer pagesPerBlock = 5;
		Integer startPage = ((currentPage - 1) / pagesPerBlock) * pagesPerBlock + 1;
		Integer endPage = Math.min(startPage + pagesPerBlock - 1, totalPages);

		pageDto.setItemsPerPage(pageSize);
		pageDto.setCurrentPage(currentPage);
		pageDto.setTotalRows(totalRows);
		pageDto.setTotalPages(totalPages);
		pageDto.setOffset(offset);
		pageDto.setStartPage(startPage);
		pageDto.setEndPage(endPage);
		
		Map<String, Object> param = new HashMap<>();
		param.put("searchType", searchDto.getSearchType());
		param.put("searchKeyword", searchDto.getSearchKeyword());
		param.put("pageSize", pageSize);
		param.put("offset", offset);
		
		List<PostDto> posts = postMapper.listPost(param);
		response.put("posts", posts);
		response.put("pagination", pageDto);
		return response;
	}
}