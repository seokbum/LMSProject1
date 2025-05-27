package com.ldb.lms.domain;

import java.util.Date;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class Notice {
    private String noticeId; 
    private String writerId; 
    private String noticeTitle; 
    private String noticeContent;
    private Date noticeCreatedAt;
    private Date noticeUpdatedAt;
    private String noticeFile; 
    private Integer noticeReadCount; 
    private String noticePassword;
}