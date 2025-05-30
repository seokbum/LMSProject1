package com.ldb.lms.controller.board;

import com.ldb.lms.dto.board.notice.NoticeDto;
import com.ldb.lms.dto.board.notice.NoticePaginationDto;
import com.ldb.lms.dto.board.notice.NoticeSearchDto;
import com.ldb.lms.service.board.NoticeService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.util.Map;

@Slf4j
@Controller
@RequestMapping("/notice")
@RequiredArgsConstructor
public class NoticeController {

    private final NoticeService noticeService;

    @GetMapping("/download")
    public void downloadFile(
            @RequestParam("filePath") String filePath,
            HttpServletResponse response,
            HttpServletRequest request) throws Exception {
        noticeService.handleFileDownload(filePath, response, request);
    }

    @GetMapping("getNotices")
    public String getNotices(
            @ModelAttribute NoticeSearchDto searchDto,
            @RequestParam(value = "pageNum", defaultValue = "1") int pageNum,
            @ModelAttribute NoticePaginationDto pageDto,
            Model model) {
    	pageDto.setCurrentPage(pageNum);
        noticeService.populateNoticesModel(searchDto, pageDto, model);
        return "board/notice/getNotices";
    }

    @GetMapping("createNotice")
    public String showCreateNotice(Model model, HttpSession session) {
        noticeService.prepareCreateNotice(session, model);
        return "board/notice/createNotice";
    }

    @GetMapping("getNoticeDetail")
    public String getNoticeDetail(
            @RequestParam("noticeId") String noticeId,
            Model model) {
        noticeService.populateNoticeDetail(noticeId, model);
        return "board/notice/getNoticeDetail";
    }

    @GetMapping("deleteNotice")
    public String showDeleteNotice(
            @RequestParam("noticeId") String noticeId,
            HttpSession session,
            Model model) {
        noticeService.prepareDeleteNotice(noticeId, session, model);
        return "board/notice/deleteNotice";
    }

    @PostMapping("delete")
    public String deleteNotice(
            @RequestParam("noticeId") String noticeId,
            @RequestParam("pass") String password,
            HttpSession session,
            Model model) {
        return noticeService.handleDeleteNotice(noticeId, password, session, model);
    }

    @GetMapping("updateNotice")
    public String showUpdateNotice(
            @RequestParam("noticeId") String noticeId,
            HttpSession session,
            Model model) {
        noticeService.prepareUpdateNotice(noticeId, session, model);
        return "board/notice/updateNotice";
    }

    @PostMapping("update")
    public String updateNotice(
            @ModelAttribute NoticeDto noticeDto,
            @RequestParam(value = "noticeFile", required = false) MultipartFile file,
            HttpServletRequest request,
            HttpSession session,
            Model model) {
        ResponseEntity<Map<String, String>> responseEntity = noticeService.handleUpdateNotice(noticeDto, file, request, session);
        Map<String, String> response = responseEntity.getBody();
        if (response != null && response.containsKey("redirectUrl")) {
            return "redirect:" + response.get("redirectUrl");
        } else {
            model.addAttribute("msg", response != null ? response.get("error") : "공지사항 수정 실패");
            return "board/notice/updateNotice";
        }
    }
}