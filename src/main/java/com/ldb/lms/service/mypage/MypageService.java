package com.ldb.lms.service.mypage;

import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.mindrot.jbcrypt.BCrypt;
import org.springframework.stereotype.Service;

import com.ldb.lms.dto.mypage.LoginDto;
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
	
	public void loginChk(HttpServletRequest request) {
		String login = (String)request.getSession().getAttribute("login");
		
		
		
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

	



}
