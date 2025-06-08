<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="jakarta.tags.core" prefix="c"%>
<%@ taglib uri="jakarta.tags.fmt" prefix="fmt"%>
<%@ taglib uri="jakarta.tags.functions" prefix="fn"%>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>문의게시판 게시물 상세</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css" rel="stylesheet">
    <style>
        .board-container {
            margin: 30px 0;
            padding: 20px;
            width: 100%;
            font-family: 'Noto Sans KR', sans-serif;
        }
        .post-title {
            font-size: 2.2rem;
            color: #343a40;
            margin-bottom: 1.5rem;
            font-weight: bold;
        }
        .table {
            background-color: #fff;
            border: 1px solid #e9ecef;
            width: 100%;
        }
        .table th {
            width: 15%;
            background-color: #f1f3f5;
            color: #495057;
            padding: 10px;
            vertical-align: middle;
        }
        .table td {
            padding: 10px;
            vertical-align: middle;
        }
        .btn-custom {
            padding: 0.75rem 1.25rem;
            font-size: 1rem;
            font-weight: 500;
        }
        .comment-section, .reply-section {
            margin-top: 20px;
        }
        .comment-item, .reply-item {
            border-bottom: 1px solid #e2e8f0;
            padding: 10px 0;
        }
        .comment-actions, .reply-actions {
            display: flex;
            gap: 10px;
            margin-top: 5px;
        }
        .edit-form, .reply-form {
            display: none;
            margin-top: 10px;
        }
        .reply-form {
            margin-left: 20px;
        }
        .new-reply-form {
            display: none;
            margin-top: 10px;
        }
    </style>
</head>
<body>
<div class="board-container">
    <h1 class="post-title">게시물 상세</h1>
    <table class="table">
        <tr><th>제목</th><td>${post.postTitle}</td></tr>
        <tr><th>작성자</th><td>${post.userName}</td></tr>
        <tr><th>작성일</th><td><fmt:formatDate value="${post.postCreatedAt}" pattern="yyyy-MM-dd HH:mm:ss"/></td></tr>
        <tr><th>조회수</th><td>${post.postReadCount}</td></tr>
        <tr><th>내용</th><td>${post.postContent}</td></tr>
        <tr><th>첨부파일</th>
            <td>
                <c:if test="${not empty post.existingFilePath}">
                    <a href="${post.existingFilePath}" download="${fn:substringAfter(post.existingFilePath, '_')}" class="btn btn-sm btn-outline-secondary">다운로드</a>
                </c:if>
                <c:if test="${empty post.existingFilePath}">없음</c:if>
            </td>
        </tr>
    </table>
    <div class="text-end mb-5">
        <a href="/post/getPosts" class="btn btn-secondary btn-custom">목록</a>
        <a href="/post/updatePost?postId=${post.postId}" class="btn btn-secondary btn-custom">수정</a>
        <a href="/post/deletePost?postId=${post.postId}" class="btn btn-danger btn-custom">삭제</a>
        <a href="/post/replyPost?postId=${post.postId}" class="btn btn-primary">답글 작성</a>
    </div>

    <div class="comment-section">
        <h3>댓글</h3>
        <c:forEach var="comment" items="${commentList}">
            <c:if test="${empty comment.parentCommentId}">
                <div class="comment-item" id="comment-${comment.commentId}">
                    <p><strong>${comment.writerName}</strong> (<fmt:formatDate value="${comment.createdAt}" pattern="yyyy-MM-dd HH:mm"/>)</p>
                    <p class="comment-content">${comment.commentContent}</p>
                    <div class="comment-actions">
                        <button class="btn btn-sm btn-outline-info reply-comment" data-comment-id="${comment.commentId}">대댓글</button>
                        <button class="btn btn-sm btn-outline-primary edit-comment" data-comment-id="${comment.commentId}">수정</button>
                        <button class="btn btn-sm btn-outline-danger delete-comment" data-comment-id="${comment.commentId}">삭제</button>
                    </div>
                    <form class="edit-form" id="edit-form-${comment.commentId}">
                        <input type="hidden" name="commentId" value="${comment.commentId}">
                        <input type="hidden" name="postId" value="${post.postId}">
                        <div class="mb-3">
                            <label for="editCommentContent-${comment.commentId}" class="form-label">댓글 내용</label>
                            <textarea class="form-control" name="commentContent" required>${comment.commentContent}</textarea>
                        </div>
                        <div class="text-end">
                            <button type="submit" class="btn btn-primary btn-sm">저장</button>
                            <button type="button" class="btn btn-secondary btn-sm cancel-edit" data-comment-id="${comment.commentId}">취소</button>
                        </div>
                    </form>
                    <div class="reply-form" id="reply-form-${comment.commentId}">
                        <form id="replyForm-${comment.commentId}">
                            <input type="hidden" name="postId" value="${post.postId}">
                            <input type="hidden" name="parentCommentId" value="${comment.commentId}">
                            <div class="mb-3">
                                <label for="replyContent-${comment.commentId}" class="form-label">대댓글 내용</label>
                                <textarea class="form-control" id="replyContent-${comment.commentId}" name="commentContent" required></textarea>
                            </div>
                            <div class="text-end">
                                <button type="submit" class="btn btn-primary btn-sm">등록</button>
                                <button type="button" class="btn btn-secondary btn-sm cancel-reply" data-comment-id="${comment.commentId}">취소</button>
                            </div>
                        </form>
                    </div>
                    <c:forEach var="child" items="${commentList}">
                        <c:if test="${child.parentCommentId eq comment.commentId}">
                            <div class="comment-item ms-4 border-start ps-3 mt-2" id="comment-${child.commentId}">
                                <p><strong>${child.writerName}</strong> (<fmt:formatDate value="${child.createdAt}" pattern="yyyy-MM-dd HH:mm"/>)</p>
                                <p class="comment-content">${child.commentContent}</p>
                                <div class="comment-actions">
                                    <button class="btn btn-sm btn-outline-primary edit-comment" data-comment-id="${child.commentId}">수정</button>
                                    <button class="btn btn-sm btn-outline-danger delete-comment" data-comment-id="${child.commentId}">삭제</button>
                                </div>
                                <form class="edit-form" id="edit-form-${child.commentId}">
                                    <input type="hidden" name="commentId" value="${child.commentId}">
                                    <input type="hidden" name="postId" value="${post.postId}">
                                    <div class="mb-3">
                                        <label for="editCommentContent-${child.commentId}" class="form-label">댓글 내용</label>
                                        <textarea class="form-control" name="commentContent" required>${child.commentContent}</textarea>
                                    </div>
                                    <div class="text-end">
                                        <button type="submit" class="btn btn-primary btn-sm">저장</button>
                                        <button type="button" class="btn btn-secondary btn-sm cancel-edit" data-comment-id="${child.commentId}">취소</button>
                                    </div>
                                </form>
                            </div>
                        </c:if>
                    </c:forEach>
                </div>
            </c:if>
        </c:forEach>

        <form id="commentForm" class="mt-4">
            <input type="hidden" name="postId" value="${post.postId}">
            <div class="mb-3">
                <label for="commentContent" class="form-label">댓글 내용</label>
                <textarea class="form-control" id="commentContent" name="commentContent" rows="3" required></textarea>
            </div>
            <div class="text-end">
                <button type="submit" class="btn btn-primary">댓글 작성</button>
            </div>
        </form>
    </div>
