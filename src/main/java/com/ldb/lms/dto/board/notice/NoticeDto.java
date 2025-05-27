package com.ldb.lms.dto.board.notice;

import java.util.Date;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class NoticeDto {
    private String noticeId;
    private String userName; 
    private String noticeTitle;
    private String noticeContent;
    private Date noticeCreatedAt;
    private Date noticeUpdatedAt;
    private String noticeFile;
    private Integer noticeReadCount;
    private String noticePassword;
}