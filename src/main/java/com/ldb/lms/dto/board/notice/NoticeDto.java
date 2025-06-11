package com.ldb.lms.dto.board.notice;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;
import org.springframework.web.multipart.MultipartFile;
import java.util.Date;

@Getter
@Setter
public class NoticeDto {

    private String noticeId;
    private String writerId;
    private String userName;

    @NotBlank(message = "제목을 입력해주세요.")
    private String noticeTitle;

    @NotBlank(message = "내용을 입력해주세요.")
    private String noticeContent;

    @NotBlank(message = "비밀번호를 입력해주세요.")
    private String noticePassword;

    private Date noticeCreatedAt;
    private Date noticeUpdatedAt;
    private MultipartFile noticeFile;
    private Integer noticeReadCount;
    private String existingFilePath;
}