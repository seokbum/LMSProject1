package com.ldb.lms.service.board;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;
import com.ldb.lms.dto.board.notice.NoticeDto;
import com.ldb.lms.dto.board.notice.NoticePaginationDto;
import com.ldb.lms.dto.board.notice.NoticeSearchDto;
import com.ldb.lms.mapper.board.NoticeMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import java.io.File;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class NoticeService {

    private final NoticeMapper noticeMapper;

    public Map<String, Object> listNotice(NoticeSearchDto searchDto, NoticePaginationDto pageDto) {
        log.info("listNotice: searchDto: {}, pageDto: {}", searchDto, pageDto);
        Integer pageSize = pageDto.getItemsPerPage() != null ? pageDto.getItemsPerPage() : 10;
        Integer currentPage = pageDto.getCurrentPage() != null ? pageDto.getCurrentPage() : 1;
        Integer offset = (currentPage - 1) * pageSize;
        Integer totalRows = noticeMapper.countNotices(searchDto);
        log.info("listNotice: totalRows: {}", totalRows);
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
        log.info("listNotice: notices size: {}", notices.size());
        Map<String, Object> response = new HashMap<>();
        response.put("notices", notices);
        response.put("pagination", pageDto);
        response.put("searchDto", searchDto);
        return response;
    }

    @Transactional
    public void saveNotice(NoticeDto noticeDto, MultipartFile file, String professorId) throws Exception {
        log.info("saveNotice called with noticeDto: {}, professorId: {}", noticeDto, professorId);
        if (!StringUtils.hasText(noticeDto.getNoticeTitle())) {
            throw new IllegalArgumentException("제목은 필수입니다.");
        }
        if (!StringUtils.hasText(noticeDto.getNoticeContent())) {
            throw new IllegalArgumentException("내용은 필수입니다.");
        }
        if (!StringUtils.hasText(noticeDto.getNoticePassword())) {
            throw new IllegalArgumentException("비밀번호는 필수입니다.");
        }
        // notice_id 생성 로직
        if (noticeDto.getNoticeId() == null || noticeDto.getNoticeId().isEmpty()) {
            String lastId = noticeMapper.getLastNoticeId(); 
            String newId = generateNextNoticeId(lastId);
            noticeDto.setNoticeId(newId);
            log.info("Generated new noticeId: {}", newId);
        }
        noticeDto.setWriterId(professorId);
        log.info("Setting writerId to: {}", professorId);
        try {
            noticeMapper.insertNotice(noticeDto);
            log.info("Notice inserted successfully, noticeId: {}", noticeDto.getNoticeId());
        } catch (Exception e) {
            log.error("saveNotice: 공지사항 저장 실패, noticeId: {}, error: {}", noticeDto.getNoticeId(), e.getMessage());
            throw new RuntimeException("공지사항 저장 중 오류 발생", e);
        }
    }

    // 새 notice_id 생성 메서드
    private String generateNextNoticeId(String lastId) {
        if (lastId == null || !lastId.startsWith("N") || lastId.length() != 5) {
            return "N0001"; 
        }
        try {
            int num = Integer.parseInt(lastId.substring(1)); 
            num++; 
            return String.format("N%04d", num); 
        } catch (NumberFormatException e) {
            log.warn("Invalid lastId format: {}, returning N0001", lastId);
            return "N0001";
        }
    }
}