package com.ldb.lms.controller.mypage;

import java.io.IOException;
import java.util.Map;


import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.SessionAttribute;

import com.ldb.lms.controller.learning_support.LearningApiController;
import com.ldb.lms.service.mypage.MypageService;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequestMapping("/api/mypage")
public class MypageApiController {

    private final MypageService mypageService;

    MypageApiController(MypageService mypageService) {
        this.mypageService = mypageService;
    }

	

}
