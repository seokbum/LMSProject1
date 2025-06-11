package com.ldb.lms.dto.board.post;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.springframework.web.multipart.MultipartFile;
import java.util.Date;

@Getter
@Setter
@ToString
public class PostDto {

    private String postId;
    private String authorId;
    private String userName;

    @NotBlank(message = "게시물 제목을 입력해주세요.")
    private String postTitle;

    @NotBlank(message = "게시물 내용을 입력해주세요.")
    private String postContent;

    @NotBlank(message = "게시물 비밀번호를 입력해주세요.")
    private String postPassword;

    private Integer postGroup;
    private Integer postGroupLevel;
    private Integer postGroupStep;
    private String existingFilePath;
    private Integer postReadCount;
    private Date postCreatedAt;
    private Date postUpdatedAt;
    private Integer postNotice;
    private MultipartFile postFile;
    private String parentPostId;
}