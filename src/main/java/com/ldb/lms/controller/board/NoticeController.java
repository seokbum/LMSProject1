package com.ldb.lms.controller.board;

import com.ldb.lms.dto.board.notice.NoticeDto;
import com.ldb.lms.dto.board.notice.NoticePaginationDto;
import com.ldb.lms.dto.board.notice.NoticeSearchDto;
import com.ldb.lms.service.board.NoticeService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;

@Slf4j
@Controller
@RequestMapping("/notice")
@RequiredArgsConstructor
public class NoticeController {

    private final NoticeService noticeService;

    // 공지사항 목록 페이지 렌더링
    @GetMapping("getNotices")
    public String getNotices(
            @ModelAttribute NoticeSearchDto searchDto,
            @ModelAttribute NoticePaginationDto pageDto,
            Model model) {
        log.info("getNotices called with searchDto: {}, pageDto: {}", searchDto, pageDto);
        noticeService.populateNoticesModel(searchDto, pageDto, model);
        return "board/notice/getNotices";
    }

    // 공지사항 작성 페이지 렌더링
    @GetMapping("createNotice")
    public String showCreateNotice(Model model, HttpSession session) {
        String professorId = (String) session.getAttribute("professorId");
        if (professorId == null) {
            professorId = "P001"; // 하드코딩
        }
        noticeService.prepareCreateNotice(professorId, model);
        return "board/notice/createNotice";
    }

    // 공지사항 작성 처리
    @PostMapping("write")
    public String writeNotice(
            @ModelAttribute NoticeDto noticeDto,
            @RequestParam(value = "noticeFile", required = false) MultipartFile file,
            HttpSession session,
            HttpServletRequest request,
            Model model) {
        String professorId = (String) session.getAttribute("professorId");
        if (professorId == null) {
            professorId = "P001"; // 하드코딩
        }
        try {
            noticeService.saveNotice(noticeDto, file, professorId, request);
            return "redirect:/notice/getNotices";
        } catch (Exception e) {
            log.error("writeNotice: 공지사항 등록 실패", e);
            model.addAttribute("error", "공지사항 저장 중 오류 발생: " + e.getMessage());
            return "board/notice/createNotice";
        }
    }

    // 공지사항 상세 페이지 렌더링
    @GetMapping("getNoticeDetail")
    public String getNoticeDetail(
            @RequestParam("noticeId") String noticeId,
            Model model) {
        noticeService.populateNoticeDetail(noticeId, model);
        return "board/notice/getNoticeDetail";
    }

    // 공지사항 삭제 페이지 렌더링
    @GetMapping("deleteNotice")
    public String showDeleteNotice(
            @RequestParam("noticeId") String noticeId,
            HttpSession session,
            Model model) {
        String professorId = (String) session.getAttribute("professorId");
        if (professorId == null) {
            professorId = "P001"; // 하드코딩
        }
        noticeService.prepareDeleteNotice(noticeId, professorId, model);
        return "board/notice/deleteNotice";
    }

    // 공지사항 삭제 처리
    @PostMapping("delete")
    public String deleteNotice(
            @RequestParam("noticeId") String noticeId,
            @RequestParam("pass") String password,
            HttpSession session,
            Model model) {
        String professorId = (String) session.getAttribute("professorId");
        if (professorId == null) {
            professorId = "P001"; // 하드코딩
        }
        try {
            noticeService.deleteNotice(noticeId, professorId, password);
            return "redirect:/notice/getNotices";
        } catch (IllegalArgumentException e) {
            log.warn("deleteNotice: {}", e.getMessage());
            model.addAttribute("msg", e.getMessage());
            noticeService.populateNoticeDetail(noticeId, model);
            return "board/notice/deleteNotice";
        } catch (Exception e) {
            log.error("deleteNotice: 공지사항 삭제 실패, noticeId: {}", noticeId, e);
            model.addAttribute("msg", "공지사항 삭제 중 오류 발생: " + e.getMessage());
            noticeService.populateNoticeDetail(noticeId, model);
            return "board/notice/deleteNotice";
        }
    }
    
    @PostMapping("update")
    public String updateNotice(
            @ModelAttribute NoticeDto noticeDto,
            @RequestParam(value = "noticeFile", required = false) MultipartFile file,
            HttpSession session,
            HttpServletRequest request,
            Model model) {
        String professorId = (String) session.getAttribute("professorId");
        if (professorId == null) {
            professorId = "P001"; // 하드코딩
        }
        try {
            noticeService.updateNotice(noticeDto, file, professorId, request);
            return "redirect:/notice/getNoticeDetail?noticeId=" + noticeDto.getNoticeId();
        } catch (Exception e) {
            log.error("updateNotice: 공지사항 수정 실패", e);
            model.addAttribute("error", "공지사항 수정 중 오류 발생: " + e.getMessage());
            model.addAttribute("notice", noticeDto); // 수정 실패 시 데이터 유지
            return "board/notice/updateNotice";
        }
    }
    
    @GetMapping("updateNotice")
    public String showUpdateNotice(
            @RequestParam("noticeId") String noticeId,
            HttpSession session,
            Model model) {
        String professorId = (String) session.getAttribute("professorId");
        if (professorId == null) {
            professorId = "P001"; // 하드코딩
        }
        NoticeDto notice = noticeService.getNotice(noticeId);
        if (notice == null) {
            model.addAttribute("msg", "공지사항이 존재하지 않습니다.");
            return "board/notice/getNoticeDetail";
        }
        if (!notice.getWriterId().equals(professorId)) {
            model.addAttribute("msg", "수정 권한이 없습니다.");
            return "board/notice/getNoticeDetail";
        }
        model.addAttribute("notice", notice);
        return "board/notice/updateNotice";
    }
}