<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="jakarta.tags.core" prefix="c"%>
<!DOCTYPE html>
<html lang="ko">
<head>
    <meta charset="UTF-8">
    <title>게시물 삭제</title>
</head>
<body>
<div class="container-fluid">
    <div class="row justify-content-center mt-5">
        <div class="col-md-6">
            <div class="card card-danger shadow-sm">
                <div class="card-header"><h3 class="card-title">게시물 삭제</h3></div>
                <form id="deleteForm">
                    <div class="card-body">
                        <p>게시물을 삭제하면 복구할 수 없습니다. 비밀번호를 입력해주세요.</p>
                        <p><strong>제목:</strong> ${post.postTitle}</p>
                        <div class="mb-3">
                            <label for="postPassword" class="form-label">비밀번호</label>
                            <input type="password" id="postPassword" class="form-control" required>
                        </div>
                    </div>
                    <div class="card-footer text-end">
                        <a href="/post/getPostDetail?postId=${post.postId}" class="btn btn-secondary">취소</a>
                        <button type="submit" class="btn btn-danger">삭제 확인</button>
                    </div>
                </form>
            </div>
        </div>
    </div>
</div>
<script>
    $('#deleteForm').on('submit', function(e) {
        e.preventDefault();
        const password = $('#postPassword').val();
        if (!password) {
            alert("비밀번호를 입력해주세요.");
            return;
        }
        const url = "/api/post/deletePost/" + "${post.postId}" + "?postPassword=" + encodeURIComponent(password);
        $.ajax({
            url: url,
            type: 'POST',
            success: function(res) {
                window.location.href = res.data;
            },
            error: function(xhr) {
                alert('삭제 실패: ' + (xhr.responseJSON ? xhr.responseJSON.message : "서버 오류"));
            }
        });
    });
</script>
</body>
</html>