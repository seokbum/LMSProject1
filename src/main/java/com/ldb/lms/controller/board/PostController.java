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
import org.springframework.web.multipart.MultipartFile;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

@Slf4j
@Controller
@RequestMapping("/post")
@RequiredArgsConstructor
public class PostController {

    private final PostService postService;

    @GetMapping("/download")
    public void downloadFile(
            @RequestParam("filePath") String filePath,
            HttpServletResponse response,
            HttpServletRequest request) throws Exception {
        postService.handleFileDownload(filePath, response, request);
    }

    @GetMapping("getPosts")
    public String getPosts(
            @ModelAttribute PostSearchDto searchDto,
            @RequestParam(value = "pageNum", defaultValue = "1") int pageNum,
            @ModelAttribute PostPaginationDto pageDto,
            @RequestParam(value = "postType", defaultValue = "post") String postType,
            Model model) {
    	searchDto.setPostNotice("notice".equals(postType) ? 1 : 0);
        pageDto.setCurrentPage(pageNum);
        postService.populatePostsModel(searchDto, pageDto, model);
        model.addAttribute("postType", postType);
        return "board/post/getPosts";
    }

    @GetMapping("createPost")
    public String showCreatePost(
            @RequestParam(value = "postType", defaultValue = "post") String postType,
            Model model,
            HttpSession session) {
        postService.prepareCreatePost(session, model, postType);
        return "board/post/createPost";
    }

    @GetMapping("getPostDetail")
    public String getPostDetail(
            @RequestParam("postId") String postId,
            @RequestParam(value = "postType", defaultValue = "post") String postType,
            Model model) {
        postService.populatePostDetail(postId, model);
        return "board/post/getPostDetail";
    }

    @GetMapping("deletePost")
    public String showDeletePost(
            @RequestParam("postId") String postId,
            @RequestParam(value = "postType", defaultValue = "post") String postType,
            HttpSession session,
            Model model) {
        postService.prepareDeletePost(postId, session, model);
        model.addAttribute("postType", postType);
        return "board/post/deletePost";
    }

    @PostMapping("delete")
    public String deletePost(
            @RequestParam("postId") String postId,
            @RequestParam("pass") String password,
            HttpSession session,
            Model model,
            HttpServletRequest request) {
        return postService.handleDeletePost(postId, password, session, model, request);
    }

    @GetMapping("updatePost")
    public String showUpdatePost(
            @RequestParam("postId") String postId,
            @RequestParam(value = "postType", defaultValue = "post") String postType,
            HttpSession session,
            Model model) {
        postService.prepareUpdatePost(postId, session, model);
        return "board/post/updatePost";
    }

    @PostMapping("update")
    public String updatePost(
            @ModelAttribute PostDto postDto,
            @RequestPart(value = "postFile", required = false) MultipartFile file,
            @RequestParam(value = "postType", defaultValue = "post") String postType,
            HttpServletRequest request,
            HttpSession session,
            Model model) {
        postDto.setPostNotice("notice".equals(postType) ? 1 : 0);
        postService.handleUpdatePost(postDto, file, request, session);
        return "redirect:/post/getPostDetail?postId=" + postDto.getPostId() + "&postType=" + postType;
    }

    @GetMapping("replyPost")
    public String showReplyPost(
            @RequestParam("postId") String postId,
            HttpSession session,
            Model model) {
        postService.prepareReplyPost(postId, session, model);
        return "board/post/createPost";
    }
}