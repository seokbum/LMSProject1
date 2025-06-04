<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>

<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<c:set var="path" value="${pageContext.request.contextPath}"
	scope="application" />

<!DOCTYPE html>
<html lang="ko">
<head>

<meta charset="UTF-8">
<meta name="viewport" content="width=device-width, initial-scale=1.0">
<title>Find Password</title>
<link
	href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css"
	rel="stylesheet">
<style>
body {
	font-family: 'Noto Sans KR', sans-serif;
	background-color: #f8f9fa;
	display: flex;
	justify-content: center;
	align-items: center;
	height: 100vh;
	margin: 0;
}

.card {
	border: none;
	box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
	width: 100%;
	max-width: 400px;
	padding: 20px;
}

.btn-custom {
	background-color: #007bff;
	color: #ffffff;
	border: none;
}

.btn-custom:hover {
	background-color: #0056b3;
}

.btn-link-custom {
	color: #007bff;
	text-decoration: none;
}

.btn-link-custom:hover {
	text-decoration: underline;
}
 /* Button container for alignment */
    .button-group {
        display: flex;
        justify-content: center;
        gap: 15px;
        flex-wrap: wrap;
        margin: 20px 0;
    }

    /* General button styling */
    .custom-btn {
        padding: 12px 24px;
        font-size: 20px;
        font-weight: 500;
        border: none;
        border-radius: 8px;
        cursor: pointer;
        transition: all 0.3s ease;
        box-shadow: 0 2px 5px rgba(0, 0, 0, 0.2);
    }
/* Info button (비밀번호변경하기) */
    .custom-btn-info {
        background-color: #17a2b8;
        color: white;
    }
    /* Secondary button (로그인화면으로) */
    .custom-btn-secondary {
        background-color: #6c757d;
        color: white;
    }
     /* Styling for the error/success message */
    .message-text {
        font-size: 24px;
        font-weight: 600;
        color: red; /* Blue for default message */
        margin-bottom: 20px;
        padding: 10px;
        border-radius: 5px;
        background-color: #e7f1ff;
        transition: all 0.3s ease;
    }
    /* Success message specific style */
    .message-text.success {
        color: #28a745; /* Green for success */
        background-color: #d4edda;
    }
</style>
</head>
<body>
	<div class="card">
	<c:if test="${msg==null}">
	<h4 class="text-center mb-4">비밀번호 찾기</h4>
		<form action="findPwProcess" name="f" method="post">
			<div class="mb-3">
				<label for="id" class="form-label">아이디</label> <input type="text"
					class="form-control" id="id" name="id" placeholder="아이디 입력" onkeyup="delMsg()">
			</div>
			<div class="mb-3">
				<label for="email" class="form-label">이메일</label> <input
					type="email" class="form-control" id="email" name="email"
					placeholder="이메일 입력" onkeyup="delMsg()">
			</div>
			<button class="btn btn-custom w-100 mb-3">비밀번호 찾기</button>
			<div class="text-center">
			
				<a href="javascript:c()" class="btn-link-custom">로그인 화면으로 돌아가기</a>
			</div>
		</form>
	</c:if>
		
<c:if test="${msg!=null}">
    <div class="message-container">
        <div class="mb-3">
            <h2 class="message-text ${msg=='success' ? 'success' : ''}" id="errorMsg">${msg}</h2>
        </div>
        <div class="button-group">
            <button class="custom-btn custom-btn-secondary" onclick="c()">로그인화면으로</button>
            <c:if test="${msg=='success'}">
                <form action="updatePw" method="post">
                    <input type="hidden" name="id" value="${id}">
                    <input type="hidden" name="email" value="${email}">
                    <button type="submit" class="custom-btn custom-btn-info">비밀번호변경하기</button>
                </form>
            </c:if>
        </div>
    </div>
    <br><br>
</c:if>
	</div>


	<script
		src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/js/bootstrap.bundle.min.js"></script>
	<script type="text/javascript">
    	function c(){
                window.close();      
    	}
    	function delMsg(){
			const msg = document.querySelector("#errorMsg");
			msg.style.display = 'none';
    	}
    	
    	

		
    </script>
</body>
</html>