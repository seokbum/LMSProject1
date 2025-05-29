<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>공지사항 게시물 상세</title>
</head>
<body>
    <div class="notice-container">
        <h1 class="notice-title fs-1" >공지사항</h1>
            <table class="table">
                <tr>
                    <th>제목</th>
                    <td>${notice.noticeTitle}</td>
                </tr>
                <tr>
                    <th>작성자</th>
                    <td>${notice.userName}</td>
                </tr>
                <tr>
                    <th>작성일</th>
                    <td><fmt:formatDate value="${notice.noticeCreatedAt}" pattern="yyyy-MM-dd HH:mm:ss"/></td>
                </tr>
                <tr>
                    <th>조회수</th>
                    <td>${notice.noticeReadCount}</td>
                </tr>
                <tr>
                    <th>내용</th>
                    <td>${notice.noticeContent}</td>
                </tr>
                <tr>
                    <th>첨부파일</th>
                    <td>
                        <c:if test="${not empty notice.noticeFile}">
                            <a href="/dist/assets/upload/${notice.noticeFile}" download="${notice.noticeFile}" class="btn btn-sm btn-outline-secondary">다운로드</a>
                        </c:if>
                        <c:if test="${empty notice.noticeFile}">
                            없음
                        </c:if>
                    </td>
                </tr>
            </table>
            <div class="text-end mb-5">
                <a href="getNotices" class="btn btn-secondary btn-custom">목록</a>
                <a href="updateNotice?noticeId=${notice.noticeId}" class="btn btn-secondary btn-custom">수정</a>
                <a href="deleteNotice?noticeId=${notice.noticeId}" class="btn btn-danger btn-custom">삭제</a>
            </div>
    </div>
</body>
</html>