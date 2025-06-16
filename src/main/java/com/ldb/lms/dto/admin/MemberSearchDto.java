package com.ldb.lms.dto.admin;

import com.ldb.lms.dto.board.post.PostPaginationDto; 
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class MemberSearchDto extends PostPaginationDto {
    private String searchId; 
    private String searchName; 
    private String searchEmail; 
    private String searchPhone; 
    private String searchType;
}