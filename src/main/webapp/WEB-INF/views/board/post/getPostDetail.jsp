<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="jakarta.tags.core" prefix="c" %>
<%@ taglib uri="jakarta.tags.fmt" prefix="fmt" %>
<%@ taglib uri="jakarta.tags.functions" prefix="fn" %>
<!DOCTYPE html>
<html lang="ko">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>게시물 상세</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css" rel="stylesheet">
    <style>
        @import url('https://fonts.googleapis.com/css2?family=Noto+Sans+KR:wght@400;500;700&display=swap');

        body {
            background-color: #f8f9fa;
            font-family: 'Noto Sans KR', sans-serif;
        }

        .post-container {
            margin: 30px 0;
            padding: 20px;
            width: 100%;
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
        }

        .btn-custom {
            padding: 0.75rem 1.25rem;
            font-size: 1rem;
            font-weight: 500;
        }

        .comment-form,
        .comment-list {
            margin-top: 20px;
        }

        .comment-item {
            border-bottom: 1px solid #e2e8f0;
            padding: 10px 0;
        }

        .comment-actions {
            display: flex;
            gap: 10px;
        }

        .edit-form {
            display: none;
            margin-top: 10px;
        }
    </style>
</head>
<body>
<div class="post-container">
    <h2 class="post-title fs-1">게시물 상세</h2>
    <table class="table">
        <tr>
            <th>제목</th>
            <td>${post.postTitle}</td>
        </tr>
        <tr>
            <th>작성자</th>
            <td>${post.userName}</td>
        </tr>
        <tr>
            <th>작성일</th>
            <td><fmt:formatDate value="${post.postCreatedAt}" pattern="yyyy-MM-dd HH:mm:ss"/></td>
        </tr>
        <tr>
            <th>조회수</th>
            <td>${post.postReadCount}</td>
        </tr>
        <tr>
            <th>내용</th>
            <td>${post.postContent}</td>
        </tr>
        <tr>
            <th>첨부파일</th>
            <td>
                <c:if test="${not empty post.existingFilePath}">
                    <a href="${pageContext.request.contextPath}${post.existingFilePath}" 
                       download="${fn:substringAfter(post.existingFilePath, '_')}" 
                       class="btn btn-sm btn-outline-secondary">다운로드</a>
                </c:if>
                <c:if test="${empty post.existingFilePath}">
                    없음
                </c:if>
            </td>
        </tr>
    </table>
    <div class="text-end mb-5">
        <a href="/post/getPosts" class="btn btn-secondary btn-custom">목록</a>
        <c:if test="${post.authorId == 'P001'}">
            <a href="/post/updatePost?postId=${post.postId}&authorId=P001" 
               class="btn btn-secondary btn-custom">수정</a>
            <a href="/post/deletePost?postId=${post.postId}" 
               class="btn btn-danger btn-custom">삭제</a>
        </c:if>
        <c:if test="${post.postNotice != 1}">
            <a href="/post/replyPost?postId=${post.postId}&authorId=P001" 
               class="btn btn-secondary btn-custom">답글</a>
        </c:if>
    </div>
    <div class="comment-form">
        <h4>댓글 작성</h4>
        <form id="commentForm" method="post">
            <input type="hidden" name="postId" value="${post.postId}">
            <div class="mb-3">
                <label for="commentAuthorId" class="form-label">ID</label>
                <input type="text" class="form-control" id="commentAuthorId" name="writerId" 
                       value="P001" readonly required>
            </div>
            <div class="mb-3">
                <label for="commentContent" class="form-label">내용</label>
                <textarea class="form-control" id="commentContent" name="commentContent" required></textarea>
            </div>
            <div class="text-end">
                <button type="submit" class="btn btn-primary">등록</button>
            </div>
        </form>
    </div>
    <div class="comment-list">
        <h4>댓글 목록</h4>
        <c:forEach var="comment" items="${commentList}">
            <div class="comment-item" id="comment-${comment.commentId}">
                <p><strong>${comment.writerName}</strong> (<fmt:formatDate value="${comment.createdAt}" pattern="yyyy-MM-dd"/>)</p>
                <p class="comment-content">${comment.commentContent}</p>
                <c:if test="${comment.writerId == 'P001'}">
                    <div class="comment-actions">
                        <button class="btn btn-sm btn-outline-primary edit-comment" 
                                data-comment-id="${comment.commentId}">수정</button>
                        <button class="btn btn-sm btn-outline-danger delete-comment" 
                                data-comment-id="${comment.commentId}">삭제</button>
                    </div>
                    <form class="edit-form" id="edit-form-${comment.commentId}">
                        <input type="hidden" name="commentId" value="${comment.commentId}">
                        <input type="hidden" name="writerId" value="P001">
                        <div class="mb-3">
                            <textarea class="form-control" name="commentContent" required>${comment.commentContent}</textarea>
                        </div>
                        <div class="text-end">
                            <button type="submit" class="btn btn-primary btn-sm">저장</button>
                            <button type="button" class="btn btn-secondary btn-sm cancel-edit">취소</button>
                        </div>
                    </form>
                </c:if>
            </div>
        </c:forEach>
    </div>
