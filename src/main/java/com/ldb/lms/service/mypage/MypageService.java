package com.ldb.lms.service.mypage;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;

import org.mindrot.jbcrypt.BCrypt;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.ldb.lms.dto.mypage.LoginDto;
import com.ldb.lms.mapper.learning_support.CourseMapper;
import com.ldb.lms.mapper.mypage.ProStuMapper;
import com.ldb.lms.mapper.mypage.ProfessorMapper;
import com.ldb.lms.mapper.mypage.StudentMapper;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;



@Service
@RequiredArgsConstructor
public class MypageService {
	
	
	private final StudentMapper studentMapper;
	
	private final ProfessorMapper professroMapper;
	
	private final ProStuMapper proStuMapper;
	


		
		
		
	public Map<String,String> login(String id , String password , HttpServletRequest request) {
		LoginDto dto = new LoginDto();
		dto.setProfessorId(id);
		dto.setProfessorId(id);
		
		Map<String, String> map = proStuMapper.loginChk(dto);
		if(map == null) {
			return null;
		}
		else {
			System.out.println("loginChk : "+map);
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
					if(studentMapper.selectStatus(dbId)!=null) { 
						
						/*request.setAttribute("msg","퇴학한사람은 로그인할수없어요");
						request.setAttribute("url","doLogin");
						return "alert";*/
						return null;
					}
				}

				request.getSession().setAttribute("login", dbId);
				request.setAttribute("msg", dbName+"님이 로그인 하셨습니다");
				request.setAttribute("url","index");
				return map;

			}
			else{
				request.setAttribute("msg", "비번을 확인하세요");
				request.setAttribute("url","doLogin");
			}

			
		}
		return null;
		
	}



}
