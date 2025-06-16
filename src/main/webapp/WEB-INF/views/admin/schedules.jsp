<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" isELIgnored="false"%>
<%@ taglib uri="jakarta.tags.core" prefix="c"%>
<%@ taglib uri="jakarta.tags.fmt" prefix="fmt"%>
<%@ taglib uri="jakarta.tags.functions" prefix="fn"%>

<!DOCTYPE html>
<html lang="ko">
<head>
    <meta charset="UTF-8">
    <title>학사일정 관리</title>
    <link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css" />
    <style>
        ul.timeline::before {
            content: none !important;
        }

        body {
            font-family: "Noto Sans KR", sans-serif;
            background-color: #f7fafc;
        }

        .container {
            max-width: 1200px;
            margin: 0 auto;
            padding: 20px;
        }

        .card {
            background: white;
            border-radius: 8px;
            box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
            padding: 20px;
            margin-bottom: 20px;
        }

        .table {
            width: 100%;
            border-collapse: collapse;
            margin-top: 10px;
        }

        .table th,
        .table td {
            border: 1px solid #e2e8f0;
            padding: 10px;
            text-align: left;
        }

        .table th {
            background: #edf2f7;
            font-weight: 600;
        }

        .btn {
            padding: 8px 16px;
            border-radius: 4px;
            transition: background 0.2s;
        }

        .btn-primary {
            background: #3182ce;
            color: white;
        }

        .btn-primary:hover {
            background: #2b6cb0;
        }

        .btn-secondary {
            background: #e2e8f0;
            color: #4a5568;
        }

        .btn-secondary:hover {
            background: #cbd5e0;
        }

        .message {
            padding: 1rem;
            margin-bottom: 1.25rem;
            border-radius: 0.5rem;
            font-size: 1rem;
            font-weight: 500;
            text-align: center;
            box-shadow: 0 1px 2px rgba(0, 0, 0, 0.05);
            display: none;
        }

        .message.success {
            background-color: #d4edda;
            color: #155724;
            border: 1px solid #c3e6cb;
        }

        .message.error {
            background-color: #f8d7da;
            color: #721c24;
            border: 1px solid #f5c6cb;
        }

        .form-section h2,
        .list-section h2 {
            font-size: 1.75rem;
            font-weight: 600;
            color: #2d3748;
            margin-bottom: 1.5rem;
            padding-bottom: 0.5rem;
            border-bottom: 1px solid #e2e8f0;
        }

        .form-group {
            margin-bottom: 1rem;
        }

        .form-group label {
            display: block;
            font-size: 0.95rem;
            font-weight: 500;
            color: #4a5568;
            margin-bottom: 0.4rem;
        }

        .form-group input[type="text"],
        .form-group input[type="date"],
        .form-group textarea,
        .form-group select {
            width: calc(100% - 2px);
            padding: 0.75rem;
            border: 1px solid #cbd5e0;
            border-radius: 0.375rem;
            font-size: 1rem;
            color: #2d3748;
            box-sizing: border-box;
            transition: border-color 0.2s ease-in-out, box-shadow 0.2s ease-in-out;
        }

        .form-group input[type="text"]:focus,
        .form-group input[type="date"]:focus,
        .form-group textarea:focus,
        .form-group select:focus {
            border-color: #4299e1;
            box-shadow: 0 0 0 3px rgba(66, 153, 225, 0.5);
            outline: none;
        }

        .form-group textarea {
            min-height: 80px;
            resize: vertical;
        }

        #scheduleForm button {
            cursor: pointer;
            font-size: 1rem;
            font-weight: 500;
            margin-top: 1rem;
            padding: 0.75rem 1.5rem;
            border-radius: 0.375rem;
            border: none;
            transition: background-color 0.2s ease-in-out, box-shadow 0.2s ease-in-out,
                       color 0.2s ease-in-out, border-color 0.2s ease-in-out;
        }

        #saveButton.btn-primary {
            background-color: transparent;
            color: #3182ce;
            border: 1px solid #3182ce;
            box-shadow: none;
        }

        #saveButton.btn-primary:hover {
            background-color: #3182ce;
            color: white;
            box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
        }

        #resetButton.btn-secondary {
            background-color: transparent;
            color: #a0aec0;
            border: 1px solid #a0aec0;
            box-shadow: none;
        }

        #resetButton.btn-secondary:hover {
            background-color: #a0aec0;
            color: white;
            box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
        }

        .edit-btn.btn,
        .delete-btn.btn {
            padding: 0.5rem 0.8rem;
            font-size: 0.875rem;
            border-radius: 0.25rem;
            cursor: pointer;
            border: none;
            transition: background-color 0.2s ease-in-out, color 0.2s ease-in-out,
                       border-color 0.2s ease-in-out;
        }

        .edit-btn.btn {
            background-color: transparent;
            color: #f6ad55;
            border: 1px solid #f6ad55;
            margin-right: 0.5rem;
            box-shadow: none;
        }

        .edit-btn.btn:hover {
            background-color: #f6ad55;
            color: white;
            box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
        }

        .delete-btn.btn {
            background-color: transparent;
            color: #ef4444;
            border: 1px solid #ef4444;
            box-shadow: none;
        }

        .delete-btn.btn:hover {
            background-color: #ef4444;
            color: white;
            box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
        }

        .schedule-container {
            padding: 20px;
            background-color: #fff;
            border-radius: 8px;
            box-shadow: 0 0 10px rgba(0, 0, 0, 0.05);
            margin: 20px;
        }

        .form-group select {
            width: 100%;
            padding: 0.75rem;
            border: 1px solid #cbd5e0;
            border-radius: 0.375rem;
            font-size: 1rem;
            color: #2d3748;
            box-sizing: border-box;
            transition: border-color 0.2s ease-in-out, box-shadow 0.2s ease-in-out;
            background-color: white;
        }

        .form-group select:focus {
            border-color: #4299e1;
            box-shadow: 0 0 0 3px rgba(66, 153, 225, 0.5);
            outline: none;
        }

        #scheduleTable td:last-child {
            display: flex;
            gap: 5px;
            align-items: center;
            justify-content: flex-start;
        }
    </style>
