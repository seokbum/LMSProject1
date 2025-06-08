<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<%@taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>

<!DOCTYPE html>
<html lang="ko">
<head>
<meta charset="UTF-8">
<title>강의등록(교수지원)</title>
<style>
    body {
        font-family: 'Noto Sans KR', sans-serif;
        background-color: #f8f9fa;
    }
    .content {
        padding: 50px;
    }
    .card {
        border: none;
        box-shadow: 0 0.125rem 0.25rem rgba(0, 0, 0, 0.075);
        margin-bottom: 20px;
    }
    .btn-custom {
        background-color: #dc3545;
        color: #ffffff;
        border: none;
    }
    .btn-custom:hover {
        background-color: #c82333;
    }
    .table th {
        background-color: #f1f3f5;
    }
    .form-label-custom {
        font-weight: bold;
        margin-bottom: 0.3rem;
    }
    .input-group-text-custom {
        background-color: #e9ecef;
        border: 1px solid #ced4da;
    }
    .form-select-sm, .form-control-sm {
        padding: 0.375rem 0.75rem;
        font-size: 0.875rem;
        border-radius: 0.2rem;
        flex: 1;
        min-width: 0;
    }
    #courseTime .time-input-group {
        display: flex;
        align-items: center;
        flex-wrap: wrap;
    }
    #courseTime .time-input-group > * {
        margin-right: 0.5rem;
    }
    #courseTime .time-input-group > select {
        flex-basis: 30%;
    }
    #courseTime .time-input-group > .input-group {
        flex-basis: 30%;
    }
    #courseTime .time-input-group > .mx-2 {
        flex-basis: auto;
    }
    #courseTime .time-input-group > *:last-child {
        margin-right: 0;
    }
    .input-group-sm > .form-control,
    .input-group-sm > .form-select,
    .input-group-sm > .input-group-text {
        height: calc(1.5em + 0.5rem + 2px);
        padding: 0.25rem 0.5rem;
        font-size: 0.875rem;
        border-radius: 0.2rem;
    }
    .time-instruction {
        font-size: 0.875rem;
        color: #6c757d;
    }
    .mb-3.row .col-sm-10 .time-instruction.error { 
        color: red !important;
    }
    
    .error-message {
        color: #dc3545;
        font-size: 0.875rem;
        margin-top: 0.25rem;
    }
    #errorMsg {
        display: none;
        font-size: 1.25rem;
        color: #dc3545;
        font-weight: bold;
        text-align: center;
        padding: 10px 20px;
        background-color: #fff3f3;
        border-radius: 5px;
        box-shadow: 0 0.125rem 0.25rem rgba(0, 0, 0, 0.1);
        z-index: 1000;
    }
    #errorMsg.error {
        display: block;
    }
    #errorMsg i {
        margin-right: 8px;
        vertical-align: middle;
        color: #dc3545;
    }
    @media (max-width: 576px) {
        #courseTime .time-input-group {
            flex-direction: column;
            align-items: stretch;
        }
        #courseTime .time-input-group > * {
            margin-right: 0;
            margin-bottom: 0.5rem;
        }
        #errorMsg {
            font-size: 1rem;
            padding: 8px 15px;
        }
    }
