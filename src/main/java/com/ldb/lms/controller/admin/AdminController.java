package com.ldb.lms.controller.admin;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/admin")
@RequiredArgsConstructor
public class AdminController {

    // 학사일정 관리 페이지로 이동
    @GetMapping("/schedules")
    public String showScheduleManagementPage() {
        return "admin/schedules";
    }

    // 회원 관리 페이지로 이동
    @GetMapping("/members")
    public String showMemberManagementPage() {
        return "admin/members";
    }
}