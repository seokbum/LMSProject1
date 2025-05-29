<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib uri="jakarta.tags.core" prefix="c"%>
<%@ taglib uri="jakarta.tags.fmt" prefix="fmt"%>           
<%@ taglib uri="jakarta.tags.functions" prefix="fn"%> 

<head>
    <title>공지사항 목록</title> 
    <style>
        @import url('https://fonts.googleapis.com/css2?family=Noto+Sans+KR:wght@400;500;700&display=swap');
        .notice-container {
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
        .notice-btn-primary {
            padding: 8px 16px;
            background: #3182ce;
            color: white;
            border: none;
            border-radius: 4px;
            cursor: pointer;
        }
        .notice-btn-primary:hover {
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
        /* 테이블 컬럼 너비 조정 (원하는 비율로 조정) */
        .table th:nth-child(1), .table td:nth-child(1) { width: 10%; } /* 번호 */
        .table th:nth-child(2), .table td:nth-child(2) { width: 40%; } /* 제목 */
        .table th:nth-child(3), .table td:nth-child(3) { width: 20%; } /* 작성자 */
        .table th:nth-child(4), .table td:nth-child(4) { width: 20%; } /* 작성일 */
        .table th:nth-child(5), .table td:nth-child(5) { width: 10%; } /* 조회수 */

        /* 부트스트랩 클래스 사용 시 충돌 방지를 위한 추가 스타일 */
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
        <div class="notice-container mt-5">
            <h2 class="text-center fs-1">공지사항</h2>

            <form id="searchForm" class="mb-4" action="${pageContext.request.contextPath}/notice/getNotices" method="get">
                <div class="row">
                    <div class="col-md-3">
                        <label for="searchType" class="form-label">검색 조건</label>
                        <select name="searchType" id="searchType" class="form-select">
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
                        <label for="searchKeyword" class="form-label">검색어</label>
                        <input type="text" name="searchKeyword" id="searchKeyword" class="form-control" value="${searchDto.searchKeyword}" placeholder="검색어를 입력하세요">
                    </div>
                    <div class="col-md-3">
                        <label class="form-label"> </label>
                        <button type="submit" id="searchButton" class="notice-btn-primary w-100">검색</button>
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
                                        <a href="${pageContext.request.contextPath}/notice/getNoticeDetail?noticeId=${notice.noticeId}"><c:out value="${notice.noticeTitle}"/></a>
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
                <a href="${pageContext.request.contextPath}/notice/createNotice" class="notice-btn-primary">글쓰기</a>
            </div>

            <nav>
                <ul class="pagination justify-content-center">
                    <li class="page-item <c:if test="${pagination.currentPage <= 1}">disabled</c:if>">
                        <a class="page-link" href="${pageContext.request.contextPath}/notice/getNotices?pageNum=${pagination.currentPage - 1}&searchType=${searchDto.searchType}&searchKeyword=${searchDto.searchKeyword}">이전</a>
                    </li>
                    <c:forEach var="i" begin="${pagination.startPage}" end="${pagination.endPage}">
                        <li class="page-item <c:if test="${i == pagination.currentPage}">active</c:if>">
                            <a class="page-link" href="${pageContext.request.contextPath}/notice/getNotices?pageNum=${i}&searchType=${searchDto.searchType}&searchKeyword=${searchDto.searchKeyword}">${i}</a>
                        </li>
                    </c:forEach>
                    <li class="page-item <c:if test="${pagination.currentPage >= pagination.totalPages}">disabled</c:if>">
                        <a class="page-link" href="${pageContext.request.contextPath}/notice/getNotices?pageNum=${pagination.currentPage + 1}&searchType=${searchDto.searchType}&searchKeyword=${searchDto.searchKeyword}">다음</a>
                    </li>
                </ul>
            </nav>
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