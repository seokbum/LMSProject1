package com.ldb.lms.domain;

import java.util.Date;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class PostComment {
    private String commentId;
    private String postId;
    private String writerId;
    private String commentContent;
    private String parentCommentId;
    private Date createdAt;
    private Date updatedAt;
}