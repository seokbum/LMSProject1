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
            @RequestParam(value = "authorId", defaultValue = "S001") String authorId,
            Model model) {
        // pageDto 기본값 설정
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
            @RequestParam(value = "authorId", defaultValue = "S001") String authorId,
            Model model) {
        return postService.prepareCreatePostView(authorId, model);
    }

    @GetMapping("updatePost")
    public String updatePost(
            @RequestParam("postId") String postId,
            @RequestParam(value = "authorId", defaultValue = "S001") String authorId,
            Model model) {
        return postService.prepareUpdatePostView(postId, authorId, model);
    }

    @GetMapping("getPostDetail")
    public String getPostDetail(
            @RequestParam("postId") String postId,
            @RequestParam(value = "readcnt", defaultValue = "") String readcnt,
            @RequestParam(value = "authorId", defaultValue = "S001") String authorId,
            Model model) {
        return postService.preparePostDetailView(postId, readcnt, authorId, model);
    }

    @GetMapping("replyPost")
    public String replyPost(
            @RequestParam("postId") String postId,
            @RequestParam(value = "authorId", defaultValue = "S001") String authorId,
            Model model) {
        return postService.prepareReplyPostView(postId, authorId, model);
    }

    @GetMapping("deletePost")
    public String deletePost(
            @RequestParam("postId") String postId,
            @RequestParam(value = "authorId", defaultValue = "S001") String authorId,
            Model model) {
        return postService.prepareDeletePostView(postId, authorId, model);
    }
}