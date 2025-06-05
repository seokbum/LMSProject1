package com.ldb.lms.controller.board;

import com.ldb.lms.dto.ApiResponseDto;
import com.ldb.lms.dto.board.post.CommentDto;
import com.ldb.lms.dto.board.post.PostDto;
import com.ldb.lms.dto.board.post.PostPaginationDto;
import com.ldb.lms.dto.board.post.PostSearchDto;
import com.ldb.lms.service.board.PostService;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import java.util.Map;

@Slf4j
@Controller 
@RequestMapping("/api/post")
public class PostApiController {

    private final PostService postService;
    private final ObjectMapper objectMapper;

    public PostApiController(PostService postService) {
        this.postService = postService;
        this.objectMapper = new ObjectMapper();
    }

    @GetMapping
    @ResponseBody
    public ResponseEntity<ApiResponseDto<Map<String, Object>>> getPosts(
            @ModelAttribute PostSearchDto searchDto,
            @ModelAttribute PostPaginationDto pageDto,
            @RequestParam(value = "postType", defaultValue = "post") String postType) {
        return ResponseEntity.ok(postService.getPosts(searchDto, pageDto, postType));
    }

    @PostMapping("/uploadImage")
    @ResponseBody
    public ResponseEntity<ApiResponseDto<String>> uploadImage(
            @RequestParam("file") MultipartFile file,
            HttpServletRequest request) {
        return ResponseEntity.ok(postService.handleImageUpload(file, request));
    }

    @PostMapping("/create")
    @ResponseBody
    public ResponseEntity<ApiResponseDto<String>> createPost(
            @ModelAttribute PostDto postDto,
            @RequestPart(value = "file", required = false) MultipartFile file,
            HttpSession session,
            HttpServletRequest request) {
        postDto.setAuthorId("P001");
        return ResponseEntity.ok(postService.handleCreatePost(postDto, file, request));
    }

    @PostMapping("/update")
    @ResponseBody
    public ResponseEntity<ApiResponseDto<String>> updatePost(
            @RequestParam("post") String postJson,
            @RequestPart(value = "file", required = false) MultipartFile file,
            HttpSession session,
            HttpServletRequest request) throws Exception {
        PostDto postDto = objectMapper.readValue(postJson, PostDto.class);
        postDto.setAuthorId("P001");
        return ResponseEntity.ok(postService.handleUpdatePost(postDto, file, request));
    }

    @PostMapping("/reply")
    @ResponseBody
    public ResponseEntity<ApiResponseDto<String>> replyPost(
            @RequestParam("post") String postJson,
            @RequestPart(value = "file", required = false) MultipartFile file,
            HttpSession session,
            HttpServletRequest request) throws Exception {
        PostDto postDto = objectMapper.readValue(postJson, PostDto.class);
        postDto.setAuthorId("P001");
        return ResponseEntity.ok(postService.handleReplyPost(postDto, file, request));
    }

    @PostMapping("/comment")
    @ResponseBody
    public ResponseEntity<ApiResponseDto<String>> writeComment(
            @RequestBody CommentDto commentDto,
            HttpSession session,
            HttpServletRequest request) {
        commentDto.setWriterId("P001");
        return ResponseEntity.ok(postService.handleWriteComment(commentDto, request));
    }

    @PutMapping("/comment")
    @ResponseBody
    public ResponseEntity<ApiResponseDto<String>> updateComment(
            @RequestBody CommentDto commentDto,
            HttpSession session,
            HttpServletRequest request) {
        commentDto.setWriterId("P001");
        return ResponseEntity.ok(postService.handleUpdateComment(commentDto, request));
    }

    @DeleteMapping("/comment")
    @ResponseBody
    public ResponseEntity<ApiResponseDto<String>> deleteComment(
            @RequestBody Map<String, String> requestBody,
            HttpSession session,
            HttpServletRequest request) {
        String commentId = requestBody.get("commentId");
        String writerIdFromClient = requestBody.get("writerId");
        String loggedInWriterId = "P001";

        if (!loggedInWriterId.equals(writerIdFromClient)) {
            return ResponseEntity.ok(new ApiResponseDto<>(false, "댓글을 삭제할 권한이 없습니다.", null));
        }

        return ResponseEntity.ok(postService.handleDeleteComment(commentId, loggedInWriterId, request));
    }
}