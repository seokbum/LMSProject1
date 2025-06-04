package com.ldb.lms.service.board;

import com.ldb.lms.dto.board.post.CommentDto;
import com.ldb.lms.dto.board.post.PostDto;
import com.ldb.lms.dto.board.post.PostPaginationDto;
import com.ldb.lms.dto.board.post.PostSearchDto;
import com.ldb.lms.mapper.board.PostMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.web.multipart.MultipartFile;
import jakarta.servlet.http.HttpServletRequest;

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

    public String getAuthorName(String authorId) {
        // student 또는 professor 테이블에서 이름 조회 (임시 구현)
        return authorId.startsWith("P") ? "교수 사용자" : "학생 사용자";
    }

    private String handleFileUpload(MultipartFile file, HttpServletRequest request) throws IOException {
        if (file == null || file.isEmpty()) {
            return null;
        }
        String realPath = request.getServletContext().getRealPath("/");
        String uploadPath = realPath + UPLOAD_DIR;
        File dir = new File(uploadPath);
        if (!dir.exists()) {
            dir.mkdirs();
        }
        String originalFileName = file.getOriginalFilename();
        String uniqueFileName = System.currentTimeMillis() + "_" + originalFileName;
        File destFile = new File(uploadPath + "/" + uniqueFileName);
        file.transferTo(destFile);
        return UPLOAD_DIR + "/" + uniqueFileName;
    }

    public ResponseEntity<Map<String, Object>> getPosts(PostSearchDto searchDto, PostPaginationDto pageDto, String postType) {
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
        if (pageDto.getCurrentPage() == null || pageDto.getCurrentPage() < 1) {
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
        postDto.setAuthorId(authorId);
        model.addAttribute("postDto", postDto);
        model.addAttribute("userName", getAuthorName(authorId));
        return "board/post/createPost";
    }

    @Transactional
    public String prepareUpdatePostView(String postId, String authorId, Model model) {
        PostDto post = postMapper.getPost(postId);
        if (post == null) {
            model.addAttribute("error", "게시물을 찾을 수 없습니다.");
            return "redirect:/post/getPosts";
        }
        model.addAttribute("post", post);
        model.addAttribute("userName", getAuthorName(post.getAuthorId()));
        model.addAttribute("authorId", authorId);
        return "board/post/updatePost";
    }

    @Transactional
    public String preparePostDetailView(String postId, String readcnt, String authorId, Model model) {
        PostDto post = postMapper.getPost(postId);
        if (post == null) {
            model.addAttribute("error", "게시물을 찾을 수 없습니다.");
            return "redirect:/post/getPosts";
        }
        if (!"f".equals(readcnt)) {
            postMapper.incrementReadCount(postId);
            post.setPostReadCount(post.getPostReadCount() + 1);
        }
        model.addAttribute("post", post);
        model.addAttribute("commentList", postMapper.selectCommentList(postId));
        model.addAttribute("authorId", authorId);
        return "board/post/getPostDetail";
    }

    @Transactional
    public String prepareReplyPostView(String postId, String authorId, Model model) {
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
        model.addAttribute("userName", getAuthorName(authorId));
        return "board/post/replyPost";
    }

    @Transactional
    public String prepareDeletePostView(String postId, String authorId, Model model) {
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
    public ResponseEntity<Map<String, String>> handleCreatePost(PostDto postDto, HttpServletRequest request) {
        Map<String, String> response = new HashMap<>();
        try {
            if (postDto.getPostNotice() != null && postDto.getPostNotice() == 1 && !postDto.getAuthorId().startsWith("P")) {
                response.put("error", "학생은 공지사항을 작성할 수 없습니다.");
                return ResponseEntity.badRequest().body(response);
            }
            String filePath = handleFileUpload(postDto.getPostFile(), request);
            postDto.setExistingFilePath(filePath);
            String newPostId = generateNewPostId();
            postDto.setPostId(newPostId);
            postDto.setPostGroup(getNextGroup());
            postDto.setPostGroupLevel(0);
            postDto.setPostGroupStep(0);
            postDto.setUserName(getAuthorName(postDto.getAuthorId()));
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
    public ResponseEntity<Map<String, String>> handleUpdatePost(PostDto postDto, HttpServletRequest request) {
        Map<String, String> response = new HashMap<>();
        try {
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
            String filePath = handleFileUpload(postDto.getPostFile(), request);
            postDto.setExistingFilePath(filePath != null ? filePath : existingPost.getExistingFilePath());
            postDto.setUserName(getAuthorName(postDto.getAuthorId()));
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
    public ResponseEntity<Map<String, String>> handleReplyPost(PostDto postDto, HttpServletRequest request) {
        Map<String, String> response = new HashMap<>();
        try {
            PostDto parentPost = postMapper.getPost(postDto.getParentPostId());
            if (parentPost == null) {
                response.put("error", "부모 게시물을 찾을 수 없습니다.");
                return ResponseEntity.badRequest().body(response);
            }
            if (parentPost.getPostNotice() != null && parentPost.getPostNotice() == 1) {
                response.put("error", "공지사항에는 답글을 작성할 수 없습니다.");
                return ResponseEntity.badRequest().body(response);
            }
            String filePath = handleFileUpload(postDto.getPostFile(), request);
            postDto.setExistingFilePath(filePath);
            String newPostId = generateNewPostId();
            postDto.setPostId(newPostId);
            postDto.setPostGroup(parentPost.getPostGroup());
            postDto.setPostGroupLevel(parentPost.getPostGroupLevel() + 1);
            postMapper.updateGroupStep(Map.of("postGroup", parentPost.getPostGroup(), "postGroupStep", parentPost.getPostGroupStep() + 1));
            postDto.setPostGroupStep(parentPost.getPostGroupStep() + 1);
            postDto.setUserName(getAuthorName(postDto.getAuthorId()));
            postMapper.insertPost(postDto);
            response.put("message", "답글이 작성되었습니다.");
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
    public ResponseEntity<Map<String, String>> handleWriteComment(CommentDto commentDto) {
        Map<String, String> response = new HashMap<>();
        try {
            commentDto.setCommentId(generateNewCommentId());
            // writer_name은 PostMapper.xml에서 동적으로 매핑
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
            CommentDto existingComment = postMapper.selectComment(commentDto.getCommentId());
            if (existingComment == null) {
                response.put("error", "댓글을 찾을 수 없습니다.");
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
    public ResponseEntity<Map<String, String>> handleDeleteComment(String commentId, String postId) {
        Map<String, String> response = new HashMap<>();
        try {
            CommentDto comment = postMapper.selectComment(commentId);
            if (comment == null) {
                response.put("error", "댓글을 찾을 수 없습니다.");
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
                response.put("url", request.getContextPath() + "/" + filePath);
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