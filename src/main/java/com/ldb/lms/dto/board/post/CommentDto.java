package com.ldb.lms.dto.board.post;

import lombok.Data;

@Data
public class CommentDto {
    private String commentId;
    private String postId;
    private String writerId;
    private String writerName; 
    private String commentContent;
    private String parentCommentId;
    private String createdAt;
    private String updatedAt;
}