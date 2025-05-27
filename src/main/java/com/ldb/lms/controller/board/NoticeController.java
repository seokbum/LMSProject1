package com.ldb.lms.controller.board;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import com.ldb.lms.dto.board.notice.NoticeDto;
import com.ldb.lms.dto.board.notice.NoticePaginationDto;
import com.ldb.lms.dto.board.notice.NoticeSearchDto;
import com.ldb.lms.service.board.NoticeService;
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
        log.info("getNotices: searchDto: {}, pageDto: {}", searchDto, pageDto);
        model.addAttribute("notices", noticeService.listNotice(searchDto, pageDto).get("notices"));
        model.addAttribute("pagination", noticeService.listNotice(searchDto, pageDto).get("pagination"));
        model.addAttribute("searchDto", searchDto);
        return "board/notice/getNotices";
    }

    @GetMapping("createNotice")
    public String createNotice(
            @SessionAttribute(value = "professorId", required = false) String professorId) {
        log.info("createNotice: professorId: {}", professorId);
        if (professorId == null) {
            professorId = "P001"; // 하드코딩
            log.warn("createNotice: professorId가 null입니다. 임시로 P001로 설정합니다. 실제 운영 환경에서는 인증된 사용자 ID를 사용해야 합니다.");
        }
        return "board/notice/createNotice"; 
    }

    @PostMapping("write")
    public String writeNotice(
            @ModelAttribute NoticeDto noticeDto,
            @RequestParam("noticeFile") MultipartFile file,
            @SessionAttribute(value = "professorId", required = false) String professorId,
            Model model) {
        log.info("writeNotice: noticeDto: {}, professorId: {}", noticeDto, professorId);
        if (professorId == null) {
            professorId = "P001"; // 하드코딩
            log.warn("writeNotice: professorId가 null입니다. 임시로 P001로 설정합니다. 실제 운영 환경에서는 인증된 사용자 ID를 사용해야 합니다.");
        }
        try {
            noticeService.saveNotice(noticeDto, file, professorId);
            return "redirect:/notice/getNotices";
        } catch (Exception e) {
            log.error("writeNotice: 공지사항 등록 실패", e);
            model.addAttribute("msg", "공지사항 등록 실패: " + e.getMessage());
            model.addAttribute("noticeDto", noticeDto);
            return "board/notice/createNotice"; 
        }
    }

    @PostMapping("uploadImage")
    @ResponseBody
    public Map<String, String> uploadImage(@RequestParam("file") MultipartFile file) {
        Map<String, String> response = new HashMap<>();
        try {
            String uploadDir = "src/main/resources/static/uploads/"; 
            File dir = new File(uploadDir);
            if (!dir.exists()) dir.mkdirs();
            String fileName = UUID.randomUUID().toString() + "_" + file.getOriginalFilename();
            File dest = new File(uploadDir + fileName);
            file.transferTo(dest);
            response.put("url", "/uploads/" + fileName); 
        } catch (Exception e) {
            log.error("uploadImage: 이미지 업로드 실패", e);
            response.put("error", "이미지 업로드 실패");
        }
        return response;
    }
}