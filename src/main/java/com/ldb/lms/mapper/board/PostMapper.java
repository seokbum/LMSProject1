package com.ldb.lms.mapper.board;

import com.ldb.lms.dto.board.post.CommentDto;
import com.ldb.lms.dto.board.post.PostDto;
import com.ldb.lms.dto.board.post.PostSearchDto;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;
import java.util.Map;

@Mapper
public interface PostMapper {

    List<PostDto> listNotices(PostSearchDto searchDto);
    
    List<PostDto> listPosts(Map<String, Object> params);
    
    Integer countPosts(PostSearchDto searchDto);
    
    PostDto getPost(String postId);
    
    String getLastPostId(); 
    
    Integer getMaxGroup();
    
    void insertPost(PostDto postDto);
    
    void updatePost(PostDto postDto);
    
    void deletePost(String postId);
    
    void incrementReadCount(String postId);
    
    void updateGroupStep(Map<String, Object> param); 
    
    String getLastCommentId();
    
    void insertComment(CommentDto commentDto);
    
    void updateComment(CommentDto commentDto);
    
    void deleteComment(String commentId);
    
    void deleteCommentsByPostId(String postId); 
    
    CommentDto selectComment(String commentId);
    
    List<CommentDto> selectCommentList(String postId);
}