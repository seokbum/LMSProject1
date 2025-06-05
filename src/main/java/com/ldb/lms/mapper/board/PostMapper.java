package com.ldb.lms.mapper.board;

import com.ldb.lms.dto.board.post.CommentDto;
import com.ldb.lms.dto.board.post.PostDto;
import com.ldb.lms.dto.board.post.PostSearchDto;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;
import java.util.Map;

@Mapper
public interface PostMapper {

    String getLastPostId();

    String getLastCommentId();

    Integer getMaxGroup(); // 

    void insertPost(PostDto postDto);

    void updatePost(PostDto postDto);

    void deletePost(String postId);

    void deleteCommentsByPostId(String postId);

    void incrementReadCount(String postId);

    PostDto getPost(String postId);

    List<PostDto> listPosts(Map<String, Object> param);

    List<PostDto> listNotices(PostSearchDto searchDto);

    Integer countPosts(PostSearchDto searchDto);

    void updateGroupStep(Map<String, Object> param);

    void insertComment(CommentDto commentDto);

    void updateComment(CommentDto commentDto);

    void deleteComment(String commentId);

    CommentDto selectComment(String commentId);

    List<CommentDto> selectCommentList(String postId);
}