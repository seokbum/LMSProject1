package com.ldb.lms.service.board;


import org.springframework.stereotype.Service;

import com.ldb.lms.mapper.board.NoticeMapper;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class NoticeService {
	
	private final NoticeMapper noticeMapper; 
}
