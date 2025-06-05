package com.ldb.lms.service.board;

import com.ldb.lms.dto.ApiResponseDto;
import com.ldb.lms.dto.board.post.CommentDto;
import com.ldb.lms.dto.board.post.PostDto;
import com.ldb.lms.dto.board.post.PostPaginationDto;
import com.ldb.lms.dto.board.post.PostSearchDto;
import com.ldb.lms.mapper.board.PostMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional; // import 유지
import org.springframework.ui.Model;
import org.springframework.web.multipart.MultipartFile;
import jakarta.servlet.http.HttpServletRequest;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

@Slf4j
@Service
@RequiredArgsConstructor
public class PostService {

    private final PostMapper postMapper;

    private String getUploadDir(HttpServletRequest request) {
        if (request == null) {
            throw new IllegalArgumentException("HttpServletRequest is required to determine upload directory.");
        }
        String realPath = request.getServletContext().getRealPath("");
        if (realPath == null) {
            log.warn("Real path is not available via getServletContext().getRealPath(\"\"). Using user.dir as fallback for development.");
            realPath = System.getProperty("user.dir") + File.separator + "src" + File.separator + "main" + File.separator + "resources" + File.separator + "static";
        }
        Path uploadPath = Paths.get(realPath, "dist", "assets", "upload");
        return uploadPath.toString();
    }

    @Transactional 
    public String preparePostsView(PostSearchDto searchDto, PostPaginationDto pageDto, String postType, String authorId, Model model) {
        try {
            List<PostDto> notices = postMapper.listNotices(searchDto);
            model.addAttribute("notices", notices);

            searchDto.setPostNotice(0);
            int totalRows = postMapper.countPosts(searchDto);
            pageDto.setTotalRows(totalRows);
            pageDto.calculatePagination();

            Map<String, Object> params = new HashMap<>();
            params.put("searchDto", searchDto);
            params.put("pageDto", pageDto);

            List<PostDto> posts = postMapper.listPosts(params);
            model.addAttribute("posts", posts);
            model.addAttribute("pagination", pageDto);
            model.addAttribute("searchDto", searchDto);
            model.addAttribute("postType", postType);
            model.addAttribute("currentAuthorId", authorId);

            return "board/post/getPosts";
        } catch (Exception e) {
            log.error("게시물 목록 조회 중 오류 발생: {}", e.getMessage(), e);
            model.addAttribute("error", "게시물 목록을 불러오는 데 실패했습니다.");
            return "error/500";
        }
    }

    public String prepareCreatePostView(String authorId, Model model) {
        model.addAttribute("authorId", authorId);
        return "board/post/createPost";
    }

    @Transactional 
    public String prepareUpdatePostView(String postId, String authorId, Model model) {
        try {
            PostDto post = postMapper.getPost(postId);
            if (post == null) {
                model.addAttribute("error", "게시물을 찾을 수 없습니다.");
                return "error/404";
            }
            if (!post.getAuthorId().equals(authorId)) {
                model.addAttribute("error", "해당 게시물을 수정할 권한이 없습니다.");
                return "error/403";
            }

            model.addAttribute("post", post);
            model.addAttribute("currentAuthorId", authorId);
            return "board/post/updatePost";
        } catch (Exception e) {
            log.error("게시물 수정 화면 준비 중 오류 발생: {}", e.getMessage(), e);
            model.addAttribute("error", "게시물 수정 화면을 불러오는 데 실패했습니다.");
            return "error/500";
        }
    }

    @Transactional 
    public String preparePostDetailView(String postId, String readcnt, String authorId, Model model) {
        try {
            if ("true".equalsIgnoreCase(readcnt)) {
                postMapper.incrementReadCount(postId);
            }

            PostDto post = postMapper.getPost(postId);
            if (post == null) {
                model.addAttribute("error", "게시물을 찾을 수 없습니다.");
                return "error/404";
            }

            List<CommentDto> commentList = postMapper.selectCommentList(postId);

            model.addAttribute("post", post);
            model.addAttribute("commentList", commentList);
            model.addAttribute("currentAuthorId", authorId);
            return "board/post/getPostDetail";
        } catch (Exception e) {
            log.error("게시물 상세 조회 중 오류 발생: {}", e.getMessage(), e);
            model.addAttribute("error", "게시물 상세 정보를 불러오는 데 실패했습니다.");
            return "error/500";
        }
    }

