<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="jakarta.tags.core" prefix="c" %>
<%@ taglib uri="jakarta.tags.fmt" prefix="fmt" %>
<%@ taglib uri="jakarta.tags.functions" prefix="fn" %>
<!DOCTYPE html>
<html lang="ko">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>게시물 등록</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css" rel="stylesheet">
    <link rel="stylesheet" href="https://uicdn.toast.com/editor/latest/toastui-editor.min.css" />
    <script src="https://uicdn.toast.com/editor/latest/toastui-editor-all.min.js"></script>
    <script src="https://ajax.googleapis.com/ajax/libs/jquery/3.7.1/jquery.min.js"></script> <style>
        @import url('https://fonts.googleapis.com/css2?family=Noto+Sans+KR:wght@400;500;700&display=swap');
        .post-container {
            width: 100%;
            max-width: 800px; 
            margin: 0 auto;
            padding: 20px;
            font-family: 'Noto Sans KR', sans-serif;
        }
        .form-label {
            font-weight: 500;
            margin-bottom: 5px;
            font-size: 16px;
        }
        .form-control, .form-select {
            padding: 10px;
            border: 1px solid #e2e8f0;
            border-radius: 4px;
            width: 100%;
            font-size: 16px;
        }
        .post-btn-primary {
            padding: 10px 20px;
            background: #3182ce;
            color: white;
            border: none;
            border-radius: 4px;
            cursor: pointer;
            font-size: 16px;
        }
        .post-btn-primary:hover {
            background: #2b6cb0;
        }
        .toast-editor {
            border: 1px solid #e2e8f0;
            border-radius: 4px;
            width: 100%;
        }
        #editor {
            width: 100%;
        }
    </style>
</head>
<body>
<div class="post-container mt-5">
    <h2 class="text-center h2">게시물 등록</h2>
    <form id="postForm" action="/api/post/write" method="post" enctype="multipart/form-data">
        <div class="mb-3">
            <label for="authorId" class="form-label">작성자 ID</label>
            <input type="text" class="form-control" id="authorId" name="authorId" value="${currentAuthorId}" readonly required>
        </div>
        <div class="mb-3">
            <label for="postPassword" class="form-label">비밀번호</label>
            <input type="password" class="form-control" id="postPassword" name="postPassword" required>
        </div>
        <div class="mb-3">
            <label for="postTitle" class="form-label">제목</label>
            <input type="text" class="form-control" id="postTitle" name="postTitle" required>
        </div>
        <div class="mb-3">
            <label for="content" class="form-label">내용</label>
            <textarea id="content" name="postContent" style="display: none;"></textarea>
            <div id="editor" class="toast-editor"></div>
        </div>
        <div class="mb-3">
            <label for="postFile" class="form-label">첨부파일</label>
            <input type="file" class="form-control" id="postFile" name="postFile">
        </div>
        <div class="mb-3 form-check">
            <input type="checkbox" class="form-check-input" id="postNotice" name="postNotice" value="1">
            <label class="form-check-label" for="postNotice">공지사항</label>
        </div>
        <div class="text-end">
            <button type="submit" class="post-btn-primary">등록</button>
            <a href="/post/getPosts" class="btn btn-secondary">취소</a>
        </div>
    </form>
</div>

<script>
let editor;
$(document).ready(function () {
    editor = new toastui.Editor({
        el: document.querySelector('#editor'),
        height: '400px',
        initialEditType: 'wysiwyg',
        previewStyle: 'vertical',
        hooks: {
            addImageBlobHook: (blob, callback) => {
                const formData = new FormData();
                formData.append('file', blob);
                $.ajax({
                    url: '/api/post/uploadImage',
                    type: 'POST',
                    data: formData,
                    processData: false,
                    contentType: false,
                    success: (response) => {
                        if (response.success && response.data && response.data.url) {
                            callback(response.data.url, response.data.fileName || "image");
                        } else {
                            console.error('Upload failed:', response.message || response.error);
                            alert('이미지 업로드 실패: ' + (response.message || response.error || "알 수 없는 오류"));
                        }
                    },
                    error: (xhr) => {
                        console.error('Image upload failed:', xhr.responseJSON);
                        alert('이미지 업로드 중 오류 발생');
                    }
                });
                return false;
            }
        }
    });

    $('#postForm').on('submit', function (e) {
        e.preventDefault();
        const formData = new FormData();
        const postData = {
            postTitle: $('#postTitle').val(),
            postContent: editor.getHTML(),
            postPassword: $('#postPassword').val(),
            authorId: $('#authorId').val(),
            postNotice: $('#postNotice').is(':checked') ? 1 : 0 
        };
        // !!!!! 이 부분을 수정합니다 !!!!!
        formData.append('post', new Blob([JSON.stringify(postData)], { type: 'application/json' })); 
        
        const fileInput = $('#postFile')[0];
        if (fileInput.files.length > 0) {
            formData.append('file', fileInput.files[0]);
        }

        $.ajax({
            url: '/api/post/write',
            type: 'POST',
            data: formData,
            processData: false,
            contentType: false, 
            success: (response) => {
                console.log("Post create response:", response);
                if (response.success) { 
                    alert(response.message);
                    // PostService에서 data 필드에 redirectUrl을 담도록 수정했으므로, data를 확인합니다.
                    window.location.href = response.data || '/post/getPosts'; 
                } else {
                    alert('게시물 저장 실패: ' + (response.message || response.error || "알 수 없는 오류"));
                }
            },
            error: (xhr) => {
                console.error('Write post failed:', xhr.responseJSON);
                const errorMessage = xhr.responseJSON && xhr.responseJSON.message ? xhr.responseJSON.message : "알 수 없는 오류 발생";
                alert('게시물 저장 중 오류 발생: ' + errorMessage);
            }
        });
    });
});
</script>
</body>
</html>