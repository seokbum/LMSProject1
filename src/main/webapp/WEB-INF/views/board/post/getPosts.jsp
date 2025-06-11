<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="jakarta.tags.core" prefix="c"%>
<%@ taglib uri="jakarta.tags.fmt" prefix="fmt"%>
<%@ taglib uri="jakarta.tags.functions" prefix="fn"%>
<!DOCTYPE html>
<html lang="ko">
<head>
    <meta charset="UTF-8">
    <title>문의게시판</title>
</head>
<body>
    <div class="container-fluid">
        <div class="row">
            <div class="col-12">
                <div class="card shadow-sm">
                    <div class="card-header">
                        <h3 class="card-title" style="font-weight: 600;">문의게시판</h3>
                    </div>
                    <div class="card-body">
                        <div class="mb-4 p-3 bg-light rounded border">
                            <form id="searchForm" action="/post/getPosts" method="get" class="row gx-2 gy-2 align-items-center">
                                <div class="col-md-3">
                                    <select name="searchType" class="form-select form-select-sm">
                                        <option value="postTitle,userName,postContent" ${empty search.searchType or search.searchType == 'postTitle,userName,postContent' ? 'selected' : ''}>전체</option>
                                        <option value="postTitle" ${search.searchType == 'postTitle' ? 'selected' : ''}>제목</option>
                                        <option value="userName" ${search.searchType == 'userName' ? 'selected' : ''}>작성자</option>
                                        <option value="postContent" ${search.searchType == 'postContent' ? 'selected' : ''}>내용</option>
                                    </select>
                                </div>
                                <div class="col-md-7">
                                    <input type="text" name="searchKeyword" class="form-control form-control-sm" placeholder="검색어를 입력하세요" value="${fn:escapeXml(search.searchKeyword)}">
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
                                <c:forEach var="post" items="${noticeList}">
                                    <tr class="table-warning">
                                        <td><span class="badge bg-dark">공지</span></td>
                                        <td class="text-start">
                                            <a href="/post/getPostDetail?postId=${post.postId}" class="text-dark text-decoration-none fw-bold">${fn:escapeXml(post.postTitle)}</a>
                                        </td>
                                        <td>${fn:escapeXml(post.userName)}</td>
                                        <td>
                                            <c:set var="todayDate" value="<%= new java.util.Date() %>" />
                                            <fmt:formatDate var="todayFormatted" value="${todayDate}" pattern="yyyy-MM-dd" />
                                            <fmt:formatDate var="postDateFormatted" value="${post.postCreatedAt}" pattern="yyyy-MM-dd" />
                                            <c:choose>
                                                <c:when test="${todayFormatted == postDateFormatted}">
                                                    <fmt:formatDate value="${post.postCreatedAt}" pattern="HH:mm" />
                                                </c:when>
                                                <c:otherwise>
                                                    <fmt:formatDate value="${post.postCreatedAt}" pattern="yyyy-MM-dd" />
                                                </c:otherwise>
                                            </c:choose>
                                        </td>
                                        <td>${post.postReadCount}</td>
                                    </tr>
                                </c:forEach>
                                <c:choose>
                                    <c:when test="${not empty postList}">
                                        <c:forEach var="post" items="${postList}" varStatus="status">
                                            <tr>
                                                <td>${pagination.totalRows - pagination.offset - status.index}</td>
                                                <td class="text-start">
                                                    <c:if test="${post.postGroupLevel > 0}">
                                                        <span style="padding-left: ${post.postGroupLevel * 20}px;"></span>
                                                        <span class="text-secondary">↳&nbsp;</span>
                                                    </c:if>
                                                    <a href="/post/getPostDetail?postId=${post.postId}" class="text-dark text-decoration-none">${fn:escapeXml(post.postTitle)}</a>
                                                </td>
                                                <td>${fn:escapeXml(post.userName)}</td>
                                                <td>
                                                    <c:set var="todayDate" value="<%= new java.util.Date() %>" />
                                                    <fmt:formatDate var="todayFormatted" value="${todayDate}" pattern="yyyy-MM-dd" />
                                                    <fmt:formatDate var="postDateFormatted" value="${post.postCreatedAt}" pattern="yyyy-MM-dd" />
                                                    <c:choose>
                                                        <c:when test="${todayFormatted == postDateFormatted}">
                                                            <fmt:formatDate value="${post.postCreatedAt}" pattern="HH:mm" />
                                                        </c:when>
                                                        <c:otherwise>
                                                            <fmt:formatDate value="${post.postCreatedAt}" pattern="yyyy-MM-dd" />
                                                        </c:otherwise>
                                                    </c:choose>
                                                </td>
                                                <td>${post.postReadCount}</td>
                                            </tr>
                                        </c:forEach>
                                    </c:when>
                                    <c:otherwise>
                                        <tr>
                                            <td colspan="5" class="text-center text-muted py-5">게시물이 없습니다.</td>
                                        </tr>
                                    </c:otherwise>
                                </c:choose>
                            </tbody>
                        </table>
                    </div>
                    <div class="card-footer clearfix">
                        <a href="/post/createPost" class="btn btn-primary float-end">글쓰기</a>
                        <nav class="d-flex justify-content-center">
                            <ul class="pagination m-0">
                                <c:if test="${pagination.prev}">
                                    <li class="page-item">
                                        <a class="page-link" href="?currentPage=${pagination.currentPage - 1}&searchType=${fn:escapeXml(search.searchType)}&searchKeyword=${fn:escapeXml(search.searchKeyword)}">이전</a>
                                    </li>
                                </c:if>
                                <c:forEach var="i" begin="${pagination.startPage}" end="${pagination.endPage}">
                                    <li class="page-item ${i == pagination.currentPage ? 'active' : ''}">
                                        <a class="page-link" href="?currentPage=${i}&searchType=${fn:escapeXml(search.searchType)}&searchKeyword=${fn:escapeXml(search.searchKeyword)}">${i}</a>
                                    </li>
                                </c:forEach>
                                <c:if test="${pagination.next}">
                                    <li class="page-item">
                                        <a class="page-link" href="?currentPage=${pagination.currentPage + 1}&searchType=${fn:escapeXml(search.searchType)}&searchKeyword=${fn:escapeXml(search.searchKeyword)}">다음</a>
                                    </li>
                                </c:if>
                            </ul>
                        </nav>
                    </div>
                </div>
            </div>
        </div>
    </div>
</body>
</html>