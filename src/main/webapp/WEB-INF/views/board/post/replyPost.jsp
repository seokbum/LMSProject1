<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="jakarta.tags.core" prefix="c"%>
<%@ taglib uri="jakarta.tags.functions" prefix="fn"%>
<!DOCTYPE html>
<html lang="ko">
<head>
    <meta charset="UTF-8">
    <title>답글 작성</title>
    <link rel="stylesheet" href="https://uicdn.toast.com/editor/latest/toastui-editor.min.css" />
</head>
<body>
    <div class="container-fluid">
        <div class="card shadow-sm">
            <div class="card-header">
                <h3 class="card-title" style="font-weight: 600;">답글 작성</h3>
            </div>
            <form id="postForm">
                <div class="card-body">
                    <div class="alert alert-secondary">
                        <strong>원글:</strong> ${fn:escapeXml(parentPost.postTitle)}
                    </div>
                    <div class="mb-3">
                        <label for="postTitle" class="form-label">제목</label>
                        <input type="text" class="form-control" id="postTitle" value="Re: ${fn:escapeXml(parentPost.postTitle)}" required>
                    </div>
                    <div class="mb-3">
                        <label class="form-label">내용</label>
                        <div id="editor"></div>
                    </div>
                    <div class="mb-3">
                        <label for="postPassword" class="form-label">비밀번호</label>
                        <input type="password" class="form-control" id="postPassword" required>
                    </div>
                </div>
                <div class="card-footer text-end">
                    <a href="/post/getPostDetail?postId=${parentPost.postId}" class="btn btn-secondary">취소</a>
                    <button type="button" id="submitBtn" class="btn btn-primary">답글 등록</button>
                </div>
            </form>
        </div>
    </div>
    <script src="https://uicdn.toast.com/editor/latest/toastui-editor-all.min.js"></script>
    <script>
        $(document).ready(function() {
            const editor = new toastui.Editor({
                el: document.querySelector('#editor'),
                height: '400px',
                initialEditType: 'wysiwyg',
                previewStyle: 'vertical'
            });

            $('#submitBtn').on('click', function () {
                const formData = new FormData();
                const postData = {
                    parentPostId: "${parentPost.postId}",
                    postTitle: $('#postTitle').val(),
                    postContent: editor.getHTML(),
                    postPassword: $('#postPassword').val(),
                    postNotice: 0
                };
                
                if (!postData.postPassword.trim()) {
                    alert("비밀번호를 입력해주세요.");
                    $('#postPassword').focus();
                    return;
                }

                formData.append('postDto', new Blob([JSON.stringify(postData)], { type: 'application/json' }));

                $.ajax({
                    url: '/api/post/createPost',
                    type: 'POST',
                    data: formData,
                    processData: false,
                    contentType: false,
                    success: (res) => {
                        alert(res.message);
                        window.location.href = res.data;
                    },
                    error: (xhr) => {
                        alert('답글 등록 실패: ' + (xhr.responseJSON ? xhr.responseJSON.message : "서버 오류"));
                    }
                });
            });
        });
    </script>
</body>
</html>