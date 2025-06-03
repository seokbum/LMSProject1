package com.ldb.lms.dto.professor_support;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class PaginationDto {
	
	private String professorId;
	private Integer page = 1;				// 클라이언트로부터 요청받은 페이지번호
	private Integer startPage;          // 클라이언트에 표시할 시작 페이지 번호
	private Integer endPage;			// 클라이언트에 표시할 마지막 페이지 번호
    private Integer totalRows;          // 총 데이터 수
    private Integer itemsPerPage = 10;  // 페이지당 로우 수(기본값:10)
    private Integer totalPages;         // 총 페이지 수
    private Integer offset; 			// 페이지조회시 시작할 지점
    private String search;   			// 검색 키워드 
    private String sortDirection;   	// 정렬방향
    
}