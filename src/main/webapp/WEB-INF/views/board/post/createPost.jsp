<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="jakarta.tags.core" prefix="c"%>
<!DOCTYPE html>
<html lang="ko">
<head>
    <meta charset="UTF-8">
    <title>문의게시판 작성</title>
    <link rel="stylesheet" href="https://uicdn.toast.com/editor/latest/toastui-editor.min.css" />
</head>
<body>
    <div class="container-fluid">
        <div class="card shadow-sm">
            <div class="card-header">
                <h3 class="card-title" style="font-weight: 600;">문의게시판 작성</h3>
            </div>
            <form id="postForm">
                <div class="card-body">
                    <div class="mb-3">
                        <label for="postTitle" class="form-label">제목</label>
                        <input type="text" class="form-control" id="postTitle" required>
                    </div>
                    <div class="mb-3">
                        <label class="form-label">내용</label>
                        <div id="editor"></div>
                    </div>
                    <div class="mb-3">
                        <label for="postPassword" class="form-label">비밀번호</label>
                        <input type="password" class="form-control" id="postPassword" required>
                    </div>
                    <div class="mb-3">
                        <label for="postFile" class="form-label">첨부 파일</label>
                        <input type="file" class="form-control" id="postFile">
                    </div>
                    <div class="form-check">
                        <input class="form-check-input" type="checkbox" id="postNotice" value="1">
                        <label class="form-check-label" for="postNotice">공지글로 등록</label>
                    </div>
                </div>
                <div class="card-footer text-end">
                    <a href="/post/getPosts" class="btn btn-secondary">취소</a>
                    <button type="button" id="submitBtn" class="btn btn-primary">등록</button>
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
                    postTitle: $('#postTitle').val(),
                    postContent: editor.getHTML(),
                    postPassword: $('#postPassword').val(),
                    postNotice: $('#postNotice').is(':checked') ? 1 : 0
                };
                
                if (!postData.postTitle.trim()) {
                    alert("제목을 입력해주세요.");
                    $('#postTitle').focus();
                    return;
                }
                if (!postData.postPassword.trim()) {
                    alert("비밀번호를 입력해주세요.");
                    $('#postPassword').focus();
                    return;
                }

                formData.append('postDto', new Blob([JSON.stringify(postData)], { type: 'application/json' }));
                
                const fileInput = $('#postFile')[0];
                if (fileInput.files.length > 0) {
                    formData.append('postFile', fileInput.files[0]);
                }

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
                        alert('게시물 등록 실패: ' + (xhr.responseJSON ? xhr.responseJSON.message : "서버 오류"));
                    }
                });
            });
        });
    </script>
</body>
</html>