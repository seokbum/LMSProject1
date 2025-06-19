<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="jakarta.tags.core" prefix="c" %>
<%@ taglib uri="jakarta.tags.fmt" prefix="fmt" %>
<%@ taglib uri="jakarta.tags.functions" prefix="fn" %>

<%
    int imageCount = 15;
    int randomImageNumber = (int)(Math.random() * imageCount) + 1;
    pageContext.setAttribute("randomImageNumber", randomImageNumber);

    java.util.Date currentDate = new java.util.Date();
    java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssXXX");
    sdf.setTimeZone(java.util.TimeZone.getTimeZone("Asia/Seoul"));
    String currentDateStr = sdf.format(currentDate);
    pageContext.setAttribute("currentDateStr", currentDateStr);
%>

<!doctype html>
<html lang="ko">
<head>
    <title>메인화면</title>
    <link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/bootstrap-icons@1.11.3/font/bootstrap-icons.min.css">
    <style>
        body::before {
            content: "";
            position: fixed;
            top: 0;
            left: 0;
            width: 100%;
            height: 100%;
            background-image: url("/dist/assets/picture/backSchool2.jpg");
            background-size: cover; /* Changed to cover to fill the entire screen */
            background-position: center; /* Centered the background */
            background-repeat: no-repeat;
            background-attachment: fixed;
            opacity: 0.7; /* Added slight transparency */
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
                                                <c:otherwise>
									                관리자님
									            </c:otherwise>
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
                                            <c:otherwise>
								                관리자
								            </c:otherwise>
                                        </c:choose>
                                    </c:if>
                                </p>
                                <p class="welcome-text"><strong>현재 학기:</strong> <span id="currentSemesterText">정보 없음</span></p>
                                <p class="welcome-text">
                                    <c:if test="${not empty sessionScope.m}">
                                        <c:choose>
                                            <c:when test="${user['class'].simpleName eq 'Student' and not empty user.studentName}">
                                                LDB 학사관리시스템에서 수강신청, 성적확인, 공지사항 등을 편리하게 이용하세요.
                                            </c:when>
                                            <c:when test="${user['class'].simpleName eq 'Professor' and not empty user.professorName}">
                                                LDB 학사관리시스템에서 강의등록, 강의관리, 성적관리 등을 편리하게 이용하세요.
                                            </c:when>
                                            <c:otherwise>
                                                 LDB 학사관리시스템의 원활한 운영을 위해 항상 최선을 다하겠습니다.
                                            </c:otherwise>
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
        const serverCurrentDateStr = "${currentDateStr}";
        const today = new Date(serverCurrentDateStr);
        today.setHours(0, 0, 0, 0);

        function determineCurrentSemesterTypeByMonth(date) {
            const month = date.getMonth() + 1;

            if (month >= 3 && month <= 6) {
                return "1학기";
            } else if (month >= 7 && month <= 8) {
                return "여름방학";
            } else if (month >= 9 && month <= 12) {
                return "2학기";
            } else if (month >= 1 && month <= 2) {
                return "겨울방학";
            }
            return "미분류";
        }

        const currentSemesterType = determineCurrentSemesterTypeByMonth(today);

        $("#seasonInfo").html("현재 LDB대학교는 " + currentSemesterType + " 중입니다.");
        $("#currentSemesterText").html(currentSemesterType);

        $.ajax({
            url: "/api/admin/schedule",
            type: "GET",
            dataType: "json",
            cache: false,
            data: { semesterType: currentSemesterType },
            success: function(response) {
                let timeline = $("#scheduleTimeline");
                timeline.empty();

                if (!response.success || !response.data || !response.data.schedules || response.data.schedules.length === 0) {
                    timeline.append("<li class='timeline-empty'>현재 학기/방학 기간의 학사 일정이 없습니다.</li>");
                    return;
                }

                const schedules = response.data.schedules;

                $.each(schedules, function(index, schedule) {
                    let dateRange = "";
                    if (schedule.scheduleStartDate && schedule.scheduleEndDate) {
                        const start = new Date(schedule.scheduleStartDate);
                        const end = new Date(schedule.scheduleEndDate);
                        const formatOptions = { year: "numeric", month: "2-digit", day: "2-digit" };
                        dateRange = start.toLocaleDateString("ko-KR", formatOptions) + " ~ " + end.toLocaleDateString("ko-KR", formatOptions);
                    } else if (schedule.scheduleStartDate) {
                        const formatOptions = { year: "numeric", month: "2-digit", day: "2-digit" };
                        dateRange = new Date(schedule.scheduleStartDate).toLocaleDateString("ko-KR", formatOptions);
                    } else {
                        dateRange = "날짜 없음";
                    }

                    let semesterInfo = schedule.semesterType ? " <span class='badge badge-info'>" + schedule.semesterType + "</span>" : "";

                    let timelineItem = "<li>" +
                        "<div class='timeline-item'>" +
                        "<span class='time'><i class='bi bi-clock'></i> " + dateRange + "</span>" +
                        "<h3 class='timeline-header'>" + schedule.scheduleTitle + semesterInfo + "</h3>" +
                        "<div class='timeline-body'>" + (schedule.scheduleDescription || "설명 없음") + "</div>" +
                        "</div>" +
                        "</li>";
                    timeline.append(timelineItem);
                });
            },
            error: function(xhr, status, error) {
                console.error("일정 불러오기 오류:", status, error);
                $("#scheduleTimeline").append("<li class='timeline-empty'>학사 일정을 불러오는 데 실패했습니다.</li>");
                $("#seasonInfo").html("학기 정보 불러오기 실패");
                $("#currentSemesterText").html("불러오기 실패");
            }
        });
    });
    </script>
</body>
</html>