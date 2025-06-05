package com.ldb.lms.dto.board.post;

import java.util.Date;


import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class CommentDto {
    private String commentId;
    private String postId;
    private String writerId;
    private String userName; 
    private String commentContent;
    private String parentCommentId;
    private Date createdAt;
    private Date updatedAt;
}