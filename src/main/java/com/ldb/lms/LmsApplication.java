package com.ldb.lms;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;

@SpringBootApplication
@MapperScan("com.ldb.lms.mapper.mybatis")
public class LmsApplication extends SpringBootServletInitializer {
	public static void main(String[] args) {
		SpringApplication.run(LmsApplication.class, args);
	}
}