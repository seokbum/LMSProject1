package com.ldb.lms.controller.board;

import com.ldb.lms.dto.ApiResponseDto;
import com.ldb.lms.dto.board.post.CommentDto;
import com.ldb.lms.dto.board.post.PostDto;
import com.ldb.lms.service.board.PostService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/post")
@RequiredArgsConstructor
public class PostApiController {

    private final PostService postService;

    private ResponseEntity<ApiResponseDto<Object>> createValidationErrorResponse(BindingResult bindingResult) {
        Map<String, String> errors = new HashMap<>();
        for (FieldError error : bindingResult.getFieldErrors()) {
            errors.put(error.getField(), error.getDefaultMessage());
        }
        return ResponseEntity.badRequest().body(new ApiResponseDto<>(false, "입력 값을 확인해주세요.", errors));
    }

    @PostMapping("createPost")
    public ResponseEntity<?> createPost(
            @Valid @RequestPart("postDto") PostDto postDto,
            BindingResult bindingResult,
            @RequestPart(value = "postFile", required = false) MultipartFile postFile,
            HttpServletRequest request,
            HttpSession session) {
        
        if (bindingResult.hasErrors()) {
            return createValidationErrorResponse(bindingResult);
        }
        postDto.setPostFile(postFile);
        return postService.handleCreatePostApi(postDto, request, session);
    }
    
    @PostMapping("updatePost")
    public ResponseEntity<?> updatePost(
            @Valid @RequestPart("postDto") PostDto postDto,
            BindingResult bindingResult,
            @RequestPart(value = "postFile", required = false) MultipartFile postFile,
            @RequestParam(value = "removeFile", required = false) Boolean removeFile,
            HttpServletRequest request,
            HttpSession session) {

        if (bindingResult.hasErrors()) {
            return createValidationErrorResponse(bindingResult);
        }
        postDto.setPostFile(postFile);
        return postService.handleUpdatePostApi(postDto, removeFile, request, session);
    }

    @PostMapping("deletePost/{postId}")
    public ResponseEntity<ApiResponseDto<String>> deletePost(
            @PathVariable String postId,
            @RequestParam("postPassword") String postPassword,
            HttpServletRequest request,
            HttpSession session) {
        
        return postService.handleDeletePostApi(postId, postPassword, request, session);
    }

    @PostMapping("comments/write")
    public ResponseEntity<ApiResponseDto<CommentDto>> writeComment(
            @Valid @RequestBody CommentDto commentDto,
            BindingResult bindingResult,
            HttpSession session) {
        
        if (bindingResult.hasErrors()) {
            String errorMessage = bindingResult.getFieldError().getDefaultMessage();
            return ResponseEntity.badRequest().body(new ApiResponseDto<>(false, errorMessage, null));
        }

        try {
            CommentDto savedComment = postService.saveComment(commentDto, session);
            return ResponseEntity.ok(new ApiResponseDto<>(true, "댓글이 등록되었습니다.", savedComment));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(new ApiResponseDto<>(false, e.getMessage(), null));
        }
    }

    @PutMapping("comments/update")
    public ResponseEntity<ApiResponseDto<String>> updateComment(
            @Valid @RequestBody CommentDto commentDto,
            BindingResult bindingResult,
            HttpSession session) {
        
        if (bindingResult.hasErrors()) {
            return ResponseEntity.badRequest().body(new ApiResponseDto<>(false, bindingResult.getFieldError().getDefaultMessage(), null));
        }
        try {
            postService.updateComment(commentDto, session);
            return ResponseEntity.ok(new ApiResponseDto<>(true, "댓글이 수정되었습니다.", null));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(new ApiResponseDto<>(false, e.getMessage(), null));
        }
    }

    @PostMapping("comments/delete/{commentId}")
    public ResponseEntity<ApiResponseDto<String>> deleteComment(
            @PathVariable String commentId,
            HttpSession session) {
        try {
            postService.deleteComment(commentId, session);
            return ResponseEntity.ok(new ApiResponseDto<>(true, "댓글이 삭제되었습니다.", null));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(new ApiResponseDto<>(false, e.getMessage(), null));
        }
    }
}