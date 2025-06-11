package com.ldb.lms.controller.board;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ldb.lms.dto.board.post.CommentDto;
import com.ldb.lms.dto.board.post.PostPaginationDto;
import com.ldb.lms.dto.board.post.PostSearchDto;
import com.ldb.lms.service.board.PostService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List; 
import java.util.Map;

@Controller
@RequestMapping("/post")
@RequiredArgsConstructor
@Slf4j
public class PostController {

    private final PostService postService;

    @GetMapping("getPosts")
    public String getPosts(
            @ModelAttribute PostSearchDto searchDto,
            @RequestParam(value = "currentPage", defaultValue = "1") int currentPage,
            Model model,
            HttpSession session) {

        PostPaginationDto pageDto = new PostPaginationDto();
        pageDto.setCurrentPage(currentPage);
        
        Map<String, Object> pageData = postService.getPostsPageData(searchDto, pageDto);
        model.addAllAttributes(pageData);
        model.addAttribute("currentAuthorId", postService.getCurrentUserId(session));
        return "board/post/getPosts";
    }

    @GetMapping("getPostDetail")
    public String getPostDetail(
            @RequestParam("postId") String postId, 
            Model model, 
            HttpSession session) {
        Map<String, Object> postData = postService.getPostDetailDataWithCommentsJson(postId);
        
        model.addAllAttributes(postData);
        model.addAttribute("currentAuthorId", postService.getCurrentUserId(session));
        return "board/post/getPostDetail";
    }

    @GetMapping("createPost")
    public String createPostForm(Model model, HttpSession session) {
        Map<String, Object> formData = postService.getCreatePostFormData(session);
        model.addAllAttributes(formData);
        return "board/post/createPost";
    }

    @GetMapping("updatePost")
    public String updatePostForm(
            @RequestParam("postId") String postId, 
            Model model, 
            HttpSession session) {
        
        Map<String, Object> formData = postService.getUpdatePostFormData(postId, session);
        model.addAllAttributes(formData);
        return "board/post/updatePost";
    }

    @GetMapping("deletePost")
    public String deletePostConfirm(
            @RequestParam("postId") String postId, 
            Model model, 
            HttpSession session) {
        
        Map<String, Object> formData = postService.getDeletePostFormData(postId, session);
        model.addAllAttributes(formData);
        return "board/post/deletePost";
    }

    @GetMapping("replyPost")
    public String replyPostForm(
            @RequestParam("postId") String postId, 
            Model model, 
            HttpSession session) {
        
        Map<String, Object> formData = postService.getReplyPostFormData(postId, session);
        model.addAllAttributes(formData);
        return "board/post/replyPost";
    }
    
    @GetMapping("download")
    public void downloadFile(
            @RequestParam("filePath") String filePath,
            HttpServletResponse response,
            HttpServletRequest request) throws Exception {
        
        postService.handleFileDownload(filePath, response, request);
    }
}