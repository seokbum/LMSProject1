<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="jakarta.tags.core" prefix="c"%>
<%@ taglib uri="jakarta.tags.functions" prefix="fn"%>
<!DOCTYPE html>
<html lang="ko">
<head>
    <meta charset="UTF-8">
    <title>공지사항 수정</title>
    <link rel="stylesheet" href="https://uicdn.toast.com/editor/latest/toastui-editor.min.css" />
</head>
<body>
<div class="container-fluid">
     <div class="card shadow-sm">
        <div class="card-header"><h3 class="card-title fw-bold">공지사항 수정</h3></div>
        <form id="noticeForm">
            <input type="hidden" id="noticeId" value="${notice.noticeId}">
            <div class="card-body">
                <div class="mb-3">
                    <label for="noticeTitle" class="form-label">제목</label>
                    <input type="text" class="form-control" id="noticeTitle" value="${notice.noticeTitle}">
                    <div class="invalid-feedback" id="noticeTitle-error"></div>
                </div>
                <div class="mb-3">
                    <label class="form-label">내용</label>
                    <div id="noticeContentSource" style="display: none;"><c:out value="${notice.noticeContent}" escapeXml="false"/></div>
                    <div id="editor"></div>
                    <div class="invalid-feedback d-block" id="noticeContent-error"></div>
                </div>
                <div class="mb-3">
                    <label for="noticePassword" class="form-label">비밀번호</label>
                    <input type="password" class="form-control" id="noticePassword" placeholder="수정을 위해 비밀번호를 다시 입력하세요">
                    <div class="invalid-feedback" id="noticePassword-error"></div>
                </div>
                <div class="mb-3">
                    <label for="noticeFile" class="form-label">첨부파일</label>
                    <c:if test="${not empty notice.existingFilePath}">
                        <p class="form-text">현재 파일: <a href="/notice/download?filePath=${notice.existingFilePath}">${fn:substringAfter(notice.existingFilePath, '_')}</a></p>
                    </c:if>
                    <input type="file" class="form-control" id="noticeFile">
                </div>
            </div>
            <div class="card-footer text-end">
                <a href="/notice/getNoticeDetail?noticeId=${notice.noticeId}" class="btn btn-secondary">취소</a>
                <button type="submit" class="btn btn-primary">수정</button>
            </div>
        </form>
    </div>
</div>
<script src="https://ajax.googleapis.com/ajax/libs/jquery/3.6.0.min.js"></script>
<script src="https://uicdn.toast.com/editor/latest/toastui-editor-all.min.js"></script>
<script>
    $(document).ready(function () {
        const initialContent = $('#noticeContentSource').html();
        const editor = new toastui.Editor({
            el: document.querySelector('#editor'),
            height: '400px',
            initialEditType: 'wysiwyg',
            initialValue: initialContent
        });

        function clearErrors() {
            $(".form-control").removeClass("is-invalid");
            $(".invalid-feedback").text("");
        }

        function displayErrors(errors) {
            clearErrors();
            for (const field in errors) {
                $("#" + field).addClass("is-invalid");
                $("#" + field + "-error").text(errors[field]);
            }
        }

        $('#noticeForm').on('submit', function (e) {
            e.preventDefault();
            clearErrors();
            
            const formData = new FormData();
            const noticeData = {
                noticeId: $('#noticeId').val(),
                noticeTitle: $('#noticeTitle').val(),
                noticeContent: editor.getHTML(),
                noticePassword: $('#noticePassword').val()
            };
            formData.append('notice', new Blob([JSON.stringify(noticeData)], { type: 'application/json' }));
            
            const fileInput = $('#noticeFile')[0];
            if (fileInput.files.length > 0) {
                formData.append('file', fileInput.files[0]);
            }

            $.ajax({
                url: "/api/notice/update",
                type: "POST",
                data: formData,
                processData: false,
                contentType: false,
                success: function (response) {
                    if(response.redirectUrl) {
                       window.location.href = response.redirectUrl;
                    }
                },
                error: function (xhr) {
                    if (xhr.status === 400 && xhr.responseJSON && xhr.responseJSON.data) {
                        displayErrors(xhr.responseJSON.data);
                    } else {
                        alert('수정 실패: ' + (xhr.responseJSON ? xhr.responseJSON.error : "서버 오류"));
                    }
                }
            });
        });
    });
</script>
</body>
</html>