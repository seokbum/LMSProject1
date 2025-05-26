package com.ldb.lms.service.board;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;

import com.ldb.lms.dto.board.notice.NoticeDto;
import com.ldb.lms.dto.board.notice.NoticeSearchDto;
import com.ldb.lms.dto.board.notice.NoticePaginationDto;
import com.ldb.lms.mapper.board.NoticeMapper;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class NoticeService {
	
	private final NoticeMapper noticeMapper; 
	
	public Map<String, Object> listNotice(NoticeSearchDto searchDto, NoticePaginationDto pageDto) {
        Integer pageSize = pageDto.getItemsPerPage() != null ? pageDto.getItemsPerPage() : 10;
        Integer currentPage = pageDto.getCurrentPage() != null ? pageDto.getCurrentPage() : 1;
        Integer offset = (currentPage - 1) * pageSize;
        Integer totalRows = noticeMapper.countNotices(searchDto);
        Integer totalPages = (int) Math.ceil((double) totalRows / pageSize);

        int pageBlock = 5;
        int startPage = ((currentPage - 1) / pageBlock) * pageBlock + 1;
        int endPage = Math.min(startPage + pageBlock - 1, totalPages);

        pageDto.setItemsPerPage(pageSize);
        pageDto.setCurrentPage(currentPage);
        pageDto.setTotalRows(totalRows);
        pageDto.setTotalPages(totalPages);
        pageDto.setOffset(offset);
        pageDto.setStartPage(startPage);
        pageDto.setEndPage(endPage);

        Map<String, Object> param = new HashMap<>();
        param.put("searchDto", searchDto);
        param.put("pageDto", pageDto);

        List<NoticeDto> notices = noticeMapper.listNotice(param);

        Map<String, Object> response = new HashMap<>();
        response.put("notices", notices);
        response.put("pagination", pageDto);
        response.put("searchDto", searchDto);
        return response;
    }
}