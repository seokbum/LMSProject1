<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib uri="jakarta.tags.core" prefix="c"%>
<%@ taglib uri="jakarta.tags.fmt" prefix="fmt"%>           
<%@ taglib uri="jakarta.tags.functions" prefix="fn"%> 
<!DOCTYPE html>
<html lang="ko">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>문의게시판 목록</title> 
    <style>
        @import url('https://fonts.googleapis.com/css2?family=Noto+Sans+KR:wght@400;500;700&display=swap');
        .post-container { 
            width: 100%; 
            margin: 0 auto; 
            padding: 20px; 
            font-family: 'Noto Sans KR', Arial, sans-serif; 
        }
        .form-label { 
            font-weight: 500; 
            margin-bottom: 5px; 
        }
        .form-select, .form-control { 
            padding: 8px; 
            border: 1px solid #e2e8f0; 
            border-radius: 4px; 
            width: 100%; 
        }
        .post-btn-primary { 
            padding: 8px 16px; 
            background: #3182ce; 
            color: white; 
            border: none; 
            border-radius: 4px; 
            cursor: pointer; 
        }
        .post-btn-primary:hover { 
            background: #2b6cb0; 
        }
        .row { 
            display: flex; 
            flex-wrap: wrap; 
            margin: 0 -15px; 
        }
        .col-md-3, .col-md-6 { 
            padding: 0 15px; 
        }
        .col-md-3 { 
            flex: 0 0 25%; 
            max-width: 25%; 
        }
        .col-md-6 { 
            flex: 0 0 50%; 
            max-width: 50%; 
        }
        .table { 
            font-size: 16px; 
            width: 100%; 
            border-collapse: collapse; 
            margin-top: 10px; 
        }
        .table th, .table td { 
            border: 1px solid #e2e8f0; 
            padding: 12px; 
            vertical-align: middle; 
            word-wrap: break-word; 
        }
        .table th { 
            background: #edf2f7; 
            font-weight: 600; 
        }
        .table th:nth-child(1), .table td:nth-child(1) { width: 10%; } /* 번호 */
        .table th:nth-child(2), .table td:nth-child(2) { width: 40%; } /* 제목 */
        .table th:nth-child(3), .table td:nth-child(3) { width: 20%; } /* 작성자 */
        .table th:nth-child(4), .table td:nth-child(4) { width: 20%; } /* 작성일 */
        .table th:nth-child(5), .table td:nth-child(5) { width: 10%; } /* 조회수 */
        .pagination .page-item.disabled .page-link { 
            pointer-events: none; 
            opacity: 0.6; 
        }
        .pagination .page-item.active .page-link { 
            background-color: #3182ce; 
            border-color: #3182ce; 
            color: white; 
        }
        .pagination .page-link { 
            color: #3182ce; 
        }
        .pagination .page-link:hover { 
            color: #2b6cb0; 
        }
    </style>