</div>

<script>
$(document).ready(function() {
    console.log("jQuery loaded:", typeof $ !== "undefined");

    $("#commentForm").submit(function(e) {
        e.preventDefault();
        const formData = {
            postId: $("input[name='postId']").val(),
            commentContent: $("#commentContent").val()
        };
        $.ajax({
            url: "/api/post/comments/write",
            type: "POST",
            data: JSON.stringify(formData),
            contentType: "application/json",
            success: (response) => {
                console.log("댓글 작성 성공:", response);
                window.location.reload();
            },
            error: (xhr) => {
                console.error("댓글 등록 중 오류:", xhr);
                alert("댓글 등록 중 오류: " + (xhr.responseJSON?.error || xhr.statusText));
            }
        });
    });

    $(".edit-comment").click(function() {
        const commentId = $(this).data("comment-id");
        $(`#comment-${commentId} .comment-content`).hide();
        $(`#comment-${commentId} .comment-actions`).hide();
        $(`#edit-form-${commentId}`).show();
    });

    $(".edit-form").submit(function(e) {
        e.preventDefault();
        const commentId = $(this).find("input[name='commentId']").val();
        const postId = $(this).find("input[name='postId']").val();
        const commentContent = $(this).find("textarea[name='commentContent']").val();

        const formData = {
            commentId: commentId,
            postId: postId,
            commentContent: commentContent
        };

        $.ajax({
            url: "/api/post/comments/update",
            type: "POST",
            data: JSON.stringify(formData),
            contentType: "application/json",
            success: (response) => {
                console.log("댓글 수정 성공:", response);
                window.location.reload();
            },
            error: (xhr) => {
                console.error("댓글 수정 중 오류:", xhr);
                alert("댓글 수정 중 오류: " + (xhr.responseJSON?.error || xhr.statusText));
            }
        });
    });

    $(".delete-comment").click(function() {
        if (!confirm("댓글을 삭제하시겠습니까?")) return;
        const commentId = $(this).data("comment-id");
        const postId = $(this).closest(".comment-item").find("input[name='postId']").val();

        $.ajax({
            url: "/api/post/comments/delete",
            type: "POST",
            data: JSON.stringify({
                commentId: commentId,
                postId: postId
            }),
            contentType: "application/json",
            success: (response) => {
                console.log("댓글 삭제 성공:", response);
                window.location.reload();
            },
            error: (xhr) => {
                console.error("댓글 삭제 중 오류:", xhr);
                alert("댓글 삭제 중 오류: " + (xhr.responseJSON?.error || xhr.statusText));
            }
        });
    });

    $(".reply-comment").click(function() {
        const commentId = $(this).data("comment-id");
        $(`#reply-form-${commentId}`).toggle();
    });

    $(".reply-form form").submit(function(e) {
        e.preventDefault();
        const postId = $(this).find("input[name='postId']").val();
        const parentCommentId = $(this).find("input[name='parentCommentId']").val();
        const commentContent = $(this).find("textarea[name='commentContent']").val();

        const formData = {
            postId: postId,
            parentCommentId: parentCommentId,
            commentContent: commentContent
        };

        $.ajax({
            url: "/api/post/comments/write",
            type: "POST",
            data: JSON.stringify(formData),
            contentType: "application/json",
            success: (response) => {
                console.log("대댓글 작성 성공:", response);
                window.location.reload();
            },
            error: (xhr) => {
                console.error("대댓글 등록 중 오류:", xhr);
                alert("대댓글 등록 중 오류: " + (xhr.responseJSON?.error || xhr.statusText));
            }
        });
    });

    $(".cancel-edit, .cancel-reply").click(function() {
        const commentId = $(this).data("comment-id");
        $(`#edit-form-${commentId}`).hide();
        $(`#reply-form-${commentId}`).hide();
        $(`#comment-${commentId} .comment-content`).show();
        $(`#comment-${commentId} .comment-actions`).show();
    });
});
</script>
</body>
</html>