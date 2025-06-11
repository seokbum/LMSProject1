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
import java.io.FileInputStream;
import java.io.OutputStream;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class NoticeService {

    private final NoticeMapper noticeMapper;

    private String getCurrentUserId(HttpSession session) {
        return (String) session.getAttribute("login");
    }

    private String getUploadDir(HttpServletRequest request) {
        String realPath = request.getServletContext().getRealPath("");
        return realPath + "/dist/assets/upload/";
    }

    private String uploadFileAndGetPath(MultipartFile file, HttpServletRequest request) throws Exception {
        if (file == null || file.isEmpty()) {
            return null;
        }
        String uploadDir = getUploadDir(request);
        File dir = new File(uploadDir);
        if (!dir.exists()) {
            dir.mkdirs();
        }
        String storedFileName = UUID.randomUUID().toString() + "_" + file.getOriginalFilename();
        Path destPath = Paths.get(uploadDir, storedFileName);
        file.transferTo(destPath);
        return "/dist/assets/upload/" + storedFileName;
    }

    private void deleteFile(String filePath, HttpServletRequest request) throws Exception {
        if (StringUtils.hasText(filePath)) {
            String uploadDir = getUploadDir(request);
            String actualFileName = filePath.substring(filePath.lastIndexOf("/") + 1);
            Path fileToDelete = Paths.get(uploadDir, actualFileName);
            Files.deleteIfExists(fileToDelete);
        }
    }
    
    @Transactional
    public String generateNewNoticeId() {
        String lastNoticeId = noticeMapper.getLastNoticeId();
        if (lastNoticeId == null || lastNoticeId.isEmpty()) {
            return "N001";
        }
        int number = Integer.parseInt(lastNoticeId.substring(1)) + 1;
        return "N" + String.format("%03d", number);
    }
    
    public ResponseEntity<Map<String, String>> handleImageUpload(MultipartFile file, HttpServletRequest request) {
        Map<String, String> response = new HashMap<>();
        try {
            String filePath = uploadFileAndGetPath(file, request);
            response.put("url", filePath);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("handleImageUpload: 이미지 업로드 실패", e);
            response.put("error", "이미지 업로드 중 오류 발생");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    public void populateNoticesModel(NoticeSearchDto searchDto, NoticePaginationDto pageDto, Model model, HttpSession session) {
        int totalRows = noticeMapper.countNotices(searchDto);
        pageDto.setTotalRows(totalRows);
        pageDto.calculatePagination();
        
        Map<String, Object> params = new HashMap<>();
        params.put("searchDto", searchDto);
        params.put("pageDto", pageDto);
        
        List<NoticeDto> notices = noticeMapper.listNotice(params);
        
        model.addAttribute("notices", notices);
        model.addAttribute("pagination", pageDto);
        model.addAttribute("searchDto", searchDto);
        model.addAttribute("currentWriterId", getCurrentUserId(session));
    }

    @Transactional
    public void populateNoticeDetail(String noticeId, Model model, HttpSession session) {
        noticeMapper.incrementReadCount(noticeId);
        NoticeDto notice = noticeMapper.getNotice(noticeId);
        model.addAttribute("notice", notice);
        model.addAttribute("currentWriterId", getCurrentUserId(session));
    }

    public void prepareCreateNotice(HttpSession session, Model model) {
        model.addAttribute("currentWriterId", getCurrentUserId(session));
    }

    public void prepareUpdateNotice(String noticeId, HttpSession session, Model model) {
        model.addAttribute("notice", noticeMapper.getNotice(noticeId));
        model.addAttribute("currentWriterId", getCurrentUserId(session));
    }

    public void prepareDeleteNotice(String noticeId, HttpSession session, Model model) {
        model.addAttribute("notice", noticeMapper.getNotice(noticeId));
        model.addAttribute("currentWriterId", getCurrentUserId(session));
    }
    
    @Transactional
    public ResponseEntity<Map<String, String>> handleWriteNotice(NoticeDto noticeDto, MultipartFile file, HttpServletRequest request, HttpSession session) {
        Map<String, String> response = new HashMap<>();
        try {
            String currentUserId = getCurrentUserId(session);
            if (!StringUtils.hasText(currentUserId) || !currentUserId.startsWith("P")) {
                throw new SecurityException("공지사항 작성 권한이 없습니다.");
            }

            noticeDto.setWriterId(currentUserId);
            noticeDto.setNoticeFile(file); 
            String filePath = uploadFileAndGetPath(noticeDto.getNoticeFile(), request);
            noticeDto.setExistingFilePath(filePath);
            
            noticeDto.setNoticeId(generateNewNoticeId());
            noticeMapper.insertNotice(noticeDto);
            
            response.put("redirectUrl", "/notice/getNotices");
            return ResponseEntity.ok(response);

        } catch (Exception e) {
            log.error("handleWriteNotice: 공지사항 저장 실패", e);
            response.put("error", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    @Transactional
    public ResponseEntity<Map<String, String>> handleUpdateNotice(NoticeDto noticeDto, MultipartFile file, HttpServletRequest request, HttpSession session) {
        Map<String, String> response = new HashMap<>();
        try {
            String currentUserId = getCurrentUserId(session);
            NoticeDto existingNotice = noticeMapper.getNotice(noticeDto.getNoticeId());

            if (existingNotice == null) {
                throw new IllegalArgumentException("게시물이 존재하지 않습니다.");
            }
            if (!existingNotice.getWriterId().equals(currentUserId)) {
                throw new SecurityException("수정 권한이 없습니다.");
            }
            if (!existingNotice.getNoticePassword().equals(noticeDto.getNoticePassword())) {
                throw new IllegalArgumentException("비밀번호가 일치하지 않습니다.");
            }

            String filePath = existingNotice.getExistingFilePath();
            if (file != null && !file.isEmpty()) {
                deleteFile(filePath, request);
                filePath = uploadFileAndGetPath(file, request);
            }
            noticeDto.setExistingFilePath(filePath);
            noticeMapper.updateNotice(noticeDto);
            
            response.put("redirectUrl", "/notice/getNoticeDetail?noticeId=" + noticeDto.getNoticeId());
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("handleUpdateNotice: 공지사항 수정 실패", e);
            response.put("error", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }
    
    @Transactional
    public void deleteNotice(String noticeId, String currentUserId, String password, HttpServletRequest request) throws Exception {
        if (!StringUtils.hasText(currentUserId)) {
            throw new SecurityException("로그인 정보가 없습니다.");
        }
        NoticeDto notice = noticeMapper.getNotice(noticeId);
        if (notice == null) {
            throw new IllegalArgumentException("공지사항이 존재하지 않습니다.");
        }
        if (!notice.getWriterId().equals(currentUserId)) {
            throw new SecurityException("삭제 권한이 없습니다.");
        }
        if (!notice.getNoticePassword().equals(password)) {
            throw new IllegalArgumentException("비밀번호가 일치하지 않습니다.");
        }
        
        if (request != null && StringUtils.hasText(notice.getExistingFilePath())) {
            deleteFile(notice.getExistingFilePath(), request);
        }
        noticeMapper.deleteNotice(noticeId);
    }

    public void handleFileDownload(String filePath, HttpServletResponse response, HttpServletRequest request) throws Exception {
        if (!StringUtils.hasText(filePath) || !filePath.startsWith("/dist/assets/upload/")) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Invalid file path");
            return;
        }
        String uploadDir = getUploadDir(request);
        String fullPath = uploadDir + filePath.substring("/dist/assets/upload/".length());
        File file = new File(fullPath);

        if (!file.exists() || !file.canRead()) {
            response.sendError(HttpServletResponse.SC_NOT_FOUND, "File not found");
            return;
        }

        String originalFileName = filePath.substring(filePath.lastIndexOf("_") + 1);
        String encodedFileName = URLEncoder.encode(originalFileName, StandardCharsets.UTF_8).replaceAll("\\+", "%20");
        
        response.setContentType("application/octet-stream");
        response.setHeader("Content-Disposition", "attachment; filename=\"" + encodedFileName + "\"");
        response.setContentLengthLong(file.length());

        try (FileInputStream fis = new FileInputStream(file); OutputStream os = response.getOutputStream()) {
            byte[] buffer = new byte[4096];
            int length;
            while ((length = fis.read(buffer)) > 0) {
                os.write(buffer, 0, length);
            }
            os.flush();
        }
    }
    
    public List<NoticeDto> getRecentNotices() {
        NoticeSearchDto searchDto = new NoticeSearchDto();
        
        NoticePaginationDto pageDto = new NoticePaginationDto();
        pageDto.setItemsPerPage(5); 
        pageDto.setOffset(0); 

        Map<String, Object> params = new HashMap<>();
        params.put("searchDto", searchDto); 
        params.put("pageDto", pageDto);

        return noticeMapper.listNotice(params);
    }
}