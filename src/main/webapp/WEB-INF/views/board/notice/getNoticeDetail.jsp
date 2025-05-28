<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<c:set var="path" value="${pageContext.request.contextPath}"/>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>공지사항 게시물 상세</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css" rel="stylesheet">
    <style>
        body {
            background-color: #f8f9fa;
        }
        .container {
            width: 100%;
            max-width: none;
            padding: 30px;
            border-radius: 8px;
            box-shadow: 0 1px 3px rgba(0, 0, 0, 0.05);
            border: 1px solid #e9ecef;
        }
        .table {
            width: 100%;
            table-layout: fixed;
        }
        .table th, .table td {
            padding: 12px;
            vertical-align: middle;
            word-wrap: break-word;
        }
        .table th {
            width: 20%;
            background: #edf2f7;
            font-weight: 600;
        }
        .table td {
            width: 80%;
        }
        .btn {
            border-radius: 0.25rem;
            padding: 0.75rem 1.25rem;
            font-size: 1rem;
            font-weight: 500;
        }
        .btn-secondary {
            background-color: #6c757d;
            border-color: #6c757d;
            color: #fff;
        }
        .btn-secondary:hover {
            background-color: #5a6268;
            border-color: #545b62;
        }
        .btn-danger {
            background-color: #dc3545;
            border-color: #dc3545;
            color: #fff;
        }
        .btn-danger:hover {
            background-color: #c82333;
            border-color: #bd2130;
        }
        .text-end {
            margin-bottom: 2rem;
        }
        .alert-warning {
            padding: 1rem 1.5rem;
            margin-bottom: 1.5rem;
            border: 1px solid #ffeeba;
            border-radius: 0.25rem;
            background-color: #fff3cd;
            color: #856404;
        }
        .fs-1 {
            font-size: 2.2rem !important;
            color: #343a40;
            margin-bottom: 1.5rem;
            font-weight: bold;
        }
    </style>
</head>
<body>
    <div class="container mt-5">
        <h1 class="fs-1">공지사항</h1>
        <c:if test="${empty notice && empty msg}">
            <div class="alert alert-warning">게시물이 존재하지 않습니다.</div>
        </c:if>
        <c:if test="${not empty notice}">
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
                            <a href="${path}/dist/assets/upload/${notice.noticeFile}" download="${notice.noticeFile}">${notice.noticeFile}</a>
                        </c:if>
                        <c:if test="${empty notice.noticeFile}">
                            없음
                        </c:if>
                    </td>
                </tr>
            </table>
            <div class="text-end mb-5">
                <a href="${path}/notice/getNotices" class="btn btn-secondary">목록</a>
                <!-- 나중에 권한 체크 필요 시 아래 주석 해제 및 수정
                <c:if test="${notice.writerId == sessionScope.login}">
                    <a href="${path}/notice/updateNotice?noticeId=${notice.noticeId}" class="btn btn-secondary">수정</a>
                    <a href="${path}/notice/deleteNotice?noticeId=${notice.noticeId}" class="btn btn-danger">삭제</a>
                </c:if> -->
                <a href="${path}/notice/updateNotice?noticeId=${notice.noticeId}" class="btn btn-secondary">수정</a>
                <a href="${path}/notice/deleteNotice?noticeId=${notice.noticeId}" class="btn btn-danger">삭제</a>
            </div>
        </c:if>
    </div>
</body>
</html>