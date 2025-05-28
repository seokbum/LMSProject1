package com.ldb.lms.controller.board;

import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import com.ldb.lms.dto.board.notice.NoticePaginationDto;
import com.ldb.lms.dto.board.notice.NoticeSearchDto;
import com.ldb.lms.service.board.NoticeService;

import jakarta.servlet.http.HttpServletRequest;
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
    @ResponseBody
    public Map<String, String> uploadImage(@RequestParam("file") MultipartFile file, HttpServletRequest request) {
        Map<String, String> response = new HashMap<>();
        try {
            String filePath = noticeService.uploadImage(file, request);
            response.put("url", filePath);
        } catch (Exception e) {
            log.error("uploadImage: 이미지 업로드 실패", e);
            response.put("error", "이미지 업로드 실패");
        }
        return response;
    }
}