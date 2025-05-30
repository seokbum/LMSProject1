package com.ldb.lms.service.board;

import com.ldb.lms.dto.board.notice.NoticeDto;
import com.ldb.lms.dto.board.notice.NoticePaginationDto;
import com.ldb.lms.dto.board.notice.NoticeSearchDto;
import com.ldb.lms.mapper.board.NoticeMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.File;
import java.nio.file.Files;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class NoticeService {

    private final NoticeMapper noticeMapper;

    private String getUploadDir(HttpServletRequest request) {
        if (request == null) {
            throw new IllegalArgumentException("HttpServletRequest is required to determine upload directory.");
        }
        String realPath = request.getServletContext().getRealPath("");
        if (realPath == null) {
            throw new RuntimeException("Real path is not available. Check deployment configuration.");
        }
        return realPath + "/dist/assets/upload/";
    }

    private void validateNoticeDto(NoticeDto noticeDto, String professorId) {
        if (noticeDto == null) {
            log.error("validateNoticeDto: NoticeDto가 null입니다.");
            throw new IllegalArgumentException("공지사항 데이터가 제공되지 않았습니다.");
        }
        if (!StringUtils.hasText(noticeDto.getNoticeTitle())) {
            log.error("validateNoticeDto: 공지사항 제목이 누락되었습니다.");
            throw new IllegalArgumentException("제목은 필수입니다.");
        }
        if (!StringUtils.hasText(noticeDto.getNoticeContent())) {
            log.error("validateNoticeDto: 공지사항 내용이 누락되었습니다.");
            throw new IllegalArgumentException("내용은 필수입니다.");
        }
        if (!StringUtils.hasText(noticeDto.getNoticePassword())) {
            log.error("validateNoticeDto: 공지사항 비밀번호가 누락되었습니다.");
            throw new IllegalArgumentException("비밀번호는 필수입니다.");
        }
        if (!StringUtils.hasText(professorId)) {
            log.error("validateNoticeDto: 교수 ID가 유효하지 않습니다.");
            throw new SecurityException("사용자 정보를 확인할 수 없습니다.");
        }
    }

    @Transactional
    public void saveNotice(NoticeDto noticeDto, String professorId, HttpServletRequest request) {
        validateNoticeDto(noticeDto, professorId);

        try {
            String lastNoticeId = noticeMapper.getLastNoticeId();
            long nextId = lastNoticeId != null ? Long.parseLong(lastNoticeId.replace("N", "")) + 1 : 1;
            String noticeId = "N" + nextId;
            noticeDto.setNoticeId(noticeId);
            noticeDto.setWriterId(professorId);

            MultipartFile file = noticeDto.getNoticeFile();
            String filePath = null;
            if (file != null && !file.isEmpty()) {
                String uploadDir = getUploadDir(request);
                File dir = new File(uploadDir);
                if (!dir.exists()) {
                    boolean created = dir.mkdirs();
                    if (!created) {
                        log.error("Failed to create directory: {}", dir.getAbsolutePath());
                        throw new RuntimeException("디렉토리 생성 실패: " + dir.getAbsolutePath());
                    }
                    log.info("Directory created: {}", dir.getAbsolutePath());
                }
                String fileName = UUID.randomUUID().toString() + "_" + file.getOriginalFilename();
                File dest = new File(dir, fileName);
                file.transferTo(dest);
                filePath = "/dist/assets/upload/" + fileName;
            }

            noticeDto.setExistingFilePath(filePath);
            noticeMapper.insertNotice(noticeDto);
            log.info("Notice saved: noticeId={}", noticeId);
        } catch (Exception e) {
            log.error("saveNotice: 공지사항 저장 실패", e);
            throw new RuntimeException("공지사항 저장 중 오류 발생: " + e.getMessage(), e);
        }
    }

    @Transactional
    public String uploadImage(MultipartFile file, HttpServletRequest request) {
        try {
            if (file == null || file.isEmpty()) {
                log.warn("uploadImage: 파일이 제공되지 않았습니다.");
                throw new IllegalArgumentException("파일이 제공되지 않았습니다.");
            }

            String uploadDir = getUploadDir(request);
            File dir = new File(uploadDir);
            if (!dir.exists()) {
                boolean created = dir.mkdirs();
                if (!created) {
                    log.error("Failed to create directory: {}", dir.getAbsolutePath());
                    throw new RuntimeException("디렉토리 생성 실패: " + dir.getAbsolutePath());
                }
                log.info("Directory created: {}", dir.getAbsolutePath());
            }

            String fileName = UUID.randomUUID().toString() + "_" + file.getOriginalFilename();
            File dest = new File(dir, fileName);
            file.transferTo(dest);

            String filePath = "/dist/assets/upload/" + fileName;
            log.info("Image uploaded successfully: {}", filePath);
            return filePath;
        } catch (Exception e) {
            log.error("uploadImage: 이미지 업로드 실패", e);
            throw new RuntimeException("이미지 업로드 실패: " + e.getMessage(), e);
        }
    }

    public ResponseEntity<Map<String, String>> handleImageUpload(MultipartFile file, HttpServletRequest request) {
        Map<String, String> response = new HashMap<>();
        try {
            String filePath = uploadImage(file, request);
            String baseUrl = request.getRequestURL().toString().replace(request.getRequestURI(), request.getContextPath());
            String imageUrl = baseUrl + filePath;
            response.put("url", imageUrl);
            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException e) {
            log.warn("handleImageUpload: 잘못된 요청 - {}", e.getMessage());
            response.put("error", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        } catch (Exception e) {
            log.error("handleImageUpload: 이미지 업로드 실패", e);
            response.put("error", "이미지 업로드 중 오류 발생: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    public ResponseEntity<Map<String, String>> handleWriteNotice(NoticeDto noticeDto, MultipartFile file, HttpServletRequest request, HttpSession session) {
        Map<String, String> response = new HashMap<>();
        try {
            String professorId = getProfessorIdFromSession(session);
            noticeDto.setNoticeFile(file);
            saveNotice(noticeDto, professorId, request);

            response.put("message", "공지사항이 성공적으로 저장되었습니다.");
            response.put("redirectUrl", request.getContextPath() + "/notice/getNotices");
            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException | SecurityException e) {
            log.warn("handleWriteNotice: 잘못된 요청 - {}", e.getMessage());
            response.put("error", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        } catch (Exception e) {
            log.error("handleWriteNotice: 공지사항 저장 실패", e);
            response.put("error", "공지사항 저장 중 오류 발생: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    public ResponseEntity<Map<String, String>> handleUpdateNotice(NoticeDto noticeDto, MultipartFile file, HttpServletRequest request, HttpSession session) {
        Map<String, String> response = new HashMap<>();
        try {
            String professorId = getProfessorIdFromSession(session);
            noticeDto.setNoticeFile(file);
            updateNotice(noticeDto, professorId, request);
            response.put("redirectUrl", request.getContextPath() + "/notice/getNoticeDetail?noticeId=" + noticeDto.getNoticeId());
            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException e) {
            log.warn("handleUpdateNotice: 잘못된 요청 - {}", e.getMessage());
            response.put("error", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        } catch (Exception e) {
            log.error("handleUpdateNotice: 공지사항 수정 실패", e);
            response.put("error", "공지사항 수정 중 오류 발생: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    public void populateNoticesModel(NoticeSearchDto searchDto, NoticePaginationDto pageDto, Model model) {
        Map<String, Object> result = listNotice(searchDto, pageDto);
        model.addAttribute("notices", result.get("notices"));
        model.addAttribute("pagination", result.get("pagination"));
        model.addAttribute("searchDto", searchDto);
    }

    public void prepareCreateNotice(HttpSession session, Model model) {
        String professorId = getProfessorIdFromSession(session);
        model.addAttribute("noticeDto", new NoticeDto());
    }

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

    public void prepareDeleteNotice(String noticeId, HttpSession session, Model model) {
        String professorId = getProfessorIdFromSession(session);
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

    public String handleDeleteNotice(String noticeId, String password, HttpSession session, Model model) {
        try {
            String professorId = getProfessorIdFromSession(session);
            deleteNotice(noticeId, professorId, password);
            return "redirect:/notice/getNotices";
        } catch (IllegalArgumentException e) {
            log.warn("handleDeleteNotice: {}", e.getMessage());
            model.addAttribute("msg", e.getMessage());
            populateNoticeDetail(noticeId, model);
            return "board/notice/deleteNotice";
        } catch (Exception e) {
            log.error("handleDeleteNotice: 공지사항 삭제 실패, noticeId: {}", noticeId, e);
            model.addAttribute("msg", "공지사항 삭제 중 오류 발생: " + e.getMessage());
            populateNoticeDetail(noticeId, model);
            return "board/notice/deleteNotice";
        }
    }

    public void prepareUpdateNotice(String noticeId, HttpSession session, Model model) {
        String professorId = getProfessorIdFromSession(session);
        NoticeDto notice = getNotice(noticeId);
        if (notice != null) {
            notice.setNoticeFile(null);
        }
        log.info("prepareUpdateNotice: noticeId={}, notice={}", noticeId, notice);
        model.addAttribute("notice", notice);
    }

    public void handleFileDownload(String filePath, HttpServletResponse response, HttpServletRequest request) throws Exception {
        try {
            if (!StringUtils.hasText(filePath) || !filePath.startsWith("/dist/assets/upload/")) {
                log.warn("Invalid file path: {}", filePath);
                response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Invalid file path");
                return;
            }

            String uploadDir = getUploadDir(request);
            String absoluteFilePath = uploadDir + filePath.substring("/dist/assets/upload/".length());
            File file = new File(absoluteFilePath);
            if (!file.exists()) {
                log.warn("File not found: {}", absoluteFilePath);
                response.sendError(HttpServletResponse.SC_NOT_FOUND, "File not found");
                return;
            }

            String mimeType = Files.probeContentType(file.toPath());
            if (mimeType == null) {
                mimeType = "application/octet-stream";
            }
            response.setContentType(mimeType);
            response.setContentLengthLong(file.length());

            String fileName = filePath.substring(filePath.lastIndexOf("_") + 1);
            response.setHeader("Content-Disposition", "attachment; filename=\"" + fileName + "\"");

            Files.copy(file.toPath(), response.getOutputStream());
            response.getOutputStream().flush();
        } catch (Exception e) {
            log.error("handleFileDownload failed: {}", filePath, e);
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "File download failed");
        }
    }

    private String getProfessorIdFromSession(HttpSession session) {
        String professorId = (String) session.getAttribute("professorId");
        if (professorId == null) {
            professorId = "P001";
            log.warn("professorId is null, set to default: {}", professorId);
        }
        return professorId;
    }

    public Map<String, Object> listNotice(NoticeSearchDto searchDto, NoticePaginationDto pageDto) {
        Integer pageSize = pageDto.getItemsPerPage() != null ? pageDto.getItemsPerPage() : 10;
        Integer currentPage = pageDto.getCurrentPage() != null && pageDto.getCurrentPage() > 0 ? pageDto.getCurrentPage() : 1;
        log.info("listNotice: pageSize={}, currentPage={}", pageSize, currentPage);
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

            if (StringUtils.hasText(notice.getExistingFilePath())) {
                log.warn("deleteNotice: 파일 삭제는 호출자가 처리해야 합니다. filePath: {}", notice.getExistingFilePath());
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
    public void updateNotice(NoticeDto noticeDto, String professorId, HttpServletRequest request) {
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

            MultipartFile file = noticeDto.getNoticeFile();
            String filePath = noticeDto.getExistingFilePath();
            if (file != null && !file.isEmpty()) {
                String uploadDir = getUploadDir(request);
                File dir = new File(uploadDir);
                if (!dir.exists()) {
                    boolean created = dir.mkdirs();
                    if (!created) {
                        log.error("Failed to create directory: {}", dir.getAbsolutePath());
                        throw new RuntimeException("디렉토리 생성 실패: " + dir.getAbsolutePath());
                    }
                    log.info("Directory created: {}", dir.getAbsolutePath());
                }
                String fileName = UUID.randomUUID().toString() + "_" + file.getOriginalFilename();
                File dest = new File(dir, fileName);
                file.transferTo(dest);
                filePath = "/dist/assets/upload/" + fileName;
            }

            noticeDto.setExistingFilePath(filePath);
            noticeMapper.updateNotice(noticeDto);
            log.info("Notice updated: noticeId={}", noticeDto.getNoticeId());
        } catch (Exception e) {
            log.error("updateNotice: 공지사항 수정 실패", e);
            throw new RuntimeException("공지사항 수정 중 오류 발생: " + e.getMessage(), e);
        }
    }
}