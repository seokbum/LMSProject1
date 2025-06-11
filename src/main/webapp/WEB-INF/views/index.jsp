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

    .container-custom {
        margin-top: 50px;
        margin-left: 55px;
    }

    .card-custom {
        padding: 15px;
        min-height: 250px;
        margin-bottom: 50px;
        display: flex;
        flex-direction: column;
        justify-content: center;
        max-width: 400px;
        margin-left: 20px;
    }

    .card-body {
        display: flex;
        flex-direction: column;
        justify-content: center;
        align-items: stretch;
        flex-grow: 1;
    }

    .welcome-text {
        margin-bottom: 15px;
        font-size: 1.1rem;
    }

    .quick-access .btn {
        font-size: 1rem;
        padding: 10px 20px;
        margin: 5px;
    }

    .list-group-item {
        display: flex;
        justify-content: space-between;
        align-items: center;
        padding: 10px 15px;
        margin-bottom: 5px;
    }

    .timeline-item {
        padding: 10px 15px;
        margin-bottom: 15px;
    }
    .timeline-item .time {
        margin-bottom: 5px;
    }
    .timeline-item h3 {
        font-size: 1.1rem;
    }
    .timeline-body {
        font-size: 0.95rem;
    }
    .timeline-empty {
        text-align: center;
        color: #6c757d;
        margin-top: 20px;
    }
    .season-info {
        text-align: center;
        font-weight: bold;
        padding: 10px;
        margin-bottom: 15px;
        border-radius: 5px;
        background-color: #e9ecef;
        color: #343a40;
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
                                <h5>
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
                                    <c:forEach var="notice" items="${recentNotices}">
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
                                <div id="seasonInfo" class="season-info"></div>
                                <ul class="timeline timeline-inverse" id="scheduleTimeline">
                                </ul>
                            </div>
                        </div>
                    </div>
                </div>
            </div>
        </div>
    </div>
    <script src="https://ajax.googleapis.com/ajax/libs/jquery/3.7.1/jquery.min.js"></script>
    <script>
    $(document).ready(function() {
        function displaySeasonInfo() {
            const today = new Date();
            const year = today.getFullYear();
            const month = today.getMonth() + 1;
            const day = today.getDate();

            let seasonMessage = "현재 기간 정보 없음";

            if (month >= 3 && month <= 6) {
                if (month === 3 || month === 4 || month === 5) {
                    seasonMessage = "현재 LDB대학교는 1학기 중입니다.";
                } else if (month === 6 && day <= 20) {
                    seasonMessage = "현재 LDB대학교는 1학기 중입니다.";
                }
            }
            if (month >= 6 && month <= 8) {
                if (month === 6 && day >= 21) {
                    seasonMessage = "현재 LDB대학교는 여름방학 기간 입니다.";
                } else if (month === 7) {
                    seasonMessage = "현재 LDB대학교는 여름방학 기간 입니다.";
                } else if (month === 8 && day <= 28) {
                    seasonMessage = "현재 LDB대학교는 여름방학 기간입니다.";
                }
            }
            if (month >= 8 && month <= 12) {
                if (month === 8 && day >= 29) {
                    seasonMessage = "현재 LDB대학교는 2학기 중입니다.";
                } else if (month === 9 || month === 10 || month === 11) {
                    seasonMessage = "현재 LDB대학교는 2학기 중입니다.";
                } else if (month === 12 && day <= 10) {
                    seasonMessage = "현재 LDB대학교는 2학기 중입니다.";
                }
            }
            if (month === 12 && day >= 11) {
                seasonMessage = "현재 LDB대학교는 겨울방학 기간 입니다.";
            } else if (month === 1 || month === 2) {
                 seasonMessage = "현재 LDB대학교는 겨울방학 기간 입니다.";
            }

            $("#seasonInfo").html(seasonMessage);
        }

        displaySeasonInfo();
        
        $.ajax({
            url: "/api/admin/schedule",
            type: "GET",
            dataType: "json",
            cache: false,
            success: function(response) { 
                let timeline = $("#scheduleTimeline");
                timeline.empty();
                
                if (!response.success || !response.data || !response.data.schedules || response.data.schedules.length === 0) {
                    console.log("스케줄 데이터가 없거나 조회에 실패했습니다. (API 응답 없음)");
                    return;
                }
                
                $.each(response.data.schedules, function(index, schedule) {
                    let formattedDate = schedule.scheduleDateFormatted || "날짜 없음";
                    
                    let timelineItem = '<li>' +
                        '<div class="timeline-item">' +
                            '<span class="time"><i class="bi bi-clock"></i> ' + formattedDate + '</span>' +
                            '<h3 class="timeline-header">' + schedule.scheduleTitle + '</h3>' +
                            '<div class="timeline-body">' + schedule.scheduleDescription + '</div>' +
                        '</div>' +
                    '</li>';
                    timeline.append(timelineItem);
                });
            },
            error: function(xhr, status, error) {
                console.error("스케줄 불러오기 오류:", status, error);
                console.error("응답 상태:", xhr.status);
                console.error("전체 응답:", xhr.responseText);
            }
        });
    });
    </script>
</body>
</html>