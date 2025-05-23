<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>Insert title here</title>
</head>
<body>
<div class="container mt-3">
    <div class="alert alert-info">
        <p><strong>Debug:</strong> EL (1 + 1) = ${1 + 1}</p>
        <p><strong>Debug:</strong> `pageContext.request.contextPath` = <%= request.getContextPath() %></p>
        <p><strong>Debug:</strong> `path` 변수 (c:set) = ${path}</p>
        <p><strong>Debug:</strong> `pathFromController` (모델) = ${pathFromController}</p>
        <p><strong>Debug:</strong> `testResult` (모델) = ${testResult}</p>
        <p><strong>Debug:</strong> 현재 접속 URL = ${pageContext.request.requestURL}</p>
    </div>
</div>

<div class="hero-section text-white text-center py-5">
    <div class="container">
        <h1 class="display-4 mb-3">LDB학사관리시스템에 오신 것을 환영합니다!</h1>
        <p class="lead mb-4">학생과 교직원 여러분의 편리한 학사 업무를 지원합니다.</p>
        <a href="${path}/mypage/userInfo" class="btn btn-primary btn-lg me-2">개인정보 확인</a>
        <a href="${path}/notice/getNotices" class="btn btn-outline-light btn-lg">공지사항 보기</a>
    </div>
</div>

<div class="container my-5">
    <h2 class="text-center mb-4">주요 기능</h2>
    <div class="row row-cols-1 row-cols-md-3 g-4">
        <div class="col">
            <div class="card h-100 shadow-sm feature-card">
                <div class="card-body text-center">
                    <i class="bi bi-person-circle fs-1 text-primary mb-3"></i>
                    <h5 class="card-title">마이페이지</h5>
                    <p class="card-text">개인정보, 성적, 시간표 등을 한눈에 확인하세요.</p>
                    <a href="${path}/mypage/index" class="btn btn-sm btn-outline-secondary">자세히 보기</a>
                </div>
            </div>
        </div>
        <div class="col">
            <div class="card h-100 shadow-sm feature-card">
                <div class="card-body text-center">
                    <i class="bi bi-book fs-1 text-success mb-3"></i>
                    <h5 class="card-title">수강 및 강의 관리</h5>
                    <p class="card-text">수강 신청, 강의 현황 확인, 강의 등록 및 관리.</p>
                    <a href="${path}/learning_support/registerCourse" class="btn btn-sm btn-outline-secondary">이동하기</a>
                </div>
            </div>
        </div>
        <div class="col">
            <div class="card h-100 shadow-sm feature-card">
                <div class="card-body text-center">
                    <i class="bi bi-chat-dots fs-1 text-info mb-3"></i>
                    <h5 class="card-title">소통 공간</h5>
                    <p class="card-text">공지사항 및 문의 게시판을 통해 소통하세요.</p>
                    <a href="${path}/post/getPosts" class="btn btn-sm btn-outline-secondary">이동하기</a>
                </div>
            </div>
        </div>
    </div>
</div>
</body>
</html>