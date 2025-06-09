<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="jakarta.tags.core" prefix="c"%>
<%@ taglib uri="jakarta.tags.fmt" prefix="fmt"%>
<%@ taglib uri="jakarta.tags.functions" prefix="fn"%>
<!DOCTYPE html>
<html lang="ko">
<head>
    <meta charset="UTF-8">
    <title>문의게시판 상세</title>
    <style>
        .comment-actions .btn-link { text-decoration: none; }
        .edit-form, .reply-form { display: none; }
        .reply-comment { margin-left: 50px; border-left: 2px solid #e9ecef; padding-left: 15px; }
    </style>
</head>
<body>
    <div class="container-fluid">
        <div class="card shadow-sm">
            <div class="card-header">
                <h3 class="card-title" style="font-weight: 600;">문의게시판 상세</h3>
            </div>
            <div class="card-body">
                <table class="table table-bordered">
                    <tbody>
                        <tr>
                            <th style="width: 15%; background-color: #f8f9fa;">제목</th>
                            <td>${fn:escapeXml(post.postTitle)}</td>
                        </tr>
                        <tr>
                            <th style="background-color: #f8f9fa;">작성자</th>
                            <td>${fn:escapeXml(post.userName)}</td>
                        </tr>
                        <tr>
                            <th style="background-color: #f8f9fa;">작성일</th>
                            <td><fmt:formatDate value="${post.postCreatedAt}" pattern="yyyy-MM-dd HH:mm"/></td>
                        </tr>
                        <tr>
                            <th style="background-color: #f8f9fa;">조회수</th>
                            <td>${post.postReadCount}</td>
                        </tr>
                        <tr>
                            <th style="background-color: #f8f9fa;">내용</th>
                            <td style="min-height: 200px; vertical-align: top;"><div>${post.postContent}</div></td>
                        </tr>
                        <c:if test="${not empty post.existingFilePath}">
                            <tr>
                                <th style="background-color: #f8f9fa;">첨부파일</th>
                                <td><a href="${post.existingFilePath}" download class="text-decoration-none">${fn:substringAfter(post.existingFilePath, '_')}</a></td>
                            </tr>
                        </c:if>
                    </tbody>
                </table>
            </div>
            <div class="card-footer text-end">
                <a href="/post/getPosts" class="btn btn-secondary">목록</a>
                <a href="/post/replyPost?postId=${post.postId}" class="btn btn-info">답글</a>
                <a href="/post/updatePost?postId=${post.postId}" class="btn btn-warning">수정</a>
                <a href="/post/deletePost?postId=${post.postId}" class="btn btn-danger">삭제</a>
            </div>
        </div>

        <div class="card shadow-sm mt-4">
            <div class="card-header"><h4 class="card-title">댓글 (${fn:length(comments)})</h4></div>
            <div class="card-body">
                <c:forEach var="comment" items="${comments}">
                    <c:if test="${empty comment.parentCommentId}">
                        <div class="comment-item pt-3 pb-3 border-bottom" id="comment-${comment.commentId}">
                            <div class="d-flex justify-content-between">
                                <span class="fw-bold">${fn:escapeXml(comment.writerName)}</span>
                                <span class="text-muted small"><fmt:formatDate value="${comment.createdAt}" pattern="yyyy.MM.dd HH:mm"/></span>
                            </div>
                            <div class="comment-content my-2"><p class="mb-0">${fn:escapeXml(comment.commentContent)}</p></div>
                            <div class="comment-actions text-end">
                                <button type="button" class="btn btn-link text-primary p-0 me-2 reply-comment-btn" data-comment-id="${comment.commentId}">대댓글</button>
                                <button type="button" class="btn btn-link text-secondary p-0 me-2 edit-comment-btn" data-comment-id="${comment.commentId}">수정</button>
                                <button type="button" class="btn btn-link text-danger p-0 delete-comment-btn" data-comment-id="${comment.commentId}">삭제</button>
                            </div>
                            <div class="edit-form p-3 mt-2 bg-light rounded" id="edit-form-${comment.commentId}">
                                <textarea class="form-control form-control-sm edit-content-textarea" rows="3">${fn:escapeXml(comment.commentContent)}</textarea>
                                <div class="d-flex justify-content-end gap-2 mt-2">
                                    <button type="button" class="btn btn-sm btn-secondary cancel-edit-btn">취소</button>
                                    <button type="button" class="btn btn-sm btn-primary save-edit-btn" data-comment-id="${comment.commentId}">저장</button>
                                </div>
                            </div>
                            <div class="reply-form p-3 mt-2 bg-light rounded" id="reply-form-${comment.commentId}">
                                <textarea class="form-control form-control-sm reply-content-textarea" rows="2" placeholder="대댓글을 입력하세요"></textarea>
                                <div class="d-flex justify-content-end gap-2 mt-2">
                                    <button type="button" class="btn btn-sm btn-secondary cancel-reply-btn">취소</button>
                                    <button type="button" class="btn btn-sm btn-info submit-reply-btn" data-parent-id="${comment.commentId}">등록</button>
                                </div>
                            </div>
                            <c:forEach var="reply" items="${comments}">
                                <c:if test="${reply.parentCommentId eq comment.commentId}">
                                    <div class="comment-item reply-comment pt-3 pb-3 mt-3" id="comment-${reply.commentId}">
                                         <div class="d-flex justify-content-between">
                                            <div><span class="fw-bold me-2">↳ ${fn:escapeXml(reply.writerName)}</span></div>
                                            <span class="text-muted small"><fmt:formatDate value="${reply.createdAt}" pattern="yyyy.MM.dd HH:mm"/></span>
                                        </div>
                                        <div class="comment-content my-2"><p class="mb-0">${fn:escapeXml(reply.commentContent)}</p></div>
                                        <div class="comment-actions text-end">
                                            <button type="button" class="btn btn-link text-secondary p-0 me-2 edit-comment-btn" data-comment-id="${reply.commentId}">수정</button>
                                            <button type="button" class="btn btn-link text-danger p-0 delete-comment-btn" data-comment-id="${reply.commentId}">삭제</button>
                                        </div>
                                         <div class="edit-form p-3 mt-2 bg-light rounded" id="edit-form-${reply.commentId}">
                                            <textarea class="form-control form-control-sm edit-content-textarea" rows="3">${fn:escapeXml(reply.commentContent)}</textarea>
                                            <div class="d-flex justify-content-end gap-2 mt-2">
                                                <button type="button" class="btn btn-sm btn-secondary cancel-edit-btn">취소</button>
                                                <button type="button" class="btn btn-sm btn-primary save-edit-btn" data-comment-id="${reply.commentId}">저장</button>
                                            </div>
                                        </div>
                                    </div>
                                </c:if>
                            </c:forEach>
                        </div>
                    </c:if>
                </c:forEach>
            </div>
            <div class="card-footer">
                <div class="d-flex gap-2">
                    <textarea id="newCommentContent" class="form-control" rows="3" placeholder="댓글을 입력하세요"></textarea>
                    <button type="button" class="btn btn-primary" id="submitCommentButton" style="width: 120px;">댓글 등록</button>
                </div>
            </div>
        </div>
    </div>
    
    <script src="https://ajax.googleapis.com/ajax/libs/jquery/3.7.1/jquery.min.js"></script>
    <script>
        $(document).ready(function() {
            const postId = "${post.postId}";

            // 새 댓글 등록
            $("#submitCommentButton").on("click", function() {
                const content = $("#newCommentContent").val();
                if (!content.trim()) { alert("댓글 내용을 입력하세요."); return; }
                const commentData = { postId: postId, commentContent: content, parentCommentId: null };
                $.ajax({
                    url: "/api/post/comments/write", type: "POST", contentType: "application/json", data: JSON.stringify(commentData),
                    success: (res) => { alert(res.message); location.reload(); },
                    error: (xhr) => { alert("댓글 등록 실패: " + (xhr.responseJSON ? xhr.responseJSON.message : "서버 오류")); }
                });
            });
            
            // 대댓글 버튼 클릭
            $(".card-body").on("click", ".reply-comment-btn", function() {
                const commentId = $(this).data("comment-id");
                $('.reply-form').not('#reply-form-' + commentId).slideUp();
                $('#reply-form-' + commentId).slideToggle();
            });

            // 대댓글 취소 버튼
            $(".card-body").on("click", ".cancel-reply-btn", function() { $(this).closest('.reply-form').slideUp(); });

            // 대댓글 등록
            $(".card-body").on("click", ".submit-reply-btn", function() {
                const parentId = $(this).data("parent-id");
                const content = $('#reply-form-' + parentId).find(".reply-content-textarea").val();
                if (!content.trim()) { alert("대댓글 내용을 입력하세요."); return; }
                const commentData = { postId: postId, commentContent: content, parentCommentId: parentId };
                $.ajax({
                    url: "/api/post/comments/write", type: "POST", contentType: "application/json", data: JSON.stringify(commentData),
                    success: (res) => { alert(res.message); location.reload(); },
                    error: (xhr) => { alert("대댓글 등록 실패: " + (xhr.responseJSON ? xhr.responseJSON.message : "서버 오류")); }
                });
            });

            // 수정 버튼 클릭
            $(".card-body").on("click", ".edit-comment-btn", function() {
                const commentId = $(this).data("comment-id");
                $('.edit-form').not('#edit-form-' + commentId).slideUp();
                $('.reply-form').slideUp();
                $("#comment-" + commentId).find(".comment-content, .comment-actions").hide();
                $("#edit-form-" + commentId).slideDown();
            });

            // 수정 취소
            $(".card-body").on("click", ".cancel-edit-btn", function() {
                const form = $(this).closest('.edit-form');
                form.slideUp(() => form.closest('.comment-item').find(".comment-content, .comment-actions").show());
            });

            // 수정 저장
            $(".card-body").on("click", ".save-edit-btn", function() {
                const commentId = $(this).data("comment-id");
                const content = $("#edit-form-" + commentId).find(".edit-content-textarea").val();
                if (!content.trim()) { alert("수정할 내용을 입력하세요."); return; }
                const commentData = { commentId: commentId, commentContent: content };
                $.ajax({
                    url: "/api/post/comments/update", type: "PUT", contentType: "application/json", data: JSON.stringify(commentData),
                    success: (res) => { alert(res.message); location.reload(); },
                    error: (xhr) => { alert("댓글 수정 실패: " + (xhr.responseJSON ? xhr.responseJSON.message : "서버 오류")); }
                });
            });

            // 댓글 삭제
            $(".card-body").on("click", ".delete-comment-btn", function() {
                if (!confirm("정말로 이 댓글을 삭제하시겠습니까?")) return;
                
                const commentId = $(this).data("comment-id");
                
               
                const deleteUrl = "/api/post/comments/delete/" + commentId;
                
                
                console.log("요청 URL:", deleteUrl);
                
                $.ajax({
                    url: deleteUrl, 
                    type: "POST", 
                    success: (res) => {
                        alert(res.message);
                        location.reload();
                    },
                    error: (xhr) => {
                        alert("댓글 삭제 실패: " + (xhr.responseJSON ? xhr.responseJSON.message : "서버 오류"));
                    }
                });
            });
        });
    </script>
</body>
</html>