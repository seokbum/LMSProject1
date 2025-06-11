package com.ldb.lms.controller.board;

import com.ldb.lms.dto.ApiResponseDto;
import com.ldb.lms.dto.board.notice.NoticeDto;
import com.ldb.lms.service.board.NoticeService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/notice")
@RequiredArgsConstructor
public class NoticeApiController {

    private final NoticeService noticeService;

    private ResponseEntity<ApiResponseDto<Object>> createValidationErrorResponse(BindingResult bindingResult) {
        Map<String, String> errors = new HashMap<>();
        for (FieldError error : bindingResult.getFieldErrors()) {
            errors.put(error.getField(), error.getDefaultMessage());
        }
        return ResponseEntity.badRequest().body(new ApiResponseDto<>(false, "입력 값을 확인해주세요.", errors));
    }

    @PostMapping("write")
    public ResponseEntity<?> writeNotice(
            @Valid @RequestPart("notice") NoticeDto noticeDto,
            BindingResult bindingResult,
            @RequestPart(value = "file", required = false) MultipartFile file,
            HttpServletRequest request,
            HttpSession session) {
        
        if (bindingResult.hasErrors()) {
            return createValidationErrorResponse(bindingResult);
        }
        return noticeService.handleWriteNotice(noticeDto, file, request, session);
    }
    
    @PostMapping("update")
    public ResponseEntity<?> updateNotice(
            @Valid @RequestPart("notice") NoticeDto noticeDto,
            BindingResult bindingResult,
            @RequestPart(value = "file", required = false) MultipartFile file,
            HttpServletRequest request,
            HttpSession session) {
        
        if (bindingResult.hasErrors()) {
            return createValidationErrorResponse(bindingResult);
        }
        return noticeService.handleUpdateNotice(noticeDto, file, request, session);
    }
    
    @PostMapping("delete/{noticeId}") 
    public ResponseEntity<ApiResponseDto<String>> deleteNotice(
            @PathVariable String noticeId,
            @RequestParam("password") String password,
            HttpServletRequest request,
            HttpSession session) {
        try {
            noticeService.deleteNotice(noticeId, noticeService.getCurrentUserId(session), password, request);
            return ResponseEntity.ok(new ApiResponseDto<>(true, "공지사항이 삭제되었습니다.", "/notice/getNotices"));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(new ApiResponseDto<>(false, e.getMessage(), null));
        }
    }
}