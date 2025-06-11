package com.ldb.lms.service.board;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ldb.lms.dto.ApiResponseDto;
import com.ldb.lms.dto.board.post.CommentDto;
import com.ldb.lms.dto.board.post.PostDto;
import com.ldb.lms.dto.board.post.PostPaginationDto;
import com.ldb.lms.dto.board.post.PostSearchDto;
import com.ldb.lms.mapper.mybatis.board.PostMapper;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class PostService {

    private final PostMapper postMapper;
    private final ObjectMapper objectMapper;

    public String getCurrentUserId(HttpSession session) {
        return (String) session.getAttribute("login");
    }

    private Path getUploadDirPath(HttpServletRequest request) {
        String realPath = request.getServletContext().getRealPath("");
        Path uploadDirPath = Paths.get(realPath, "dist", "assets", "upload");
        if (!Files.exists(uploadDirPath)) {
            try {
                Files.createDirectories(uploadDirPath);
            } catch (IOException e) {
                log.error("업로드 디렉토리 생성 실패", e);
                throw new RuntimeException("업로드 디렉토리 생성 불가", e);
            }
        }
        return uploadDirPath;
    }

    private String uploadFileAndGetPath(MultipartFile file, HttpServletRequest request) throws IOException {
        if (file == null || file.isEmpty()) {
            return null;
        }
        Path uploadDirPath = getUploadDirPath(request);
        String storedFileName = UUID.randomUUID().toString() + "_" + file.getOriginalFilename();
        Path destPath = uploadDirPath.resolve(storedFileName);
        file.transferTo(destPath);
        return "/dist/assets/upload/" + storedFileName;
    }

    private void deleteFile(String filePath, HttpServletRequest request) {
        if (!StringUtils.hasText(filePath)) {
            return;
        }
        try {
            Path uploadDirPath = getUploadDirPath(request);
            String actualFileName = filePath.substring(filePath.lastIndexOf("/") + 1);
            Path fileToDelete = uploadDirPath.resolve(actualFileName);
            Files.deleteIfExists(fileToDelete);
        } catch (IOException e) {
            log.error("파일 삭제 실패", e);
        }
    }

    @Transactional
    public ResponseEntity<ApiResponseDto<String>> handleCreatePostApi(PostDto postDto, HttpServletRequest request, HttpSession session) {
        try {
            String authorId = getCurrentUserId(session);
            if (!StringUtils.hasText(authorId)) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new ApiResponseDto<>(false, "로그인이 필요합니다.", null));
            }

            postDto.setAuthorId(authorId);
            postDto.setPostId(generateNewPostId());
            postDto.setPostNotice(postDto.getPostNotice() == null ? 0 : postDto.getPostNotice());
            postDto.setPostReadCount(0);

            String parentPostId = postDto.getParentPostId();
            if (StringUtils.hasText(parentPostId)) {
                PostDto parentPost = postMapper.getPost(parentPostId);
                if (parentPost == null) {
                    return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ApiResponseDto<>(false, "원글이 존재하지 않습니다.", null));
                }
                postDto.setPostGroup(parentPost.getPostGroup());
                postDto.setPostGroupLevel(parentPost.getPostGroupLevel() + 1);
                Map<String, Object> updateParam = new HashMap<>();
                updateParam.put("postGroup", parentPost.getPostGroup());
                updateParam.put("postGroupStep", parentPost.getPostGroupStep());
                postMapper.updateGroupStep(updateParam);
                postDto.setPostGroupStep(parentPost.getPostGroupStep() + 1);
            } else {
                Integer maxGroup = postMapper.getMaxGroup();
                postDto.setPostGroup(maxGroup != null ? maxGroup + 1 : 1);
                postDto.setPostGroupLevel(0);
                postDto.setPostGroupStep(0);
            }

            String filePath = uploadFileAndGetPath(postDto.getPostFile(), request);
            postDto.setExistingFilePath(filePath);
            postMapper.insertPost(postDto);

            String redirectUrl = "/post/getPosts";
            return ResponseEntity.ok(new ApiResponseDto<>(true, "게시물이 성공적으로 등록되었습니다.", redirectUrl));
        } catch (Exception e) {
            log.error("게시물 작성 실패", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new ApiResponseDto<>(false, "서버 오류로 인해 게시물 작성에 실패했습니다.", null));
        }
    }

    @Transactional
    public ResponseEntity<ApiResponseDto<String>> handleUpdatePostApi(PostDto postDto, Boolean removeFile, HttpServletRequest request, HttpSession session) {
        try {
            String currentUserId = getCurrentUserId(session);
            PostDto existingPost = postMapper.getPost(postDto.getPostId());

            if (existingPost == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ApiResponseDto<>(false, "수정할 게시물이 존재하지 않습니다.", null));
            }
            if (!existingPost.getAuthorId().equals(currentUserId)) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body(new ApiResponseDto<>(false, "수정 권한이 없습니다.", null));
            }
            if (!existingPost.getPostPassword().equals(postDto.getPostPassword())) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ApiResponseDto<>(false, "비밀번호가 일치하지 않습니다.", null));
            }

            String filePath = existingPost.getExistingFilePath();
            if (Boolean.TRUE.equals(removeFile)) {
                deleteFile(filePath, request);
                filePath = null;
            }
            MultipartFile newFile = postDto.getPostFile();
            if (newFile != null && !newFile.isEmpty()) {
                if (StringUtils.hasText(filePath)) {
                    deleteFile(filePath, request);
                }
                filePath = uploadFileAndGetPath(newFile, request);
            }
            postDto.setExistingFilePath(filePath);
            postMapper.updatePost(postDto);

            String redirectUrl = "/post/getPostDetail?postId=" + postDto.getPostId();
            return ResponseEntity.ok(new ApiResponseDto<>(true, "게시물이 성공적으로 수정되었습니다.", redirectUrl));
        } catch (Exception e) {
            log.error("게시물 수정 실패", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new ApiResponseDto<>(false, "서버 오류로 인해 게시물 수정에 실패했습니다.", null));
        }
    }

    @Transactional
    public ResponseEntity<ApiResponseDto<String>> handleDeletePostApi(String postId, String postPassword, HttpServletRequest request, HttpSession session) {
        try {
            String currentUserId = getCurrentUserId(session);
            PostDto existingPost = postMapper.getPost(postId);

            if (existingPost == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ApiResponseDto<>(false, "삭제할 게시물이 존재하지 않습니다.", null));
            }
            if (!existingPost.getAuthorId().equals(currentUserId)) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body(new ApiResponseDto<>(false, "삭제 권한이 없습니다.", null));
            }
            if (!existingPost.getPostPassword().equals(postPassword)) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ApiResponseDto<>(false, "비밀번호가 일치하지 않습니다.", null));
            }

            deleteFile(existingPost.getExistingFilePath(), request);
            postMapper.deleteCommentsByPostId(postId);
            postMapper.deletePost(postId);

            return ResponseEntity.ok(new ApiResponseDto<>(true, "게시물이 삭제되었습니다.", "/post/getPosts"));
        } catch (Exception e) {
            log.error("게시물 삭제 실패", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new ApiResponseDto<>(false, "서버 오류로 인해 게시물 삭제에 실패했습니다.", null));
        }
    }

    public Map<String, Object> getPostsPageData(PostSearchDto searchDto, PostPaginationDto pageDto) {
        Map<String, Object> modelData = new HashMap<>();
        PostSearchDto noticeSearchDto = new PostSearchDto();
        noticeSearchDto.setPostNotice(1);
        modelData.put("noticeList", postMapper.listNotices(noticeSearchDto));

        searchDto.setPostNotice(0);
        int totalCount = postMapper.countPosts(searchDto);
        pageDto.setTotalRows(totalCount);
        pageDto.calculatePagination();

        Map<String, Object> params = new HashMap<>();
        params.put("searchDto", searchDto);
        params.put("pageDto", pageDto);

        modelData.put("postList", postMapper.listPosts(params));
        modelData.put("pagination", pageDto);
        modelData.put("search", searchDto);
        return modelData;
    }

    @Transactional
    public Map<String, Object> getPostDetailData(String postId) {
        Map<String, Object> modelData = new HashMap<>();
        postMapper.incrementReadCount(postId);
        PostDto post = postMapper.getPost(postId);
        if (post != null) {
            modelData.put("post", post);
            modelData.put("comments", postMapper.selectCommentList(postId));
        }
        return modelData;
    }

    @Transactional
    public Map<String, Object> getPostDetailDataWithCommentsJson(String postId) {
        Map<String, Object> postData = getPostDetailData(postId);

        try {
            Object commentsObj = postData.get("comments");
            String commentsJsonString = "[]";
            if (commentsObj instanceof List<?>) {
                @SuppressWarnings("unchecked")
                List<CommentDto> comments = (List<CommentDto>) commentsObj;
                if (comments != null) {
                    commentsJsonString = objectMapper.writeValueAsString(comments);
                }
            }
            log.info("commentsJsonString from Service: {}", commentsJsonString);
            postData.put("commentsJsonString", commentsJsonString);
        } catch (Exception e) {
            log.error("댓글 목록 JSON 변환 실패 (Service)", e);
            postData.put("commentsJsonString", "[]");
        }
        return postData;
    }


    public Map<String, Object> getCreatePostFormData(HttpSession session) {
        Map<String, Object> modelData = new HashMap<>();
        modelData.put("currentAuthorId", getCurrentUserId(session));
        return modelData;
    }

    public Map<String, Object> getUpdatePostFormData(String postId, HttpSession session) {
        Map<String, Object> modelData = new HashMap<>();
        modelData.put("post", postMapper.getPost(postId));
        modelData.put("currentAuthorId", getCurrentUserId(session));
        return modelData;
    }

    public Map<String, Object> getDeletePostFormData(String postId, HttpSession session) {
        Map<String, Object> modelData = new HashMap<>();
        modelData.put("post", postMapper.getPost(postId));
        return modelData;
    }

    public Map<String, Object> getReplyPostFormData(String postId, HttpSession session) {
        Map<String, Object> modelData = new HashMap<>();
        modelData.put("parentPost", postMapper.getPost(postId));
        modelData.put("currentAuthorId", getCurrentUserId(session));
        return modelData;
    }

    private synchronized String generateNewPostId() {
        String maxPostId = postMapper.getLastPostId();
        if (maxPostId == null || maxPostId.isEmpty()) {
            return "PO001";
        }
        int number = Integer.parseInt(maxPostId.substring(2)) + 1;
        return "PO" + String.format("%03d", number);
    }

    private synchronized String generateNewCommentId() {
        String maxCommentId = postMapper.getLastCommentId();
        if (maxCommentId == null || maxCommentId.isEmpty()) {
            return "CM001";
        }
        int number = Integer.parseInt(maxCommentId.substring(2)) + 1;
        return "CM" + String.format("%03d", number);
    }

    @Transactional
    public CommentDto saveComment(CommentDto commentDto, HttpSession session) {
        String writerId = getCurrentUserId(session);
        if (!StringUtils.hasText(writerId)) {
            throw new IllegalArgumentException("로그인이 필요합니다.");
        }
        commentDto.setWriterId(writerId);
        commentDto.setCommentId(generateNewCommentId());
        postMapper.insertComment(commentDto);
        return postMapper.selectComment(commentDto.getCommentId());
    }

    @Transactional
    public void updateComment(CommentDto commentDto, HttpSession session) {
        String currentUserId = getCurrentUserId(session);
        if (!StringUtils.hasText(currentUserId)) {
            throw new IllegalArgumentException("로그인이 필요합니다.");
        }
        CommentDto existingComment = postMapper.selectComment(commentDto.getCommentId());
        if (existingComment == null) {
            throw new IllegalArgumentException("댓글이 존재하지 않습니다.");
        }
        if (!existingComment.getWriterId().equals(currentUserId)) {
            throw new IllegalArgumentException("수정 권한이 없습니다.");
        }
        postMapper.updateComment(commentDto);
    }

    @Transactional
    public void deleteComment(String commentId, HttpSession session) {
        String currentUserId = getCurrentUserId(session);
        if (!StringUtils.hasText(currentUserId)) {
            throw new IllegalArgumentException("로그인이 필요합니다.");
        }
        CommentDto existingComment = postMapper.selectComment(commentId);
        if (existingComment == null) {
            throw new IllegalArgumentException("댓글이 존재하지 않습니다.");
        }
        if (!existingComment.getWriterId().equals(currentUserId)) {
            throw new IllegalArgumentException("삭제 권한이 없습니다.");
        }
        postMapper.deleteComment(commentId);
    }

    public void handleFileDownload(String filePath, HttpServletResponse response, HttpServletRequest request) throws Exception {
        if (!StringUtils.hasText(filePath) || !filePath.startsWith("/dist/assets/upload/")) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Invalid file path");
            return;
        }
        Path uploadDirPath = getUploadDirPath(request);
        String fullPath = uploadDirPath.toString() + filePath.substring("/dist/assets/upload/".length());
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
}