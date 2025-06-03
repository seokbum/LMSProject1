package com.ldb.lms.controller.board;

import com.ldb.lms.dto.board.post.PostDto;
import com.ldb.lms.dto.board.post.PostPaginationDto;
import com.ldb.lms.dto.board.post.PostSearchDto;
import com.ldb.lms.service.board.PostService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Slf4j
@Controller
@RequestMapping("/post")
@RequiredArgsConstructor
public class PostController {

    private final PostService postService;

    @GetMapping("getPosts")
    public String getPosts(
            @ModelAttribute PostSearchDto searchDto,
            @RequestParam(value = "pageNum", defaultValue = "1") int pageNum,
            @ModelAttribute PostPaginationDto pageDto,
            Model model) {
        log.debug("getPosts - pageNum: {}, searchDto: {}", pageNum, searchDto);
        pageDto.setCurrentPage(pageNum);
        postService.populatePostsModel(searchDto, pageDto, model);
        log.debug("getPosts - model attributes: {}", model.asMap());
        return "board/post/getPosts";
    }

    @GetMapping("getPostDetail")
    public String getPostDetail(
            @RequestParam("postId") String postId,
            Model model) {
        postService.populatePostDetail(postId, model);
        return "board/post/getPostDetail";
    }

    @GetMapping("createPost")
    public String showCreatePost(Model model) {
        postService.prepareCreatePost(model);
        return "board/post/writePost";
    }

    @GetMapping("updatePost")
    public String showUpdatePost(
            @RequestParam("postId") String postId,
            Model model) {
        postService.prepareUpdatePost(postId, model);
        return "board/post/updatePost";
    }

    @GetMapping("deletePost")
    public String showDeletePost(
            @RequestParam("postId") String postId,
            Model model) {
        postService.prepareDeletePost(postId, model);
        return "board/post/deletePost";
    }

    @GetMapping("replyPost")
    public String showReplyPost(
            @RequestParam("postId") String postId,
            Model model) {
        postService.prepareReplyPost(postId, model);
        return "board/post/writePost";
    }
}