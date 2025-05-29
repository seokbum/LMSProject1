package com.ldb.lms.controller.board;

import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import com.ldb.lms.dto.board.notice.NoticeDto;
import com.ldb.lms.dto.board.notice.NoticePaginationDto;
import com.ldb.lms.dto.board.notice.NoticeSearchDto;
import com.ldb.lms.service.board.NoticeService;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
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
        return ResponseEntity.ok(noticeService.listNotice(searchDto, pageDto));
    }
    
    @PostMapping("uploadImage")
    public ResponseEntity<Map<String, String>> uploadImage(
            @RequestParam("file") MultipartFile file,
            HttpServletRequest request) {
        return noticeService.handleImageUpload(file, request);
    }

    @PostMapping("write")
    public ResponseEntity<Map<String, String>> writeNotice(
            @RequestPart("notice") NoticeDto noticeDto,
            @RequestPart(value = "file", required = false) MultipartFile file,
            HttpServletRequest request,
            HttpSession session) {
        return noticeService.handleWriteNotice(noticeDto, file, request, session);
    }
    
    @PostMapping("update")
    public ResponseEntity<Map<String, String>> updateNotice(
            @RequestPart("notice") NoticeDto noticeDto,
            @RequestPart(value = "file", required = false) MultipartFile file,
            HttpServletRequest request,
            HttpSession session) {
        return noticeService.handleUpdateNotice(noticeDto, file, request, session);
    }
}