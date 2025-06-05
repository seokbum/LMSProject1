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
        .post-container {
            width: 100%; 
            max-width: none; 
            padding: 30px; 
            font-family: 'Noto Sans KR', Arial, sans-serif; 
        }
        .form-group { 
            margin-bottom: 1.5rem; 
        }
        .form-group label { 
            font-weight: 500; color: #495057; 
            margin-bottom: 0.5rem; 
            display: block; 
        }
        .form-control { 
            border: 1px solid #ced4da; 
            border-radius: 0.25rem; 
            padding: 0.75rem 1rem; 
            font-size: 1rem; 
            width: 100%; 
        }
        .post-btn-primary { 
            padding: 8px 16px; 
            background: #dc3545; 
            color: white; 
            border: none; 
            border-radius: 4px; 
            cursor: pointer; 
        }
        .post-btn-primary:hover { 
            background: #c82333; 
        }
        .btn-secondary { 
            padding: 8px 16px; 
            background-color: #6c757d; 
            border-color: #6c757d; 
            color: #fff; 
            border-radius: 4px; 
        }
        .btn-secondary:hover { 
            background-color: #5a6268; 
            border-color: #545b62; 
        }
        .fs-1 { 
            font-size: 2.2rem; 
            color: #343a40; 
            margin-bottom: 1.5rem; 
            font-weight: 500; 
        }
        .ml-2 { 
            margin-left: 0.5rem; 
            color: #6c757d; 
        }
        .mt-4 { 
            margin-top: 2rem; 
        }
        .btn-group { 
            margin-top: 1rem;
        }
        .btn-group > .btn { 
            margin-right: 0.5rem; 
        }
    </style>
</head>
<body>
<div class="post-container mt-5">
    <h2 class="text-center fs-1">게시물 삭제</h2>
    <form action="/api/post/delete" method="post" class="mt-4">
        <input type="hidden" name="postId" value="${post.postId}">
        <div class="form-group">
            <label class="form-label">ID:</label>
            <span class="ml-2">${post.authorId}</span>
        </div>
        <div class="form-group">
            <label class="form-label">작성자:</label>
            <span class="ml-2">${post.userName}</span>
        </div>
        <div class="form-group">
            <label class="form-label">제목:</label>
            <span class="ml-2">${post.postTitle}</span>
        </div>
        <div class="form-group">
            <label for="pass">비밀번호:</label>
            <input type="password" name="pass" id="pass" class="form-control" required>
        </div>
        <div class="btn-group mt-1">
            <button type="submit" class="post-btn-primary">삭제</button>
            <a href="/post/getPostDetail?postId=${post.postId}" class="btn btn-secondary">취소</a>
        </div>
    </form>
</div>
</body>
</html>