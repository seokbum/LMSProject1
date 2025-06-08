package com.ldb.lms.controller.board;

import java.util.Map;

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

import com.ldb.lms.dto.ApiResponseDto;
import com.ldb.lms.dto.board.post.CommentDto;
import com.ldb.lms.dto.board.post.PostDto;
import com.ldb.lms.service.board.PostService;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequestMapping("/api/post")
@RequiredArgsConstructor
public class PostApiController {

    private final PostService postService;

    @PostMapping("/uploadImage")
    public ResponseEntity<ApiResponseDto<Map<String, String>>> uploadImage(
            @RequestParam("file") MultipartFile file,
            HttpServletRequest request) {
        log.info("uploadImage API 호출: 파일명 = {}", file != null ? file.getOriginalFilename() : "없음");
        return postService.handleImageUploadApi(file, request);
    }

    @PostMapping("/create")
    public ResponseEntity<ApiResponseDto<String>> createPostApi(
            @RequestPart("post") PostDto postDto,
            @RequestPart(value = "file", required = false) MultipartFile file,
            HttpServletRequest request,
            HttpSession session) {
        log.info("createPostApi 호출 - 제목: {}", postDto.getPostTitle());
        return postService.handleCreatePostApi(postDto, file, request, session);
    }

    @PutMapping("/update")
    public ResponseEntity<ApiResponseDto<String>> updatePostApi(
            @RequestPart("post") PostDto postDto,
            @RequestPart(value = "file", required = false) MultipartFile file,
            @RequestParam(value = "removeFile", required = false) Boolean removeFile,
            HttpServletRequest request,
            HttpSession session) {
        log.info("updatePostApi 호출 - 게시물 ID: {}, 제목: {}", postDto.getPostId(), postDto.getPostTitle());
        return postService.handleUpdatePostApi(postDto, file, removeFile, request, session);
    }

    @DeleteMapping("/delete/{postId}")
    public ResponseEntity<ApiResponseDto<String>> deletePostApi(
            @PathVariable("postId") String postId,
            @RequestParam("postPassword") String postPassword,
            HttpServletRequest request,
            HttpSession session) {
        log.info("deletePostApi 호출 - 게시물 ID: {}", postId);
        return postService.handleDeletePostApi(postId, postPassword, request, session);
    }

    @PostMapping("/reply")
    public ResponseEntity<ApiResponseDto<String>> replyPostApi(
            @RequestPart("post") PostDto postDto,
            @RequestPart(value = "file", required = false) MultipartFile file,
            HttpServletRequest request,
            HttpSession session) {
        log.info("replyPostApi 호출 - 부모 게시물 ID: {}, 제목: {}", postDto.getParentPostId(), postDto.getPostTitle());
        return postService.handleCreatePostApi(postDto, file, request, session); // 답변 글도 결국은 새로운 글 생성과 동일 로직
    }

    @PostMapping("/comment")
    public ResponseEntity<ApiResponseDto<String>> addCommentApi(
            @RequestBody CommentDto commentDto,
            HttpSession session) {
        log.info("addCommentApi 호출 - 게시물 ID: {}, 내용: {}", commentDto.getPostId(), commentDto.getCommentContent());
        try {
            postService.saveComment(commentDto, session);
            return ResponseEntity.ok(new ApiResponseDto<>(true, "댓글이 성공적으로 등록되었습니다.", null));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(new ApiResponseDto<>(false, e.getMessage(), null));
        } catch (Exception e) {
            log.error("댓글 등록 중 오류 발생", e);
            return ResponseEntity.status(500).body(new ApiResponseDto<>(false, "댓글 등록 중 오류가 발생했습니다.", null));
        }
    }

    @PutMapping("/comment")
    public ResponseEntity<ApiResponseDto<String>> updateCommentApi(
            @RequestBody CommentDto commentDto) {
        log.info("updateCommentApi 호출 - 댓글 ID: {}, 내용: {}", commentDto.getCommentId(), commentDto.getCommentContent());
        try {
            postService.updateComment(commentDto);
            return ResponseEntity.ok(new ApiResponseDto<>(true, "댓글이 성공적으로 수정되었습니다.", null));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(new ApiResponseDto<>(false, e.getMessage(), null));
        } catch (Exception e) {
            log.error("댓글 수정 중 오류 발생", e);
            return ResponseEntity.status(500).body(new ApiResponseDto<>(false, "댓글 수정 중 오류가 발생했습니다.", null));
        }
    }

    @DeleteMapping("/comment/{commentId}")
    public ResponseEntity<ApiResponseDto<String>> deleteCommentApi(
            @PathVariable("commentId") String commentId) {
        log.info("deleteCommentApi 호출 - 댓글 ID: {}", commentId);
        try {
            postService.deleteComment(commentId);
            return ResponseEntity.ok(new ApiResponseDto<>(true, "댓글이 성공적으로 삭제되었습니다.", null));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(new ApiResponseDto<>(false, e.getMessage(), null));
        } catch (Exception e) {
            log.error("댓글 삭제 중 오류 발생", e);
            return ResponseEntity.status(500).body(new ApiResponseDto<>(false, "댓글 삭제 중 오류가 발생했습니다.", null));
        }
    }
}