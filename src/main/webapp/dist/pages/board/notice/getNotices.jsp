<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<!DOCTYPE html>
<html xmlns:th="http://www.thymeleaf.org">
<head>
    <meta charset="UTF-8">
    <title>공지사항</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css" rel="stylesheet">
</head>
<body>
    <div class="container mt-5">
        <h2 class="text-center fs-1">공지사항</h2>
        <div th:if="${msg != null}" class="alert alert-danger" th:text="${msg}"></div>
        <!-- 검색 폼 -->
        <form th:action="@{/notice/list}" method="get" class="mb-4">
            <div class="row">
                <div class="col-md-3">
                    <select name="searchType" class="form-select">
                        <option value="">전체</option>
                        <option th:value="'userName'" th:selected="${searchDto.searchType == 'userName'}">작성자</option>
                        <option th:value="'noticeTitle'" th:selected="${searchDto.searchType == 'noticeTitle'}">제목</option>
                        <option th:value="'noticeContent'" th:selected="${searchDto.searchType == 'noticeContent'}">내용</option>
                        <option th:value="'noticeTitle,userName'" th:selected="${searchDto.searchType == 'noticeTitle,userName'}">제목+작성자</option>
                        <option th:value="'noticeTitle,noticeContent'" th:selected="${searchDto.searchType == 'noticeTitle,noticeContent'}">제목+내용</option>
                        <option th:value="'userName,noticeContent'" th:selected="${searchDto.searchType == 'userName,noticeContent'}">작성자+내용</option>
                        <option th:value="'noticeTitle,userName,noticeContent'" th:selected="${searchDto.searchType == 'noticeTitle,userName,noticeContent'}">제목+작성자+내용</option>
                    </select>
                </div>
                <div class="col-md-6">
                    <input type="text" name="searchKeyword" class="form-control" th:value="${searchDto.searchKeyword}" placeholder="검색어를 입력하세요">
                </div>
                <div class="col-md-3">
                    <button type="submit" class="btn btn-primary w-100">검색</button>
                </div>
            </div>
        </form>
        <!-- 공지사항 목록 -->
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
                <tr th:each="notice, status : ${notices}">
                    <td th:text="${pagination.totalRows - (pagination.offset + status.index)}"></td>
                    <td>
                        <a th:href="@{/notice/view(noticeId=${notice.noticeId})}" th:text="${notice.noticeTitle}"></a>
                    </td>
                    <td th:text="${notice.userName}"></td>
                    <td>
                        <span th:if="${#dates.format(#dates.createToday(), 'yyyy-MM-dd') == #dates.format(notice.noticeCreatedAt, 'yyyy-MM-dd')}"
                              th:text="${#dates.format(notice.noticeCreatedAt, 'HH:mm')}"></span>
                        <span th:unless="${#dates.format(#dates.createToday(), 'yyyy-MM-dd') == #dates.format(notice.noticeCreatedAt, 'yyyy-MM-dd')}"
                              th:text="${#dates.format(notice.noticeCreatedAt, 'yyyy-MM-dd')}"></span>
                    </td>
                    <td th:text="${notice.noticeReadCount}"></td>
                </tr>
                <tr th:if="${#lists.isEmpty(notices)}">
                    <td colspan="5" class="text-center">공지사항이 없습니다.</td>
                </tr>
            </tbody>
        </table>
        <div class="text-end">
            <a th:if="${#authentication.principal != null and #authentication.authorities.contains('ROLE_PROFESSOR')}"
               th:href="@{/notice/add}" class="btn btn-primary">글쓰기</a>
        </div>
        <nav>
            <ul class="pagination justify-content-center">
                <li th:classappend="${pagination.currentPage <= 1} ? 'disabled'" class="page-item">
                    <a class="page-link" th:href="@{/notice/list(pageNum=${pagination.currentPage - 1}, searchType=${searchDto?.searchType}, searchKeyword=${searchDto?.searchKeyword})}">이전</a>
                </li>
                <li th:each="i : ${#numbers.sequence(pagination.startPage, pagination.endPage)}"
                    th:classappend="${i == pagination.currentPage} ? 'active'" class="page-item">
                    <a class="page-link" th:href="@{/notice/list(pageNum=${i}, searchType=${searchDto?.searchType}, searchKeyword=${searchDto?.searchKeyword})}" th:text="${i}"></a>
                </li>
                <li th:classappend="${pagination.currentPage >= pagination.totalPages} ? 'disabled'" class="page-item">
                    <a class="page-link" th:href="@{/notice/list(pageNum=${pagination.currentPage + 1}, searchType=${searchDto?.searchType}, searchKeyword=${searchDto?.searchKeyword})}">다음</a>
                </li>
            </ul>
        </nav>
    </div>
</body>
</html>