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

import com.ldb.lms.dto.ApiResponseDto;
import com.ldb.lms.dto.board.post.CommentDto;
import com.ldb.lms.dto.board.post.PostDto;
import com.ldb.lms.dto.board.post.PostPaginationDto;
import com.ldb.lms.dto.board.post.PostSearchDto;
import com.ldb.lms.mapper.mybatis.board.PostMapper;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class PostService {

    private final PostMapper postMapper;

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
                log.error("업로드 디렉토리 생성 실패: {}", uploadDirPath, e);
                throw new RuntimeException("업로드 디렉토리를 생성할 수 없습니다.", e);
            }
        }
        return uploadDirPath;
    }

    public String uploadFileAndGetPath(MultipartFile file, HttpServletRequest request) throws IOException {
        if (file == null || file.isEmpty()) {
            return null;
        }
        Path uploadDirPath = getUploadDirPath(request);
        String originalFilename = file.getOriginalFilename();
        String storedFileName = UUID.randomUUID().toString() + "_" + originalFilename;
        Path destPath = uploadDirPath.resolve(storedFileName);
        file.transferTo(destPath);
        return "/dist/assets/upload/" + storedFileName;
    }

    public void deleteFile(String filePath, HttpServletRequest request) {
        if (!StringUtils.hasText(filePath)) {
            return;
        }
        try {
            Path uploadDirPath = getUploadDirPath(request);
            String actualFileName = filePath.substring(filePath.lastIndexOf("/") + 1);
            Path fileToDelete = uploadDirPath.resolve(actualFileName);
            Files.deleteIfExists(fileToDelete);
        } catch (IOException e) {
            log.error("파일 삭제 실패: {}", filePath, e);
        }
    }

    @Transactional
    public ResponseEntity<ApiResponseDto<String>> handleCreatePostApi(PostDto postDto, HttpServletRequest request, HttpSession session) {
        try {
            if (!StringUtils.hasText(postDto.getPostTitle()) || !StringUtils.hasText(postDto.getPostContent()) || !StringUtils.hasText(postDto.getPostPassword())) {
                ApiResponseDto<String> response = new ApiResponseDto<>(false, "제목, 내용, 비밀번호는 필수 입력값입니다.", null);
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
            }
            String authorId = getCurrentUserId(session);
            if (!StringUtils.hasText(authorId)) {
                ApiResponseDto<String> response = new ApiResponseDto<>(false, "로그인이 필요합니다.", null);
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
            }
            
            postDto.setAuthorId(authorId);
            postDto.setPostId(generateNewPostId());
            postDto.setPostNotice(postDto.getPostNotice() == null ? 0 : postDto.getPostNotice());
            postDto.setPostCreatedAt(new Date());
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
            
            String redirectUrl = "/post/getPostDetail?postId=" + postDto.getPostId();
            return ResponseEntity.ok(new ApiResponseDto<>(true, "게시물이 성공적으로 등록되었습니다.", redirectUrl));
        } catch (Exception e) {
            log.error("게시물 작성 실패", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new ApiResponseDto<>(false, "서버 오류로 인해 게시물 작성에 실패했습니다.", null));
        }
    }

    @Transactional
    public ResponseEntity<ApiResponseDto<String>> handleUpdatePostApi(PostDto postDto, Boolean removeFile, HttpServletRequest request, HttpSession session) {
        try {
            if (!StringUtils.hasText(postDto.getPostTitle()) || !StringUtils.hasText(postDto.getPostContent()) || !StringUtils.hasText(postDto.getPostPassword())) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ApiResponseDto<>(false, "제목, 내용, 비밀번호는 필수 입력값입니다.", null));
            }

            PostDto existingPost = postMapper.getPost(postDto.getPostId());
            if (existingPost == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ApiResponseDto<>(false, "수정할 게시물이 존재하지 않습니다.", null));
            }
            if (!existingPost.getPostPassword().equals(postDto.getPostPassword())) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ApiResponseDto<>(false, "비밀번호가 일치하지 않습니다.", null));
            }
            String currentUserId = getCurrentUserId(session);
            if (!existingPost.getAuthorId().equals(currentUserId)) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body(new ApiResponseDto<>(false, "게시물을 수정할 권한이 없습니다.", null));
            }

            postDto.setPostUpdatedAt(new Date());
            String currentFilePath = existingPost.getExistingFilePath();
            String finalFilePath = currentFilePath;

            if (Boolean.TRUE.equals(removeFile)) {
                deleteFile(currentFilePath, request);
                finalFilePath = null;
            }

            MultipartFile newFile = postDto.getPostFile();
            if (newFile != null && !newFile.isEmpty()) {
                if (StringUtils.hasText(currentFilePath)) {
                    deleteFile(currentFilePath, request);
                }
                finalFilePath = uploadFileAndGetPath(newFile, request);
            }

            postDto.setExistingFilePath(finalFilePath);
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
            PostDto existingPost = postMapper.getPost(postId);
            if (existingPost == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ApiResponseDto<>(false, "삭제할 게시물이 존재하지 않습니다.", null));
            }
            if (!existingPost.getPostPassword().equals(postPassword)) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ApiResponseDto<>(false, "비밀번호가 일치하지 않습니다.", null));
            }
            String currentUserId = getCurrentUserId(session);
            if (!existingPost.getAuthorId().equals(currentUserId)) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body(new ApiResponseDto<>(false, "게시물을 삭제할 권한이 없습니다.", null));
            }

            deleteFile(existingPost.getExistingFilePath(), request);
            postMapper.deleteCommentsByPostId(postId);
            postMapper.deletePost(postId);
            
            return ResponseEntity.ok(new ApiResponseDto<>(true, "게시물이 성공적으로 삭제되었습니다.", "/post/getPosts"));
        } catch (Exception e) {
            log.error("게시물 삭제 실패", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new ApiResponseDto<>(false, "서버 오류로 인해 게시물 삭제에 실패했습니다.", null));
        }
    }

    public ResponseEntity<ApiResponseDto<Map<String, String>>> handleImageUploadApi(MultipartFile file, HttpServletRequest request) {
        try {
            String filePath = uploadFileAndGetPath(file, request);
            Map<String, String> data = new HashMap<>();
            data.put("url", filePath);
            data.put("fileName", file.getOriginalFilename());
            return ResponseEntity.ok(new ApiResponseDto<>(true, "이미지 업로드 성공", data));
        } catch (Exception e) {
            log.error("이미지 업로드 실패", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new ApiResponseDto<>(false, "이미지 업로드 실패: " + e.getMessage(), null));
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

    public Map<String, Object> getCreatePostFormData(HttpSession session) {
        Map<String, Object> modelData = new HashMap<>();
        modelData.put("currentAuthorId", getCurrentUserId(session)); 
        return modelData;
    }

    public Map<String, Object> getUpdatePostFormData(String postId, HttpSession session) {
        Map<String, Object> modelData = new HashMap<>();
        PostDto post = postMapper.getPost(postId);
        modelData.put("post", post);
        modelData.put("existingFileUrl", post.getExistingFilePath());
        return modelData;
    }

    @Transactional
    public Map<String, Object> getPostDetailData(String postId) {
        Map<String, Object> modelData = new HashMap<>();
        PostDto post = postMapper.getPost(postId);
        if (post != null) {
            postMapper.incrementReadCount(postId);
            modelData.put("post", post);
            modelData.put("comments", postMapper.selectCommentList(postId));
        }
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
        if (maxPostId == null || maxPostId.isEmpty()) return "PO001";
        int number = Integer.parseInt(maxPostId.substring(2)) + 1;
        return "PO" + String.format("%03d", number);
    }

    @Transactional
    public void saveComment(CommentDto commentDto, HttpSession session) {
        String writerId = getCurrentUserId(session);
        if (!StringUtils.hasText(writerId)) { 
            throw new IllegalArgumentException("로그인 후에 댓글을 작성할 수 있습니다.");
        }
        if (!StringUtils.hasText(commentDto.getCommentContent())) {
            throw new IllegalArgumentException("댓글 내용은 필수 입력값입니다.");
        }
        commentDto.setWriterId(writerId);
        commentDto.setCommentId(generateNewCommentId());
        commentDto.setCreatedAt(new Date());
        commentDto.setUpdatedAt(new Date());
        postMapper.insertComment(commentDto);
    }

    @Transactional
    public void updateComment(CommentDto commentDto, HttpSession session) {
        String currentUserId = getCurrentUserId(session);
        if (!StringUtils.hasText(currentUserId)) {
            throw new IllegalArgumentException("댓글을 수정하려면 로그인이 필요합니다.");
        }
        CommentDto existingComment = postMapper.selectComment(commentDto.getCommentId());
        if (existingComment == null) {
            throw new IllegalArgumentException("수정할 댓글이 존재하지 않습니다.");
        }
        if (!existingComment.getWriterId().equals(currentUserId)) {
            throw new IllegalArgumentException("댓글을 수정할 권한이 없습니다.");
        }
        commentDto.setUpdatedAt(new Date());
        postMapper.updateComment(commentDto);
    }

    @Transactional
    public void deleteComment(String commentId, HttpSession session) {
        String currentUserId = getCurrentUserId(session);
        if (!StringUtils.hasText(currentUserId)) {
            throw new IllegalArgumentException("댓글을 삭제하려면 로그인이 필요합니다.");
        }
        CommentDto existingComment = postMapper.selectComment(commentId);
        if (existingComment == null) {
            throw new IllegalArgumentException("삭제할 댓글이 존재하지 않습니다.");
        }
        if (!existingComment.getWriterId().equals(currentUserId)) {
            throw new IllegalArgumentException("댓글을 삭제할 권한이 없습니다.");
        }
        postMapper.deleteComment(commentId);
    }

    private synchronized String generateNewCommentId() {
        String maxCommentId = postMapper.getLastCommentId();
        if (maxCommentId == null || maxCommentId.isEmpty()) return "CM001";
        int number = Integer.parseInt(maxCommentId.substring(2)) + 1;
        return "CM" + String.format("%03d", number);
    }
}