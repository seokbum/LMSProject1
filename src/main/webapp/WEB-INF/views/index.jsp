<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="jakarta.tags.core" prefix="c" %>
<%@ taglib uri="jakarta.tags.fmt" prefix="fmt" %>
<%@ taglib uri="jakarta.tags.functions" prefix="fn" %>

<%
int imageCount = 15;
int randomImageNumber = (int)(Math.random() * imageCount) + 1;
pageContext.setAttribute("randomImageNumber", randomImageNumber);
%>

<!doctype html>
<html lang="en">
<head>
    <title>메인화면</title>
    <link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/bootstrap-icons@1.11.3/font/bootstrap-icons.min.css">
    <style>
        /* 기본 배경 스타일 */
        body::before {
            content: '';
            position: fixed;
            top: 0;
            left: 0;
            width: 100%;
            height: 100%;
            background-image: url('/dist/assets/picture/backWon${randomImageNumber}.jpg');
            background-size: 510px 900px;
            background-position: 100% 25%;
            background-repeat: no-repeat;
            background-attachment: fixed;
            filter: brightness(0.7);
            z-index: -1;
        }

        /* 컨테이너 위치 */
        .container-custom {
            margin-top: 50px;
            margin-left: 55px;
        }

        /* 카드 스타일 */
        .card-custom {
            padding: 20px; /* 여백 줄임 */
            min-height: 350px; /* 높이 유지 */
            margin-bottom: 50px;
            display: flex;
            flex-direction: column;
            justify-content: center; /* 수직 중앙 */
        }

        /* 카드 바디 중앙 정렬 */
        .card-body {
            display: flex;
            flex-direction: column;
            justify-content: center; /* 수직 중앙 */
            align-items: stretch; /* 수평은 채우기 */
            flex-grow: 1; /* 바디가 카드 높이 채우도록 */
        }

        /* 환영 메시지 텍스트 스타일 */
        .welcome-text {
            margin-bottom: 15px; /* 텍스트 간 간격 */
            font-size: 1.1rem; /* 살짝 큰 폰트 */
        }

        /* 빠른 액세스 버튼 */
        .quick-access .btn {
            font-size: 1rem; /* 버튼 텍스트 크기 */
            padding: 10px 20px; /* 버튼 패딩 */
            margin: 5px; /* 버튼 간 간격 */
        }

        /* 공지사항 리스트 */
        .list-group-item {
            display: flex;
            justify-content: space-between;
            align-items: center; /* 수직 중앙 */
            padding: 10px 15px; /* 패딩 조정 */
            margin-bottom: 5px; /* 항목 간 간격 */
        }

        /* 타임라인 스타일 */
        .timeline-item {
            padding: 10px 15px; /* 패딩 조정 */
            margin-bottom: 15px; /* 항목 간 간격 */
        }
        .timeline-item .time {
            margin-bottom: 5px; /* 날짜와 제목 간격 */
        }
        .timeline-item h3 {
            font-size: 1.1rem; /* 제목 크기 */
        }
        .timeline-body {
            font-size: 0.95rem; /* 본문 크기 */
        }
        /* 타임라인 빈 경우 */
        .timeline-empty {
            text-align: center;
            color: #6c757d; /* 회색 텍스트 */
            margin-top: 20px;
        }
    </style>
