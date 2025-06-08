<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="jakarta.tags.core" prefix="c"%>
<%@ taglib uri="jakarta.tags.fmt" prefix="fmt"%>
<%@ taglib uri="jakarta.tags.functions" prefix="fn"%>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>게시물 삭제</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css" rel="stylesheet">
    <style>
        .board-container {
            margin: 30px 0;
            padding: 20px;
            width: 100%;
            font-family: 'Noto Sans KR', sans-serif;
        }
        .form-label {
            font-weight: bold;
        }
        .btn-custom {
            padding: 0.75rem 1.25rem;
            font-size: 1rem;
            font-weight: 500;
        }
    </style>
</head>
<body>
<div class="board-container">
    <h1 class="fs-1">게시물 삭제</h1>
    <form id="deleteForm" action="/post/delete" method="post">
        <input type="hidden" name="postId" value="${post.postId}">
        <div class="mb-3">
            <label for="postPassword" class="form-label">비밀번호</label>
            <input type="password" class="form-control" id="postPassword" name="postPassword" required>
        </div>
        <div class="text-end">
            <button type="submit" class="btn btn-danger btn-custom">삭제</button>
            <a href="/post/getPostDetail?postId=${post.postId}" class="btn btn-secondary btn-custom">취소</a>
        </div>
    </form>
</div>

<script>
$(document).ready(function() {
    $("#deleteForm").submit(function(e) {
        e.preventDefault();
        const formData = $(this).serialize();
        $.ajax({
            url: "/api/post/delete",
            type: "POST",
            data: formData,
            contentType: "application/x-www-form-urlencoded",
            success: (response) => { window.location.href = response.redirectUrl; },
            error: (xhr) => { alert("삭제 중 오류: " + xhr.responseJSON?.error); }
        });
    });
});
</script>
</body>
</html>