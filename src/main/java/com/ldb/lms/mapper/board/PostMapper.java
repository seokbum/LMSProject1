package com.ldb.lms.mapper.board;

import com.ldb.lms.dto.board.post.CommentDto;
import com.ldb.lms.dto.board.post.PostDto;
import com.ldb.lms.dto.board.post.PostSearchDto;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

@Mapper
public interface PostMapper {

    String getLastPostId();

    String getLastCommentId();

    void insertPost(PostDto postDto);

    void updatePost(PostDto postDto);

    void deletePost(@Param("postId") String postId);

    void deleteWithComments(@Param("postId") String postId);

    void incrementReadCount(@Param("postId") String postId);

    PostDto getPost(@Param("postId") String postId);

    List<Map<String, Object>> listNotices(Map<String, Object> param);

    List<Map<String, Object>> listPosts(Map<String, Object> param);

    Integer countPosts(PostSearchDto searchDto);

    void updateGroupStep(@Param("postGroup") Integer postGroup, @Param("postGroupStep") Integer postGroupStep);

    void insertComment(CommentDto commentDto);

    void updateComment(CommentDto commentDto);

    void deleteComment(@Param("commentId") String commentId);

    CommentDto selectComment(@Param("commentId") String commentId);

    List<CommentDto> selectCommentList(@Param("postId") String postId);
}