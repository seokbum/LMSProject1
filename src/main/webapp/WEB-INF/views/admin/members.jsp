<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html lang="ko">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>회원 관리</title>
    
    <script src="https://ajax.googleapis.com/ajax/libs/jquery/3.7.1/jquery.min.js"></script>
    
    <style>
        body {
            font-family: 'Noto Sans KR', sans-serif;
            margin: 20px;
            background-color: #f7fafc;
            color: #2d3748; 
        }
        h1 {
            color: #333;
            margin-bottom: 25px;
            font-size: 1.8rem;
        }

        .card-container {
            background: white;
            border-radius: 8px;
            box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
            padding: 25px;
        }
        .card-header-custom {
            border-bottom: 1px solid #e2e8f0;
            padding-bottom: 15px;
            margin-bottom: 20px;
        }
        .card-title-custom {
            font-size: 1.5rem;
            font-weight: 600;
            color: #3182ce;
        }

        .member-search-form {
            margin-bottom: 25px;
            display: flex;
            align-items: center;
            flex-wrap: wrap;
            gap: 15px;
            padding: 15px;
            background-color: #edf2f7; 
            border-radius: 8px;
            box-shadow: inset 0 1px 3px rgba(0,0,0,0.05);
        }
        .member-search-form label {
            font-size: 1rem;
            font-weight: 600;
            color: #4a5568;
            white-space: nowrap;
        }
        .member-search-form input[type="text"],
        .member-search-form select {
            padding: 10px 12px;
            border: 1px solid #cbd5e0;
            border-radius: 6px;
            font-size: 1rem;
            color: #2d3748;
            min-width: 120px; 
        }
        .member-search-form input[type="text"]:focus,
        .member-search-form select:focus {
            border-color: #4299e1;
            box-shadow: 0 0 0 3px rgba(66, 153, 225, 0.2); 
            outline: none; 
        }
        .member-search-form .search-button {
            padding: 10px 20px;
            background-color: #3182ce;
            color: white;
            border: none;
            border-radius: 6px;
            cursor: pointer;
            font-size: 1rem;
            font-weight: 600;
            transition: background-color 0.2s ease, transform 0.1s ease;
            white-space: nowrap; 
        }
        .member-search-form .search-button:hover {
            background-color: #2b6cb0; 
            transform: translateY(-1px);
        }
        .member-search-form .search-button:active {
            transform: translateY(0); 
        }

        .member-table {
            width: 100%;
            border-collapse: separate;
            border-spacing: 0; 
            margin-top: 25px;
            box-shadow: 0 1px 3px rgba(0, 0, 0, 0.1); 
            border-radius: 8px; 
            overflow: hidden; 
        }
        .member-table th,
        .member-table td {
            border: 1px solid #e2e8f0;
            padding: 12px 15px;
            text-align: left;
        }
        .member-table th {
            background: #e2e8f0;
            font-weight: 700;
            color: #2d3748;
            white-space: nowrap;
        }
        .member-table tr:nth-child(even) { 
            background-color: #f9fbfd;
        }
        .member-table tbody tr:hover { 
            background-color: #f0f4f7;
            cursor: pointer;
        }
        .member-table td {
            color: #4a5568;
        }
        .member-table .delete-btn {
            padding: 8px 12px;
            font-size: 0.9rem;
            border-radius: 4px;
            background-color: transparent;
            color: #e53e3e;
            border: 1px solid #e53e3e;
            transition: all 0.2s ease;
        }
        .member-table .delete-btn:hover {
            background-color: #e53e3e;
            color: white;
            box-shadow: 0 1px 3px rgba(0, 0, 0, 0.1);
        }

        .member-pagination {
            margin-top: 30px;
            text-align: center;
            display: flex;
            justify-content: center;
            align-items: center;
            gap: 8px;
        }
        .member-pagination a,
        .member-pagination span {
            display: inline-flex;
            justify-content: center;
            align-items: center;
            min-width: 35px; 
            height: 35px;
            padding: 0 8px;
            border: 1px solid #cbd5e0;
            border-radius: 5px;
            text-decoration: none;
            color: #3182ce;
            transition: all 0.2s ease;
            font-weight: 500;
        }
        .member-pagination a:hover {
            background-color: #e6f0fa;
            border-color: #a7d3f2;
        }
        .member-pagination .current-page {
            background-color: #3182ce;
            color: white;
            border-color: #3182ce;
            font-weight: 700;
            cursor: default;
        }
        .member-pagination .current-page:hover {
            background-color: #3182ce; 
            border-color: #3182ce;
        }
    </style>
