<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="jakarta.tags.core" prefix="c"%>
<%@ taglib uri="jakarta.tags.fmt" prefix="fmt"%>           
<%@ taglib uri="jakarta.tags.functions" prefix="fn"%> 
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>공지사항 상세</title>
</head>
<body>
    <div class="container-fluid">
        <div class="card shadow-sm">
            <div class="card-header"><h3 class="card-title" style="font-weight: 600;">공지사항 상세</h3></div>
            <div class="card-body">
                <table class="table table-bordered">
                    <tbody>
                        <tr>
                            <th style="width: 15%; background-color: #f8f9fa;">제목</th>
                            <td>${fn:escapeXml(notice.noticeTitle)}</td>
                        </tr>
                        <tr>
                            <th style="background-color: #f8f9fa;">작성자</th>
                            <td>${fn:escapeXml(notice.userName)}</td>
                        </tr>
                        <tr>
                            <th style="background-color: #f8f9fa;">작성일</th>
                            <td><fmt:formatDate value="${notice.noticeCreatedAt}" pattern="yyyy-MM-dd HH:mm"/></td>
                        </tr>
                        <tr>
                            <th style="background-color: #f8f9fa;">조회수</th>
                            <td>${notice.noticeReadCount}</td>
                        </tr>
                        <tr>
                            <th style="background-color: #f8f9fa;">내용</th>
                            <td style="min-height: 200px; vertical-align: top;"><div>${notice.noticeContent}</div></td>
                        </tr>
                        <c:if test="${not empty notice.existingFilePath}">
                            <tr>
                                <th style="background-color: #f8f9fa;">첨부파일</th>
                                <td><a href="/notice/download?filePath=${notice.existingFilePath}" class="text-decoration-none">${fn:substringAfter(notice.existingFilePath, '_')}</a></td>
                            </tr>
                        </c:if>
                    </tbody>
                </table>
            </div>
            <div class="card-footer text-end">
                <a href="/notice/getNotices" class="btn btn-secondary">목록</a>
                <c:if test="${currentWriterId eq notice.writerId}">
                    <a href="/notice/updateNotice?noticeId=${notice.noticeId}" class="btn btn-warning">수정</a>
                    <a href="/notice/deleteNotice?noticeId=${notice.noticeId}" class="btn btn-danger ms-1">삭제</a>
                </c:if>
            </div>
        </div>
    </div>
</body>
</html>