package com.ldb.lms.service.board;

import com.ldb.lms.dto.board.post.*;
import com.ldb.lms.mapper.board.PostMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class PostService {
    private final PostMapper postMapper;
    private static final String UPLOAD_DIR = "dist/assets/post_upload";

    private synchronized String generateNewPostId() {
        String maxPostId = postMapper.getLastPostId();
        if (maxPostId == null || maxPostId.isEmpty()) {
            return "PO001";
        }
        try {
            int number = Integer.parseInt(maxPostId.substring(2)) + 1;
            return "PO" + String.format("%03d", number);
        } catch (NumberFormatException e) {
            log.error("Post ID generation failed: {}", maxPostId, e);
            return "PO001";
        }
    }

    private synchronized String generateNewCommentId() {
        String maxCommentId = postMapper.getLastCommentId();
        if (maxCommentId == null || maxCommentId.isEmpty()) {
            return "CM001";
        }
        try {
            int number = Integer.parseInt(maxCommentId.substring(2)) + 1;
            return "CM" + String.format("%03d", number);
        } catch (NumberFormatException e) {
            log.error("Comment ID generation failed: {}", maxCommentId, e);
            return "CM001";
        }
    }

    private synchronized String getNextGroup() {
        String maxGroup = postMapper.getMaxGroup();
        if (maxGroup == null || maxGroup.isEmpty()) {
            return "G001";
        }
        try {
            int number = Integer.parseInt(maxGroup.substring(1)) + 1;
            return "G" + String.format("%03d", number);
        } catch (NumberFormatException e) {
            log.error("Group ID generation failed: {}", maxGroup, e);
            return "G001";
        }
    }

    private String handleFileUpload(MultipartFile file, HttpServletRequest request) throws IOException {
        if (file == null || file.isEmpty()) {
            return null;
        }
        try {
            String realPath = request.getServletContext().getRealPath("/");
            String uploadPath = realPath + UPLOAD_DIR;
            File dir = new File(uploadPath);
            if (!dir.exists() && !dir.mkdirs()) {
                log.error("Failed to create upload directory: {}", uploadPath);
                throw new IOException("Failed to create upload directory: " + uploadPath);
            }
            String originalFileName = file.getOriginalFilename();
            String uniqueFileName = System.currentTimeMillis() + "_" + originalFileName;
            File destFile = new File(uploadPath + File.separator + uniqueFileName);
            file.transferTo(destFile);
            return "/" + UPLOAD_DIR + "/" + uniqueFileName;
        } catch (IOException e) {
            log.error("File upload failed: {}", file.getOriginalFilename(), e);
            throw e;
        }
    }

    public ResponseEntity<Map<String, Object>> getPosts(PostSearchDto searchDto, PostPaginationDto pageDto, String postType) {
        if (searchDto == null || pageDto == null) {
            return ResponseEntity.badRequest().body(Map.of("error", "Invalid parameters"));
        }
        if ("notice".equals(postType)) {
            searchDto.setPostNotice(1);
        } else {
            searchDto.setPostNotice(null);
        }
        Map<String, Object> result = new HashMap<>();
        int totalRows = postMapper.countPosts(searchDto);
        pageDto.setTotalRows(totalRows);

        if (pageDto.getItemsPerPage() == null) {
            pageDto.setItemsPerPage(10);
        }
        if (pageDto.getCurrentPage() == null || pageDto.getCurrentPage() <= 0) {
            pageDto.setCurrentPage(1);
        }

        pageDto.setTotalPages((int) Math.ceil((double) totalRows / pageDto.getItemsPerPage()));
        pageDto.setOffset((pageDto.getCurrentPage() - 1) * pageDto.getItemsPerPage());
        pageDto.setStartPage(Math.max(1, pageDto.getCurrentPage() - 2));
        pageDto.setEndPage(Math.min(pageDto.getTotalPages(), pageDto.getCurrentPage() + 2));

        List<PostDto> posts = postMapper.listPosts(Map.of("searchDto", searchDto, "pageDto", pageDto));
        result.put("posts", posts);
        result.put("pagination", pageDto);
        if ("post".equals(postType)) {
            result.put("notices", postMapper.listNotices(new PostSearchDto()));
        }
        return ResponseEntity.ok(result);
    }

    public String preparePostsView(PostSearchDto searchDto, PostPaginationDto pageDto, String postType, String authorId, Model model) {
        Map<String, Object> result = getPosts(searchDto, pageDto, postType).getBody();
        model.addAttribute("posts", result.get("posts"));
        model.addAttribute("notices", result.get("notices"));
        model.addAttribute("pagination", result.get("pagination"));
        model.addAttribute("searchDto", searchDto);
        model.addAttribute("authorId", authorId);
        return "board/post/getPosts";
    }

    public String prepareCreatePostView(String authorId, Model model) {
        PostDto postDto = new PostDto();
        // 테스트용 P001 하드코딩
        authorId = "P001";
        // 실제 구현 시: String authorId = (String) request.getSession().getAttribute("userId");
        postDto.setAuthorId(authorId);
        model.addAttribute("postDto", postDto);
        return "board/post/createPost";
    }

    @Transactional
    public String prepareUpdatePostView(String postId, String authorId, Model model) {
        if (postId == null) {
            model.addAttribute("error", "게시물 ID가 필요합니다.");
            return "redirect:/post/getPosts";
        }
        // 테스트용 P001 하드코딩
        authorId = "P001";
        // 실제 구현 시: String authorId = (String) request.getSession().getAttribute("userId");
        PostDto post = postMapper.getPost(postId);
        if (post == null) {
            model.addAttribute("error", "게시물을 찾을 수 없습니다.");
            return "redirect:/post/getPosts";
        }
        model.addAttribute("post", post);
        model.addAttribute("authorId", authorId);
        return "board/post/updatePost";
    }

    @Transactional
    public String preparePostDetailView(String postId, String readcnt, String authorId, Model model) {
        if (postId == null) {
            model.addAttribute("error", "게시물 ID가 필요합니다.");
            return "redirect:/post/getPosts";
        }
        // 테스트용 P001 하드코딩
        authorId = "P001";
        // 실제 구현 시: String authorId = (String) request.getSession().getAttribute("userId");
        PostDto post = postMapper.getPost(postId);
        if (post == null) {
            model.addAttribute("error", "게시물을 찾을 수 없습니다.");
            return "redirect:/post/getPosts";
        }
        if (!"f".equals(readcnt)) {
            postMapper.incrementReadCount(postId);
            post.setPostReadCount((post.getPostReadCount() != null ? post.getPostReadCount() : 0) + 1);
        }
        model.addAttribute("post", post);
        model.addAttribute("commentList", postMapper.selectCommentList(postId));
        model.addAttribute("authorId", authorId);
        return "board/post/getPostDetail";
    }

    @Transactional
    public String prepareReplyPostView(String postId, String authorId, Model model) {
        if (postId == null) {
            model.addAttribute("error", "부모 게시물 ID가 필요합니다.");
            return "redirect:/post/getPosts";
        }
        // 테스트용 P001 하드코딩
        authorId = "P001";
        // 실제 구현 시: String authorId = (String) request.getSession().getAttribute("userId");
        PostDto parentPost = postMapper.getPost(postId);
        if (parentPost == null) {
            model.addAttribute("error", "부모 게시물을 찾을 수 없습니다.");
            return "redirect:/post/getPosts";
        }
        PostDto postDto = new PostDto();
        postDto.setParentPostId(postId);
        postDto.setAuthorId(authorId);
        model.addAttribute("postDto", postDto);
        model.addAttribute("parentPostId", parentPost.getPostId());
        model.addAttribute("authorId", authorId);
        return "board/post/replyPost";
    }

    @Transactional
    public String prepareDeletePostView(String postId, String authorId, Model model) {
        if (postId == null) {
            model.addAttribute("error", "게시물 ID가 필요합니다.");
            return "redirect:/post/getPosts";
        }
        // 테스트용 P001 하드코딩
        authorId = "P001";
        // 실제 구현 시: String authorId = (String) request.getSession().getAttribute("userId");
        PostDto post = postMapper.getPost(postId);
        if (post == null) {
            model.addAttribute("error", "게시물을 찾을 수 없습니다.");
            return "redirect:/post/getPosts";
        }
        model.addAttribute("post", post);
        model.addAttribute("authorId", authorId);
        return "board/post/deletePost";
    }

    @Transactional
    public ResponseEntity<Map<String, String>> handleCreatePost(PostDto postDto, MultipartFile file, HttpServletRequest request) {
        Map<String, String> response = new HashMap<>();
        try {
            if (postDto == null || postDto.getAuthorId() == null || postDto.getPostTitle() == null || 
                postDto.getPostContent() == null || postDto.getPostPassword() == null) {
                response.put("error", "필수 입력값이 누락되었습니다.");
                return ResponseEntity.badRequest().body(response);
            }
            // 테스트용 P001 하드코딩
            postDto.setAuthorId("P001");
            // 실제 구현 시: String authorId = (String) request.getSession().getAttribute("userId");
            if (postDto.getPostNotice() != null && postDto.getPostNotice() == 1 && !postDto.getAuthorId().startsWith("P")) {
                response.put("error", "학생은 공지사항을 작성할 수 없습니다.");
                return ResponseEntity.badRequest().body(response);
            }
            String filePath = handleFileUpload(file, request);
            postDto.setExistingFilePath(filePath);
            String newPostId = generateNewPostId();
            postDto.setPostId(newPostId);
            postDto.setPostGroup(getNextGroup());
            postDto.setPostGroupLevel(0);
            postDto.setPostGroupStep(0);
            postMapper.insertPost(postDto);
            response.put("message", "게시물이 작성되었습니다.");
            response.put("redirectUrl", "/post/getPosts");
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("Post creation failed: {}", postDto, e);
            response.put("error", "게시물 작성 실패: " + e.getMessage());
            return ResponseEntity.status(500).body(response);
        }
    }

    @Transactional
    public ResponseEntity<Map<String, String>> handleUpdatePost(PostDto postDto, MultipartFile file, HttpServletRequest request) {
        Map<String, String> response = new HashMap<>();
        try {
            if (postDto == null || postDto.getPostId() == null || postDto.getPostPassword() == null || 
                postDto.getPostContent() == null) {
                response.put("error", "필수 입력값이 누락되었습니다.");
                return ResponseEntity.badRequest().body(response);
            }
            // 테스트용 P001 하드코딩
            postDto.setAuthorId("P001");
            // 실제 구현 시: String authorId = (String) request.getSession().getAttribute("userId");
            if (postDto.getPostNotice() != null && postDto.getPostNotice() == 1 && !postDto.getAuthorId().startsWith("P")) {
                response.put("error", "학생은 공지사항을 수정할 수 없습니다.");
                return ResponseEntity.badRequest().body(response);
            }
            PostDto existingPost = postMapper.getPost(postDto.getPostId());
            if (existingPost == null) {
                response.put("error", "게시물을 찾을 수 없습니다.");
                return ResponseEntity.badRequest().body(response);
            }
            if (!existingPost.getPostPassword().equals(postDto.getPostPassword())) {
                response.put("error", "비밀번호가 일치하지 않습니다.");
                return ResponseEntity.badRequest().body(response);
            }
            String filePath = handleFileUpload(file, request);
            postDto.setExistingFilePath(filePath != null ? filePath : existingPost.getExistingFilePath());
            postMapper.updatePost(postDto);
            response.put("message", "게시물이 수정되었습니다.");
            response.put("redirectUrl", "/post/getPostDetail?postId=" + postDto.getPostId());
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("Post update failed: {}", postDto, e);
            response.put("error", "게시물 수정 실패: " + e.getMessage());
            return ResponseEntity.status(500).body(response);
        }
    }

    @Transactional
    public ResponseEntity<Map<String, String>> handleReplyPost(PostDto postDto, MultipartFile file, HttpServletRequest request) {
        Map<String, String> response = new HashMap<>();
        try {
            if (postDto == null || postDto.getParentPostId() == null || postDto.getPostContent() == null) {
                response.put("error", "필수 입력값이 누락되었습니다.");
                return ResponseEntity.badRequest().body(response);
            }
            // 테스트용 P001 하드코딩
            postDto.setAuthorId("P001");
            // 실제 구현 시: String authorId = (String) request.getSession().getAttribute("userId");
            PostDto parentPost = postMapper.getPost(postDto.getParentPostId());
            if (parentPost == null) {
                response.put("error", "부모 게시물을 찾을 수 없습니다.");
                return ResponseEntity.badRequest().body(response);
            }
            if (parentPost.getPostNotice() != null && parentPost.getPostNotice() == 1) {
                response.put("error", "공지사항에는 답글을 작성할 수 없습니다.");
                return ResponseEntity.badRequest().body(response);
            }
            String filePath = handleFileUpload(file, request);
            postDto.setExistingFilePath(filePath);
            String newPostId = generateNewPostId();
            postDto.setPostId(newPostId);
            postDto.setPostGroup(parentPost.getPostGroup());
            postDto.setPostGroupLevel(parentPost.getPostGroupLevel() + 1);
            postMapper.updateGroupStep(Map.of("postGroup", parentPost.getPostGroup(), "postGroupStep", parentPost.getPostGroupStep() + 1));
            postDto.setPostGroupStep(parentPost.getPostGroupStep() + 1);
            postMapper.insertPost(postDto);
            response.put("message", "답글을 작성했습니다.");
            response.put("redirectUrl", "/post/getPosts");
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("Reply post failed: {}", postDto, e);
            response.put("error", "답글 작성 실패: " + e.getMessage());
            return ResponseEntity.status(500).body(response);
        }
    }

    @Transactional
    public ResponseEntity<Map<String, String>> handleDeletePost(String postId, String password, String authorId, HttpServletRequest request) {
        Map<String, String> response = new HashMap<>();
        try {
            if (postId == null || password == null || authorId == null) {
                response.put("error", "잘못된 요청 데이터");
                return ResponseEntity.badRequest().body(response);
            }
            // 테스트용 P001 하드코딩
            authorId = "P001";
            // 실제 구현 시: String authorId = (String) request.getSession().getAttribute("userId");
            PostDto post = postMapper.getPost(postId);
            if (post == null) {
                response.put("error", "게시물을 찾을 수 없습니다.");
                return ResponseEntity.badRequest().body(response);
            }
            if (!post.getPostPassword().equals(password)) {
                response.put("error", "비밀번호가 일치하지 않습니다.");
                return ResponseEntity.badRequest().body(response);
            }
            if (!post.getAuthorId().equals(authorId)) {
                response.put("error", "작성자만 삭제할 수 있습니다.");
                return ResponseEntity.badRequest().body(response);
            }
            if (post.getExistingFilePath() != null) {
                String realPath = request.getServletContext().getRealPath("/");
                File file = new File(realPath + post.getExistingFilePath());
                if (file.exists()) {
                    file.delete();
                }
            }
            postMapper.deleteCommentsByPostId(postId);
            postMapper.deletePost(postId);
            response.put("message", "게시물이 삭제되었습니다.");
            response.put("redirectUrl", "/post/getPosts");
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("Post deletion failed: {}", postId, e);
            response.put("error", "게시물 삭제 실패: " + e.getMessage());
            return ResponseEntity.status(500).body(response);
        }
    }

    @Transactional
    public ResponseEntity<Map<String, String>> handleDeletePost(String postId, String password, String authorId) {
        Map<String, String> response = new HashMap<>();
        try {
            if (postId == null || password == null || authorId == null) {
                response.put("error", "잘못된 요청 데이터");
                return ResponseEntity.badRequest().body(response);
            }
            // 테스트용 P001 하드코딩
            authorId = "P001";
            // 실제 구현 시: String authorId = (String) request.getSession().getAttribute("userId");
            PostDto post = postMapper.getPost(postId);
            if (post == null) {
                response.put("error", "게시물을 찾을 수 없습니다.");
                return ResponseEntity.badRequest().body(response);
            }
            if (!post.getPostPassword().equals(password)) {
                response.put("error", "비밀번호가 일치하지 않습니다.");
                return ResponseEntity.badRequest().body(response);
            }
            if (!post.getAuthorId().equals(authorId)) {
                response.put("error", "작성자만 삭제할 수 있습니다.");
                return ResponseEntity.badRequest().body(response);
            }
            // 파일 삭제는 서버 경로를 알 수 없으므로 생략 (필요 시 별도 처리)
            postMapper.deleteCommentsByPostId(postId);
            postMapper.deletePost(postId);
            response.put("message", "게시물이 삭제되었습니다.");
            response.put("redirectUrl", "/post/getPosts");
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("Post deletion failed: {}", postId, e);
            response.put("error", "게시물 삭제 실패: " + e.getMessage());
            return ResponseEntity.status(500).body(response);
        }
    }

    @Transactional
    public ResponseEntity<Map<String, String>> handleWriteComment(CommentDto commentDto) {
        Map<String, String> response = new HashMap<>();
        try {
            if (commentDto == null || commentDto.getPostId() == null || 
                commentDto.getWriterId() == null || commentDto.getCommentContent() == null) {
                response.put("error", "필수 입력값이 누락되었습니다.");
                return ResponseEntity.badRequest().body(response);
            }
            // 테스트용 P001 하드코딩
            commentDto.setWriterId("P001");
            // 실제 구현 시: commentDto.setWriterId((String) request.getSession().getAttribute("userId"));
            // writerName은 Mapper에서 조인으로 처리
            commentDto.setCommentId(generateNewCommentId());
            postMapper.insertComment(commentDto);
            response.put("message", "댓글이 등록되었습니다.");
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("Comment creation failed: {}", commentDto, e);
            response.put("error", "댓글 등록 실패: " + e.getMessage());
            return ResponseEntity.status(500).body(response);
        }
    }

    @Transactional
    public ResponseEntity<Map<String, String>> handleUpdateComment(CommentDto commentDto) {
        Map<String, String> response = new HashMap<>();
        try {
            if (commentDto == null || commentDto.getCommentId() == null || 
                commentDto.getCommentContent() == null || commentDto.getWriterId() == null) {
                response.put("error", "필수 입력값이 누락되었습니다.");
                return ResponseEntity.badRequest().body(response);
            }
            // 테스트용 P001 하드코딩
            commentDto.setWriterId("P001");
            // 실제 구현 시: commentDto.setWriterId((String) request.getSession().getAttribute("userId"));
            CommentDto existingComment = postMapper.selectComment(commentDto.getCommentId());
            if (existingComment == null) {
                response.put("error", "댓글을 찾을 수 없습니다.");
                return ResponseEntity.badRequest().body(response);
            }
            if (!existingComment.getWriterId().equals(commentDto.getWriterId())) {
                response.put("error", "작성자만 수정할 수 있습니다.");
                return ResponseEntity.badRequest().body(response);
            }
            postMapper.updateComment(commentDto);
            response.put("message", "댓글이 수정되었습니다.");
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("Comment update failed: {}", commentDto, e);
            response.put("error", "댓글 수정 실패: " + e.getMessage());
            return ResponseEntity.status(500).body(response);
        }
    }

    @Transactional
    public ResponseEntity<Map<String, String>> handleDeleteComment(String commentId, String writerId) {
        Map<String, String> response = new HashMap<>();
        try {
            if (commentId == null || writerId == null) {
                response.put("error", "잘못된 요청 데이터");
                return ResponseEntity.badRequest().body(response);
            }
            // 테스트용 P001 하드코딩
            writerId = "P001";
            // 실제 구현 시: String writerId = (String) request.getSession().getAttribute("userId");
            CommentDto comment = postMapper.selectComment(commentId);
            if (comment == null) {
                response.put("error", "댓글을 찾을 수 없습니다.");
                return ResponseEntity.badRequest().body(response);
            }
            if (!comment.getWriterId().equals(writerId)) {
                response.put("error", "작성자만 삭제할 수 있습니다.");
                return ResponseEntity.badRequest().body(response);
            }
            postMapper.deleteComment(commentId);
            response.put("message", "댓글이 삭제되었습니다.");
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("Comment deletion failed: {}", commentId, e);
            response.put("error", "댓글 삭제 실패: " + e.getMessage());
            return ResponseEntity.status(500).body(response);
        }
    }

    @Transactional
    public ResponseEntity<Map<String, String>> handleImageUpload(MultipartFile file, HttpServletRequest request) {
        Map<String, String> response = new HashMap<>();
        try {
            String filePath = handleFileUpload(file, request);
            if (filePath != null) {
                response.put("url", request.getContextPath() + filePath);
                return ResponseEntity.ok(response);
            } else {
                response.put("error", "이미지 업로드 실패");
                return ResponseEntity.badRequest().body(response);
            }
        } catch (Exception e) {
            log.error("Image upload failed", e);
            response.put("error", "이미지 업로드 실패: " + e.getMessage());
            return ResponseEntity.status(500).body(response);
        }
    }
}