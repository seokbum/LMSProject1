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
    <script src="https://ajax.googleapis.com/ajax/libs/jquery/3.7.1/jquery.min.js"></script> <style>
        @import url('https://fonts.googleapis.com/css2?family=Noto+Sans+KR:wght@400;500;700&display=swap');
        .post-container {
            width: 100%;
            max-width: 800px; 
            margin: 0 auto;
            padding: 20px;
            font-family: 'Noto Sans KR', Arial, sans-serif; 
        }
        .form-label {
            font-weight: 500;
            margin-bottom: 5px; 
            font-size: 16px; 
        }
        .form-select, .form-control {
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
    <h2 class="text-center h2">게시물 수정</h2>
    <form id="postForm" action="update" method="post" enctype="multipart/form-data">
        <input type="hidden" name="postId" value="${post.postId}">
        
        <div class="mb-3">
            <label for="authorId" class="form-label">ID</label>
            <input type="text" class="form-control" id="authorId" name="authorId" value="${post.authorId}" readonly>
            <span class="form-text">ID: ${post.authorId}</span>
        </div>
        <div class="mb-3">
            <label for="userName" class="form-label">작성자</label>
            <input type="text" class="form-control" id="userName" value="${post.userName}" readonly>
        </div>

        <div class="mb-3">
            <label for="postTitle" class="form-label">제목</label>
            <input type="text" class="form-control" id="postTitle" name="postTitle" value="${post.postTitle}" required>
        </div>
        
        <div class="mb-3">
            <label for="content" class="form-label">내용</label>
            <textarea id="content" name="postContent" style="display: none;"></textarea>
            <div id="editor" class="toast-editor">${post.postContent}</div> 
        </div>
        
        <div class="mb-3">
            <label for="postPassword" class="form-label">비밀번호</label>
            <input type="password" class="form-control" id="postPassword" name="postPassword" required>
        </div>
        
        <div class="mb-3">
            <label for="postFile" class="form-label">첨부파일</label>
            <input type="file" class="form-control" id="postFile" name="postFile">
            <c:if test="${not empty post.existingFilePath}">
                <p id="currentFileDisplay">현재 첨부파일: 
                    <a href="${post.existingFilePath}" target="_blank">${fn:substringAfter(post.existingFilePath, '/dist/assets/upload/')}</a>
                    <input type="checkbox" id="removeFile" name="removeFile" class="form-check-input ms-2"> 파일 삭제
                </p>
            </c:if>
        </div>
        
        <div class="mb-3 form-check">
            <input type="checkbox" name="postNotice" id="postNotice" class="form-check-input" value="1"
                   <c:if test="${post.postNotice == 1}">checked</c:if>
                   <c:if test="${post.authorId != null && !post.authorId.startsWith('P')}">disabled</c:if>>
            <label class="form-check-label" for="postNotice">공지사항</label>
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
            initialValue: $('#editor').html(), 
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
                            if (response.success && response.data && response.data.url) {
                                callback(response.data.url, response.data.fileName || "image");
                            } else {
                                console.error("이미지 업로드 실패:", response.message || response.error);
                                alert("이미지 업로드 실패: " + (response.message || response.error || "알 수 없는 오류"));
                            }
                        },
                        error: (xhr) => {
                            console.error("Image upload failed:", xhr.responseJSON);
                            alert("이미지 업로드 오류");
                        }
                    });
                    return false;
                }
            }
        });

        $('#removeFile').change(function() {
            if ($(this).is(':checked')) {
                $('#postFile').prop('disabled', true);
                $('#postFile').val(''); 
            } else {
                $('#postFile').prop('disabled', false);
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
                postNotice: $("#postNotice").is(":checked") ? 1 : 0 
            };
            
            formData.append("post", new Blob([JSON.stringify(postData)], { type: "application/json" })); 
            
            const fileInput = $("#postFile")[0];
            const removeFileChecked = $("#removeFile").is(":checked");

            if (fileInput.files.length > 0 && !removeFileChecked) {
                formData.append("file", fileInput.files[0]);
            }

            if (removeFileChecked) {
                formData.append("removeFile", "true"); 
            }
            
            $.ajax({
                url: "/api/post/update",
                type: "POST",
                data: formData,
                processData: false, 
                contentType: false, 
                success: (response) => {
                    if (response.success) { 
                        alert(response.message);
                        
                        window.location.href = response.data || `/post/getPostDetail?postId=${postData.postId}`; 
                    } else {
                        alert("게시물 수정 실패: " + (response.message || response.error || "알 수 없는 오류"));
                    }
                },
                error: (xhr) => {
                    console.error("Update post failed:", xhr.responseJSON);
                    const errorMessage = xhr.responseJSON && xhr.responseJSON.message ? xhr.responseJSON.message : "알 수 없는 오류 발생";
                    alert("게시물 수정 중 오류: " + errorMessage);
                }
            });
        });
    });
</script>
</body>
</html>