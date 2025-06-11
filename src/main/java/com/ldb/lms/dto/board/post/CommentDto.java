package com.ldb.lms.dto.board.post;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import java.util.Date;

@Getter
@Setter
@ToString
public class CommentDto {

    private String commentId;
    private String postId;
    private String writerId;
    private String writerName;

    @NotBlank(message = "댓글 내용을 입력해주세요.")
    private String commentContent;
    
    private String parentCommentId;
    private Date createdAt;
    private Date updatedAt;
}