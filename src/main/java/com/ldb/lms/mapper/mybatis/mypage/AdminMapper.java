package com.ldb.lms.mapper.mybatis.mypage;

import com.ldb.lms.dto.admin.MemberDto;
import com.ldb.lms.dto.admin.MemberSearchDto;
import com.ldb.lms.dto.mypage.AdminDto;
import java.util.List;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface AdminMapper {

    // 관리자 ID로 관리자 정보를 조회합니다.
    AdminDto selectOne(String id);

    // 검색 조건에 맞는 전체 회원(학생 + 교수)의 수를 조회합니다.
    int getTotalMembersCount(MemberSearchDto searchDto);

    // 검색 조건 및 페이징에 따라 학생 또는 교수 목록을 조회합니다.
    List<MemberDto> getMemberList(MemberSearchDto searchDto);

    // 특정 학생 ID로 학생을 삭제합니다.
    int deleteStudent(String studentId);

    // 특정 교수 ID로 교수를 삭제합니다.
    int deleteProfessor(String professorId);
}