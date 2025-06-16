package com.ldb.lms.service.admin;

import com.ldb.lms.dto.ApiResponseDto;
import com.ldb.lms.dto.admin.MemberDto;
import com.ldb.lms.dto.admin.MemberSearchDto;
import com.ldb.lms.dto.schedule.ScheduleDto;
import com.ldb.lms.mapper.mybatis.mypage.AdminMapper;
import com.ldb.lms.mapper.mybatis.schedule.ScheduleMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class AdminService {

    private final ScheduleMapper scheduleMapper;
    
    private final AdminMapper adminMapper;

    public ApiResponseDto<Map<String, Object>> getSchedulesForApi(String semesterType) {
        try {
            List<ScheduleDto> schedules;

            if (semesterType != null && !semesterType.isEmpty() && !semesterType.equals("전체")) {
                schedules = scheduleMapper.listSchedules(semesterType);
            } else {
                schedules = scheduleMapper.listSchedules(null);
            }

            Map<String, Object> data = new HashMap<>();
            data.put("schedules", schedules);
            return new ApiResponseDto<>(true, "스케줄 조회 성공", data);
        } catch (Exception e) {
            log.error("스케줄 조회 실패 (API)", e);
            return new ApiResponseDto<>(false, "스케줄 조회 실패", null);
        }
    }

    @Transactional
    public ApiResponseDto<Void> addSchedule(ScheduleDto scheduleDto) {
        try {
            if (scheduleDto.getSemesterType() == null || scheduleDto.getSemesterType().isEmpty()) {
                return new ApiResponseDto<>(false, "학기/방학 구분을 선택해주세요.", null);
            }

            scheduleMapper.insertSchedule(scheduleDto);
            return new ApiResponseDto<>(true, "학사일정 추가 성공", null);
        } catch (Exception e) {
            log.error("학사일정 추가 실패", e);
            return new ApiResponseDto<>(false, "학사일정 추가 실패: " + e.getMessage(), null);
        }
    }

    @Transactional
    public ApiResponseDto<Void> updateSchedule(ScheduleDto scheduleDto) {
        try {
            ScheduleDto existingSchedule = scheduleMapper.getScheduleById(scheduleDto.getScheduleId());
            if (existingSchedule == null) {
                return new ApiResponseDto<>(false, "존재하지 않는 학사일정입니다.", null);
            }
            
            if (scheduleDto.getSemesterType() == null || scheduleDto.getSemesterType().isEmpty()) {
                return new ApiResponseDto<>(false, "학기/방학 구분을 선택해주세요.", null);
            }

            scheduleMapper.updateSchedule(scheduleDto);
            return new ApiResponseDto<>(true, "학사일정 수정 성공", null);
        } catch (Exception e) {
            log.error("학사일정 수정 실패", e);
            return new ApiResponseDto<>(false, "학사일정 수정 실패: " + e.getMessage(), null);
        }
    }

    @Transactional
    public ApiResponseDto<Void> deleteSchedule(Integer scheduleId) {
        try {
            ScheduleDto existingSchedule = scheduleMapper.getScheduleById(scheduleId);
            if (existingSchedule == null) {
                return new ApiResponseDto<>(false, "존재하지 않는 학사일정입니다.", null);
            }
            scheduleMapper.deleteSchedule(scheduleId);
            return new ApiResponseDto<>(true, "학사일정 삭제 성공", null);
        } catch (Exception e) {
            log.error("학사일정 삭제 실패", e);
            return new ApiResponseDto<>(false, "학사일정 삭제 실패: " + e.getMessage(), null);
        }
    }

    public ApiResponseDto<ScheduleDto> getScheduleDetail(Integer scheduleId) {
        try {
            ScheduleDto schedule = scheduleMapper.getScheduleById(scheduleId);
            if (schedule == null) {
                return new ApiResponseDto<>(false, "존재하지 않는 학사일정입니다.", null);
            }
            return new ApiResponseDto<>(true, "학사일정 상세 조회 성공", schedule);
        } catch (Exception e) {
            log.error("학사일정 상세 조회 실패", e);
            return new ApiResponseDto<>(false, "학사일정 상세 조회 실패: " + e.getMessage(), null);
        }
    }

    public ApiResponseDto<Map<String, Object>> getMembersForApi(MemberSearchDto searchDto) {
        try {

            int totalRows = adminMapper.getTotalMembersCount(searchDto);
            searchDto.setTotalRows(totalRows);
            searchDto.calculatePagination();

            List<MemberDto> members = adminMapper.getMemberList(searchDto);

            Map<String, Object> data = new HashMap<>();
            data.put("members", members);
            data.put("pagination", searchDto); 

            return new ApiResponseDto<>(true, "회원 목록 조회 성공", data);
        } catch (Exception e) {
            log.error("회원 목록 조회 실패", e);
            return new ApiResponseDto<>(false, "회원 목록 조회 실패: " + e.getMessage(), null);
        }
    }

    @Transactional
    public ApiResponseDto<Void> deleteMember(String id, String type) {
        try {
            int deletedRows = 0;
            if ("STUDENT".equalsIgnoreCase(type)) {
                deletedRows = adminMapper.deleteStudent(id);
            } else if ("PROFESSOR".equalsIgnoreCase(type)) {
                deletedRows = adminMapper.deleteProfessor(id);
            } else {
                return new ApiResponseDto<>(false, "유효하지 않은 회원 타입입니다.", null);
            }

            if (deletedRows > 0) {
                return new ApiResponseDto<>(true, "회원 삭제 성공", null);
            } else {
                return new ApiResponseDto<>(false, "회원을 찾을 수 없거나 삭제에 실패했습니다.", null);
            }
        } catch (Exception e) {
            log.error("회원 삭제 실패", e);
            return new ApiResponseDto<>(false, "회원 삭제 실패: " + e.getMessage(), null);
        }
    }
}