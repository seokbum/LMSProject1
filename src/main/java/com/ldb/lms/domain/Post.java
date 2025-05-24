package com.ldb.lms.domain;

import java.util.Date;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class Post {
    private String postId;
    private String authorId;
    private String postTitle;
    private String postContent;
    private Date postCreatedAt;
    private Date postUpdatedAt;
    private Integer postGroup;
    private Integer postGroupLevel;
    private Integer postGroupStep;
    private String postFile;
    private Integer postReadCount;
    private String postPassword;
    private Integer postNotice;
    private String authorName;

}