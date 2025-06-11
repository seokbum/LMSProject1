package com.ldb.lms.dto.board.post;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PostPaginationDto {

    private int itemsPerPage = 10;
    private int currentPage = 1;
    private int totalRows;
    private int totalPages;
    private int offset;
    private int startPage;
    private int endPage;
    private boolean prev;
    private boolean next;

    public void calculatePagination() {
        if (totalRows <= 0) {
            totalRows = 0;
        }
        
        totalPages = (int) Math.ceil((double) totalRows / itemsPerPage);
        
        if (totalPages == 0) {
            totalPages = 1;
        }
        
        if (currentPage > totalPages) {
            currentPage = totalPages;
        }

        offset = (currentPage - 1) * itemsPerPage;
        
        int pageBlockSize = 5;
        startPage = ((currentPage - 1) / pageBlockSize) * pageBlockSize + 1;
        endPage = Math.min(startPage + pageBlockSize - 1, totalPages);

        this.prev = this.currentPage > 1;
        this.next = this.currentPage < this.totalPages;
    }
}