</head>
<body>
    <div class="app-content-header">
        <div class="container-fluid">
            <div class="row">
                <div class="col-sm-6">
                    <h3 class="mb-0">학사일정 관리</h3>
                </div>
                <div class="col-sm-6">
                    <ol class="breadcrumb float-sm-end">
                        <li class="breadcrumb-item"><a href="#">Home</a></li>
                        <li class="breadcrumb-item active" aria-current="page">학사일정</li>
                    </ol>
                </div>
            </div>
        </div>
    </div>

    <div class="app-content">
        <div class="container-fluid">
            <div class="schedule-container">
                <div id="message" class="message"></div>

                <div class="form-section card">
                    <h2>일정 추가/수정</h2>
                    <form id="scheduleForm">
                        <input type="hidden" id="scheduleId" name="scheduleId">
                        <div class="form-group">
                            <label for="scheduleTitle">제목:</label>
                            <input type="text" id="scheduleTitle" name="scheduleTitle" required>
                        </div>
                        <div class="form-group">
                            <label for="scheduleDescription">설명:</label>
                            <textarea id="scheduleDescription" name="scheduleDescription"></textarea>
                        </div>
                        <div class="form-group">
                            <label for="scheduleStartDate">시작일:</label>
                            <input type="date" id="scheduleStartDate" name="scheduleStartDate" required>
                        </div>
                        <div class="form-group">
                            <label for="scheduleEndDate">종료일:</label>
                            <input type="date" id="scheduleEndDate" name="scheduleEndDate" required>
                        </div>
                        <div class="form-group">
                            <label for="semesterType">학기/방학 구분:</label>
                            <select id="semesterType" name="semesterType" required>
                                <option value="">선택하세요</option>
                                <option value="1학기">1학기</option>
                                <option value="여름방학">여름방학</option>
                                <option value="2학기">2학기</option>
                                <option value="겨울방학">겨울방학</option>
                            </select>
                        </div>
                        <button type="submit" id="saveButton" class="btn btn-primary">저장</button>
                        <button type="type" id="resetButton" class="btn btn-secondary">초기화</button>
                    </form>
                </div>

                <div class="list-section card">
                    <h2>일정 목록</h2>
                    <div class="form-group" style="width: 200px; margin-bottom: 20px;">
                        <label for="filterSemesterType">학기/방학 필터:</label>
                        <select id="filterSemesterType" class="form-control">
                            <option value="전체">전체</option>
                            <option value="1학기">1학기</option>
                            <option value="여름방학">여름방학</option>
                            <option value="2학기">2학기</option>
                            <option value="겨울방학">겨울방학</option>
                        </select>
                    </div>
                    <table id="scheduleTable" class="table">
                        <thead>
                            <tr>
                                <th>ID</th>
                                <th>제목</th>
                                <th>설명</th>
                                <th>시작일</th>
                                <th>종료일</th>
                                <th>학기/방학</th>
                                <th>관리</th>
                            </tr>
                        </thead>
                        <tbody></tbody>
                    </table>
                </div>
            </div>
        </div>
    </div>

    <script>
        const API_BASE_URL = "/api/admin/schedule";
        const scheduleForm = document.getElementById("scheduleForm");
        const scheduleTableBody = document.querySelector("#scheduleTable tbody");
        const scheduleIdInput = document.getElementById("scheduleId");
        const scheduleTitleInput = document.getElementById("scheduleTitle");
        const scheduleDescriptionInput = document.getElementById("scheduleDescription");
        const scheduleStartDateInput = document.getElementById("scheduleStartDate");
        const scheduleEndDateInput = document.getElementById("scheduleEndDate");
        const semesterTypeSelect = document.getElementById("semesterType");
        const filterSemesterTypeSelect = document.getElementById("filterSemesterType");
        const saveButton = document.getElementById("saveButton");
        const resetButton = document.getElementById("resetButton");
        const messageDiv = document.getElementById("message");

        function showMessage(msg, isError = false) {
            messageDiv.textContent = msg;
            messageDiv.className = "message " + (isError ? "error" : "success");
            messageDiv.style.display = "block";
            setTimeout(() => {
                messageDiv.style.display = "none";
            }, 3000);
        }

        function formatDate(dateString) {
            if (!dateString) return "";
            const date = new Date(dateString);
            const year = date.getFullYear();
            const month = String(date.getMonth() + 1).padStart(2, "0");
            const day = String(date.getDate()).padStart(2, "0");
            return year + "-" + month + "-" + day;
        }

        async function fetchSchedules() {
            const filterValue = filterSemesterTypeSelect.value;
            let url = API_BASE_URL;
            if (filterValue && filterValue !== "전체") {
                url += "?semesterType=" + encodeURIComponent(filterValue);
            }

            try {
                const response = await fetch(url);
                const apiResponse = await response.json();

                if (apiResponse.success) {
                    const schedules = apiResponse.data.schedules;
                    scheduleTableBody.innerHTML = "";
                    schedules.forEach(schedule => {
                        const row = scheduleTableBody.insertRow();
                        row.insertCell(0).textContent = schedule.scheduleId;
                        row.insertCell(1).textContent = schedule.scheduleTitle;
                        row.insertCell(2).textContent = schedule.scheduleDescription;
                        row.insertCell(3).textContent = formatDate(schedule.scheduleStartDate);
                        row.insertCell(4).textContent = formatDate(schedule.scheduleEndDate);
                        row.insertCell(5).textContent = schedule.semesterType;

                        const actionsCell = row.insertCell(6);
                        const editButton = document.createElement("button");
                        editButton.textContent = "수정";
                        editButton.className = "edit-btn btn";
                        editButton.onclick = () => loadScheduleForEdit(schedule.scheduleId);

                        const deleteButton = document.createElement("button");
                        deleteButton.textContent = "삭제";
                        deleteButton.className = "delete-btn btn";
                        deleteButton.onclick = () => deleteSchedule(schedule.scheduleId);

                        actionsCell.appendChild(editButton);
                        actionsCell.appendChild(deleteButton);
                    });
                } else {
                    showMessage(apiResponse.message, true);
                }
            } catch (error) {
                console.error("Error fetching schedules:", error);
                showMessage("일정 목록을 불러오는데 실패했습니다.", true);
            }
        }

        scheduleForm.addEventListener("submit", async (event) => {
            event.preventDefault();

            const scheduleId = scheduleIdInput.value;
            const scheduleData = {
                scheduleTitle: scheduleTitleInput.value,
                scheduleDescription: scheduleDescriptionInput.value,
                scheduleStartDate: scheduleStartDateInput.value,
                scheduleEndDate: scheduleEndDateInput.value,
                semesterType: semesterTypeSelect.value
            };

            if (!scheduleData.semesterType) {
                showMessage("학기/방학 구분을 선택해주세요.", true);
                return;
            }

            let url = API_BASE_URL;
            let method = "POST";

            if (scheduleId) {
                url = API_BASE_URL + "/" + scheduleId;
                method = "PUT";
            }

            try {
                const response = await fetch(url, {
                    method: method,
                    headers: {
                        "Content-Type": "application/json"
                    },
                    body: JSON.stringify(scheduleData)
                });
                const apiResponse = await response.json();

                if (apiResponse.success) {
                    showMessage(apiResponse.message || (scheduleId ? "일정 수정 성공!" : "일정 추가 성공!"));
                    resetForm();
                    fetchSchedules();
                } else {
                    showMessage(apiResponse.message, true);
                }
            } catch (error) {
                console.error("Error saving schedule:", error);
                showMessage((scheduleId ? "일정 수정 중 오류 발생!" : "일정 추가 중 오류 발생!"), true);
            }
        });

        async function loadScheduleForEdit(id) {
            try {
                const response = await fetch(API_BASE_URL + "/" + id);
                const apiResponse = await response.json();

                if (apiResponse.success) {
                    const schedule = apiResponse.data;
                    scheduleIdInput.value = schedule.scheduleId;
                    scheduleTitleInput.value = schedule.scheduleTitle;
                    scheduleDescriptionInput.value = schedule.scheduleDescription;
                    scheduleStartDateInput.value = formatDate(schedule.scheduleStartDate);
                    scheduleEndDateInput.value = formatDate(schedule.scheduleEndDate);
                    semesterTypeSelect.value = schedule.semesterType;
                    saveButton.textContent = "수정";
                } else {
                    showMessage(apiResponse.message, true);
                }
            } catch (error) {
                console.error("Error loading schedule for edit:", error);
                showMessage("일정 정보를 불러오는데 실패했습니다.", true);
            }
        }

        async function deleteSchedule(id) {
            if (!confirm("정말로 이 일정을 삭제하시겠습니까?")) {
                return;
            }

            try {
                const response = await fetch(API_BASE_URL + "/" + id, {
                    method: "DELETE"
                });

                if (response.status === 204) {
                    showMessage("일정 삭제 성공!");
                    fetchSchedules();
                } else {
                    const apiResponse = await response.json();
                    showMessage(apiResponse.message || "일정 삭제 실패!", true);
                }
            } catch (error) {
                console.error("Error deleting schedule:", error);
                showMessage("일정 삭제 중 오류!", true);
            }
        }

        function resetForm() {
            scheduleForm.reset();
            scheduleIdInput.value = "";
            saveButton.textContent = "저장";
            semesterTypeSelect.value = "";
        }

        resetButton.addEventListener("click", resetForm);
        filterSemesterTypeSelect.addEventListener("change", fetchSchedules);

        document.addEventListener("DOMContentLoaded", fetchSchedules);
    </script>
</body>
</html>