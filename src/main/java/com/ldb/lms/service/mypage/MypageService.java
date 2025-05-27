package com.ldb.lms.service.mypage;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.UUID;

import org.mindrot.jbcrypt.BCrypt;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartRequest;
import org.springframework.web.multipart.support.StandardServletMultipartResolver;

import com.ldb.lms.dto.learning_support.DeptDto;
import com.ldb.lms.dto.mypage.Dept;
import com.ldb.lms.dto.mypage.LoginDto;
import com.ldb.lms.mapper.mypage.DeptMapper;
import com.ldb.lms.mapper.mypage.ProStuMapper;
import com.ldb.lms.mapper.mypage.ProfessorMapper;
import com.ldb.lms.mapper.mypage.StudentMapper;


import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;



@Service
@RequiredArgsConstructor
public class MypageService {


	private final StudentMapper studentMapper;

	private final ProfessorMapper professroMapper;

	private final ProStuMapper proStuMapper;

	private final DeptMapper deptMapper;

	public boolean loginChk(HttpServletRequest request) {
		String login = (String)request.getSession().getAttribute("login");
		if(login==null) {
			return false;
		}
		else {
			return true;
		}


	}


	public Map<String,String> login(String id , String password , HttpServletRequest request) {
		LoginDto dto = new LoginDto();
		dto.setProfessorId(id);
		dto.setStudentId(id);

		Map<String, String> map = proStuMapper.loginChk(dto);

		if(map == null) {
			request.setAttribute("out", "존재하지않는 정보");
			return null;
		}
		else {

			String dbId="";
			String dbPw="";
			String dbName="";
			Set<Entry<String,String>> entrySet = map.entrySet();
			for (Entry<String, String> entry : entrySet) {
				if(entry.getKey().contains("id")) {
					dbId = entry.getValue();
				}
				else if(entry.getKey().contains("password")) {
					dbPw = entry.getValue();
				}
				else {
					dbName = entry.getValue();
				}
			} //dbId,dbPw,dbName 꺼내기종료

			//Bcrypt.checkpw(입력,검증) : 입력과 검증(암호화된비번) 을 비교할수있음
			if(BCrypt.checkpw(password, dbPw) ){

				if(dbId.contains("S")) { //학생 중 퇴학상태인 학생을 검증하는 단계
					if(studentMapper.selectStatus(dbId).equals("퇴학")) { 
						request.setAttribute("out", "퇴학한사람은 로그인 할 수 없어요");
						return null;
					}
				}
				//모든정보가 일치한다면
				request.getSession().setAttribute("login", dbId);
				request.setAttribute("out",null);
				return map;

			}
			else{
				request.setAttribute("out", "아이디혹은비밀번호를 확인해주세요");
				return null;
			}
		}
	}


	public void getDeptAll(HttpServletRequest request) {
		List<Dept> list = deptMapper.selectAll();
		request.setAttribute("dept", list);
	}


	public void picture(HttpServletRequest request) {
		// 파일 저장 경로 설정
		String path = request.getServletContext().getRealPath("") + "/dist/assets/picture/";
		System.out.println("Service: Save path: " + path); // 디버깅용 경로 출력

		// 기준 디렉토리 실제 경로
		File directory = new File(path);
		if (!directory.exists()) {
			boolean created = directory.mkdirs(); // 폴더가 없으면 생성
			if (!created) {
				throw new RuntimeException("디렉토리 생성에 실패했습니다: " + path);
			}
		}

		// MultipartFile 가져오기
		MultipartFile file = null;
		try {
			// Spring의 MultipartResolver를 통해 파일 추출
			file = ((MultipartRequest) request).getFile("picture");
		} catch (Exception e) {
			throw new IllegalStateException("멀티파트 요청 처리에 실패했습니다: " + e.getMessage());
		}

		// 파일 null 또는 비어 있는지 체크
		if (file == null || file.isEmpty()) {
			System.out.println("Service: File is null or empty");
			throw new IllegalArgumentException("파일이 업로드되지 않았거나 비어 있습니다.");
		}

		// 파일명 충돌 방지를 위해 고유한 파일명 생성
		String originalFilename = file.getOriginalFilename();
		String fileExtension = "";
		if (originalFilename != null && originalFilename.contains(".")) {
			fileExtension = originalFilename.substring(originalFilename.lastIndexOf("."));
		} else {
			throw new IllegalArgumentException("파일 이름이 유효하지 않거나 확장자가 없습니다: " + originalFilename);
		}
		String fname = UUID.randomUUID().toString() + fileExtension; // UUID로 고유 파일명 생성

		// 파일 저장
		File destFile = new File(path + fname);
		try {
			file.transferTo(destFile);
			System.out.println("Service: Uploaded file: " + fname);
		} catch (IOException e) {
			e.printStackTrace();
			throw new RuntimeException("파일 업로드에 실패했습니다: " + e.getMessage());
		}

		// 파일명 저장
		request.setAttribute("fname", fname);
	}
}
