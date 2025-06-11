<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="jakarta.tags.core" prefix="c"%>
<%@ taglib uri="jakarta.tags.functions" prefix="fn"%>
<!DOCTYPE html>
<html lang="ko">
<head>
    <meta charset="UTF-8">
    <title>문의게시판 수정</title>
    <link rel="stylesheet" href="https://uicdn.toast.com/editor/latest/toastui-editor.min.css"/>
</head>
<body>
<div class="container-fluid">
     <div class="card shadow-sm">
        <div class="card-header"><h3 class="card-title" style="font-weight: 600;">문의게시판 수정</h3></div>
        <form id="postForm">
            <input type="hidden" id="postId" value="${post.postId}">
            <div class="card-body">
                <div class="mb-3">
                    <label for="postTitle" class="form-label">제목</label>
                    <input type="text" class="form-control" id="postTitle" value="${fn:escapeXml(post.postTitle)}">
                    <div class="invalid-feedback" id="postTitle-error"></div>
                </div>
                <div class="mb-3">
                    <label class="form-label">내용</label>
                    <div id="postContentSource" style="display: none;"><c:out value="${post.postContent}" escapeXml="false"/></div>
                    <div id="editor"></div>
                    <div class="invalid-feedback d-block" id="postContent-error"></div>
                </div>
                <div class="mb-3">
                    <label for="postPassword" class="form-label">비밀번호</label>
                    <input type="password" class="form-control" id="postPassword" placeholder="수정을 위해 비밀번호를 다시 입력하세요">
                    <div class="invalid-feedback" id="postPassword-error"></div>
                </div>
                <div class="mb-3">
                    <label for="postFile" class="form-label">첨부 파일</label>
                    <c:if test="${not empty post.existingFilePath}">
                        <div class="alert alert-light border p-2 mb-2 d-flex justify-content-between align-items-center">
                            <span>현재 파일: ${fn:substringAfter(post.existingFilePath, '_')}</span>
                            <div class="form-check">
                                <input class="form-check-input" type="checkbox" id="removeFile" value="true">
                                <label class="form-check-label" for="removeFile">파일 삭제</label>
                            </div>
                        </div>
                    </c:if>
                    <input type="file" class="form-control" id="postFile">
                </div>
                <c:if test="${fn:startsWith(currentAuthorId, 'P')}">
                    <div class="form-check">
                        <input class="form-check-input" type="checkbox" id="postNotice" value="1" <c:if test="${post.postNotice == 1}">checked</c:if>>
                        <label class="form-check-label" for="postNotice">공지글로 등록</label>
                    </div>
                </c:if>
            </div>
            <div class="card-footer text-end">
                <a href="/post/getPostDetail?postId=${post.postId}" class="btn btn-secondary">취소</a>
                <button type="button" id="submitBtn" class="btn btn-primary">수정 완료</button>
            </div>
        </form>
    </div>
</div>
<script src="https://ajax.googleapis.com/ajax/libs/jquery/3.6.0.min.js"></script>
<script src="https://uicdn.toast.com/editor/latest/toastui-editor-all.min.js"></script>
<script>
    $(document).ready(function () {
        const initialContent = $('#postContentSource').html();
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
                $("#" + field + "-error").text(errors[field]).show();
            }
        }

        $('#submitBtn').on('click', function () {
            clearErrors();
            const formData = new FormData();
            const postData = {
                postId: $('#postId').val(),
                postTitle: $('#postTitle').val(),
                postContent: editor.getHTML(),
                postPassword: $('#postPassword').val(),
                postNotice: $('#postNotice').is(':checked') ? 1 : 0
            };

            formData.append("postDto", new Blob([JSON.stringify(postData)], { type: "application/json" }));
            formData.append("removeFile", $('#removeFile').is(':checked'));
            
            const fileInput = $('#postFile')[0];
            if (fileInput.files.length > 0) {
                formData.append("postFile", fileInput.files[0]);
            }

            $.ajax({
                url: '/api/post/updatePost',
                type: 'POST',
                data: formData,
                processData: false,
                contentType: false,
                success: function (res) {
                    window.location.href = res.data;
                },
                error: function (xhr) {
                    if (xhr.status === 400 && xhr.responseJSON && xhr.responseJSON.data) {
                        displayErrors(xhr.responseJSON.data);
                    } else {
                        alert(xhr.responseJSON ? xhr.responseJSON.message : "요청 처리 중 오류가 발생했습니다.");
                    }
                }
            });
        });
    });
</script>
</body>
</html>