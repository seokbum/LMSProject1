package com.ldb.lms.mapper.board;

import java.util.List;
import java.util.Map;
import org.apache.ibatis.annotations.Mapper;
import com.ldb.lms.dto.board.notice.NoticeDto;
import com.ldb.lms.dto.board.notice.NoticeSearchDto;

@Mapper
public interface NoticeMapper {

    List<NoticeDto> listNotice(Map<String, Object> param);

    Integer countNotices(NoticeSearchDto searchDto);

    void insertNotice(NoticeDto noticeDto);
}