package com.ldb.lms.service.board;

import com.ldb.lms.dto.board.post.PostDto;
import com.ldb.lms.dto.board.post.PostPaginationDto;
import com.ldb.lms.dto.board.post.PostSearchDto;
import com.ldb.lms.dto.board.post.CommentDto;
import com.ldb.lms.mapper.board.PostMapper;
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
import java.io.File;
import java.nio.file.Files;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class PostService {

    private final PostMapper postMapper;

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

    private void validatePostDto(PostDto postDto, String authorId) {
        if (postDto == null) {
            log.error("validatePostDto: PostDto is null");
            throw new IllegalArgumentException("게시물 데이터가 필요합니다.");
        }
        if (!StringUtils.hasText(postDto.getPostTitle())) {
            log.error("validatePostDto: 제목이 없습니다.");
            throw new IllegalArgumentException("제목은 필수입니다.");
        }
        if (!StringUtils.hasText(postDto.getPostContent())) {
            log.error("validatePostDto: 내용이 없습니다.");
            throw new IllegalArgumentException("내용은 필수입니다.");
        }
        if (!StringUtils.hasText(postDto.getPostPassword())) {
            log.error("validatePostDto: 비밀번호가 없습니다.");
            throw new IllegalArgumentException("비밀번호는 필수입니다.");
        }
        if (!StringUtils.hasText(authorId)) {
            log.error("validatePostDto: 작성자 ID가 유효하지 않습니다.");
            throw new SecurityException("사용자 정보를 확인할 수 없습니다.");
        }
    }

    private void validateCommentDto(CommentDto commentDto) {
        if (commentDto == null) {
            log.error("validateCommentDto: CommentDto is null");
            throw new IllegalArgumentException("댓글 데이터가 필요합니다.");
        }
        if (!StringUtils.hasText(commentDto.getCommentContent())) {
            log.error("validateCommentDto: 댓글 내용이 없습니다.");
            throw new IllegalArgumentException("댓글 내용은 필수입니다.");
        }
    }

    @Transactional
    public void savePost(PostDto postDto, String authorId, HttpServletRequest request) {
        validatePostDto(postDto, authorId);

        try {
            String lastPostId = postMapper.getLastPostId();
            long nextId = lastPostId != null ? Long.parseLong(lastPostId.replace("PO", "")) + 1 : 1;
            String postId = "PO" + String.format("%03d", nextId);
            postDto.setPostId(postId);
            postDto.setAuthorId(authorId);

            MultipartFile file = postDto.getPostFile();
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

            postDto.setExistingFilePath(filePath);

            if (StringUtils.hasText(postDto.getPostGroup())) {
                postMapper.updateGroupStep(Integer.parseInt(postDto.getPostGroup()), Integer.parseInt(postDto.getPostGroupStep()));
            }

            postMapper.insertPost(postDto);
            log.info("Post saved: postId={}", postId);
        } catch (Exception e) {
            log.error("savePost: 게시물 저장 실패", e);
            throw new RuntimeException("게시물 저장 실패: " + e.getMessage(), e);
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

    public ResponseEntity<Map<String, String>> handleCreatePost(PostDto postDto, MultipartFile file, HttpServletRequest request) {
        Map<String, String> response = new HashMap<>();
        try {
            // 임시 authorId 설정 (운영 환경에서는 인증된 사용자 정보 사용)
            String authorId = "S001"; // 테스트용
            postDto.setPostFile(file);
            savePost(postDto, authorId, request);
            response.put("message", "게시물이 성공적으로 저장되었습니다.");
            response.put("redirectUrl", request.getContextPath() + "/post/getPostDetail?postId=" + postDto.getPostId());
            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException | SecurityException e) {
            log.warn("handleCreatePost: 잘못된 요청 - {}", e.getMessage());
            response.put("error", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        } catch (Exception e) {
            log.error("handleCreatePost: 게시물 저장 실패", e);
            response.put("error", "게시물 저장 중 오류 발생: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    public ResponseEntity<Map<String, String>> handleUpdatePost(PostDto postDto, MultipartFile file, HttpServletRequest request) {
        Map<String, String> response = new HashMap<>();
        try {
            // 임시 authorId 설정 (운영 환경에서는 인증된 사용자 정보 사용)
            String authorId = "S001"; // 테스트용
            postDto.setPostFile(file);
            updatePost(postDto, authorId, request);
            response.put("message", "게시물이 수정되었습니다.");
            response.put("redirectUrl", request.getContextPath() + "/post/getPostDetail?postId=" + postDto.getPostId());
            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException | SecurityException e) {
            log.warn("handleUpdatePost: 잘못된 요청 - {}", e.getMessage());
            response.put("error", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        } catch (Exception e) {
            log.error("handleUpdatePost: 게시물 수정 실패", e);
            response.put("error", "게시물 수정 중 오류 발생: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    public void populatePostsModel(PostSearchDto searchDto, PostPaginationDto pageDto, Model model) {
        Map<String, Object> result = listPosts(searchDto, pageDto);
        log.debug("populatePostsModel - notices: {}, posts: {}, pagination: {}", result.get("notices"), result.get("posts"), result.get("pagination"));
        model.addAttribute("notices", result.get("notices"));
        model.addAttribute("posts", result.get("posts"));
        model.addAttribute("pagination", result.get("pagination"));
        model.addAttribute("searchDto", searchDto);
    }

    public void prepareCreatePost(Model model) {
        // 임시 authorId 설정 (운영 환경에서는 인증된 사용자 정보 사용)
        String authorId = "S001"; // 테스트용
        PostDto postDto = new PostDto();
        postDto.setAuthorId(authorId);
        model.addAttribute("postDto", postDto);
    }

    public void populatePostDetail(String postId, Model model) {
        try {
            // 임시 authorId 설정 (운영 환경에서는 인증된 사용자 정보 사용)
            String authorId = "S001"; // 테스트용
            PostDto post = getPost(postId);
            if (post == null) {
                model.addAttribute("msg", "게시물이 존재하지 않습니다.");
                return;
            }
            model.addAttribute("post", post);
            model.addAttribute("commentList", postMapper.selectCommentList(postId));
            model.addAttribute("isLoggedIn", true);
            model.addAttribute("authorId", authorId);
        } catch (Exception e) {
            log.error("populatePostDetail: 게시물 조회 실패, postId: {}", postId, e);
            model.addAttribute("msg", "게시물 조회 중 오류 발생: " + e.getMessage());
        }
    }

    public void prepareDeletePost(String postId, Model model) {
        // 임시 authorId 설정 (운영 환경에서는 인증된 사용자 정보 사용)
        String authorId = "S001"; // 테스트용
        PostDto post = getPost(postId);
        if (post == null) {
            model.addAttribute("msg", "게시물이 존재하지 않습니다.");
            model.addAttribute("post", null);
            return;
        }
        if (!post.getAuthorId().equals(authorId)) {
            model.addAttribute("msg", "삭제 권한이 없습니다.");
            model.addAttribute("post", post);
            return;
        }
        model.addAttribute("post", post);
    }

    public ResponseEntity<Map<String, String>> handleDeletePost(String postId, String password) {
        Map<String, String> response = new HashMap<>();
        try {
            // 임시 authorId 설정 (운영 환경에서는 인증된 사용자 정보 사용)
            String authorId = "S001"; // 테스트용
            deletePost(postId, authorId, password);
            response.put("message", "게시물이 삭제되었습니다.");
            response.put("redirectUrl", "/post/getPosts");
            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException e) {
            log.warn("handleDeletePost: 잘못된 요청 - {}", e.getMessage());
            response.put("error", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        } catch (Exception e) {
            log.error("handleDeletePost: 게시물 삭제 실패, postId: {}", postId, e);
            response.put("error", "게시물 삭제 중 오류 발생: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    public void prepareUpdatePost(String postId, Model model) {
        // 임시 authorId 설정 (운영 환경에서는 인증된 사용자 정보 사용)
        String authorId = "S001"; // 테스트용
        PostDto post = getPost(postId);
        if (post != null) {
            post.setPostFile(null);
        }
        model.addAttribute("post", post);
    }

    public void prepareReplyPost(String postId, Model model) {
        // 임시 authorId 설정 (운영 환경에서는 인증된 사용자 정보 사용)
        String authorId = "S001"; // 테스트용
        PostDto parentPost = getPost(postId);
        if (parentPost == null) {
            model.addAttribute("msg", "원본 게시물이 존재하지 않습니다.");
            return;
        }
        if (parentPost.getPostNotice() == 1) {
            model.addAttribute("msg", "공지사항에는 답글을 작성할 수 없습니다.");
            return;
        }
        PostDto postDto = new PostDto();
        postDto.setPostGroup(parentPost.getPostGroup());
        postDto.setPostGroupLevel(parentPost.getPostGroupLevel() + 1);
        postDto.setPostGroupStep(parentPost.getPostGroupStep());
        postDto.setPostTitle("RE: " + parentPost.getPostTitle());
        postDto.setAuthorId(authorId);
        model.addAttribute("postDto", postDto);
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

    public Map<String, Object> listPosts(PostSearchDto searchDto, PostPaginationDto pageDto) {
        Integer pageSize = pageDto.getItemsPerPage() != null ? pageDto.getItemsPerPage() : 10;
        Integer currentPage = pageDto.getCurrentPage() != null && pageDto.getCurrentPage() > 0 ? pageDto.getCurrentPage() : 1;
        log.info("listPosts: pageSize={}, currentPage={}", pageSize, currentPage);
        Integer totalRows = postMapper.countPosts(searchDto);
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

        List<Map<String, Object>> notices = postMapper.listNotices(param);
        List<Map<String, Object>> posts = postMapper.listPosts(param);

        Map<String, Object> response = new HashMap<>();
        response.put("notices", notices);
        response.put("posts", posts);
        response.put("pagination", pageDto);
        return response;
    }

    @Transactional
    public PostDto getPost(String postId) {
        if (!StringUtils.hasText(postId)) {
            log.error("getPost: postId가 유효하지 않습니다.");
            throw new IllegalArgumentException("게시물 ID는 필수입니다.");
        }
        try {
            postMapper.incrementReadCount(postId);
            return postMapper.getPost(postId);
        } catch (Exception e) {
            log.error("getPost: 게시물 조회 실패, postId: {}", postId, e);
            throw new RuntimeException("게시물 조회 실패: " + e.getMessage(), e);
        }
    }

    @Transactional
    public void deletePost(String postId, String authorId, String password) {
        if (!StringUtils.hasText(authorId)) {
            log.error("deletePost: authorId가 유효하지 않습니다.");
            throw new SecurityException("사용자 정보를 확인할 수 없습니다.");
        }
        if (!StringUtils.hasText(password)) {
            log.error("deletePost: 비밀번호가 입력되지 않았습니다.");
            throw new IllegalArgumentException("비밀번호를 입력해주세요.");
        }
        try {
            PostDto post = postMapper.getPost(postId);
            if (post == null) {
                log.warn("deletePost: 게시물이 존재하지 않습니다, postId: {}", postId);
                throw new IllegalArgumentException("게시물이 존재하지 않습니다.");
            }
            if (!post.getAuthorId().equals(authorId)) {
                log.warn("deletePost: 삭제 권한이 없습니다, postId: {}, authorId: {}", postId, authorId);
                throw new IllegalArgumentException("삭제 권한이 없습니다.");
            }
            if (!post.getPostPassword().equals(password)) {
                log.warn("deletePost: 비밀번호가 일치하지 않습니다, postId: {}", postId);
                throw new IllegalArgumentException("비밀번호가 일치하지 않습니다.");
            }

            if (StringUtils.hasText(post.getExistingFilePath())) {
                log.warn("deletePost: 파일 삭제는 호출자가 처리해야 합니다. filePath: {}", post.getExistingFilePath());
            }

            postMapper.deleteWithComments(postId);
            postMapper.deletePost(postId);
            log.info("Post deleted: postId={}", postId);
        } catch (IllegalArgumentException e) {
            throw e;
        } catch (Exception e) {
            log.error("deletePost: 게시물 삭제 실패, postId: {}", postId, e);
            throw new RuntimeException("게시물 삭제 실패: " + e.getMessage(), e);
        }
    }

    @Transactional
    public void updatePost(PostDto postDto, String authorId, HttpServletRequest request) {
        if (!StringUtils.hasText(authorId)) {
            log.error("updatePost: authorId가 유효하지 않습니다.");
            throw new SecurityException("사용자 정보를 확인할 수 없습니다.");
        }
        if (!StringUtils.hasText(postDto.getPostPassword())) {
            log.error("updatePost: 비밀번호가 입력되지 않았습니다.");
            throw new IllegalArgumentException("비밀번호를 입력해주세요.");
        }

        try {
            PostDto existingPost = postMapper.getPost(postDto.getPostId());
            if (existingPost == null) {
                throw new IllegalArgumentException("게시물이 존재하지 않습니다.");
            }
            if (!existingPost.getAuthorId().equals(authorId)) {
                throw new IllegalArgumentException("수정 권한이 없습니다.");
            }
            if (!existingPost.getPostPassword().equals(postDto.getPostPassword())) {
                throw new IllegalArgumentException("비밀번호가 일치하지 않습니다.");
            }

            MultipartFile file = postDto.getPostFile();
            String filePath = postDto.getExistingFilePath();
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

            postDto.setExistingFilePath(filePath);
            postMapper.updatePost(postDto);
            log.info("Post updated: postId={}", postDto.getPostId());
        } catch (Exception e) {
            log.error("updatePost: 게시물 수정 실패", e);
            throw new RuntimeException("게시물 수정 중 오류 발생: " + e.getMessage(), e);
        }
    }

    @Transactional
    public ResponseEntity<Map<String, String>> handleWriteComment(CommentDto commentDto) {
        Map<String, String> response = new HashMap<>();
        try {
            // 임시 authorId 설정 (운영 환경에서는 인증된 사용자 정보 사용)
            String authorId = "S001"; // 테스트용
            validateCommentDto(commentDto);
            String lastCommentId = postMapper.getLastCommentId();
            long nextId = lastCommentId != null ? Long.parseLong(lastCommentId.replace("C", "")) + 1 : 1;
            String commentId = "C" + String.format("%03d", nextId);
            commentDto.setCommentId(commentId);
            commentDto.setWriterId(authorId);
            postMapper.insertComment(commentDto);
            response.put("message", "댓글이 작성되었습니다.");
            response.put("redirectUrl", "/post/getPostDetail?postId=" + commentDto.getPostId());
            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException e) {
            log.warn("handleWriteComment: 잘못된 요청 - {}", e.getMessage());
            response.put("error", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        } catch (Exception e) {
            log.error("handleWriteComment: 댓글 작성 실패", e);
            response.put("error", "댓글 작성 중 오류 발생: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    @Transactional
    public ResponseEntity<Map<String, String>> handleUpdateComment(CommentDto commentDto) {
        Map<String, String> response = new HashMap<>();
        try {
            // 임시 authorId 설정 (운영 환경에서는 인증된 사용자 정보 사용)
            String authorId = "S001"; // 테스트용
            CommentDto existingComment = postMapper.selectComment(commentDto.getCommentId());
            if (existingComment == null) {
                throw new IllegalArgumentException("댓글이 존재하지 않습니다.");
            }
            if (!existingComment.getWriterId().equals(authorId)) {
                throw new IllegalArgumentException("댓글 수정 권한이 없습니다.");
            }
            validateCommentDto(commentDto);
            postMapper.updateComment(commentDto);
            response.put("message", "댓글이 수정되었습니다.");
            response.put("redirectUrl", "/post/getPostDetail?postId=" + commentDto.getPostId());
            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException e) {
            log.warn("handleUpdateComment: 잘못된 요청 - {}", e.getMessage());
            response.put("error", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        } catch (Exception e) {
            log.error("handleUpdateComment: 댓글 수정 실패", e);
            response.put("error", "댓글 수정 중 오류 발생: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    @Transactional
    public ResponseEntity<Map<String, String>> handleDeleteComment(String commentId, String postId) {
        Map<String, String> response = new HashMap<>();
        try {
            // 임시 authorId 설정 (운영 환경에서는 인증된 사용자 정보 사용)
            String authorId = "S001"; // 테스트용
            CommentDto comment = postMapper.selectComment(commentId);
            if (comment == null) {
                throw new IllegalArgumentException("댓글이 존재하지 않습니다.");
            }
            if (!comment.getWriterId().equals(authorId)) {
                throw new IllegalArgumentException("댓글 삭제 권한이 없습니다.");
            }
            postMapper.deleteComment(commentId);
            response.put("message", "댓글이 삭제되었습니다.");
            response.put("redirectUrl", "/post/getPostDetail?postId=" + postId);
            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException e) {
            log.warn("handleDeleteComment: 잘못된 요청 - {}", e.getMessage());
            response.put("error", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        } catch (Exception e) {
            log.error("handleDeleteComment: 댓글 삭제 실패", e);
            response.put("error", "댓글 삭제 중 오류 발생: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }
}