    @Transactional 
    public String prepareReplyPostView(String postId, String authorId, Model model) {
        try {
            PostDto parentPost = postMapper.getPost(postId);
            if (parentPost == null) {
                model.addAttribute("error", "부모 게시물을 찾을 수 없습니다.");
                return "error/404";
            }

            PostDto replyDto = new PostDto();
            replyDto.setParentPostId(parentPost.getPostId());
            replyDto.setPostTitle("Re: " + parentPost.getPostTitle());
            replyDto.setAuthorId(authorId);

            model.addAttribute("postDto", replyDto);
            model.addAttribute("currentAuthorId", authorId);
            return "board/post/replyPost";
        } catch (Exception e) {
            log.error("답글 화면 준비 중 오류 발생: {}", e.getMessage(), e);
            model.addAttribute("error", "답글 작성 화면을 불러오는 데 실패했습니다.");
            return "error/500";
        }
    }

    @Transactional 
    public String prepareDeletePostView(String postId, String authorId, Model model) {
        try {
            PostDto post = postMapper.getPost(postId);
            if (post == null) {
                model.addAttribute("error", "게시물을 찾을 수 없습니다.");
                return "error/404";
            }
            if (!post.getAuthorId().equals(authorId)) {
                model.addAttribute("error", "해당 게시물을 삭제할 권한이 없습니다.");
                return "error/403";
            }

            model.addAttribute("post", post);
            model.addAttribute("currentAuthorId", authorId);
            return "board/post/deletePost";
        } catch (Exception e) {
            log.error("게시물 삭제 화면 준비 중 오류 발생: {}", e.getMessage(), e);
            model.addAttribute("error", "게시물 삭제 화면을 불러오는 데 실패했습니다.");
            return "error/500";
        }
    }

    @Transactional
    public String handleDeletePostByForm(String postId, String password, String authorId, HttpServletRequest request, Model model) {
        try {
            PostDto post = postMapper.getPost(postId);
            if (post == null) {
                model.addAttribute("error", "삭제할 게시물을 찾을 수 없습니다.");
                return "redirect:/post/getPostDetail?postId=" + postId + "&error=not_found";
            }

            if (!post.getAuthorId().equals(authorId)) {
                model.addAttribute("error", "해당 게시물을 삭제할 권한이 없습니다.");
                return "redirect:/post/getPostDetail?postId=" + postId + "&error=no_permission";
            }

            if (!post.getPostPassword().equals(password)) {
                model.addAttribute("error", "비밀번호가 일치하지 않습니다.");
                return "redirect:/post/getPostDetail?postId=" + postId + "&error=wrong_password";
            }

            if (post.getExistingFilePath() != null && !post.getExistingFilePath().isEmpty()) {
                deleteFile(post.getExistingFilePath(), request);
            }

            postMapper.deleteCommentsByPostId(postId);
            postMapper.deletePost(postId);

            log.info("게시물 삭제 성공: {}", postId);
            return "redirect:/post/getPosts";
        } catch (Exception e) {
            log.error("게시물 삭제 실패: {}", e.getMessage(), e);
            model.addAttribute("error", "게시물 삭제 실패: " + e.getMessage());
            return "redirect:/post/getPostDetail?postId=" + postId + "&error=server_error";
        }
    }

    @Transactional
    public ApiResponseDto<Map<String, Object>> getPosts(PostSearchDto searchDto, PostPaginationDto pageDto, String postType) {
        try {
            Map<String, Object> result = new HashMap<>();

            if ("post".equals(postType)) {
                searchDto.setPostNotice(1);
                List<PostDto> notices = postMapper.listNotices(searchDto);
                result.put("notices", notices);
            }

            if (!"notice".equals(postType)) {
                searchDto.setPostNotice(0);
                int totalRows = postMapper.countPosts(searchDto);
                pageDto.setTotalRows(totalRows);
                pageDto.calculatePagination();

                Map<String, Object> params = new HashMap<>();
                params.put("searchDto", searchDto);
                params.put("pageDto", pageDto);

                List<PostDto> posts = postMapper.listPosts(params);
                result.put("posts", posts);
                result.put("pagination", pageDto);
                result.put("searchDto", searchDto);
            }

            return new ApiResponseDto<>(true, "게시물 목록 조회 성공", result);
        } catch (Exception e) {
            log.error("API 게시물 목록 조회 중 오류 발생: {}", e.getMessage(), e);
            return new ApiResponseDto<>(false, "게시물 목록 조회 실패: " + e.getMessage(), null);
        }
    }