</head>
<body class="layout-fixed sidebar-expand-lg bg-body-tertiary">
    <div class="app-wrapper">
        <div class="app-content">
            <div class="container container-custom">
                <div class="row gx-5 gy-4">
                    <div class="col-md-6">
                        <div class="card card-primary card-outline card-custom">
                            <div class="card-header d-flex justify-content-between align-items-center">
                                <h5 class="card-title m-0">
                                    환영합니다,
                                    <c:if test="${not empty sessionScope.m}">
                                        <c:set var="user" value="${sessionScope.m}" />
                                        <strong>
                                            <c:choose>
                                                <c:when test="${user['class'].simpleName eq 'Student' and not empty user.studentName}">
                                                    ${user.studentName}님
                                                </c:when>
                                                <c:when test="${user['class'].simpleName eq 'Professor' and not empty user.professorName}">
                                                    ${user.professorName}님
                                                </c:when>
                                            </c:choose>
                                        </strong>
                                    </c:if>
                                </h5>
                            </div>
                            <div class="card-body">
                                <p class="welcome-text">
                                    <strong>역할:</strong>
                                    <c:if test="${not empty sessionScope.m}">
                                        <c:choose>
                                            <c:when test="${user['class'].simpleName eq 'Student' and not empty user.studentName}">
                                                학생
                                            </c:when>
                                            <c:when test="${user['class'].simpleName eq 'Professor' and not empty user.professorName}">
                                                교수
                                            </c:when>
                                        </c:choose>
                                    </c:if>
                                </p>
                                <p class="welcome-text"><strong>현재 학기:</strong> 2025년 1학기</p>
                                <p class="welcome-text">
                                    <c:if test="${not empty sessionScope.m}">
                                        <c:choose>
                                            <c:when test="${user['class'].simpleName eq 'Student' and not empty user.studentName}">
                                                LDB 학사관리시스템에서 수강신청, 성적확인, 공지사항 등을 편리하게 이용하세요.
                                            </c:when>
                                            <c:when test="${user['class'].simpleName eq 'Professor' and not empty user.professorName}">
                                                LDB 학사관리시스템에서 강의등록, 강의관리, 성적관리 등을 편리하게 이용하세요.
                                            </c:when>
                                        </c:choose>
                                    </c:if>
                                </p>
                            </div>
                        </div>
                    </div>
                    <div class="col-md-6">
                        <div class="card card-info card-outline card-custom">
                            <div class="card-header">
                                <h5 class="card-title">빠른 액세스</h5>
                            </div>
                            <div class="card-body quick-access">
                                <div class="d-flex flex-wrap justify-content-center">
                                    <a href="/learning_support/registerCourse" class="btn btn-primary">수강신청</a>
                                    <a href="/mypage/getCourseScores" class="btn btn-success">성적확인</a>
                                    <a href="/mypage/getCourseTimetable" class="btn btn-warning">시간표조회</a>
                                    <a href="/notice/getNotices" class="btn btn-info">공지사항</a>
                                </div>
                            </div>
                        </div>
                    </div>
                </div>
                <div class="row gx-5 gy-4">
                    <div class="col-md-6">
                        <div class="card card-warning card-outline card-custom">
                            <div class="card-header">
                                <h5 class="card-title">최신 공지사항</h5>
                                <div class="card-tools">
                                    <a href="/notice/getNotices" class="btn btn-tool">전체보기</a>
                                </div>
                            </div>
                            <div class="card-body">
                                <ul class="list-group">
                                    <c:forEach var="notice" items="${recentNotices}" begin="0" end="2">
                                        <li class="list-group-item">
                                            <a href="/notice/getNoticeDetail?noticeId=${notice.noticeId}">${fn:escapeXml(notice.noticeTitle)}</a>
                                            <span class="text-muted">
                                                <fmt:formatDate value="${notice.noticeCreatedAt}" pattern="yyyy-MM-dd" />
                                            </span>
                                        </li>
                                    </c:forEach>
                                </ul>
                            </div>
                        </div>
                    </div>
                    <div class="col-md-6">
                        <div class="card card-success card-outline card-custom">
                            <div class="card-header">
                                <h5 class="card-title">학사 일정</h5>
                            </div>
                            <div class="card-body">
                                <ul class="timeline timeline-inverse" id="scheduleTimeline">
                                </ul>
                            </div>
                        </div>
                    </div>
                </div>
            </div>
        </div>
    </div>
    <script>
    $(document).ready(function() {
        var path = "${pageContext.request.contextPath}";

        const iconClasses = [
            "bi bi-calendar-event",
            "bi bi-bell",
            "bi bi-bookmark",
            "bi bi-check-circle",
            "bi bi-star"
        ];
        
        const bgClasses = [
            "bg-primary",
            "bg-success",
            "bg-info",
            "bg-warning",
            "bg-danger"
        ];
        
        function getRandomIndex(arr) {
            return Math.floor(Math.random() * arr.length);
        }
        
        $.ajax({
            url: path + "/mypage/schedule",
            type: "GET",
            dataType: "json",
            cache: false,
            success: function(data) {
                let timeline = $("#scheduleTimeline");
                timeline.empty();
                
                if (!data.success || !data.schedules || data.schedules.length === 0) {
                    console.log("No schedules found or success is false");
                    timeline.append('<li><div class="timeline-item timeline-empty">스케줄 데이터가 없습니다.</div></li>');
                    return;
                }
                
                $.each(data.schedules, function(index, schedule) {
                    let formattedDate = schedule.scheduleDateFormatted || "날짜 없음";
                    let randomIcon = iconClasses[getRandomIndex(iconClasses)];
                    let randomBg = bgClasses[getRandomIndex(bgClasses)];
                    
                    let timelineItem = '<li>' +
                        '<i class="' + randomIcon + ' ' + randomBg + '"></i>' +
                        '<div class="timeline-item">' +
                            '<span class="time"><i class="bi bi-clock"></i> ' + formattedDate + '</span>' +
                            '<h3 class="timeline-header">' + schedule.scheduleTitle + '</h3>' +
                            '<div class="timeline-body">' + schedule.scheduleDescription + '</div>' +
                        '</div>' +
                    '</li>';
                    timeline.append(timelineItem);
                });
                timeline.append('<li><i class="fas fa-clock bg-gray"></i></li>');
            },
            error: function(xhr, status, error) {
                console.error("Error fetching schedule:", status, error);
                console.error("Response Status:", xhr.status);
                console.error("Full Response:", xhr.responseText);
                $("#scheduleTimeline").html('<li><div class="timeline-item timeline-empty">데이터를 불러오는데 실패했습니다: ' + error + ' (Status: ' + xhr.status + ')</div></li>');
            }
        });
    });
    </script>
</body>
</html>