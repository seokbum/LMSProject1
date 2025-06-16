package com.ldb.lms.controller.mypage;

import org.springframework.util.StringUtils;

public class Test {
	public static void main(String[] args) {
		String id = "S87138";
		String id2 = "P004";
		
		System.out.println(id.substring(0,id.length()-2)+"**");
		System.out.println(id2.substring(0,id2.length()-2)+"**");
		
		String id3="2";
		System.out.println(StringUtils.hasText(id3));
		
		
		
	}

}
