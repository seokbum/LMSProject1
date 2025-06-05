package com.ldb.lms.controller.board;

import com.ldb.lms.dto.board.post.PostDto;
import com.ldb.lms.dto.board.post.PostPaginationDto;
import com.ldb.lms.dto.board.post.PostSearchDto;
import com.ldb.lms.service.board.PostService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession; 
@Slf4j
@Controller
@RequestMapping("/post") 
@RequiredArgsConstructor
public class PostController {

    private final PostService postService;

    @GetMapping("getPosts")
    public String getPosts(
            @ModelAttribute PostSearchDto searchDto,
            @ModelAttribute PostPaginationDto pageDto,
            @RequestParam(value = "postType", defaultValue = "post") String postType,
            HttpServletRequest request,
            Model model) {
        String authorId = "P001"; // 테스트용 하드코딩
        if (pageDto.getItemsPerPage() == null) {
            pageDto.setItemsPerPage(10);
        }
        if (pageDto.getCurrentPage() == null || pageDto.getCurrentPage() < 1) {
            pageDto.setCurrentPage(1);
        }
        return postService.preparePostsView(searchDto, pageDto, postType, authorId, model);
    }

    @GetMapping("createPost")
    public String createPost(
            HttpServletRequest request,
            Model model) {
        String authorId = "P001"; // 테스트용 하드코딩
        return postService.prepareCreatePostView(authorId, model);
    }

    @GetMapping("updatePost")
    public String updatePost(
            @RequestParam("postId") String postId,
            HttpServletRequest request,
            Model model) {
        String authorId = "P001"; // 테스트용 하드코딩
        return postService.prepareUpdatePostView(postId, authorId, model);
    }

    @GetMapping("getPostDetail")
    public String getPostDetail(
            @RequestParam("postId") String postId,
            @RequestParam(value = "readcnt", defaultValue = "") String readcnt,
            HttpServletRequest request,
            Model model) {
        String authorId = "P001"; // 테스트용 하드코딩
        return postService.preparePostDetailView(postId, readcnt, authorId, model);
    }

    @GetMapping("replyPost")
    public String replyPost(
            @RequestParam("postId") String postId,
            HttpServletRequest request,
            Model model) {
        String authorId = "P001"; // 테스트용 하드코딩
        return postService.prepareReplyPostView(postId, authorId, model);
    }

  
    @GetMapping("deletePost") 
    public String showDeletePost( 
            @RequestParam("postId") String postId,
            HttpServletRequest request,
            Model model) {
        String authorId = "P001"; 
       
        return postService.prepareDeletePostView(postId, authorId, model);
    }

    
    @PostMapping("delete") 
    public String deletePost( 
            @RequestParam("postId") String postId,
            @RequestParam("pass") String password,
            HttpSession session, 
            HttpServletRequest request, 
            Model model) { 
        String authorId = "P001"; // 테스트용 하드코딩 (실제로는 session에서 가져옴)
        String redirectUrl = postService.handleDeletePostByForm(postId, password, authorId, request, model);
        return redirectUrl;
    }
}