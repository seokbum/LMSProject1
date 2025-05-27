package com.ldb.lms.dto.board.notice;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class NoticePaginationDto {
    private Integer itemsPerPage;
    private Integer currentPage;
    private Integer totalRows;
    private Integer totalPages;
    private Integer offset;
    private Integer startPage;
    private Integer endPage;
}