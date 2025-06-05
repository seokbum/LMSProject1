package com.ldb.lms.controller.board;

import com.ldb.lms.dto.board.post.*;
import com.ldb.lms.service.board.PostService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.web.multipart.MultipartFile;

import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/api/post")
@RequiredArgsConstructor
public class PostApiController {

    private final PostService postService;

    @GetMapping
    public ResponseEntity<Map<String, Object>> getPosts(
            @ModelAttribute PostSearchDto searchDto,
            @ModelAttribute PostPaginationDto pageDto,
            @RequestParam(value = "postType", defaultValue = "post") String postType) {
        return postService.getPosts(searchDto, pageDto, postType);
    }

    @PostMapping("/uploadImage")
    public ResponseEntity<Map<String, String>> uploadImage(
            @RequestParam("file") MultipartFile file,
            HttpServletRequest request) {
        return postService.handleImageUpload(file, request);
    }

    @PostMapping("/create")
    public ResponseEntity<Map<String, String>> createPost(
    		@ModelAttribute PostDto postDto,
            HttpServletRequest request) {
        // 테스트용 P001 하드코딩
        postDto.setAuthorId("P001");
        // 실제 구현 시: 세션에서 ID 가져오기
        // String authorId = (String) request.getSession().getAttribute("userId");
        // postDto.setAuthorId(authorId);
        return postService.handleCreatePost(postDto, postDto.getPostFile(),request);
    }

    @PostMapping("/update")
    public ResponseEntity<Map<String, String>> updatePost(
            @RequestBody PostDto postDto,
            @RequestPart(value = "file", required = false) MultipartFile file,
            HttpServletRequest request) {
        // 테스트용 P001 하드코딩
        postDto.setAuthorId("P001");
        // 실제 구현 시: String authorId = (String) request.getSession().getAttribute("userId");
        return postService.handleUpdatePost(postDto, file, request);
    }

    @PostMapping("/reply")
    public ResponseEntity<Map<String, String>> replyPost(
            @RequestBody PostDto postDto,
            @RequestPart(value = "file", required = false) MultipartFile file,
            HttpServletRequest request) {
        // 테스트용 P001 하드코딩
        postDto.setAuthorId("P001");
        // 실제 구현 시: String authorId = (String) request.getSession().getAttribute("userId");
        return postService.handleReplyPost(postDto, file, request);
    }

    @PostMapping("/delete")
    public ResponseEntity<Map<String, String>> deletePost(
            @RequestParam("postId") String postId,
            @RequestParam("pass") String password,
            @RequestParam("authorId") String authorId,
            HttpServletRequest request) {
        // 테스트용 P001 하드코딩
        authorId = "P001";
        // 실제 구현 시: authorId = (String) request.getSession().getAttribute("userId");
        return postService.handleDeletePost(postId, password, authorId);
    }

    @PostMapping("/comment")
    public ResponseEntity<Map<String, String>> writeComment(
            @RequestBody CommentDto commentDto,
            HttpServletRequest request) {
        // 테스트용 P001 하드코딩
        commentDto.setWriterId("P001");
        // 실제 구현 시: commentDto.setWriterId((String) request.getSession().getAttribute("userId"));
        return postService.handleWriteComment(commentDto);
    }

    @PutMapping("/comment")
    public ResponseEntity<Map<String, String>> updateComment(
            @RequestBody CommentDto commentDto,
            HttpServletRequest request) {
        // 테스트용 P001 하드코딩
        commentDto.setWriterId("P001");
        // 실제 구현 시: commentDto.setWriterId((String) request.getSession().getAttribute("userId"));
        return postService.handleUpdateComment(commentDto);
    }

    @DeleteMapping("/comment")
    public ResponseEntity<Map<String, String>> deleteComment(
            @RequestBody Map<String, String> request) {
        // 테스트용 P001 하드코딩
        String writerId = "P001";
        // 실제 구현 시: String writerId = (String) request.getAttribute("userId");
        return postService.handleDeleteComment(
                request.get("commentId"), writerId);
    }
}