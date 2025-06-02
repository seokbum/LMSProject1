package com.ldb.lms.mapper.board;

import java.util.List;
import java.util.Map;

import com.ldb.lms.dto.board.post.PostDto;
import com.ldb.lms.dto.board.post.PostSearchDto;

public interface PostMapper {

    Integer countPosts(PostSearchDto searchDto);

    List<PostDto> listPost(Map<String, Object> param);

    List<PostDto> listNotices(Map<String, Object> param);
}