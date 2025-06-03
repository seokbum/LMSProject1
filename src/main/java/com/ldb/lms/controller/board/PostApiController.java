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
import jakarta.servlet.http.HttpServletResponse;

import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/api/post")
@RequiredArgsConstructor
public class PostApiController {

    private final PostService postService;

    @GetMapping("getPosts")
    public ResponseEntity<Map<String, Object>> getPosts(
            @ModelAttribute PostSearchDto searchDto,
            @ModelAttribute PostPaginationDto pageDto) {
        return ResponseEntity.ok(postService.listPosts(searchDto, pageDto));
    }

    @PostMapping("uploadImage")
    public ResponseEntity<Map<String, String>> uploadImage(
            @RequestParam("file") MultipartFile file,
            HttpServletRequest request) {
        return postService.handleImageUpload(file, request);
    }

    @PostMapping("create")
    public ResponseEntity<Map<String, String>> createPost(
            @RequestPart("post") PostDto postDto,
            @RequestPart(value = "file", required = false) MultipartFile file,
            HttpServletRequest request) {
        return postService.handleCreatePost(postDto, file, request);
    }

    @PostMapping("update")
    public ResponseEntity<Map<String, String>> updatePost(
            @RequestPart("post") PostDto postDto,
            @RequestPart(value = "file", required = false) MultipartFile file,
            HttpServletRequest request) {
        return postService.handleUpdatePost(postDto, file, request);
    }

    @PostMapping("delete")
    public ResponseEntity<Map<String, String>> deletePost(
            @RequestParam("postId") String postId,
            @RequestParam("password") String password) {
        return postService.handleDeletePost(postId, password);
    }

    @GetMapping("download")
    public void downloadFile(
            @RequestParam("filePath") String filePath,
            HttpServletResponse response,
            HttpServletRequest request) throws Exception {
        postService.handleFileDownload(filePath, response, request);
    }

    @PostMapping("comment")
    public ResponseEntity<Map<String, String>> writeComment(
            @RequestBody CommentDto commentDto) {
        return postService.handleWriteComment(commentDto);
    }

    @PostMapping("updateComment")
    public ResponseEntity<Map<String, String>> updateComment(
            @RequestBody CommentDto commentDto) {
        return postService.handleUpdateComment(commentDto);
    }

    @PostMapping("deleteComment")
    public ResponseEntity<Map<String, String>> deleteComment(
            @RequestParam("commentId") String commentId,
            @RequestParam("postId") String postId) {
        return postService.handleDeleteComment(commentId, postId);
    }
}