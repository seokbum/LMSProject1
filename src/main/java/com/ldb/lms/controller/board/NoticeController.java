package com.ldb.lms.controller.board;

import com.ldb.lms.dto.board.notice.NoticePaginationDto;
import com.ldb.lms.dto.board.notice.NoticeSearchDto;
import com.ldb.lms.service.board.NoticeService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

@Controller
@RequestMapping("/notice")
@RequiredArgsConstructor
public class NoticeController {

    private final NoticeService noticeService;

    @GetMapping("getNotices")
    public String getNotices(
            @ModelAttribute NoticeSearchDto searchDto,
            @RequestParam(value = "pageNum", defaultValue = "1") int pageNum,
            Model model,
            HttpSession session) {
        
        NoticePaginationDto pageDto = new NoticePaginationDto();
        pageDto.setCurrentPage(pageNum);
        noticeService.populateNoticesModel(searchDto, pageDto, model, session);
        return "board/notice/getNotices";
    }

    @GetMapping("getNoticeDetail")
    public String getNoticeDetail(
            @RequestParam("noticeId") String noticeId,
            Model model,
            HttpSession session) {
        
        noticeService.populateNoticeDetail(noticeId, model, session);
        return "board/notice/getNoticeDetail";
    }
    
    @GetMapping("createNotice")
    public String showCreateNotice(Model model, HttpSession session) {
        noticeService.prepareCreateNotice(session, model);
        return "board/notice/createNotice";
    }

    @GetMapping("updateNotice")
    public String showUpdateNotice(
            @RequestParam("noticeId") String noticeId,
            Model model,
            HttpSession session) {
        
        noticeService.prepareUpdateNotice(noticeId, session, model);
        return "board/notice/updateNotice";
    }

    @GetMapping("deleteNotice")
    public String showDeleteNotice(
            @RequestParam("noticeId") String noticeId,
            Model model,
            HttpSession session) {
        
        noticeService.prepareDeleteNotice(noticeId, session, model);
        return "board/notice/deleteNotice";
    }
    
    @GetMapping("download")
    public void downloadFile(
            @RequestParam("filePath") String filePath,
            HttpServletResponse response,
            HttpServletRequest request) throws Exception {
        
        noticeService.handleFileDownload(filePath, response, request);
    }
}