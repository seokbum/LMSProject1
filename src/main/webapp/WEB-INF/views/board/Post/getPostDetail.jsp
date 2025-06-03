<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="jakarta.tags.core" prefix="c"%>
<!DOCTYPE html>
<html lang="ko">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>게시물 상세</title>
    <link rel="stylesheet" href="https://uicdn.toast.com/editor/latest/toastui-editor-viewer.min.css" />
    <script src="https://uicdn.toast.com/editor/latest/toastui-editor-viewer.min.js"></script>
    <script src="https://code.jquery.com/jquery-3.7.1.min.js"></script>
    <style>
        @import url("https://fonts.googleapis.com/css2?family=Noto+Sans+KR:wght@400;500;600&display=swap");
        .container { max-width: 1000px; margin: 0 auto; padding: 20px; font-family: "Noto Sans KR", sans-serif; }
        .post-title { font-size: 24px; font-weight: 600; margin-bottom: 10px; }
        .post-meta { font-size: 14px; color: #6c757d; margin-bottom: 20px; }
        .post-content { border: 1px solid #e2e8f0; padding: 20px; border-radius: 4px; }
        .btn-primary { background-color: #007bff; color: #fff; padding: 10px 20px; border: none; border-radius: 4px; cursor: pointer; }
        .btn-primary:hover { background-color: #0056b3; }
        .btn-secondary { background-color: #6c757d; color: #fff; padding: 10px 20px; border: none; border-radius: 4px; }
        .btn-secondary:hover { background-color: #555; }
        .comment-form { margin-top: 20px; }
        .comment-list { margin-top: 20px; }
        .comment-item { border-bottom: 1px solid #e2e8f0; padding: 10px 0; }
    </style>
</head>
<body>
<div class="container mt-5">
    <h2 class="post-title">${post.postTitle}</h2>
    <div class="post-meta">
        작성자: ${post.userName} | 작성일: ${post.createdAt} | 조회수: ${post.readCount}
    </div>
    <div class="post-content">
        <div id="viewer"></div>
    </div>
    <c:if test="${not empty post.postFilePath}">
        <p>첨부 파일: <a href="/post/download?filePath=${post.postFilePath}">${post.postFilePath.substring(post.postFilePath.lastIndexOf('_') + 1)}</a></p>
    </c:if>
    <div class="text-end mt-3">
        <c:if test="${isLoggedIn && post.authorId eq authorId}">
            <a href="/post/updatePost?postId=${post.postId}" class="btn btn-primary">수정</a>
            <a href="/post/deletePost?postId=${post.postId}" class="btn btn-primary">삭제</a>
        </c:if>
        <a href="/post/getPosts" class="btn btn-secondary">목록</a>
        <c:if test="${post.postNotice != 1}">
            <a href="/post/replyPost?postId=${post.postId}" class="btn btn-primary">답글</a>
        </c:if>
    </div>
    <div class="comment-form">
        <h4>댓글 작성</h4>
        <form id="commentForm">
            <input type="hidden" name="postId" value="${post.postId}">
            <div class="mb-3">
                <label for="commentAuthorId" class="form-label">ID</label>
                <input type="text" class="form-control" id="commentAuthorId" name="authorId" value="S001" readonly>
            </div>
            <div class="mb-3">
                <label for="commentContent" class="form-label">댓글 내용</label>
                <textarea class="form-control" id="commentContent" name="commentContent" required></textarea>
            </div>
            <div class="text-end">
                <button type="submit" class="btn btn-primary">댓글 등록</button>
            </div>
        </form>
    </div>
    <div class="comment-list">
        <h4>댓글 목록</h4>
        <c:forEach var="comment" items="${commentList}">
            <div class="comment-item">
                <p><strong>${comment.userName}</strong> (${comment.createdAt})</p>
                <p>${comment.commentContent}</p>
            </div>
        </c:forEach>
    </div>
</div>
<script>
    $(document).ready(function() {
        const viewer = new toastui.Editor.factory({
            el: document.querySelector("#viewer"),
            "viewer": true,
            "initialValue": "${post.postContent}"
        });

        $("#commentForm").submit(function(e) {
            e.preventDefault();
            const formData = {
                "postId": $("input[name=postId]").val(),
                "authorId": $("#commentAuthorId").val(),
                "commentContent": $("#commentContent").val()
            };
            $.ajax({
                "url": "/api/post/comment",
                "type": "POST",
                "data": JSON.stringify(formData),
                "contentType": "application/json",
                "success": (response) => {
                    if (response.message) {
                        alert(response.message);
                        window.location.reload();
                    } else {
                        alert("댓글 등록 실패: " + response.error);
                    }
                },
                "error": (xhr) => {
                    console.error("Comment post failed:", xhr);
                    alert("댓글 등록 중 오류");
                }
            });
        });
    });
</script>
</body>
</html>