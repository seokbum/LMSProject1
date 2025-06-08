package com.ldb.lms.service.board;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.List;
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
import com.ldb.lms.mapper.board.PostMapper;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class PostService {

    private final PostMapper postMapper;

    private String getUploadDir(HttpServletRequest request) {
        String realPath = request.getServletContext().getRealPath("");
        if (realPath == null) {
            throw new RuntimeException("서버 설정 오류: 업로드 디렉토리를 찾을 수 없습니다.");
        }
        String uploadDir = realPath + "/dist/assets/upload/";
        File dir = new File(uploadDir);
        if (!dir.exists() && !dir.mkdirs()) {
            throw new RuntimeException("파일 업로드 디렉토리 생성 실패.");
        }
        return uploadDir;
    }

    private String uploadFileAndGetPath(MultipartFile file, HttpServletRequest request) throws IOException {
        if (file == null || file.isEmpty()) {
            return null;
        }

        String uploadDir = getUploadDir(request);
        String originalFileName = file.getOriginalFilename();
        String fileName = UUID.randomUUID().toString() + "_" + originalFileName;
        File dest = new File(uploadDir, fileName);

        file.transferTo(dest);
        String filePath = "/dist/assets/upload/" + fileName;
        return filePath;
    }

    private void deleteFile(String filePath, HttpServletRequest request) {
        if (!StringUtils.hasText(filePath)) {
            return;
        }
        try {
            String fullPath = getUploadDir(request) + filePath.replace("/dist/assets/upload/", "");
            Path fileToDelete = Paths.get(fullPath);

            if (Files.exists(fileToDelete)) {
                Files.delete(fileToDelete);
            }
        } catch (IOException e) {
            log.error("파일 삭제 실패: {}", filePath, e);
        }
    }

    @Transactional
    public ResponseEntity<ApiResponseDto<String>> handleCreatePostApi(
            PostDto postDto,
            MultipartFile file,
            HttpServletRequest request,
            HttpSession session) {
        try {
            String authorId = getCurrentUserId(session);
            if (!StringUtils.hasText(authorId)) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                                .body(new ApiResponseDto<>(false, "사용자 정보를 확인할 수 없습니다.", null));
            }
            postDto.setAuthorId(authorId);

            String newPostId = generateNewPostId();
            postDto.setPostId(newPostId);

            if (postDto.getPostNotice() == null) {
                postDto.setPostNotice(0);
            }

            String parentPostId = postDto.getParentPostId();
            if (StringUtils.hasText(parentPostId)) {
                PostDto parentPost = postMapper.getPost(parentPostId);
                if (parentPost == null) {
                    return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                                    .body(new ApiResponseDto<>(false, "답변할 게시물이 존재하지 않습니다.", null));
                }
                postDto.setPostGroup(parentPost.getPostGroup());
                postDto.setPostGroupLevel(parentPost.getPostGroupLevel() + 1);

                Map<String, Object> updateParam = new HashMap<>();
                updateParam.put("postGroup", parentPost.getPostGroup());
                updateParam.put("postGroupStep", parentPost.getPostGroupStep());
                postMapper.updateGroupStep(updateParam);

                postDto.setPostGroupStep(parentPost.getPostGroupStep() + 1);
            } else {
                postDto.setPostGroup(postMapper.getMaxGroup() != null ? postMapper.getMaxGroup() + 1 : 1);
                postDto.setPostGroupLevel(0);
                postDto.setPostGroupStep(0);
            }

            String filePath = uploadFileAndGetPath(file, request);
            if (filePath != null) {
                postDto.setExistingFilePath(filePath);
            } else {
                postDto.setExistingFilePath(null);
            }

            postMapper.insertPost(postDto);

            return ResponseEntity.ok(new ApiResponseDto<>(true, "게시물이 성공적으로 작성되었습니다.", "/post/getPosts"));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ApiResponseDto<>(false, e.getMessage(), null));
        } catch (SecurityException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(new ApiResponseDto<>(false, e.getMessage(), null));
        } catch (Exception e) {
            log.error("handleCreatePostApi 실패: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponseDto<>(false, "게시물 작성 중 예상치 못한 오류가 발생했습니다. 잠시 후 다시 시도해주세요.", null));
        }
    }

    @Transactional
    public ResponseEntity<ApiResponseDto<String>> handleUpdatePostApi(
            PostDto postDto,
            MultipartFile file,
            Boolean removeFile,
            HttpServletRequest request,
            HttpSession session) {
        try {
            if (!StringUtils.hasText(postDto.getPostId())) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                                .body(new ApiResponseDto<>(false, "게시물 ID는 필수입니다.", null));
            }
            if (!StringUtils.hasText(postDto.getPostTitle()) || !StringUtils.hasText(postDto.getPostContent())) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                                .body(new ApiResponseDto<>(false, "제목과 내용을 모두 입력해주세요.", null));
            }
            if (!StringUtils.hasText(postDto.getPostPassword())) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                                .body(new ApiResponseDto<>(false, "비밀번호를 입력해주세요.", null));
            }

            PostDto existingPost = postMapper.getPost(postDto.getPostId());
            if (existingPost == null) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                                .body(new ApiResponseDto<>(false, "수정할 게시물이 존재하지 않습니다.", null));
            }
            if (!existingPost.getPostPassword().equals(postDto.getPostPassword())) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                                .body(new ApiResponseDto<>(false, "비밀번호가 일치하지 않습니다.", null));
            }

            postDto.setAuthorId(existingPost.getAuthorId());
            if (postDto.getPostNotice() == null) {
                postDto.setPostNotice(0);
            }

            String currentExistingFilePath = existingPost.getExistingFilePath();
            String finalFilePath = currentExistingFilePath;

            if (removeFile != null && removeFile) {
                if (StringUtils.hasText(currentExistingFilePath)) {
                    deleteFile(currentExistingFilePath, request);
                    finalFilePath = null;
                }
            }

            if (file != null && !file.isEmpty()) {
                if (StringUtils.hasText(currentExistingFilePath) && (removeFile == null || !removeFile)) {
                    deleteFile(currentExistingFilePath, request);
                }
                finalFilePath = uploadFileAndGetPath(file, request);
            }

            postDto.setExistingFilePath(finalFilePath);

            postMapper.updatePost(postDto);

            return ResponseEntity.ok(new ApiResponseDto<>(true, "게시물이 성공적으로 수정되었습니다.", "/post/getPostDetail?postId=" + postDto.getPostId()));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ApiResponseDto<>(false, e.getMessage(), null));
        } catch (SecurityException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(new ApiResponseDto<>(false, e.getMessage(), null));
        } catch (Exception e) {
            log.error("handleUpdatePostApi 실패: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponseDto<>(false, "게시물 수정 중 예상치 못한 오류가 발생했습니다. 잠시 후 다시 시도해주세요.", null));
        }
    }

    @Transactional
    public ResponseEntity<ApiResponseDto<String>> handleDeletePostApi(
            String postId,
            String postPassword,
            HttpServletRequest request,
            HttpSession session) {
        try {
            if (!StringUtils.hasText(postId)) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ApiResponseDto<>(false, "게시물 ID는 필수입니다.", null));
            }
            if (!StringUtils.hasText(postPassword)) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ApiResponseDto<>(false, "비밀번호를 입력해주세요.", null));
            }

            PostDto existingPost = postMapper.getPost(postId);
            if (existingPost == null) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ApiResponseDto<>(false, "삭제할 게시물이 존재하지 않습니다.", null));
            }
            if (!existingPost.getPostPassword().equals(postPassword)) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new ApiResponseDto<>(false, "비밀번호가 일치하지 않습니다.", null));
            }

            if (StringUtils.hasText(existingPost.getExistingFilePath())) {
                deleteFile(existingPost.getExistingFilePath(), request);
            }

            postMapper.deleteCommentsByPostId(postId);

            postMapper.deletePost(postId);

            return ResponseEntity.ok(new ApiResponseDto<>(true, "게시물이 성공적으로 삭제되었습니다.", "/post/getPosts"));
        } catch (Exception e) {
            log.error("handleDeletePostApi 실패: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponseDto<>(false, "게시물 삭제 중 예상치 못한 오류가 발생했습니다.", null));
        }
    }

    public ResponseEntity<ApiResponseDto<Map<String, String>>> handleImageUploadApi(MultipartFile file, HttpServletRequest request) {
        if (file == null || file.isEmpty()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ApiResponseDto<>(false, "업로드할 이미지가 없습니다.", null));
        }
        try {
            String filePath = uploadFileAndGetPath(file, request);
            if (filePath != null) {
                Map<String, String> data = new HashMap<>();
                data.put("url", filePath);
                data.put("fileName", file.getOriginalFilename());
                return ResponseEntity.ok(new ApiResponseDto<>(true, "이미지 업로드 성공", data));
            } else {
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                        .body(new ApiResponseDto<>(false, "이미지 파일 업로드에 실패했습니다.", null));
            }
        } catch (IOException e) {
                log.error("handleImageUploadApi 파일 처리 중 오류: {}", e.getMessage(), e);
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                        .body(new ApiResponseDto<>(false, "이미지 업로드 중 파일 처리 오류가 발생했습니다: " + e.getMessage(), null));
        } catch (RuntimeException e) {
            log.error("handleImageUploadApi 서버 설정 오류: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponseDto<>(false, "이미지 업로드 서버 설정 오류: " + e.getMessage(), null));
        } catch (Exception e) {
            log.error("handleImageUploadApi 예상치 못한 오류: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponseDto<>(false, "이미지 업로드 중 예상치 못한 오류가 발생했습니다. 잠시 후 다시 시도해주세요.", null));
        }
    }

    public Map<String, Object> getPostsPageData(PostSearchDto searchDto, PostPaginationDto pageDto) {
        Map<String, Object> modelData = new HashMap<>();

        PostSearchDto noticeSearchDto = new PostSearchDto();
        noticeSearchDto.setPostNotice(1);
        List<PostDto> noticeList = postMapper.listNotices(noticeSearchDto);
        modelData.put("noticeList", noticeList);

        searchDto.setPostNotice(0);
        int totalCount = postMapper.countPosts(searchDto);
        pageDto.setTotalRows(totalCount);
        pageDto.calculatePagination();

        Map<String, Object> params = new HashMap<>();
        params.put("searchDto", searchDto);
        params.put("pageDto", pageDto);

        List<PostDto> postList = postMapper.listPosts(params);

        modelData.put("postList", postList);
        modelData.put("pagination", pageDto);
        modelData.put("search", searchDto);
        
        return modelData;
    }

    public Map<String, Object> getCreatePostFormData(HttpSession session) {
        Map<String, Object> modelData = new HashMap<>();
        modelData.put("postDto", new PostDto());
        modelData.put("currentAuthorId", getCurrentUserId(session));
        return modelData;
    }

    public Map<String, Object> getUpdatePostFormData(String postId, HttpSession session) {
        Map<String, Object> modelData = new HashMap<>();
        PostDto post = postMapper.getPost(postId);
        if (post != null) {
            modelData.put("post", post);
            modelData.put("existingFileUrl", post.getExistingFilePath());
            modelData.put("currentAuthorId", getCurrentUserId(session));
        } else {
            modelData.put("error", "게시물을 찾을 수 없습니다.");
        }
        return modelData;
    }

    public Map<String, Object> getPostDetailData(String postId) {
        Map<String, Object> modelData = new HashMap<>();
        PostDto post = postMapper.getPost(postId);
        if (post != null) {
            postMapper.incrementReadCount(postId);
            modelData.put("post", post);
            List<CommentDto> commentList = postMapper.selectCommentList(postId);
            modelData.put("commentList", commentList);
        } else {
            modelData.put("error", "게시물을 찾을 수 없습니다.");
        }
        return modelData;
    }

    public Map<String, Object> getDeletePostFormData(String postId, HttpSession session) {
        Map<String, Object> modelData = new HashMap<>();
        PostDto post = postMapper.getPost(postId);
        if (post != null) {
            modelData.put("post", post);
            modelData.put("currentAuthorId", getCurrentUserId(session));
        } else {
            modelData.put("error", "게시물을 찾을 수 없습니다.");
        }
        return modelData;
    }

    public Map<String, Object> getReplyPostFormData(String postId, HttpSession session) {
        Map<String, Object> modelData = new HashMap<>();
        PostDto parentPost = postMapper.getPost(postId);
        if (parentPost == null) {
            modelData.put("error", "원본 게시물을 찾을 수 없습니다.");
            return modelData;
        }
        modelData.put("postDto", new PostDto());
        modelData.put("parentPost", parentPost);
        modelData.put("currentAuthorId", getCurrentUserId(session));
        return modelData;
    }

    private synchronized String generateNewPostId() {
        String maxPostId = postMapper.getLastPostId();
        if (maxPostId == null || maxPostId.isEmpty()) {
            return "PO001";
        }
        try {
            if (maxPostId.matches("^PO\\d+$")) {
                String numberPart = maxPostId.substring(2);
                int number = Integer.parseInt(numberPart);
                number++;
                return "PO" + String.format("%03d", number);
            } else {
                return "PO001";
            }
        } catch (NumberFormatException e) {
            log.error("게시물 ID 생성 중 숫자 변환 오류: {}", e.getMessage());
            return "PO001";
        }
    }

    @Transactional
    public void saveComment(CommentDto commentDto, HttpSession session) {
        String writerId = getCurrentUserId(session);
        if (!StringUtils.hasText(writerId)) {
            throw new IllegalArgumentException("댓글 작성자 ID를 확인할 수 없습니다.");
        }
        commentDto.setWriterId(writerId);

        commentDto.setCommentId(generateNewCommentId());
        postMapper.insertComment(commentDto);
    }

    @Transactional
    public void updateComment(CommentDto commentDto) {
        if (!StringUtils.hasText(commentDto.getCommentId())) {
            throw new IllegalArgumentException("수정할 댓글 ID가 필요합니다.");
        }
        if (!StringUtils.hasText(commentDto.getCommentContent())) {
            throw new IllegalArgumentException("댓글 내용을 입력해주세요.");
        }
        postMapper.updateComment(commentDto);
    }

    @Transactional
    public void deleteComment(String commentId) {
        if (!StringUtils.hasText(commentId)) {
            throw new IllegalArgumentException("삭제할 댓글 ID가 필요합니다.");
        }
        postMapper.deleteComment(commentId);
    }

    private synchronized String generateNewCommentId() {
        String maxCommentId = postMapper.getLastCommentId();
        if (maxCommentId == null || maxCommentId.isEmpty()) {
            return "CO001";
        }
        try {
            if (maxCommentId.matches("^CO\\d+$")) {
                String numberPart = maxCommentId.substring(2);
                int number = Integer.parseInt(numberPart);
                number++;
                return "CO" + String.format("%03d", number);
            } else {
                return "CO001";
            }
        } catch (NumberFormatException e) {
            log.error("댓글 ID 생성 중 숫자 변환 오류: {}", e.getMessage());
            return "CO001";
        }
    }

    public String getCurrentUserId(HttpSession session) {
        String userId = (String) session.getAttribute("userId");
        if (userId == null) {
            userId = "P001";
        }
        return userId;
    }
}