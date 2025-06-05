<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="jakarta.tags.core" prefix="c"%>
<%@ taglib uri="jakarta.tags.fmt" prefix="fmt"%>
<%@ taglib uri="jakarta.tags.functions" prefix="fn"%>
<!DOCTYPE html>
<html lang="ko">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>게시물 수정</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css" rel="stylesheet">
    <link rel="stylesheet" href="https://uicdn.toast.com/editor/latest/toastui-editor.min.css" />
    <script src="https://uicdn.toast.com/editor/latest/toastui-editor-all.min.js"></script>
    <style>
        @import url('https://fonts.googleapis.com/css2?family=Noto+Sans+KR:wght@400;500;700&display=swap');
        .post-container {
         	width: 100%;
         	margin: 0 auto;
         	padding: 20px;
         	font-family: 'Noto Sans KR', Arial, sans-serif; 
         }
        .form-label {
         	font-weight: 500;
         	margin-bottom: 5px; 
         }
        .form-select, .form-control {
         	padding: 8px; 
         	border: 1px solid #e2e8f0; 
         	border-radius: 4px; 
         	width: 100%; 
         }
        .post-btn-primary { 
        	padding: 8px 16px; 
        	background: #3182ce; 
        	color: white; 
        	border: none; 
        	border-radius: 4px; 
        	cursor: pointer; 
        	}
        .post-btn-primary:hover { 
        	background: #2b6cb0; 
        }
        .row { 
        	display: flex; 
        	flex-wrap: wrap; 
        	margin: 0 -15px; 
        }
        .col-md-3, .col-md-6 {
        	 padding: 0 15px; 
        }
        .col-md-3 {
        	 flex: 0 0 25%; 
        	 max-width: 25%; 
        }
        .col-md-6 { 
       	 	flex: 0 0 50%; 
        	max-width: 50%; 
        }
        .toast-editor {
        	 border: 1px solid #e2e8f0; 
        	 border-radius: 4px; 
        }
        #editor { 
        	width: 100%; 
        }
    </style>
</head>
<body>
<div class="post-container mt-5">
    <h2 class="text-center h2">게시물 수정</h2>
    <form id="postForm" action="/post/update" method="post" enctype="multipart/form-data">
        <input type="hidden" name="postId" value="${post.postId}">
        <div class="row mb-3">
            <div class="col-md-6">
                <label for="authorId" class="form-label">ID</label>
                <input type="text" class="form-control" id="authorId" name="authorId" value="${post.authorId}" readonly>
                <span class="form-text">ID: ${post.authorId}</span>
            </div>
            <div class="col-md-6">
                <label for="userName" class="form-label">작성자</label>
                <input type="text" class="form-control" id="userName" value="${post.userName}" readonly>
            </div>
        </div>
        <div class="row mb-3">
            <div class="col-md-6">
                <label for="postTitle" class="form-label">제목</label>
                <input type="text" class="form-control" id="postTitle" name="postTitle" value="${post.postTitle}" required>
            </div>
        </div>
        <div class="row mb-3">
            <div class="col-md-6">
                <label for="content" class="form-label">내용</label>
                <textarea id="content" name="postContent" style="display: none;">${post.postContent}</textarea>
                <div id="editor" class="toast-editor"></div>
            </div>
        </div>
        <div class="row mb-3">
            <div class="col-md-6">
                <label for="postPassword" class="form-label">비밀번호</label>
                <input type="password" class="form-control" id="postPassword" name="postPassword" required>
            </div>
        </div>
        <div class="row mb-3">
            <div class="col-md-6">
                <label for="postFile" class="form-label">첨부파일</label>
                <input type="file" class="form-control" id="postFile" name="postFile">
                <c:if test="${not empty post.existingFilePath}">
                    <p>현재 첨부파일: <a href="${post.existingFilePath}" target="_blank">${fn:substringAfter(post.existingFilePath, '_')}</a></p>
                    <input type="hidden" name="existingFilePath" value="${post.existingFilePath}">
                </c:if>
            </div>
        </div>
        <div class="row mb-3">
            <div class="col-md-6">
                <label class="form-label">공지사항</label>
                <input type="checkbox" name="postNotice" id="postNotice" class="form-check-input" value="1"
                       <c:if test="${post.postNotice == 1}">checked</c:if>
                       <c:if test="${post.authorId != null && !post.authorId.startsWith('P')}">disabled</c:if>>
                <label class="form-check-label" for="postNotice">공지사항</label>
            </div>
        </div>
        <div class="text-end">
            <button type="submit" class="post-btn-primary">수정</button>
            <a href="/post/getPostDetail?postId=${post.postId}" class="btn btn-secondary">취소</a>
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
            initialValue: $("#content").val(),
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
                postId: $("input[name=postId]").val(),
                postTitle: $("#postTitle").val(),
                postContent: editor.getHTML(),
                postPassword: $("#postPassword").val(),
                authorId: $("#authorId").val(),
                postNotice: $("#postNotice").is(":checked") ? 1 : null,
                existingFilePath: $("input[name=existingFilePath]").val()
            };
            formData.append("post", new Blob([JSON.stringify(postData)], { type: "application/json" }));
            const fileInput = $("#postFile")[0];
            if (fileInput.files.length > 0) {
                formData.append("file", fileInput.files[0]);
            }
            $.ajax({
                url: "/api/post/update",
                type: "POST",
                data: formData,
                processData: false,
                contentType: false,
                success: (response) => {
                    if (response.message) {
                        alert(response.message);
                        window.location.href = response.redirectUrl;
                    } else {
                        alert("게시물 수정 실패: " + response.error);
                    }
                },
                error: (xhr) => {
                    console.error("Update post failed:", xhr);
                    alert("게시물 수정 중 오류");
                }
            });
        });
    });
</script>