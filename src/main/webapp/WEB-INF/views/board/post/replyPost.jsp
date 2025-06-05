<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="jakarta.tags.core" prefix="c"%>
<%@ taglib uri="jakarta.tags.fmt" prefix="fmt"%>
<%@ taglib uri="jakarta.tags.functions" prefix="fn"%>
<!DOCTYPE html>
<html lang="ko">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>답글 등록</title>
    <link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css">
    <link rel="stylesheet" href="https://uicdn.com/editor/latest/toastui-editor.min.css" />
    <script src="https://uicdn.com/editor/latest/toastui-editor-all.min.js"></script>
    <style>
        @import url('https://fonts.googleapis.com/css2?family=Noto+Sans+KR:wght@400;500;700&display=swap');
        .notice-container { width: 100%; margin: 0 auto; padding: 20px; font-family: 'Noto Sans KR', sans-serif; }
        .form-label { font-weight: 500; margin-bottom: 5px; font-size: 16px; }
        .form-control { padding: 10px; border: 1px solid #e2e8f0; border-radius: 4px; width: 100%; font-size: 16px; }
        .notice-btn-primary { padding: 10px 20px; background: #3182ce; color: white; border: none; border-radius: 4px; cursor: pointer; font-size: 16px; }
        .notice-btn-primary:hover { background: #2b6cb0; }
        .toast-editor { border: 1px solid #e2e8f0; border-radius: 4px; }
        #editor { width: 100%; }
    </style>
</head>
<body>
<div class="notice-container mt-5">
    <h2 class="text-center h2">답글 등록</h2>
    <form id="postForm" action="/post/reply" method="post" enctype="multipart/form-data">
        <input type="hidden" name="parentPostId" value="${postDto.parentPostId}">
        <div class="mb-3">
            <label for="authorId" class="form-label">ID</label>
            <input type="text" class="form-control" id="authorId" name="authorId" value="${postDto.authorId != null ? postDto.authorId : 'S001'}" readonly>
            <span class="form-text">ID: ${postDto.authorId}</span>
        </div>
        <div class="mb-3">
            <label for="userName" class="form-label">작성자</label>
            <input type="text" class="form-control" id="userName" value="${postDto.userName != null ? postDto.userName : '테스트 사용자'}" readonly>
        </div>
        <div class="mb-3">
            <label for="postTitle" class="form-label">제목</label>
            <input type="text" class="form-control" id="postTitle" name="postTitle" value="${postDto.postTitle}" required>
        </div>
        <div class="mb-3">
            <label for="content" class="form-label">내용</label>
            <textarea id="content" name="postContent" style="display: none;"></textarea>
            <div id="editor" class="toast-editor"></div>
        </div>
        <div class="mb-3">
            <label for="postPassword" class="form-label">비밀번호</label>
            <input type="password" class="form-control" id="postPassword" name="postPassword" required>
        </div>
        <div class="mb-3">
            <label for="postFile" class="form-label">첨부파일</label>
            <input type="file" class="form-control" id="postFile" name="postFile">
        </div>
        <div class="text-end">
            <button type="submit" class="notice-btn-primary">등록</button>
            <a href="/post/getPostDetail?postId=${postDto.parentPostId}&authorId=${param.authorId}" class="btn btn-secondary">취소</a>
        </div>
    </form>
</div>
<script>
    let editor;
    $(document).ready(function() {
        editor = new toastui.Editor({
            el: document.querySelector("#editor"),
            height: "400px",
            initialEditType: "wysiwyg",
            previewStyle: "vertical",
            hooks: {
                addImageBlobHook: (blob, callback) => {
                    const formData = new FormData();
                    formData.append("file", blob);
                    $.ajax({
                        url: "/api/post/uploadImage",
                        type: "POST",
                        data: formData,
                        processData: false,
                        contentType: false,
                        success: (response) => {
                            if (response.url) {
                                callback(response.url, "image");
                            } else {
                                alert("이미지 업로드 실패: " + response.error);
                            }
                        },
                        error: (xhr) => {
                            console.error("Image upload failed:", xhr);
                            alert("이미지 업로드 오류");
                        }
                    });
                    return false;
                }
            }
        });

        $("#postForm").submit(function(e) {
            e.preventDefault();
            const formData = new FormData();
            const postData = {
                parentPostId: $("input[name=parentPostId]").val(),
                postTitle: $("#postTitle").val(),
                postContent: editor.getHTML(),
                postPassword: $("#postPassword").val(),
                authorId: $("#authorId").val()
            };
            formData.append("post", new Blob([JSON.stringify(postData)], { type: "application/json" }));
            const fileInput = $("#postFile")[0];
            if (fileInput.files.length > 0) {
                formData.append("file", fileInput.files[0]);
            }
            $.ajax({
                url: "/api/post/reply",
                type: "POST",
                data: formData,
                processData: false,
                contentType: false,
                success: (response) => {
                    if (response.message) {
                        alert(response.message);
                        window.location.href = response.redirectUrl;
                    } else {
                        alert("답글 작성 실패: " + response.error);
                    }
                },
                error: (xhr) => {
                    console.error("Reply post failed:", xhr);
                    alert("답글 작성 중 오류");
                }
            });
        });
    });
</script>
</body>
</html>