</head>
<body>
    <div class="app-content">
        <div class="container-fluid">
            <div class="card-container">
                <div class="card-header-custom">
                    <h3 class="card-title-custom">회원 목록</h3>
                </div>
                <div class="card-body-custom">
                    <div class="member-search-form">
                        <label for="searchMemberType">회원 유형:</label>
                        <select id="searchMemberType" name="searchMemberType">
                            <option value="ALL">전체</option>
                            <option value="STUDENT">학생</option>
                            <option value="PROFESSOR">교수</option>
                        </select>

                        <label for="searchFieldType">검색 유형:</label>
                        <select id="searchFieldType" name="searchFieldType">
                            <option value="ALL_FIELDS">전체 필드</option>
                            <option value="ID">아이디</option>
                            <option value="NAME">이름</option>
                            <option value="EMAIL">이메일</option>
                            <option value="PHONE">휴대폰 번호</option>
                        </select>
                        
                        <label for="searchKeyword">검색어:</label>
                        <input type="text" id="searchKeyword" name="searchKeyword" placeholder="검색어를 입력하세요">
                        
                        <button id="searchBtn" class="search-button">검색</button>
                    </div>

                    <table class="member-table">
                        <thead>
                            <tr>
                                <th>유형</th>
                                <th>아이디</th>
                                <th>이름</th>
                                <th>이메일</th>
                                <th>휴대폰 번호</th>
                                <th>관리</th>
                            </tr>
                        </thead>
                        <tbody id="memberList">
                            <tr><td colspan="6" style="text-align: center;">회원 정보를 불러오는 중...</td></tr>
                        </tbody>
                    </table>
                    <div class="member-pagination" id="pagination"></div>
                </div>
            </div>
        </div>
    </div>
    <script>
        let currentPage = 1; 

        $(document).ready(function() {
            loadMembers(currentPage);

            $("#searchBtn").on("click", function() {
                currentPage = 1; 
                loadMembers(currentPage);
            });

            $("#searchKeyword").on("keypress", function(e) {
                if (e.which === 13) { 
                    $("#searchBtn").trigger("click"); 
                    e.preventDefault(); 
                }
            });

            $(document).on("click", ".member-pagination a", function(e) {
                e.preventDefault(); 
                currentPage = $(this).data("page");
                loadMembers(currentPage);
            });

            $(document).on("click", ".delete-btn", function() {
                const memberId = $(this).data("id");
                const memberType = $(this).data("type");
                const confirmMessage = memberType === "학생" ? "이 학생을 정말로 삭제하시겠습니까?" : "이 교수를 정말로 삭제하시겠습니까?";

                if (confirm(confirmMessage)) {
                    deleteMember(memberId, memberType === "학생" ? "STUDENT" : "PROFESSOR");
                }
            });
        });

        function loadMembers(page) {
            const searchFieldType = $("#searchFieldType").val(); 
            const searchKeyword = $("#searchKeyword").val();   
            const searchMemberType = $("#searchMemberType").val(); 

            const searchParams = {
                currentPage: page,
                searchType: searchMemberType
            };

            if (searchFieldType === "ID") {
                searchParams.searchId = searchKeyword;
            } else if (searchFieldType === "NAME") {
                searchParams.searchName = searchKeyword;
            } else if (searchFieldType === "EMAIL") {
                searchParams.searchEmail = searchKeyword;
            } else if (searchFieldType === "PHONE") {
                searchParams.searchPhone = searchKeyword;
            } else { 
                searchParams.searchId = searchKeyword;
                searchParams.searchName = searchKeyword;
                searchParams.searchEmail = searchKeyword;
                searchParams.searchPhone = searchKeyword;
            }
            
            $.ajax({
                url: "/api/admin/members", 
                type: "GET",
                data: searchParams,
                success: function(response) {
                    if (response.success) {
                        renderMemberList(response.data.members);
                        renderPagination(response.data.pagination);
                    } else {
                        $("#memberList").html("<tr><td colspan='6' style='text-align: center; color: red;'>" + response.message + "</td></tr>");
                        $("#pagination").empty();
                    }
                },
                error: function(xhr, status, error) {
                    console.error("AJAX Error: ", status, error);
                    $("#memberList").html("<tr><td colspan='6' style='text-align: center; color: red;'>회원 정보를 불러오는 데 실패했습니다. 다시 시도해주세요.</td></tr>");
                    $("#pagination").empty();
                }
            });
        }

        function renderMemberList(members) {
            const $memberList = $("#memberList");
            $memberList.empty(); 

            if (members && members.length > 0) {
                members.forEach(function(member) {
                    const row = "<tr>" +
                                "<td>" + member.type + "</td>" +
                                "<td>" + member.id + "</td>" +
                                "<td>" + member.name + "</td>" +
                                "<td>" + (member.email || "N/A") + "</td>" + 
                                "<td>" + (member.phone || "N/A") + "</td>" + 
                                "<td><button class='delete-btn' data-id='" + member.id + "' data-type='" + member.type + "'>삭제</button></td>" +
                                "</tr>";
                    $memberList.append(row);
                });
            } else {
                $memberList.append("<tr><td colspan='6' style='text-align: center;'>검색 결과가 없습니다.</td></tr>");
            }
        }

        function renderPagination(pagination) {
            const $pagination = $("#pagination");
            $pagination.empty(); 

            if (pagination.prev) {
                $pagination.append("<a href='#' data-page='" + (pagination.currentPage - 1) + "'>이전</a>");
            }

            for (let i = pagination.startPage; i <= pagination.endPage; i++) {
                const activeClass = (i === pagination.currentPage) ? "current-page" : "";
                $pagination.append("<a href='#' data-page='" + i + "' class='" + activeClass + "'>" + i + "</a>");
            }

            if (pagination.next) { 
                $pagination.append("<a href='#' data-page='" + (pagination.endPage + 1) + "'>다음</a>");
            }
        }

        function deleteMember(id, type) {
            $.ajax({
                url: "/api/admin/members/" + type + "/" + id, 
                type: "DELETE",
                success: function(response) {
                    alert("회원이 성공적으로 삭제되었습니다.");
                    loadMembers(currentPage);
                },
                error: function(xhr, status, error) {
                    let errorMessage = "회원 삭제에 실패했습니다. 다시 시도해주세요.";
                    if (xhr.responseJSON && xhr.responseJSON.message) {
                        errorMessage = xhr.responseJSON.message;
                    }
                    alert(errorMessage);
                    console.error("AJAX Error: ", status, error, xhr.responseJSON);
                }
            });
        }
    </script>
</body>
</html>