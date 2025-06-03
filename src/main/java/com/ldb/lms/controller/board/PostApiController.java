package com.ldb.lms.controller.board;

import com.ldb.lms.dto.board.post.CommentDto;
import com.ldb.lms.dto.board.post.PostDto;
import com.ldb.lms.dto.board.post.PostPaginationDto;
import com.ldb.lms.dto.board.post.PostSearchDto;
import com.ldb.lms.service.board.PostService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;

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
    	searchDto.setPostNotice("notice".equals(postType) ? 1 : 0);
        return ResponseEntity.ok(postService.listPosts(searchDto, pageDto));
    }

    @PostMapping("uploadImage")
    public ResponseEntity<Map<String, String>> uploadImage(
            @RequestParam("file") MultipartFile file,
            HttpServletRequest request) {
        return postService.handleImageUpload(file, request);
    }

    @PostMapping("write")
    public ResponseEntity<Map<String, String>> writePost(
            @RequestPart("post") PostDto postDto,
            @RequestPart(value = "file", required = false) MultipartFile file,
            @RequestParam(value = "postType", defaultValue = "post") String postType,
            HttpServletRequest request,
            HttpSession session) {
        postDto.setPostNotice("notice".equals(postType) ? 1 : 0);
        return postService.handleWritePost(postDto, file, request, session);
    }

    @PostMapping("update")
    public ResponseEntity<Map<String, String>> updatePost(
            @RequestPart("post") PostDto postDto,
            @RequestPart(value = "file", required = false) MultipartFile file,
            @RequestParam(value = "postType", defaultValue = "post") String postType,
            HttpServletRequest request,
            HttpSession session) {
        postDto.setPostNotice("notice".equals(postType) ? 1 : 0);
        return postService.handleUpdatePost(postDto, file, request, session);
    }

    @PostMapping("delete")
    public ResponseEntity<Map<String, String>> deletePost(
            @RequestParam("postId") String postId,
            @RequestParam("password") String password,
            @RequestParam(value = "postType", defaultValue = "post") String postType,
            HttpSession session,
            HttpServletRequest request) {
        return postService.handleDeletePost(postId, password, session, request);
    }

    @PostMapping("comment")
    public ResponseEntity<Map<String, String>> writeComment(
            @RequestBody CommentDto commentDto,
            HttpSession session) {
        return postService.handleWriteComment(commentDto, session);
    }

    @PostMapping("updateComment")
    public ResponseEntity<Map<String, String>> updateComment(
            @RequestBody CommentDto commentDto,
            HttpSession session) {
        return postService.handleUpdateComment(commentDto, session);
    }

    @PostMapping("deleteComment")
    public ResponseEntity<Map<String, String>> deleteComment(
            @RequestParam("commentId") String commentId,
            @RequestParam("postId") String postId,
            HttpSession session) {
        return postService.handleDeleteComment(commentId, postId, session);
    }
}