package com.ldb.lms.controller.admin;

import com.ldb.lms.dto.ApiResponseDto;
import com.ldb.lms.dto.schedule.ScheduleDto;
import com.ldb.lms.service.admin.AdminService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/api/admin/schedule")
@RequiredArgsConstructor
public class AdminApiController {

    private final AdminService adminService;

    // 학사일정 전체 조회 
    @GetMapping
    public ResponseEntity<ApiResponseDto<Map<String, Object>>> getSchedules(
        @RequestParam(required = false) String semesterType 
    ) {
        ApiResponseDto<Map<String, Object>> response = adminService.getSchedulesForApi(semesterType); // 서비스 메서드 호출 변경
        return ResponseEntity.ok(response);
    }

    // 학사일정 상세 조회
    @GetMapping("{scheduleId}")
    public ResponseEntity<ApiResponseDto<ScheduleDto>> getScheduleDetail(@PathVariable Integer scheduleId) {
        ApiResponseDto<ScheduleDto> response = adminService.getScheduleDetail(scheduleId);
        if (response.isSuccess()) {
            return ResponseEntity.ok(response);
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
        }
    }

    // 학사일정 추가
    @PostMapping
    public ResponseEntity<ApiResponseDto<Void>> addSchedule(@RequestBody ScheduleDto scheduleDto) {
        ApiResponseDto<Void> response = adminService.addSchedule(scheduleDto);
        if (response.isSuccess()) {
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } else {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }
    }

    // 학사일정 수정
    @PutMapping("{scheduleId}")
    public ResponseEntity<ApiResponseDto<Void>> updateSchedule(@PathVariable Integer scheduleId, @RequestBody ScheduleDto scheduleDto) {
        scheduleDto.setScheduleId(scheduleId);
        ApiResponseDto<Void> response = adminService.updateSchedule(scheduleDto);
        if (response.isSuccess()) {
            return ResponseEntity.ok(response);
        } else {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }
    }

    // 학사일정 삭제
    @DeleteMapping("{scheduleId}")
    public ResponseEntity<ApiResponseDto<Void>> deleteSchedule(@PathVariable Integer scheduleId) {
        ApiResponseDto<Void> response = adminService.deleteSchedule(scheduleId);
        if (response.isSuccess()) {
            return ResponseEntity.noContent().build();
        } else {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }
    }
}