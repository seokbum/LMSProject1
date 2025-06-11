<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib uri="jakarta.tags.core" prefix="c"%>
<%@ taglib uri="jakarta.tags.fmt" prefix="fmt"%>           
<%@ taglib uri="jakarta.tags.functions" prefix="fn"%> 
<!DOCTYPE html>
<html lang="ko">
<head>
 	<meta charset="UTF-8">
    <title>공지사항 목록</title> 
</head>
<body>
    <div class="container-fluid">
        <div class="card shadow-sm">
            <div class="card-header">
                <h3 class="card-title" style="font-weight: 600;">공지사항</h3>
            </div>
            <div class="card-body">
                <div class="mb-4 p-3 bg-light rounded border">
                    <form id="searchForm" action="/notice/getNotices" method="get" class="row gx-2 gy-2 align-items-center">
                        <div class="col-md-3">
                            <select name="searchType" class="form-select form-select-sm">
                                <option value="noticeTitle,userName,noticeContent" ${empty searchDto.searchType ? 'selected' : ''}>전체</option>
                                <option value="noticeTitle" ${searchDto.searchType == 'noticeTitle' ? 'selected' : ''}>제목</option>
                                <option value="userName" ${searchDto.searchType == 'userName' ? 'selected' : ''}>작성자</option>
                                <option value="noticeContent" ${searchDto.searchType == 'noticeContent' ? 'selected' : ''}>내용</option>
                            </select>
                        </div>
                        <div class="col-md-7">
                            <input type="text" name="searchKeyword" class="form-control form-control-sm" placeholder="검색어를 입력하세요" value="${fn:escapeXml(searchDto.searchKeyword)}">
                        </div>
                        <div class="col-md-2 d-grid">
                            <button type="submit" class="btn btn-sm btn-secondary">검색</button>
                        </div>
                    </form>
                </div>

                <table class="table table-hover text-center">
                    <thead class="table-light">
                        <tr>
                            <th style="width: 10%;">번호</th>
                            <th style="width: 50%; text-align: left;">제목</th>
                            <th style="width: 15%;">작성자</th>
                            <th style="width: 15%;">작성일</th>
                            <th style="width: 10%;">조회수</th>
                        </tr>
                    </thead>
                    <tbody>
                        <c:choose>
                            <c:when test="${not empty notices}">
                                <c:forEach var="notice" items="${notices}" varStatus="status">
                                    <tr>
                                        <td><c:out value="${pagination.totalRows - (pagination.offset + status.index)}"/></td>
                                        <td class="text-start">
                                            <a href="/notice/getNoticeDetail?noticeId=${notice.noticeId}" class="text-dark text-decoration-none">
                                                <c:out value="${notice.noticeTitle}"/>
                                            </a>
                                        </td>
                                        <td><c:out value="${notice.userName}"/></td>
                                        <td>
                                            <c:set var="todayDate" value="<%= new java.util.Date() %>" />
                                            <fmt:formatDate var="todayFormatted" value="${todayDate}" pattern="yyyy-MM-dd" />
                                            <fmt:formatDate var="noticeDateFormatted" value="${notice.noticeCreatedAt}" pattern="yyyy-MM-dd" />
                                            <c:choose>
                                                <c:when test="${todayFormatted == noticeDateFormatted}">
                                                    <fmt:formatDate value="${notice.noticeCreatedAt}" pattern="HH:mm" />
                                                </c:when>
                                                <c:otherwise>
                                                    <fmt:formatDate value="${notice.noticeCreatedAt}" pattern="yyyy-MM-dd" />
                                                </c:otherwise>
                                            </c:choose>
                                        </td>
                                        <td><c:out value="${notice.noticeReadCount}"/></td>
                                    </tr>
                                </c:forEach>
                            </c:when>
                            <c:otherwise>
                                <tr>
                                    <td colspan="5" class="text-center text-muted py-5">공지사항이 없습니다.</td>
                                </tr>
                            </c:otherwise>
                        </c:choose>
                    </tbody>
                </table>
            </div>
            <div class="card-footer clearfix">
                <c:if test="${not empty currentWriterId and fn:startsWith(currentWriterId, 'P')}">
                    <a href="/notice/createNotice" class="btn btn-primary float-end">글쓰기</a>
                </c:if>
                <nav class="d-flex justify-content-center">
                    <ul class="pagination m-0">
                        <c:if test="${pagination.prev}">
                            <li class="page-item">
                                <a class="page-link" href="?pageNum=${pagination.currentPage - 1}&searchType=${fn:escapeXml(searchDto.searchType)}&searchKeyword=${fn:escapeXml(searchDto.searchKeyword)}">이전</a>
                            </li>
                        </c:if>
                        <c:forEach var="i" begin="${pagination.startPage}" end="${pagination.endPage}">
                            <li class="page-item <c:if test='${i == pagination.currentPage}'>active</c:if>">
                                <a class="page-link" href="?pageNum=${i}&searchType=${fn:escapeXml(searchDto.searchType)}&searchKeyword=${fn:escapeXml(searchDto.searchKeyword)}">${i}</a>
                            </li>
                        </c:forEach>
                        <c:if test="${pagination.next}">
                            <li class="page-item">
                                <a class="page-link" href="?pageNum=${pagination.currentPage + 1}&searchType=${fn:escapeXml(searchDto.searchType)}&searchKeyword=${fn:escapeXml(searchDto.searchKeyword)}">다음</a>
                            </li>
                        </c:if>
                    </ul>
                </nav>
            </div>
        </div>
    </div>
</body>
</html>