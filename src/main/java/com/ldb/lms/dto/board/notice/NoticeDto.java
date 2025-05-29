package com.ldb.lms.dto.board.notice;

import java.util.Date;

import org.springframework.web.multipart.MultipartFile;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class NoticeDto {
    private String noticeId;
    private String writerId;
    private String userName; 
    private String noticeTitle;
    private String noticeContent;
    private Date noticeCreatedAt;
    private Date noticeUpdatedAt;
    private MultipartFile noticeFile;
    private Integer noticeReadCount;
    private String noticePassword;
    private String existingFilePath;
}