package com.ldb.lms.dto.board.post;

import java.util.Date;

import org.springframework.web.multipart.MultipartFile;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString

public class PostDto {
	private String postId;
	private String authorId;
	private String userName;
	private String postTitle;
	private String postContent;
	private Date postCreatedAt;
	private Date postUpdatedAt;
	private MultipartFile postFile;
	private Integer postReadCount;
	private String postPassword;
	private String existingFilePath;
	private String postGroup;
	private String postGroupLevel;
	private String postGroupStep;
	private Integer postNotice;
	
	
}
