<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="jakarta.tags.core" prefix="c"%>
<!DOCTYPE html>
<html lang="ko">
<head>
    <meta charset="UTF-8">
    <title>공지사항 삭제</title>
</head>
<body>
    <div class="container-fluid">
        <div class="row justify-content-center mt-5">
            <div class="col-md-6">
                <div class="card card-danger shadow-sm">
                    <div class="card-header"><h3 class="card-title">공지사항 삭제</h3></div>
                    <form id="deleteForm">
                        <div class="card-body">
                            <p>공지사항을 삭제하시려면 비밀번호를 입력해주세요.</p>
                            <p><strong>제목:</strong> ${notice.noticeTitle}</p>
                            <div class="mb-3">
                                <label for="password" class="form-label">비밀번호</label>
                                <input type="password" id="password" class="form-control" required>
                            </div>
                        </div>
                        <div class="card-footer text-end">
                            <a href="/notice/getNoticeDetail?noticeId=${notice.noticeId}" class="btn btn-secondary">취소</a>
                            <button type="submit" class="btn btn-danger">삭제 확인</button>
                        </div>
                    </form>
                </div>
            </div>
        </div>
    </div>
    <script>
        $(document).ready(function() {
            $('#deleteForm').on('submit', function(e) {
                e.preventDefault();
                const password = $('#password').val();
                if (!password) {
                    alert("비밀번호를 입력해주세요.");
                    return;
                }
                const url = "/api/notice/delete/" + "${notice.noticeId}" + "?password=" + encodeURIComponent(password);
                
                $.ajax({
                    url: url,
                    type: 'POST',
                    success: function(response) {
                        if (response.redirectUrl) {
                            window.location.href = response.redirectUrl;
                        }
                    },
                    error: function(xhr) {
                        const errorMsg = xhr.responseJSON ? xhr.responseJSON.error : "서버 오류";
                        alert('삭제 실패: ' + errorMsg);
                    }
                });
            });
        });
    </script>
</body>
</html>