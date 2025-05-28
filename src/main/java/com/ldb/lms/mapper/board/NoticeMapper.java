package com.ldb.lms.mapper.board;

import com.ldb.lms.dto.board.notice.NoticeDto;
import com.ldb.lms.dto.board.notice.NoticeSearchDto;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;
import java.util.Map;

@Mapper
public interface NoticeMapper {

    List<NoticeDto> listNotice(Map<String, Object> param);

    Integer countNotices(NoticeSearchDto searchDto);

    void insertNotice(NoticeDto noticeDto);

    String getLastNoticeId();

    NoticeDto getNotice(String noticeId);

    void incrementReadCount(String noticeId);

    void deleteNotice(String noticeId);
    
    void updateNotice(NoticeDto noticeDto);
}