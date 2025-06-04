<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="jakarta.tags.core" prefix="c"%>
<%@ taglib uri="jakarta.tags.fmt" prefix="fmt"%>
<%@ taglib uri="jakarta.tags.functions" prefix="fn"%>
<!DOCTYPE html>
<html lang="ko">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>게시물 삭제</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css" rel="stylesheet">
    <style>
        @import url('https://fonts.googleapis.com/css2?family=Noto+Sans+KR:wght@400;500;700&display=swap');
        body { background-color: #f8f9fa; }
        .notice-container { width: 100%; max-width: none; padding: 30px; font-family: 'Noto Sans KR', sans-serif; }
        .form-group { margin-bottom: 1.5rem; }
        .form-group label { font-weight: bold; color: #495057; margin-bottom: 0.5rem; display: block; }
        .form-control { border: 1px solid #ced4da; border-radius: 0.25rem; padding: 0.75rem 1rem; font-size: 1rem; width: 100%; }
        .btn { border-radius: 0.25rem; padding: 0.75rem 1.25rem; font-size: 1rem; font-weight: 500; }
        .btn-danger { background-color: #dc3545; border-color: #dc3545; color: #fff; }
        .btn-danger:hover { background-color: #c82333; border-color: #bd2130; }
        .btn-secondary { background-color: #6c757d; border-color: #6c757d; color: #fff; }
        .btn-secondary:hover { background-color: #5a6268; border-color: #545b62; }
        .fs-1 { font-size: 2.2rem; color: #343a40; margin-bottom: 1.5rem; font-weight: bold; }
        .ml-2 { margin-left: 0.5rem; color: #6c757d; }
        .mt-4 { margin-top: 2rem; }
        .btn-group { margin-top: 1rem; }
        .btn-group > .btn { margin-right: 0.5rem; }
    </style>
</head>
<body>
<div class="notice-container mt-5">
    <h2 class="text-center fs-1">게시물 삭제</h2>
    <form action="/post/delete" method="post" class="mt-4">
        <input type="hidden" name="postId" value="${post.postId}">
        <div class="form-group">
            <label class="font-weight-bold">작성자:</label>
            <span class="ml-2">${post.userName}</span>
        </div>
        <div class="form-group">
            <label class="font-weight-bold">제목:</label>
            <span class="ml-2">${post.postTitle}</span>
        </div>
        <div class="form-group">
            <label for="pass">비밀번호:</label>
            <input type="password" name="pass" id="pass" class="form-control" required>
        </div>
        <div class="btn-group mt-1">
            <button type="submit" class="btn btn-danger">삭제</button>
            <a href="/post/getPostDetail?postId=${post.postId}" class="btn btn-secondary">취소</a>
        </div>
    </form>
</div>
</body>
</html>