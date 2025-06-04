package com.ldb.lms.dto.board.post;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class PostPaginationDto {
    private Integer itemsPerPage = 10; 
    private Integer currentPage = 1;   
    private Integer totalRows;
    private Integer totalPages;
    private Integer offset;
    private Integer startPage;
    private Integer endPage;
}