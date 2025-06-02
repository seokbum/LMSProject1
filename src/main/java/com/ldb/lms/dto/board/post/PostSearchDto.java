package com.ldb.lms.dto.board.post;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString

public class PostSearchDto {
	private String searchType; 
    private String searchKeyword;
}