</head>
<body>
    <div class="app-main"> 
        <div class="post-container mt-5">
            <h2 class="text-center fs-1">문의게시판</h2>
            <form id="searchForm" class="mb-4" action="/post/getPosts" method="get">
                <div class="row">
                    <div class="col-md-3">
                        <label for="searchType" class="form-label">검색 조건</label>
                        <select name="searchType" id="searchType" class="form-select">
                            <option value="">전체</option>
                            <option value="authorName" <c:if test="${searchDto.searchType == 'authorName'}">selected</c:if>>작성자</option>
                            <option value="postTitle" <c:if test="${searchDto.searchType == 'postTitle'}">selected</c:if>>제목</option>
                            <option value="postContent" <c:if test="${searchDto.searchType == 'postContent'}">selected</c:if>>내용</option>
                            <option value="postTitle,authorName" <c:if test="${searchDto.searchType == 'postTitle,authorName'}">selected</c:if>>제목+작성자</option>
                            <option value="postTitle,postContent" <c:if test="${searchDto.searchType == 'postTitle,postContent'}">selected</c:if>>제목+내용</option>
                            <option value="authorName,postContent" <c:if test="${searchDto.searchType == 'authorName,postContent'}">selected</c:if>>작성자+내용</option>
                            <option value="postTitle,authorName,postContent" <c:if test="${searchDto.searchType == 'postTitle,authorName,postContent'}">selected</c:if>>제목+작성자+내용</option>
                        </select>
                    </div>
                    <div class="col-md-6">
                        <label for="searchKeyword" class="form-label">검색어</label>
                        <input type="text" name="searchKeyword" id="searchKeyword" class="form-control" value="${fn:escapeXml(searchDto.searchKeyword)}" placeholder="검색어를 입력하세요">
                    </div>
                    <div class="col-md-3">
                        <label class="form-label">&nbsp;</label>
                        <button type="submit" id="searchButton" class="post-btn-primary w-100">검색</button>
                    </div>
                </div>
            </form>
            <c:if test="${not empty notices}">
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
                        <c:forEach var="post" items="${notices}">
                            <tr>
                                <td>공지</td>
                                <td>
                                    <a href="getPostDetail?postId=${post.postId}&authorId=${param.authorId}">
                                        [공지] <c:out value="${post.postTitle}"/>
                                    </a>
                                </td>
                                <td><c:out value="${post.userName}"/></td>
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
                                <td><c:out value="${post.postReadCount}"/></td>
                            </tr>
                        </c:forEach>
                    </tbody>
                </table>
            </c:if>
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
                        <c:when test="${not empty posts}">
                            <c:forEach var="post" items="${posts}" varStatus="status">
                                <tr>
                                    <td><c:out value="${pagination.totalRows - (pagination.offset + status.index)}"/></td>
                                    <td>
                                        <c:if test="${post.postGroupLevel > 0}">
                                            <span style="margin-left: ${post.postGroupLevel * 20}px;">↳</span>
                                        </c:if>
                                        <a href="/post/getPostDetail?postId=${post.postId}&authorId=${param.authorId}">
                                            <c:out value="${post.postTitle}"/>
                                        </a>
                                    </td>
                                    <td><c:out value="${post.userName}"/></td>
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
                                    <td><c:out value="${post.postReadCount}"/></td>
                                </tr>
                            </c:forEach>
                        </c:when>
                        <c:otherwise>
                            <tr>
                                <td colspan="5" class="text-center">게시물이 없습니다.</td>
                            </tr>
                        </c:otherwise>
                    </c:choose>
                </tbody>
            </table>
            <div class="text-end">
                <a href="/post/createPost?authorId=${param.authorId}" class="post-btn-primary">글쓰기</a>
            </div>
<c:if test="${pagination != null}">
    <nav>
        <ul class="pagination justify-content-center">
            <li class="page-item <c:if test="${pagination.currentPage <= 1}">disabled</c:if>">
                <a class="page-link" href="<c:url value='/post/getPosts'>
                    <c:param name='pageNum' value='${pagination.currentPage - 1}' />
                    <c:param name='searchType' value='${not empty searchDto.searchType ? searchDto.searchType : ""}' />
                    <c:param name='searchKeyword' value='${not empty searchDto.searchKeyword ? searchDto.searchKeyword : ""}' />
                    <c:param name='authorId' value='${param.authorId}' />
                </c:url>">이전</a>
            </li>
            <c:forEach var="i" begin="${pagination.startPage}" end="${pagination.endPage}">
                <li class="page-item <c:if test="${i == pagination.currentPage}">active</c:if>">
                    <a class="page-link" href="<c:url value='/post/getPosts'>
                        <c:param name='pageNum' value='${i}' />
                        <c:param name='searchType' value='${not empty searchDto.searchType ? searchDto.searchType : ""}' />
                        <c:param name='searchKeyword' value='${not empty searchDto.searchKeyword ? searchDto.searchKeyword : ""}' />
                        <c:param name='authorId' value='${param.authorId}' />
                    </c:url>">${i}</a>
                </li>
            </c:forEach>
            <li class="page-item <c:if test="${pagination.currentPage >= pagination.totalPages}">disabled</c:if>">
                <a class="page-link" href="<c:url value='/post/getPosts'>
                    <c:param name='pageNum' value='${pagination.currentPage + 1}' />
                    <c:param name='searchType' value='${not empty searchDto.searchType ? searchDto.searchType : ""}' />
                    <c:param name='searchKeyword' value='${not empty searchDto.searchKeyword ? searchDto.searchKeyword : ""}' />
                    <c:param name='authorId' value='${param.authorId}' />
                </c:url>">다음</a>
            </li>
        </ul>
    </nav>
</c:if>
        </div>
    </div>
    <script>
        $(document).ready(function() {
            $('#searchKeyword').on('keypress', function(e) {
                if (e.which === 13) {
                    e.preventDefault();
                    $('#searchForm').submit();
                }
            });
        });
    </script>
</body>
</html>