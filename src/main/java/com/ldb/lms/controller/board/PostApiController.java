package com.ldb.lms.controller.board;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import com.ldb.lms.dto.ApiResponseDto;
import com.ldb.lms.dto.board.post.PostDto;
import com.ldb.lms.dto.board.post.CommentDto;
import com.ldb.lms.service.board.PostService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/api/post")
@RequiredArgsConstructor
public class PostApiController {

    private final PostService postService;

    @PostMapping("/createPost")
    public ResponseEntity<ApiResponseDto<String>> createPost(
            @RequestPart("postDto") PostDto postDto,
            @RequestPart(value = "postFile", required = false) MultipartFile postFile,
            HttpServletRequest request,
            HttpSession session) {
        
        postDto.setPostFile(postFile);
        log.info("createPost API 호출: postTitle={}", postDto.getPostTitle());
        return postService.handleCreatePostApi(postDto, request, session);
    }

    @PostMapping("/updatePost")
    public ResponseEntity<ApiResponseDto<String>> updatePost(
            @RequestPart("postDto") PostDto postDto,
            @RequestPart(value = "postFile", required = false) MultipartFile postFile,
            @RequestParam(value = "removeFile", required = false) Boolean removeFile,
            HttpServletRequest request,
            HttpSession session) {
        
        postDto.setPostFile(postFile);
        log.info("updatePost API 호출: postId={}", postDto.getPostId());
        return postService.handleUpdatePostApi(postDto, removeFile, request, session);
    }

    @DeleteMapping("/deletePost/{postId}")
    public ResponseEntity<ApiResponseDto<String>> deletePost(
            @PathVariable String postId,
            @RequestParam("postPassword") String postPassword,
            HttpServletRequest request,
            HttpSession session) {
        
        log.info("deletePost API 호출: postId={}", postId);
        return postService.handleDeletePostApi(postId, postPassword, request, session);
    }

    @PostMapping("/imageUpload")
    public ResponseEntity<ApiResponseDto<Map<String, String>>> imageUpload(
            @RequestParam("file") MultipartFile file,
            HttpServletRequest request) {
        
        log.info("imageUpload API 호출: fileName={}", file.getOriginalFilename());
        return postService.handleImageUploadApi(file, request);
    }

    @PostMapping("/comments/write")
    public ResponseEntity<ApiResponseDto<String>> writeComment(
            @RequestBody CommentDto commentDto,
            HttpSession session) {
        
        try {
            postService.saveComment(commentDto, session);
            ApiResponseDto<String> response = new ApiResponseDto<>(true, "댓글이 성공적으로 등록되었습니다.", null);
            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException e) {
            ApiResponseDto<String> response = new ApiResponseDto<>(false, e.getMessage(), null);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        } catch (Exception e) {
            log.error("댓글 등록 중 서버 오류:", e);
            ApiResponseDto<String> response = new ApiResponseDto<>(false, "댓글 등록 중 오류가 발생했습니다.", null);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    @PutMapping("/comments/update")
    public ResponseEntity<ApiResponseDto<String>> updateComment(
            @RequestBody CommentDto commentDto,
            HttpSession session) {
        
        try {
            postService.updateComment(commentDto, session);
            ApiResponseDto<String> response = new ApiResponseDto<>(true, "댓글이 성공적으로 수정되었습니다.", null);
            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException e) {
            ApiResponseDto<String> response = new ApiResponseDto<>(false, e.getMessage(), null);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        } catch (Exception e) {
            log.error("댓글 수정 중 서버 오류:", e);
            ApiResponseDto<String> response = new ApiResponseDto<>(false, "댓글 수정 중 오류가 발생했습니다.", null);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    @PostMapping("/comments/delete/{commentId}")
    public ResponseEntity<ApiResponseDto<String>> deleteComment(
            @PathVariable String commentId,
            HttpSession session) {
        
        try {
            postService.deleteComment(commentId, session);
            ApiResponseDto<String> response = new ApiResponseDto<>(true, "댓글이 성공적으로 삭제되었습니다.", null);
            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException e) {
            ApiResponseDto<String> response = new ApiResponseDto<>(false, e.getMessage(), null);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        } catch (Exception e) {
            log.error("댓글 삭제 중 서버 오류:", e);
            ApiResponseDto<String> response = new ApiResponseDto<>(false, "댓글 삭제 중 오류가 발생했습니다.", null);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }
}