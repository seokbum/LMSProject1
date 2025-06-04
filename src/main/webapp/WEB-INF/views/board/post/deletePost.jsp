<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="jakarta.tags.core" prefix="c"%>
<!DOCTYPE html>
<html lang="ko">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>게시물 삭제</title>
    <script src="https://code.jquery.com/jquery-3.7.1.min.js"></script>
    <style>
        @import url("https://fonts.googleapis.com/css2?family=Noto+Sans+KR:wght@400;500;600&display=swap");
        .container { max-width: 600px; margin: 0 auto; padding: 20px; font-family: "Noto Sans KR", sans-serif; }
        .form-label { font-weight: 500; margin-bottom: 5px; font-size: 16px; }
        .form-control { padding: 10px; border: 1px solid #e2e8f0; border-radius: 4px; width: 100%; font-size: 16px; }
        .btn-primary { background-color: #dc3545; color: #fff; padding: 10px 20px; border: none; border-radius: 4px; cursor: pointer; }
        .btn-primary:hover { background-color: #c82333; }
        .btn-secondary { background-color: #6c757d; color: #fff; padding: 10px 20px; border: none; border-radius: 4px; }
        .btn-secondary:hover { background-color: #555; }
    </style>
</head>
<body>
<div class="container mt-5">
    <h2 class="text-center h2">게시물 삭제</h2>
    <form id="deleteForm" action="/post/delete" method="post">
        <input type="hidden" name="postId" value="${post.postId}">
        <div class="mb-3">
            <label for="authorId" class="form-label">ID</label>
            <input type="text" class="form-control" id="authorId" name="authorId" value="S001" readonly>
        </div>
        <div class="mb-3">
            <label for="userName" class="form-label">작성자</label>
            <input type="text" class="form-control" id="userName" value="${post.userName}" readonly>
        </div>
        <div class="mb-3">
            <label for="pass" class="form-label">비밀번호</label>
            <input type="password" class="form-control" id="pass" name="pass" required>
        </div>
        <div class="text-end">
            <button type="submit" class="btn btn-primary">삭제</button>
            <a href="/post/getPostDetail?postId=${post.postId}" class="btn btn-secondary">취소</a>
        </div>
    </form>
</div>
<script>
    $(document).ready(function() {
        $("#deleteForm").submit(function(e) {
            e.preventDefault();
            const formData = {
                "postId": $("input[name=postId]").val(),
                "pass": $("#pass").val(),
                "authorId": $("#authorId").val()
            };
            $.ajax({
                "url": "/post/delete",
                "type": "POST",
                "data": formData,
                "success": (response) => {
                    if (response.redirectUrl) {
                        alert("게시물이 삭제되었습니다.");
                        window.location.href = response.redirectUrl;
                    } else {
                        alert("게시물 삭제 실패: " + response.error);
                    }
                },
                "error": (xhr) => {
                    console.error("Delete post failed:", xhr);
                    alert("게시물 삭제 중 오류");
                }
            });
        });
    });
</script>
</body>
</html>