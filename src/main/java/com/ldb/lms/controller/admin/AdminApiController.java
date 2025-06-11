package com.ldb.lms.controller.admin;

import com.ldb.lms.dto.ApiResponseDto;
import com.ldb.lms.service.admin.AdminService; // AdminService 임포트

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/api/admin") 
@RequiredArgsConstructor
public class AdminApiController {

    private final AdminService adminService;

    @GetMapping("schedule") 
    public ResponseEntity<ApiResponseDto<Map<String, Object>>> getSchedules() {
        ApiResponseDto<Map<String, Object>> response = adminService.getSchedulesForApi();

        if (response.isSuccess()) {
            return ResponseEntity.ok(response);
        } else {
            return ResponseEntity.status(400).body(response);
        }
    }
}