    @Transactional
    public ApiResponseDto<String> handleImageUpload(MultipartFile file, HttpServletRequest request) {
        if (file.isEmpty()) {
            return new ApiResponseDto<>(false, "업로드할 파일이 없습니다.", null);
        }
        try {
            String savedFileName = saveFile(file, request);
            String fileUrl = request.getContextPath() + "/dist/assets/upload/" + savedFileName;
            return new ApiResponseDto<>(true, "이미지 업로드 성공", fileUrl);
        } catch (IOException e) {
            log.error("이미지 업로드 실패: {}", e.getMessage(), e);
            return new ApiResponseDto<>(false, "이미지 업로드 실패: " + e.getMessage(), null);
        }
    }

    @Transactional
    public ApiResponseDto<String> handleCreatePost(PostDto postDto, MultipartFile file, HttpServletRequest request) {
        try {
            String lastPostId = postMapper.getLastPostId();
            String newPostId = generateNextId(lastPostId, "POST");

            Integer maxGroup = postMapper.getMaxGroup();
            int newGroup = (maxGroup == null) ? 1 : maxGroup + 1;

            postDto.setPostId(newPostId);
            postDto.setPostGroup(newGroup);
            postDto.setPostGroupLevel(0);
            postDto.setPostGroupStep(0);
            postDto.setPostReadCount(0);
            postDto.setPostNotice(postDto.getPostNotice() != null ? 1 : 0);

            if (file != null && !file.isEmpty()) {
                String savedFileName = saveFile(file, request);
                postDto.setExistingFilePath(savedFileName);
            } else {
                postDto.setExistingFilePath(null);
            }

            postMapper.insertPost(postDto);
            return new ApiResponseDto<>(true, "게시물 등록이 완료되었습니다.", "/post/getPostDetail?postId=" + newPostId + "&readcnt=true");
        } catch (Exception e) {
            log.error("게시물 등록 실패: {}", e.getMessage(), e);
            return new ApiResponseDto<>(false, "게시물 등록 실패: " + e.getMessage(), null);
        }
    }

    @Transactional
    public ApiResponseDto<String> handleUpdatePost(PostDto postDto, MultipartFile file, HttpServletRequest request) {
        try {
            PostDto existingPost = postMapper.getPost(postDto.getPostId());
            if (existingPost == null) {
                return new ApiResponseDto<>(false, "수정할 게시물을 찾을 수 없습니다.", null);
            }

            if (!existingPost.getAuthorId().equals(postDto.getAuthorId())) {
                return new ApiResponseDto<>(false, "해당 게시물을 수정할 권한이 없습니다.", null);
            }

            if (!existingPost.getPostPassword().equals(postDto.getPostPassword())) {
                return new ApiResponseDto<>(false, "비밀번호가 일치하지 않습니다.", null);
            }

            postDto.setPostNotice(postDto.getPostNotice() != null ? 1 : 0);

            if (file != null && !file.isEmpty()) {
                if (existingPost.getExistingFilePath() != null) {
                    deleteFile(existingPost.getExistingFilePath(), request);
                }
                String newFileName = saveFile(file, request);
                postDto.setExistingFilePath(newFileName);
            } else {
                postDto.setExistingFilePath(existingPost.getExistingFilePath());
            }

            postMapper.updatePost(postDto);
            return new ApiResponseDto<>(true, "게시물 수정이 완료되었습니다.", "/post/getPostDetail?postId=" + postDto.getPostId());
        } catch (Exception e) {
            log.error("게시물 수정 실패: {}", e.getMessage(), e);
            return new ApiResponseDto<>(false, "게시물 수정 실패: " + e.getMessage(), null);
        }
    }

    @Transactional
    public ApiResponseDto<String> handleReplyPost(PostDto postDto, MultipartFile file, HttpServletRequest request) {
        try {
            PostDto parentPost = postMapper.getPost(postDto.getParentPostId());
            if (parentPost == null) {
                return new ApiResponseDto<>(false, "답글을 작성할 부모 게시물을 찾을 수 없습니다.", null);
            }

            String lastPostId = postMapper.getLastPostId();
            String newPostId = generateNextId(lastPostId, "POST");

            Map<String, Object> params = new HashMap<>();
            params.put("postGroup", parentPost.getPostGroup());
            params.put("postGroupStep", parentPost.getPostGroupStep() + 1);
            postMapper.updateGroupStep(params);

            postDto.setPostId(newPostId);
            postDto.setPostGroup(parentPost.getPostGroup());
            postDto.setPostGroupLevel(parentPost.getPostGroupLevel() + 1);
            postDto.setPostGroupStep(parentPost.getPostGroupStep() + 1);
            postDto.setPostReadCount(0);
            postDto.setPostNotice(0);

            if (file != null && !file.isEmpty()) {
                String savedFileName = saveFile(file, request);
                postDto.setExistingFilePath(savedFileName);
            } else {
                postDto.setExistingFilePath(null);
            }

            postMapper.insertPost(postDto);
            return new ApiResponseDto<>(true, "답글 등록이 완료되었습니다.", "/post/getPostDetail?postId=" + newPostId + "&readcnt=true");
        } catch (Exception e) {
            log.error("답글 등록 실패: {}", e.getMessage(), e);
            return new ApiResponseDto<>(false, "답글 등록 실패: " + e.getMessage(), null);
        }
    }

