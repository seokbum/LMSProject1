<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>핸드폰번호 변경</title>
    <!-- Bootstrap 5 CSS CDN -->
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css" rel="stylesheet">
    <!-- Google Fonts: Noto Sans KR -->
    <link href="https://fonts.googleapis.com/css2?family=Noto+Sans+KR:wght@400;500;700&display=swap" rel="stylesheet">
    <style>
        body {
            font-family: 'Noto Sans KR', sans-serif;
            background-color: #f8f9fa;
            display: flex;
            justify-content: center;
            align-items: center;
            min-height: 100vh;
            margin: 0;
            padding: 20px;
        }
        .container {
            max-width: 500px;
            background: #ffffff;
            padding: 30px;
            border-radius: 10px;
            box-shadow: 0 4px 20px rgba(0, 0, 0, 0.1);
        }
        h2 {
            font-weight: 700;
            color: #343a40;
            margin-bottom: 20px;
            text-align: center;
        }
        .form-control {
            border-radius: 6px;
            padding: 10px;
            font-size: 16px;
        }
        .form-control:focus {
            border-color: #007bff;
            box-shadow: 0 0 0 0.2rem rgba(0, 123, 255, 0.25);
        }
        .invalid-feedback, .valid-feedback {
            font-size: 14px;
            margin-top: 5px;
            display: block; /* 항상 표시되도록 설정 */
        }
        .btn {
            padding: 10px 20px;
            font-size: 16px;
            border-radius: 6px;
            font-weight: 500;
        }
        .btn-primary {
            background-color: #007bff;
            border-color: #007bff;
        }
        .btn-primary:hover {
            background-color: #0056b3;
            border-color: #0056b3;
        }
        .btn-secondary {
            background-color: #6c757d;
            border-color: #6c757d;
        }
        .btn-secondary:hover {
            background-color: #5a6268;
            border-color: #5a6268;
        }
        .btn-container {
            display: flex;
            justify-content: center;
            gap: 10px;
            margin-top: 20px;
        }
    </style>
</head>
<body>
    <div class="container">
        <h2>핸드폰번호 변경</h2>
        <div class="mb-3">
            <input type="text" class="form-control" id="phone" name="phone" onkeyup="phoneChk(this)" placeholder="핸드폰 번호 입력 (예: 010-1234-5678)">
            <div id="phoneValid" class="valid-feedback"></div>
        </div>
        <div class="btn-container">
            <button class="btn btn-primary" onclick="update()" type="button">변경</button>
            <button class="btn btn-secondary" onclick="closeWindow()" type="button">취소</button>
        </div>
    </div>

    <!-- Bootstrap 5 JS and Popper.js CDN -->
    <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/js/bootstrap.bundle.min.js"></script>
    <script type="text/javascript">
        document.addEventListener('DOMContentLoaded', function() {
            // 페이지 로드 후 phoneChk 초기 호출
            const phoneInput = document.querySelector("#phone");
            if (phoneInput) {
                phoneChk(phoneInput);
            }
        });

        function phoneChk(p) {
            const phoneVal = document.querySelector("#phoneValid");
            if (!phoneVal) {
                console.error("Error: #phoneValid element not found");
                return;
            }
            if (!valid(p.value, 'phone')) {
                phoneVal.className = 'invalid-feedback';
                phoneVal.innerHTML = '올바른 휴대폰 번호를 입력해주세요 (예: 010-1234-5678)';
            } else {
                phoneVal.className = 'valid-feedback';
                phoneVal.innerHTML = '유효한 번호입니다';
            }
        }

        function update() {
            const parentPhoneInput = window.opener?.document.querySelector("#phone");
            const newPhone = document.querySelector("#phone").value;

            if (!parentPhoneInput) {
                console.error("Error: Parent window's #phone element not found");
                alert("부모 창에서 전화번호 입력란을 찾을 수 없습니다.");
                window.close();
                return;
            }

            if (valid(newPhone, 'phone')) {
                parentPhoneInput.value = newPhone;
                window.close();
            } else {
                alert("!! 형식을 지키지 않았습니다. 올바른 핸드폰 번호를 입력해주세요 !!");
                window.close();
            }
        }

        function closeWindow() {
            window.close();
        }

        function valid(text, type) {
            if (type === 'phone') {
                const regex = /^(01[0126789])[ -]?\d{3,4}[ -]?\d{4}$/;
                return regex.test(text);
            }
            return false;
        }
    </script>
</body>
</html>