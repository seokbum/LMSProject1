package com.ldb.lms.dto.board.notice;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString

public class NoticeSearchDto {
	private String searchType; 
    private String searchKeyword;
}
