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

    public void calculatePagination() {
        if (totalRows == null || totalRows <= 0 || itemsPerPage == null || itemsPerPage <= 0) {
            totalPages = 1;
            offset = 0;
            startPage = 1;
            endPage = 1;
            return;
        }
        totalPages = (int) Math.ceil((double) totalRows / itemsPerPage);
        offset = (currentPage - 1) * itemsPerPage;
        startPage = Math.max(1, currentPage - 2);
        endPage = Math.min(totalPages, currentPage + 2);
    }
}