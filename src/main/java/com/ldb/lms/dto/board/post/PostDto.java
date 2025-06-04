package com.ldb.lms.dto.board.post;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.Date;

import org.springframework.web.multipart.MultipartFile;

@Getter
@Setter
@ToString
public class PostDto {
    private String postId;
    private String authorId;
    private String userName;
    private String postTitle;
    private String postContent;
    private String postPassword;
    private String postGroup;
    private Integer postGroupLevel;
    private Integer postGroupStep;
    private String existingFilePath;
    private Integer postReadCount;
    private Date postCreatedAt;
    private Date postUpdatedAt;
    private Integer postNotice; 
    private MultipartFile postFile;
    private String ParentPostId;
}