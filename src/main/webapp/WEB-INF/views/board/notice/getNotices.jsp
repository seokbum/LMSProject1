<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="jakarta.tags.core" prefix="c" %>
<%@ taglib uri="jakarta.tags.fmt" prefix="fmt" %>
<%@ taglib uri="jakarta.tags.functions" prefix="fn" %>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>공지사항</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css" rel="stylesheet">
</head>
<body>
    <div class="container mt-5">
        <h2 class="text-center fs-1">공지사항</h2>
        <c:if test="${not empty msg}">
            <div class="alert alert-danger"><c:out value="${msg}"/></div>
        </c:if>
        <form action="/notice/getNotices" method="get" class="mb-4">
            <div class="row">
                <div class="col-md-3">
                    <select name="searchType" class="form-select">
                        <option value="">전체</option>
                        <option value="userName" <c:if test="${searchDto.searchType == 'userName'}">selected</c:if>>작성자</option>
                        <option value="noticeTitle" <c:if test="${searchDto.searchType == 'noticeTitle'}">selected</c:if>>제목</option>
                        <option value="noticeContent" <c:if test="${searchDto.searchType == 'noticeContent'}">selected</c:if>>내용</option>
                        <option value="noticeTitle,userName" <c:if test="${searchDto.searchType == 'noticeTitle,userName'}">selected</c:if>>제목+작성자</option>
                        <option value="noticeTitle,noticeContent" <c:if test="${searchDto.searchType == 'noticeTitle,noticeContent'}">selected</c:if>>제목+내용</option>
                        <option value="userName,noticeContent" <c:if test="${searchDto.searchType == 'userName,noticeContent'}">selected</c:if>>작성자+내용</option>
                        <option value="noticeTitle,userName,noticeContent" <c:if test="${searchDto.searchType == 'noticeTitle,userName,noticeContent'}">selected</c:if>>제목+작성자+내용</option>
                    </select>
                </div>
                <div class="col-md-6">
                    <input type="text" name="searchKeyword" class="form-control" value="${searchDto.searchKeyword}" placeholder="검색어를 입력하세요">
                </div>
                <div class="col-md-3">
                    <button type="submit" class="btn btn-primary w-100">검색</button>
                </div>
            </div>
        </form>
        <table class="table table-bordered">
            <thead>
                <tr>
                    <th>번호</th>
                    <th>제목</th>
                    <th>작성자</th>
                    <th>작성일</th>
                    <th>조회수</th>
                </tr>
            </thead>
            <tbody>
                <c:choose>
                    <c:when test="${fn:length(notices) > 0}">
                        <c:forEach var="notice" items="${notices}" varStatus="status">
                            <tr>
                                <td><c:out value="${pagination.totalRows - (pagination.offset + status.index)}"/></td>
                                <td>
                                    <a href="<c:url value='/notice/view'><c:param name='noticeId' value='${notice.noticeId}'/></c:url>"><c:out value="${notice.noticeTitle}"/></a>
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
                            <td colspan="5" class="text-center">공지사항이 없습니다.</td>
                        </tr>
                    </c:otherwise>
                </c:choose>
            </tbody>
        </table>
        <div class="text-end">
            <a href="<c:url value='/notice/createNotice'/>" class="btn btn-primary">글쓰기</a>
        </div>
        <nav>
            <ul class="pagination justify-content-center">
                <li class="page-item <c:if test="${pagination.currentPage <= 1}">disabled</c:if>">
                    <a class="page-link" href="<c:url value='/notice/list'>
                                               <c:param name='pageNum' value='${pagination.currentPage - 1}'/>
                                               <c:if test="${not empty searchDto.searchType}"><c:param name='searchType' value='${searchDto.searchType}'/></c:if>
                                               <c:if test="${not empty searchDto.searchKeyword}"><c:param name='searchKeyword' value='${searchDto.searchKeyword}'/></c:if>
                                               </c:url>">이전</a>
                </li>
                <c:forEach var="i" begin="${pagination.startPage}" end="${pagination.endPage}">
                    <li class="page-item <c:if test="${i == pagination.currentPage}">active</c:if>">
                        <a class="page-link" href="<c:url value='/notice/list'>
                                                   <c:param name='pageNum' value='${i}'/>
                                                   <c:if test="${not empty searchDto.searchType}"><c:param name='searchType' value='${searchDto.searchType}'/></c:if>
                                                   <c:if test="${not empty searchDto.searchKeyword}"><c:param name='searchKeyword' value='${searchDto.searchKeyword}'/></c:if>
                                                   </c:url>"><c:out value="${i}"/></a>
                    </li>
                </c:forEach>
                <li class="page-item <c:if test="${pagination.currentPage >= pagination.totalPages}">disabled</c:if>">
                    <a class="page-link" href="<c:url value='/notice/list'>
                                               <c:param name='pageNum' value='${pagination.currentPage + 1}'/>
                                               <c:if test="${not empty searchDto.searchType}"><c:param name='searchType' value='${searchDto.searchType}'/></c:if>
                                               <c:if test="${not empty searchDto.searchKeyword}"><c:param name='searchKeyword' value='${searchDto.searchKeyword}'/></c:if>
                                               </c:url>">다음</a>
                </li>
            </ul>
        </nav>
    </div>
</body>
</html>