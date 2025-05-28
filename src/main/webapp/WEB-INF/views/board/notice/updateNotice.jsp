<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>
<!DOCTYPE html>
<html lang="ko">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>공지사항 수정</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css" rel="stylesheet">
    <script src="https://ajax.googleapis.com/ajax/libs/jquery/3.7.1/jquery.min.js"></script>
    <link rel="stylesheet" href="https://uicdn.toast.com/editor/latest/toastui-editor.min.css" />
    <script src="https://uicdn.toast.com/editor/latest/toastui-editor-all.min.js"></script>
    <style>
        @import url('https://fonts.googleapis.com/css2?family=Noto+Sans+KR:wght@400;500;700&display=swap');
        .notice-container {
            width: 100%;
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
        .notice-btn-primary {
            padding: 10px 20px;
            background: #3182ce;
            color: white;
            border: none;
            border-radius: 4px;
            cursor: pointer;
            font-size: 16px;
        }
        .notice-btn-primary:hover {
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
<div class="notice-container mt-5">
    <h2 class="text-center h2">공지사항 수정</h2>
    <form id="noticeForm" action="update" method="post" enctype="multipart/form-data">
        <input type="hidden" name="noticeId" value="${notice.noticeId}">
        <div class="mb-3">
            <label for="noticeTitle" class="form-label">제목</label>
            <input type="text" class="form-control" id="noticeTitle" name="noticeTitle" value="${notice.noticeTitle}" required>
        </div>
        <div class="mb-3">
            <label for="content" class="form-label">내용</label>
            <textarea id="content" name="noticeContent" style="display: none;"></textarea>
            <div id="editor" class="toast-editor">${fn:escapeXml(notice.noticeContent)}</div>
        </div>
        <div class="mb-3">
            <label for="noticePassword" class="form-label">비밀번호</label>
            <input type="password" class="form-control" id="noticePassword" name="noticePassword" required>
        </div>
        <div class="mb-3">
            <label for="noticeFile" class="form-label">첨부파일</label>
            <input type="file" class="form-control" id="noticeFile" name="noticeFile">
            <c:if test="${not empty notice.noticeFile}">
                <p>현재 첨부파일: <a href="${notice.noticeFile}" target="_blank">${fn:substringAfter(notice.noticeFile, '/dist/assets/upload/')}</a></p>
            </c:if>
        </div>
        <div class="text-end">
            <button type="submit" class="notice-btn-primary">수정</button>
            <a href="getNoticeDetail?noticeId=${notice.noticeId}" class="btn btn-secondary">취소</a>
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
            initialValue: $('#editor').html(), // 기존 내용을 초기값으로 설정
            previewStyle: 'vertical',
            hooks: {
                addImageBlobHook: (blob, callback) => {
                    const formData = new FormData();
                    formData.append('file', blob);
                    $.ajax({
                        url: '/api/notice/uploadImage',
                        type: 'POST',
                        data: formData,
                        processData: false,
                        contentType: false,
                        success: (response) => {
                            callback(response.url, 'image');
                        },
                        error: (err) => {
                            console.error('Image upload failed:', err);
                        }
                    });
                    return false;
                }
            }
        });

        $('#noticeForm').on('submit', function () {
            $('#content').val(editor.getHTML());
        });
    });
</script>
</body>
</html>