</div>
<script>
$(document).ready(function() {
    $("#commentForm").submit(function(e) {
        e.preventDefault();
        const formData = {
            postId: $("input[name='postId']").val(),
            writerId: $("#commentAuthorId").val(),
            commentContent: $("#commentContent").val()
        };
        $.ajax({
            url: "/api/post/comment",
            type: "POST",
            data: JSON.stringify(formData),
            contentType: "application/json",
            success: (response) => {
                if (response.success) {
                    alert(response.message);
                    window.location.reload();
                } else {
                    alert("댓글 등록 실패: " + response.message);
                }
            },
            error: (xhr) => {
                console.error("Comment post failed:", xhr);
                alert("댓글 등록 중 오류: " + (xhr.responseJSON?.message || "서버 오류"));
            }
        });
    });

    $(".edit-comment").click(function() {
        const commentId = $(this).data("comment-id");
        $(`#comment-${commentId} .comment-content`).hide();
        $(`#comment-${commentId} .comment-actions`).hide();
        $(`#edit-form-${commentId}`).show();
    });

    $(".cancel-edit").click(function() {
        const form = $(this).closest(".edit-form");
        const commentId = form.find("input[name='commentId']").val();
        $(`#comment-${commentId} .comment-content`).show();
        $(`#comment-${commentId} .comment-actions`).show();
        form.hide();
    });

    $(".edit-form").submit(function(e) {
        e.preventDefault();
        const formData = {
            commentId: $(this).find("input[name='commentId']").val(),
            writerId: $(this).find("input[name='writerId']").val(),
            commentContent: $(this).find("textarea[name='commentContent']").val()
        };
        $.ajax({
            url: "/api/post/comment",
            type: "PUT",
            data: JSON.stringify(formData),
            contentType: "application/json",
            success: (response) => {
                if (response.success) {
                    alert(response.message);
                    window.location.reload();
                } else {
                    alert("댓글 수정 실패: " + response.message);
                }
            },
            error: (xhr) => {
                console.error("Comment update failed:", xhr);
                alert("댓글 수정 중 오류: " + (xhr.responseJSON?.message || "서버 오류"));
            }
        });
    });

    $(".delete-comment").click(function() {
        if (!confirm("댓글을 삭제하시겠습니까?")) return;
        const commentId = $(this).data("comment-id");
        const writerId = $(`#comment-${commentId} input[name='writerId']`).val();
        $.ajax({
            url: "/api/post/comment",
            type: "DELETE",
            data: JSON.stringify({ commentId, writerId }),
            contentType: "application/json",
            success: (response) => {
                if (response.success) {
                    alert(response.message);
                    window.location.reload();
                } else {
                    alert("댓글 삭제 실패: " + response.message);
                }
            },
            error: (xhr) => {
                console.error("Comment deletion failed:", xhr);
                alert("댓글 삭제 중 오류: " + (xhr.responseJSON?.message || "서버 오류"));
            }
        });
    });
});
</script>
</body>
</html>