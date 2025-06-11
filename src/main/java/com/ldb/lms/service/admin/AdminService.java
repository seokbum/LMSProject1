package com.ldb.lms.service.admin;

import com.ldb.lms.dto.ApiResponseDto;
import com.ldb.lms.dto.schedule.ScheduleDto; // ScheduleDto 임포트
import com.ldb.lms.mapper.schedule.ScheduleMapper; // ScheduleMapper 임포트

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class AdminService {

    private final ScheduleMapper scheduleMapper;

    public ApiResponseDto<Map<String, Object>> getSchedulesForApi() {
        try {
            List<ScheduleDto> schedules = scheduleMapper.listSchedules(); 
            Map<String, Object> data = new HashMap<>();
            data.put("schedules", schedules);
            return new ApiResponseDto<>(true, "스케줄 조회 성공", data);
        } catch (Exception e) {
            log.error("스케줄 조회 실패 (API)", e);
            return new ApiResponseDto<>(false, "스케줄 조회 실패", null);
        }
    }

}