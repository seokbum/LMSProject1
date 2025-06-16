package com.ldb.lms.controller.admin;

import com.ldb.lms.dto.ApiResponseDto;
import com.ldb.lms.dto.admin.MemberSearchDto;
import com.ldb.lms.dto.schedule.ScheduleDto;
import com.ldb.lms.service.admin.AdminService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
public class AdminApiController {

    private final AdminService adminService;

    // 학사일정 전체 조회
    @GetMapping("/schedule")
    public ResponseEntity<ApiResponseDto<Map<String, Object>>> getSchedules(
        @RequestParam(required = false) String semesterType
    ) {
        ApiResponseDto<Map<String, Object>> response = adminService.getSchedulesForApi(semesterType);
        return ResponseEntity.ok(response);
    }

    // 학사일정 상세 조회
    @GetMapping("/schedule/{scheduleId}")
    public ResponseEntity<ApiResponseDto<ScheduleDto>> getScheduleDetail(@PathVariable Integer scheduleId) {
        ApiResponseDto<ScheduleDto> response = adminService.getScheduleDetail(scheduleId);
        if (response.isSuccess()) {
            return ResponseEntity.ok(response);
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
        }
    }

    // 학사일정 추가
    @PostMapping("/schedule")
    public ResponseEntity<ApiResponseDto<Void>> addSchedule(@RequestBody ScheduleDto scheduleDto) {
        ApiResponseDto<Void> response = adminService.addSchedule(scheduleDto);
        if (response.isSuccess()) {
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } else {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }
    }

    // 학사일정 수정
    @PutMapping("/schedule/{scheduleId}")
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
    @DeleteMapping("/schedule/{scheduleId}")
    public ResponseEntity<ApiResponseDto<Void>> deleteSchedule(@PathVariable Integer scheduleId) {
        ApiResponseDto<Void> response = adminService.deleteSchedule(scheduleId);
        if (response.isSuccess()) {
            return ResponseEntity.noContent().build();
        } else {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }
    }

    // 회원 목록 조회 API
    @GetMapping("/members")
    public ResponseEntity<ApiResponseDto<Map<String, Object>>> getMembers(
            MemberSearchDto searchDto
    ) {
        ApiResponseDto<Map<String, Object>> response = adminService.getMembersForApi(searchDto);
        return ResponseEntity.ok(response);
    }

    // 회원 삭제 API
    @DeleteMapping("/members/{type}/{id}")
    public ResponseEntity<ApiResponseDto<Void>> deleteMember(
            @PathVariable String id,
            @PathVariable String type
    ) {
        ApiResponseDto<Void> response = adminService.deleteMember(id, type);
        if (response.isSuccess()) {
            return ResponseEntity.noContent().build();
        } else {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }
    }
}