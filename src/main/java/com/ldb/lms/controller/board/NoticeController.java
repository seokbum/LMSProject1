package com.ldb.lms.controller.board;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import com.ldb.lms.dto.board.notice.NoticeDto;
import com.ldb.lms.dto.board.notice.NoticePaginationDto;
import com.ldb.lms.dto.board.notice.NoticeSearchDto;
import com.ldb.lms.service.board.NoticeService;

import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Slf4j
@Controller
@RequestMapping("/notice")
@RequiredArgsConstructor
public class NoticeController {

    private final NoticeService noticeService;

    @GetMapping("getNotices")
    public String getNotices(
            @ModelAttribute NoticeSearchDto searchDto,
            @ModelAttribute NoticePaginationDto pageDto,
            Model model) {
        log.info("getNotices called with searchDto: {}, pageDto: {}", searchDto, pageDto);
        model.addAttribute("notices", noticeService.listNotice(searchDto, pageDto).get("notices"));
        model.addAttribute("pagination", noticeService.listNotice(searchDto, pageDto).get("pagination"));
        model.addAttribute("searchDto", searchDto);
        return "board/notice/getNotices";
    }

    @GetMapping("createNotice")
    public String showcreateNotice(
            @SessionAttribute(value = "professorId", required = false) String professorId) {
        if (professorId == null) {
            professorId = "P001"; // 하드코딩
        }
        return "board/notice/createNotice"; 
    }
    

    @PostMapping("write")
    public String writeNotice(
            @ModelAttribute NoticeDto noticeDto,
            BindingResult bindingResult,
            @RequestParam(value = "noticeFile", required = false) MultipartFile file,
            @SessionAttribute(value = "professorId", required = false) String professorId,
            Model model,
            HttpSession session) { // HttpSession 추가로 세션 확인
        log.info("writeNotice called with noticeDto: {}, professorId: {}", noticeDto, professorId);
        if (professorId == null) {
            professorId = "P001"; // 하드코딩 경고: 실제 환경에서는 인증 로직 필요
            log.warn("professorId is null, set to default: {}", professorId);
        }
        try {
            if (file != null && !file.isEmpty()) {
                String uploadDir = "src/main/resources/static/uploads/";
                File dir = new File(uploadDir);
                if (!dir.exists()) {
                    dir.mkdirs();
                    log.info("Directory created: {}", dir.getAbsolutePath());
                }
                String fileName = UUID.randomUUID().toString() + "_" + file.getOriginalFilename();
                File dest = new File(uploadDir + fileName);
                file.transferTo(dest);
                noticeDto.setNoticeFile("/uploads/" + fileName);
            } else {
                noticeDto.setNoticeFile(null);
            }
            if (bindingResult.hasErrors()) {
                log.warn("Binding errors: {}", bindingResult.getAllErrors());
            }
            noticeService.saveNotice(noticeDto, file, professorId);
            log.info("Notice saved successfully, redirecting to getNotices");
        } catch (Exception e) {
            log.error("공지사항 등록 실패: ", e);
            model.addAttribute("error", "공지사항 저장 중 오류 발생했습니다. 상세: " + e.getMessage());
            return "board/notice/createNotice";
        }
        return "redirect:/notice/getNotices"; // 리다이렉트 확인
    }

    
}