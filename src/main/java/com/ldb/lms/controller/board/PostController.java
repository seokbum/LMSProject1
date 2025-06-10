package com.ldb.lms.controller.board;

import java.util.Map;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import com.ldb.lms.dto.board.post.PostPaginationDto;
import com.ldb.lms.dto.board.post.PostSearchDto;
import com.ldb.lms.service.board.PostService;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

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
            Model model) {
        log.info("getPosts 호출 - currentPage: {}", pageDto.getCurrentPage());
        Map<String, Object> pageData = postService.getPostsPageData(searchDto, pageDto);
        model.addAllAttributes(pageData);
        return "board/post/getPosts";
    }

    @PostMapping("searchPosts")
    public String searchPosts(
            @ModelAttribute PostSearchDto searchDto,
            @ModelAttribute PostPaginationDto pageDto,
            Model model) {
        log.info("searchPosts 호출 - searchType: {}, searchKeyword: {}", searchDto.getSearchType(), searchDto.getSearchKeyword());
        pageDto.setCurrentPage(1);
        Map<String, Object> pageData = postService.getPostsPageData(searchDto, pageDto);
        model.addAllAttributes(pageData);
        return "board/post/getPosts";
    }

    @GetMapping("getPostDetail")
    public String getPostDetail(@RequestParam("postId") String postId, Model model, HttpSession session) {
        log.info("getPostDetail 호출 - postId: {}", postId);
        Map<String, Object> postData = postService.getPostDetailData(postId);
        model.addAllAttributes(postData);
        model.addAttribute("currentAuthorId", postService.getCurrentUserId(session));
        return "board/post/getPostDetail";
    }

    @GetMapping("createPost")
    public String createPostForm(Model model, HttpSession session) {
        log.info("createPostForm 호출");
        Map<String, Object> formData = postService.getCreatePostFormData(session);
        model.addAllAttributes(formData);
        return "board/post/createPost";
    }

    @GetMapping("updatePost")
    public String updatePostForm(@RequestParam("postId") String postId, Model model, HttpSession session) {
        log.info("updatePostForm 호출 - postId: {}", postId);
        Map<String, Object> formData = postService.getUpdatePostFormData(postId, session);
        model.addAllAttributes(formData);
        return "board/post/updatePost";
    }

    @GetMapping("deletePost")
    public String deletePostConfirm(@RequestParam("postId") String postId, Model model, HttpSession session) {
        log.info("deletePostConfirm 호출 - postId: {}", postId);
        Map<String, Object> formData = postService.getDeletePostFormData(postId, session);
        model.addAllAttributes(formData);
        return "board/post/deletePost";
    }

    @GetMapping("replyPost")
    public String replyPostForm(@RequestParam("postId") String postId, Model model, HttpSession session) {
        log.info("replyPostForm 호출 - parentPostId: {}", postId);
        Map<String, Object> formData = postService.getReplyPostFormData(postId, session);
        model.addAllAttributes(formData);
        return "board/post/replyPost";
    }
}