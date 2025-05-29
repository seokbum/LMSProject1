package com.ldb.lms.service.board;

import com.ldb.lms.dto.board.notice.NoticeDto;
import com.ldb.lms.dto.board.notice.NoticePaginationDto;
import com.ldb.lms.dto.board.notice.NoticeSearchDto;
import com.ldb.lms.mapper.board.NoticeMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.ui.Model;
import org.springframework.web.multipart.MultipartFile;

import jakarta.servlet.http.HttpServletRequest;
import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class NoticeService {

    private final NoticeMapper noticeMapper;

    // 공지사항 목록 조회
    public Map<String, Object> listNotice(NoticeSearchDto searchDto, NoticePaginationDto pageDto) {

        Integer pageSize = pageDto.getItemsPerPage() != null ? pageDto.getItemsPerPage() : 10;
        Integer currentPage = pageDto.getCurrentPage() != null && pageDto.getCurrentPage() > 0 ? pageDto.getCurrentPage() : 1;
        Integer totalRows = noticeMapper.countNotices(searchDto);
        Integer totalPages = (int) Math.ceil((double) totalRows / pageSize);
        Integer offset = (currentPage - 1) * pageSize;

       
        if (totalPages == 0 && totalRows > 0) {
            totalPages = 1;
        } else if (totalPages == 0 && totalRows == 0) { 
            totalPages = 1; 
        }

        Integer pagesPerBlock = 5; 
        Integer startPage = ((currentPage - 1) / pagesPerBlock) * pagesPerBlock + 1;
        Integer endPage = Math.min(startPage + pagesPerBlock - 1, totalPages);

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

        Map<String, Object> response = new HashMap<>();
        response.put("notices", notices);
        response.put("pagination", pageDto);
        return response;
    }

    // 공지사항 목록 페이지 모델 설정
    public void populateNoticesModel(NoticeSearchDto searchDto, NoticePaginationDto pageDto, Model model) {
        Map<String, Object> result = listNotice(searchDto, pageDto);
        model.addAttribute("notices", result.get("notices"));
        model.addAttribute("pagination", result.get("pagination"));
        model.addAttribute("searchDto", searchDto);
    }

    // 공지사항 작성 페이지 준비
    public void prepareCreateNotice(String professorId, Model model) {
        if (professorId == null) {
            professorId = "P001"; // 테스트용 하드코딩
            log.warn("professorId is null, set to default: {}", professorId);
        }
        model.addAttribute("noticeDto", new NoticeDto());
    }

    // 공지사항 저장
    @Transactional
    public void saveNotice(NoticeDto noticeDto, MultipartFile file, String professorId, HttpServletRequest request) {
        if (!StringUtils.hasText(noticeDto.getNoticeTitle())) {
            log.error("saveNotice: 공지사항 제목이 누락되었습니다.");
            throw new IllegalArgumentException("공지사항 제목은 필수입니다.");
        }
        if (!StringUtils.hasText(professorId)) {
            log.error("saveNotice: 교수 ID가 유효하지 않습니다.");
            throw new SecurityException("사용자 정보를 확인할 수 없습니다.");
        }

        try {
            // 공지사항 ID 생성
            String lastNoticeId = noticeMapper.getLastNoticeId();
            long nextId = lastNoticeId != null ? Long.parseLong(lastNoticeId.replace("N", "")) + 1 : 1;
            String noticeId = "N" + nextId;
            noticeDto.setNoticeId(noticeId);
            noticeDto.setWriterId(professorId);

            // 파일 업로드 처리
            if (file != null && !file.isEmpty()) {
                String uploadDir = request.getServletContext().getRealPath("") + "/dist/assets/upload/";
                File dir = new File(uploadDir);
                if (!dir.exists()) {
                    dir.mkdirs();
                    log.info("Directory created: {}", dir.getAbsolutePath());
                }
                String fileName = UUID.randomUUID().toString() + "_" + file.getOriginalFilename();
                File dest = new File(uploadDir + fileName);
                file.transferTo(dest);
                noticeDto.setNoticeFile("/dist/assets/upload/" + fileName);
            } else {
                noticeDto.setNoticeFile(null);
            }

            noticeMapper.insertNotice(noticeDto);
            log.info("Notice saved: noticeId={}", noticeId);
        } catch (Exception e) {
            log.error("saveNotice: 공지사항 저장 실패", e);
            throw new RuntimeException("공지사항 저장 중 오류 발생: " + e.getMessage(), e);
        }
    }

    // 이미지 업로드 처리
    @Transactional
    public String uploadImage(MultipartFile file, HttpServletRequest request) {
        try {
            String uploadDir = request.getServletContext().getRealPath("") + "/dist/assets/upload/";
            File dir = new File(uploadDir);
            if (!dir.exists()) {
                dir.mkdirs();
                log.info("Directory created: {}", dir.getAbsolutePath());
            }
            String fileName = UUID.randomUUID().toString() + "_" + file.getOriginalFilename();
            File dest = new File(uploadDir + fileName);
            file.transferTo(dest);
            return "/dist/assets/upload/" + fileName;
        } catch (Exception e) {
            log.error("uploadImage: 이미지 업로드 실패", e);
            throw new RuntimeException("이미지 업로드 실패: " + e.getMessage(), e);
        }
    }

    // 공지사항 상세 조회
    @Transactional
    public NoticeDto getNotice(String noticeId) {
        if (!StringUtils.hasText(noticeId)) {
            log.error("getNotice: noticeId가 유효하지 않습니다.");
            throw new IllegalArgumentException("공지사항 ID는 필수입니다.");
        }
        try {
            noticeMapper.incrementReadCount(noticeId);
            return noticeMapper.getNotice(noticeId);
        } catch (Exception e) {
            log.error("getNotice: 공지사항 조회 실패, noticeId: {}", noticeId, e);
            throw new RuntimeException("공지사항 조회 실패: " + e.getMessage(), e);
        }
    }

    // 공지사항 상세 페이지 모델 설정
    public void populateNoticeDetail(String noticeId, Model model) {
        try {
            NoticeDto notice = getNotice(noticeId);
            if (notice == null) {
                model.addAttribute("msg", "게시물이 존재하지 않습니다.");
            }
            model.addAttribute("notice", notice);
        } catch (Exception e) {
            log.error("populateNoticeDetail: 공지사항 조회 실패, noticeId: {}", noticeId, e);
            model.addAttribute("msg", "공지사항 조회 중 오류 발생: " + e.getMessage());
            model.addAttribute("notice", null);
        }
    }

    // 공지사항 삭제 페이지 준비
    public void prepareDeleteNotice(String noticeId, String professorId, Model model) {
        if (professorId == null) {
            professorId = "P001";
        }
        NoticeDto notice = getNotice(noticeId);
        if (notice == null) {
            model.addAttribute("msg", "게시물이 존재하지 않습니다.");
            model.addAttribute("notice", null);
            return;
        }
        if (!notice.getWriterId().equals(professorId)) {
            model.addAttribute("msg", "삭제 권한이 없습니다.");
            model.addAttribute("notice", notice);
            return;
        }
        model.addAttribute("notice", notice);
    }

    // 공지사항 삭제
    @Transactional
    public void deleteNotice(String noticeId, String professorId, String password) {
        if (!StringUtils.hasText(professorId)) {
            log.error("deleteNotice: 교수 ID가 유효하지 않습니다.");
            throw new SecurityException("사용자 정보를 확인할 수 없습니다.");
        }
        if (!StringUtils.hasText(password)) {
            log.error("deleteNotice: 비밀번호가 입력되지 않았습니다.");
            throw new IllegalArgumentException("비밀번호를 입력해주세요.");
        }
        try {
            NoticeDto notice = noticeMapper.getNotice(noticeId);
            if (notice == null) {
                log.warn("deleteNotice: 공지사항이 존재하지 않습니다, noticeId: {}", noticeId);
                throw new IllegalArgumentException("공지사항이 존재하지 않습니다.");
            }
            if (!notice.getWriterId().equals(professorId)) {
                log.warn("deleteNotice: 삭제 권한이 없습니다, noticeId: {}, professorId: {}", noticeId, professorId);
                throw new IllegalArgumentException("삭제 권한이 없습니다.");
            }
            if (!notice.getNoticePassword().equals(password)) {
                log.warn("deleteNotice: 비밀번호가 일치하지 않습니다, noticeId: {}", noticeId);
                throw new IllegalArgumentException("비밀번호가 일치하지 않습니다.");
            }
            noticeMapper.deleteNotice(noticeId);
            log.info("Notice deleted: noticeId={}", noticeId);
        } catch (IllegalArgumentException e) {
            throw e;
        } catch (Exception e) {
            log.error("deleteNotice: 공지사항 삭제 실패, noticeId: {}", noticeId, e);
            throw new RuntimeException("공지사항 삭제 실패: " + e.getMessage(), e);
        }
    }

    @Transactional
    public void updateNotice(NoticeDto noticeDto, MultipartFile file, String professorId, HttpServletRequest request) {
        if (!StringUtils.hasText(professorId)) {
            log.error("updateNotice: 교수 ID가 유효하지 않습니다.");
            throw new SecurityException("사용자 정보를 확인할 수 없습니다.");
        }
        if (!StringUtils.hasText(noticeDto.getNoticePassword())) {
            log.error("updateNotice: 비밀번호가 입력되지 않았습니다.");
            throw new IllegalArgumentException("비밀번호를 입력해주세요.");
        }

        try {
            NoticeDto existingNotice = noticeMapper.getNotice(noticeDto.getNoticeId());
            if (existingNotice == null) {
                throw new IllegalArgumentException("공지사항이 존재하지 않습니다.");
            }
            if (!existingNotice.getWriterId().equals(professorId)) {
                throw new IllegalArgumentException("수정 권한이 없습니다.");
            }
            if (!existingNotice.getNoticePassword().equals(noticeDto.getNoticePassword())) {
                throw new IllegalArgumentException("비밀번호가 일치하지 않습니다.");
            }

            // 파일 처리
            if (file != null && !file.isEmpty()) {
                String filePath = uploadImage(file, request);
                noticeDto.setNoticeFile(filePath);
            } else {
                noticeDto.setNoticeFile(existingNotice.getNoticeFile()); // 기존 파일 유지
            }

            noticeMapper.updateNotice(noticeDto);
            log.info("Notice updated: noticeId={}", noticeDto.getNoticeId());
        } catch (Exception e) {
            log.error("updateNotice: 공지사항 수정 실패", e);
            throw new RuntimeException("공지사항 수정 중 오류 발생: " + e.getMessage(), e);
        }
    }
}