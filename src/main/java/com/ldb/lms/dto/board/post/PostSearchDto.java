package com.ldb.lms.dto.board.post;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PostSearchDto {
    private String searchType;
    private String searchKeyword;
    private Integer postNotice;
}