    @Transactional
    public ApiResponseDto<String> handleWriteComment(CommentDto commentDto, HttpServletRequest request) {
        try {
            String lastCommentId = postMapper.getLastCommentId();
            String newCommentId = generateNextId(lastCommentId, "COMM");

            commentDto.setCommentId(newCommentId);

            postMapper.insertComment(commentDto);
            return new ApiResponseDto<>(true, "댓글 등록이 완료되었습니다.", null);
        } catch (Exception e) {
            log.error("댓글 등록 실패: {}", e.getMessage(), e);
            return new ApiResponseDto<>(false, "댓글 등록 실패: " + e.getMessage(), null);
        }
    }

    @Transactional
    public ApiResponseDto<String> handleUpdateComment(CommentDto commentDto, HttpServletRequest request) {
        try {
            CommentDto existingComment = postMapper.selectComment(commentDto.getCommentId());
            if (existingComment == null) {
                return new ApiResponseDto<>(false, "수정할 댓글을 찾을 수 없습니다.", null);
            }
            if (!existingComment.getWriterId().equals(commentDto.getWriterId())) {
                return new ApiResponseDto<>(false, "댓글을 수정할 권한이 없습니다.", null);
            }

            postMapper.updateComment(commentDto);
            return new ApiResponseDto<>(true, "댓글 수정이 완료되었습니다.", null);
        } catch (Exception e) {
            log.error("댓글 수정 실패: {}", e.getMessage(), e);
            return new ApiResponseDto<>(false, "댓글 수정 실패: " + e.getMessage(), null);
        }
    }

    @Transactional
    public ApiResponseDto<String> handleDeleteComment(String commentId, String loggedInWriterId, HttpServletRequest request) {
        try {
            CommentDto comment = postMapper.selectComment(commentId);
            if (comment == null) {
                return new ApiResponseDto<>(false, "삭제할 댓글을 찾을 수 없습니다.", null);
            }

            if (!comment.getWriterId().equals(loggedInWriterId)) {
                return new ApiResponseDto<>(false, "댓글을 삭제할 권한이 없습니다.", null);
            }

            postMapper.deleteComment(commentId);
            return new ApiResponseDto<>(true, "댓글 삭제가 완료되었습니다.", null);
        } catch (Exception e) {
            log.error("댓글 삭제 실패: {}", e.getMessage(), e);
            return new ApiResponseDto<>(false, "댓글 삭제 실패: " + e.getMessage(), null);
        }
    }

    private String generateNextId(String lastId, String prefix) {
        if (lastId == null || !lastId.startsWith(prefix)) {
            return prefix + "00001";
        }
        int num = Integer.parseInt(lastId.substring(prefix.length()));
        return String.format(prefix + "%05d", num + 1);
    }

    private String saveFile(MultipartFile file, HttpServletRequest request) throws IOException {
        String originalFileName = file.getOriginalFilename();
        String uuid = UUID.randomUUID().toString();
        String savedFileName = uuid + "_" + originalFileName;

        Path uploadPath = Paths.get(getUploadDir(request));
        if (!Files.exists(uploadPath)) {
            Files.createDirectories(uploadPath);
        }
        Path filePath = uploadPath.resolve(savedFileName);
        file.transferTo(filePath.toFile());

        return savedFileName;
    }

    private void deleteFile(String fileName, HttpServletRequest request) {
        if (fileName != null && !fileName.isEmpty()) {
            try {
                Path realFilePath = Paths.get(getUploadDir(request), fileName);

                File fileToDelete = realFilePath.toFile();
                if (fileToDelete.exists() && fileToDelete.isFile()) {
                    Files.deleteIfExists(fileToDelete.toPath());
                    log.info("파일 삭제 성공: {}", realFilePath);
                } else {
                    log.warn("삭제할 파일이 존재하지 않거나 파일이 아닙니다: {}", realFilePath);
                }
            } catch (IOException e) {
                log.error("파일 삭제 실패: {}", fileName, e);
            }
        }
    }
}