</style>
</head>
<body>
    <form:form action="/professors/courses/register" method="post" 
     modelAttribute="registCourseDto" id="registCourseForm">
        <div class="content">
            <div class="d-flex justify-content-between align-items-center mb-4 position-relative">
		        <h2><i class="bi bi-pencil-square me-2"></i> 강의 등록</h2>
		        <c:if test="${not empty errorMsg}">
		        <h2 id="errorMsg" class="${not empty errorMsg ? 'error' : ''}">
                <i class="bi bi-exclamation-triangle-fill"></i>
                <spring:message code="${errorMsg}" />
           		</h2>
           		</c:if>
    		</div>
            <div class="card p-4">
                <div class="mb-3 row">
                    <label for="deptId" class="col-sm-2 col-form-label-custom">전공명</label>
                    <div class="col-sm-10">
                        <form:select path="deptId" class="form-select form-select-sm" id="deptId" >
                            <form:option value="" label="전공을 선택하세요"/>
                            <form:options items="${departments}" itemValue="deptId" itemLabel="deptName"/>
                        </form:select>
                        <form:errors path="deptId" cssClass="error-message"/>
                    </div>
                </div>
                <div class="mb-3 row">
                    <label for="courseName" class="col-sm-2 col-form-label-custom">강의명</label>
                    <div class="col-sm-10">
                        <form:input path="courseName" class="form-control form-control-sm" 
                               id="courseName" placeholder="하나도넛 주문 방법(예)" />
                        <form:errors path="courseName" cssClass="error-message" />
                    </div>
                </div>
                <div class="mb-3 row">
                    <label for="professorName" class="col-sm-2 col-form-label-custom">교수명</label>
                    <div class="col-sm-10">
                        <input name="professorName" class="form-control form-control-sm" 
                               id="professorName" value="${professorName}" disabled>
                    </div>
                </div>
                <div class="mb-3 row">
	                    <label for="courseEmail" class="col-sm-2 col-form-label-custom">이메일</label>
	                    <div class="col-sm-10">
	                        <form:input path="courseEmail" class="form-control form-control-sm" 
	                               id="courseEmail" placeholder="john.doe@univ.edu(예)" />
                            <form:errors path="courseEmail" cssClass="error-message" />
	                    </div>
               	</div>
                <div class="mb-3 row">
                    <label class="col-sm-2 col-form-label-custom">이수구분</label>
                    <div class="col-sm-10">
                        <div class="form-check form-check-inline">
                            <form:radiobutton path="creditCategory" id="majorRequired" 
                            	value="MajorRequired" />
                            <label class="form-check-label" for="majorRequired">전공필수</label>
                        </div>
                        <div class="form-check form-check-inline">
                            <form:radiobutton path="creditCategory" id="MajorElective" value="MajorElective" />
                            <label class="form-check-label" for="MajorElective">전공선택</label>
                        </div>
                        <div class="form-check form-check-inline">
                            <form:radiobutton path="creditCategory" value="LiberalArts" id="liberalArts"/>
                            <label class="form-check-label" for="liberalArts">교양</label>
                        </div>
                        <form:errors path="creditCategory" cssClass="error-message" />
                    </div>
                </div>
                
                <div class="mb-3 row">
                    <label for="coursePeriod" class="col-sm-2 col-form-label-custom">강의기간</label>
                    <div class="col-sm-10">
                        <form:select path="coursePeriod" class="form-select form-select-sm" 
                        	id="coursePeriod">
                            <form:option value="1학기" label="1학기"/>
                            <form:option value="2학기" label="2학기"/>
                        </form:select>
                        <form:errors path="coursePeriod" cssClass="error-message"/>
                    </div>
                </div>
                
                
                <div class="mb-3 row align-items-center" id="courseTime">
                    <label for="courseTimeYoil" class="col-sm-2 col-form-label-custom">강의시간</label>
                    <div class="col-sm-10">
                        <div class="d-flex align-items-center time-input-group">
                            <form:select path="courseTimeYoil" class="form-select form-select-sm" 
                            	id="courseTimeYoil" >
                                <form:option value="" label="요일 선택"/>
                                <form:option value="월" label="월요일" />
                                <form:option value="화" label="화요일" />
                                <form:option value="수" label="수요일" />
                                <form:option value="목" label="목요일" />
                                <form:option value="금" label="금요일" />
                            </form:select>
                            <div class="input-group input-group-sm">
                                <input type="number" class="form-control form-control-sm start-time-hour" 
                                       id="startTimeHour" name="startTimeHour" value="${fn:substring(registCourseDto.courseTimeStart, 0, 2)}" 
                                       min="9" max="17" step="1" placeholder="시작시간 입력">
                                <div class="input-group-text">:00</div>
                            </div>
                            <span class="mx-2">~</span>
                            <div class="input-group input-group-sm">
                                <input type="number" class="form-control form-control-sm end-time-hour" 
                                       id="endTimeHour" name="endTimeHour" value="${fn:substring(registCourseDto.courseTimeEnd, 0, 2)}" 
                                       min="9" max="17" step="1" placeholder="종료시간 입력">
                                <div class="input-group-text">:50</div>
                            </div>
                        </div>
                        <form:hidden path="courseTimeStart" id="startTime" />
                        <form:hidden path="courseTimeEnd" id="endTime"/>
                        <form:errors path="courseTimeStart" cssClass="error-message"/><br>
                    	<form:errors path="courseTimeEnd" cssClass="error-message"/>
                        
                    </div>
                </div>
                <div class="mb-3 row">
                    <label for="courseMaxCnt" class="col-sm-2 col-form-label-custom">정원수</label>
                    <div class="col-sm-10">
                        <form:input path="courseMaxCnt" type="number" class="form-control form-control-sm" 
                               id="courseMaxCnt" placeholder="최대정원 입력" />
                        <form:errors path="courseMaxCnt" cssClass="error-message" />
                    </div>
                </div>
                <div class="mb-3 row">
                    <label for="courseLoc" class="col-sm-2 col-form-label-custom">강의실</label>
                    <div class="col-sm-10">
                        <form:input path="courseTimeLoc" class="form-control form-control-sm" 
                               id="courseLoc" placeholder="강의실 입력(예:A302)" />
                        <form:errors path="courseTimeLoc" cssClass="error-message"/>
                    </div>
                </div>
                <div class="mb-3 row">
                    <label for="score" class="col-sm-2 col-form-label-custom">학점</label>
                    <div class="col-sm-10">
                        <form:input path="courseScore" type="number" class="form-control form-control-sm" 
                        id="score" min="1" max="6" />
                        <small class="form-text text-muted">1점에서 6점 사이의 학점을 입력하세요.</small>
                        <form:errors path="courseScore" cssClass="error-message"/>
                    </div>
                </div>
            </div>

            <div class="card p-4">
                <h5 class="mb-3">강의 설명</h5>
                <div class="mb-3">
                    <form:textarea path="coursePlan" class="form-control form-control-sm" rows="5" />
                    <small class="form-text text-muted">강의 내용을 설명해주세요.</small>
                </div>
                <div class="d-flex justify-content-end">
                    <button type="button" class="btn btn-secondary btn-sm me-2" id="btnReset">리셋 <i class="bi bi-arrow-counterclockwise"></i></button>
                    <button type="submit" class="btn btn-custom btn-sm" id="btnSubmit">등록 <i class="bi bi-check-circle"></i></button>
                </div>
            </div>
            
        </div>
    </form:form>

<script>
    $(document).ready(function() {
        if ("${message}") {
			alert("${message}");
        }
    	
    	$('#registCourseForm').on('submit', function(event) {
			var sTime = $("#startTimeHour").val();
			var eTime = $("#endTimeHour").val();
			
			$("#startTime").val(sTime + ":00");
			$("#endTime").val(eTime + ":50");
    	});

        $('#btnReset').on('click', function() {
            $('#registCourseForm')[0].reset();
            $('.error-message').remove();
        });
    });
</script>
</body>
</html>