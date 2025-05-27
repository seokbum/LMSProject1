package com.ldb.lms.controller.board;

import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.ldb.lms.dto.board.notice.NoticePaginationDto;
import com.ldb.lms.dto.board.notice.NoticeSearchDto;
import com.ldb.lms.service.board.NoticeService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequestMapping("/api/notice")
@RequiredArgsConstructor
public class NoticeApiController {

    private final NoticeService noticeService;

    @GetMapping
    public ResponseEntity<Map<String, Object>> getNotices(
            @ModelAttribute NoticeSearchDto searchDto,
            @ModelAttribute NoticePaginationDto pageDto) {
        log.info("getNotices: /api/notice, searchDto: {}, pageDto: {}", searchDto, pageDto);
        return ResponseEntity.ok(noticeService.listNotice(searchDto, pageDto));
    }
}