package com.ldb.lms.mapper.board;

import com.ldb.lms.dto.board.post.CommentDto;
import com.ldb.lms.dto.board.post.PostDto;
import com.ldb.lms.dto.board.post.PostSearchDto;
import org.apache.ibatis.annotations.Mapper;
import java.util.List;
import java.util.Map;

@Mapper
public interface PostMapper {

    void insertPost(PostDto postDto);
    
    void updatePost(PostDto postDto);
    
    void deletePost(String postId);
    
    PostDto getPost(String postId);
    
    List<PostDto> listPosts(Map<String, Object> params);
    
    List<PostDto> listNotices(PostSearchDto noticeSearchDto);
    
    int countPosts(PostSearchDto searchDto);
    
    void incrementReadCount(String postId);
    
    Integer getMaxGroup();
    
    void updateGroupStep(Map<String, Object> params);
    
    String getLastPostId();
    
    void insertComment(CommentDto commentDto);
    
    void updateComment(CommentDto commentDto);
    
    void deleteComment(String commentId);
    
    void deleteCommentsByPostId(String postId);
    
    List<CommentDto> selectCommentList(String postId);
    
    CommentDto selectComment(String commentId);
    
    String getLastCommentId();
}