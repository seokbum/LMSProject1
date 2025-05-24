package com.ldb.lms.dto.professor_support;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class PaginationDto {
	
	private String professorId;
	private Integer currentPage;        // 현재 페이지 번호
    private Integer totalRows;          // 총 로우 수
    private Integer itemsPerPage;       // 페이지당 로우 수(기본값:10)
    private Integer totalPages;         // 총 페이지 수
    private Integer offset; 			// 페이지조회시 시작할 지점
    private String search;   		// 검색 키워드 
    private String sortDirection;   // 정렬방향
    private String urlPattern;      // 페이지 이동 URL 패턴 (예: /board?page=)

}