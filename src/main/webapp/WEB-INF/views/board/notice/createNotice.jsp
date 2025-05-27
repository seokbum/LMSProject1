<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<c:set var="path" value="${pageContext.request.contextPath}"/>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>공지사항 등록</title>
    <!-- Toast UI Editor CSS -->
    <link rel="stylesheet" href="https://uicdn.toast.com/editor/latest/toastui-editor.min.css" />
    <!-- Toast UI Editor JS -->
    <script src="https://uicdn.toast.com/editor/latest/toastui-editor-all.min.js"></script>
    <style>
        .toastui-editor-defaultUI { border: 1px solid #ddd; }
        .container { max-width: 800px; }
    </style>
</head>
<body>
    <div class="container mt-5">
        <h2 class="text-center fs-1">공지사항 글쓰기</h2>
        
        <c:if test="${not empty msg}">
            <div class="alert alert-danger">${msg}</div>
            <% request.removeAttribute("msg"); %>
        </c:if>
        
        <form action="/notice/write" method="post" enctype="multipart/form-data" name="f" onsubmit="return submitForm();">
            <table class="table">
                <tr>
                    <td>글쓴이 (ID)</td>
                    <td>
                        <input type="text" name="writer_id" class="form-control" value="${sessionScope.loginId}" readonly>
                    </td>
                </tr>
                <tr>
                    <td>작성자 이름</td>
                    <td>
                        <input type="text" name="writer_name" class="form-control" value="${sessionScope.writerName}" readonly>
                    </td>
                </tr>
                <tr>
                    <td>비밀번호</td>
                    <td><input type="password" name="notice_password" class="form-control" required></td>
                </tr>
                <tr>
                    <td>제목</td>
                    <td><input type="text" name="notice_title" class="form-control" value="${param.notice_title}"></td>
                </tr>
                <tr>
                    <td>내용</td>
                    <td>
                        <div id="editor"></div>
                        <textarea name="notice_content" id="notice_content" style="display:none;">${param.notice_content}</textarea>
                    </td>
                </tr>
                <tr>
                    <td>첨부파일</td>
                    <td><input type="file" name="notice_file" class="form-control"></td>
                </tr>
                <tr>
                    <td colspan="2" class="text-end">
                        <button type="submit" class="btn btn-primary">게시물 등록</button>
                        <a href="/notice/getNotices" class="btn btn-secondary">목록</a>
                    </td>
                </tr>
            </table>
        </form>
    </div>
    
    <script>
        // Toast UI Editor 인스턴스 생성
        const editor = new toastui.Editor({
            el: document.querySelector('#editor'),
            height: '400px',
            initialEditType: 'wysiwyg',
            previewStyle: 'vertical',
            initialValue: document.getElementById('notice_content').value,
            hooks: {
                addImageBlobHook: (blob, callback) => {
                    uploadImage(blob, callback);
                    return false;
                }
            }
        });
        
        // 이미지 업로드 AJAX
        function uploadImage(blob, callback) {
            const formData = new FormData();
            formData.append("file", blob);
            $.ajax({
                url: "${path}/notice/uploadImage",
                type: "POST",
                data: formData,
                processData: false,
                contentType: false,
                success: function(response) {
                    callback(response.url, '이미지'); // 응답에서 URL 추출
                },
                error: function(xhr, status, error) {
                    console.error("Image upload error:", error);
                    alert("이미지 업로드에 실패했습니다.");
                }
            });
        }
        
        // 폼 제출 전 검증
        function submitForm() {
            const content = editor.getHTML();
            document.getElementById('notice_content').value = content;
            
            const f = document.f;
            if (!f.writer_id.value.trim()) {
                alert("글쓴이 ID를 확인하세요.");
                return false;
            }
            if (!f.writer_name.value.trim()) {
                alert("작성자 이름을 확인하세요.");
                return false;
            }
            if (!f.notice_password.value.trim()) {
                alert("비밀번호를 입력하세요.");
                f.notice_password.focus();
                return false;
            }
            if (!f.notice_title.value.trim()) {
                alert("제목을 입력하세요.");
                f.notice_title.focus();
                return false;
            }
            if (!content.trim()) {
                alert("내용을 입력하세요.");
                return false;
            }
            return true;
        }
    </script>
</body>
</html>