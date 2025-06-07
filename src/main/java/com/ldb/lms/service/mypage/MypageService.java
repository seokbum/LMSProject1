package com.ldb.lms.service.mypage;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Random;
import java.util.Set;
import java.util.UUID;

import org.mindrot.jbcrypt.BCrypt;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartRequest;

import com.ldb.lms.dto.mypage.Dept;
import com.ldb.lms.dto.mypage.FindIdDto;
import com.ldb.lms.dto.mypage.FindPwDto;
import com.ldb.lms.dto.mypage.LoginDto;
import com.ldb.lms.dto.mypage.Professor;
import com.ldb.lms.dto.mypage.RegisterUserDto;
import com.ldb.lms.dto.mypage.Student;
import com.ldb.lms.dto.mypage.UpdateInfoDto;
import com.ldb.lms.dto.mypage.UpdatePwDto;
import com.ldb.lms.mapper.mypage.DeptMapper;
import com.ldb.lms.mapper.mypage.ProStuMapper;
import com.ldb.lms.mapper.mypage.ProfessorMapper;
import com.ldb.lms.mapper.mypage.StudentMapper;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
;



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
			request.getSession().invalidate(); //일단세션 다날려
			return false;
		}
		else {
			request.setAttribute("error", "로그아웃을 하세요");
			return true;
		}
	}


	public Map<String,String> login(String id , String password , HttpServletRequest request) {
		LoginDto dto = new LoginDto();
		dto.setProfessorId(id);
		dto.setStudentId(id);
		Map<String, String> map = proStuMapper.loginChk(dto);

		if(map == null) {
			request.setAttribute("error", "존재하지않는 정보");
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
						request.setAttribute("error", "퇴학한사람은 로그인 할 수 없어요");
						return null;
					}
				}
				//모든정보가 일치한다면
				request.getSession().setAttribute("login", dbId);
				request.setAttribute("out",null);
				return map;

			}
			else{
				request.setAttribute("error", "아이디혹은비밀번호를 확인해주세요");
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

	public  String IdChk(String a) { 
		String num = null;
		if(a.equals("pro")) {
			num = createProfessorId();	
		}
		else if(a.equals("stu")) {
			num = createStudentId();
		}
		return num;
	}

	//교수의아이디를 자동생성하는 메서드(p000)
	private  String createProfessorId() {
		int[] num = {0,1,2,3,4,5,6,7,8,9}; 

		String sNum="";
		for(int i=0;i<3;i++) {
			//0 ~ (num.length-1)의 랜덤한숫자반환
			int ranNum = new Random().nextInt(num.length);
			sNum+=num[ranNum]; //랜덤한 3개의숫자
		}
		while(true) { 
			if(professroMapper.idchk("P"+sNum)<1) { //true(id가존재하지않을 시 )면 루프탈출
				break;
			}
			else {
				int iNum = Integer.parseInt(sNum);//sNum을 Integer로형변환 
				iNum +=1; // 1 증가
				sNum = String.valueOf(iNum); // sNum으로 다시넣기
			}
		}
		//p0000 형식
		return "P"+sNum;

	}
	//학생의아이디를 자동생성하는 메서드(s00000)
	private   String createStudentId() {
		int[] num = {0,1,2,3,4,5,6,7,8,9};
		String sNum="";

		for(int i=0;i<5;i++) {
			//0 ~ (num.length-1)의 랜덤한숫자반환
			int ranNum = new Random().nextInt(num.length);
			sNum+=num[ranNum]; //랜덤한 5개의숫자
		}


		while(true) { 
			if(studentMapper.idchk("S"+sNum)<1) { //true(id가존재하지않을 시 )면 루프탈출
				break;
			}
			else {
				int iNum = Integer.parseInt(sNum);//sNum을 Integer로형변환 
				iNum +=1; // 1 증가
				sNum = String.valueOf(iNum); // sNum으로 다시넣기
			}
		}

		return "S"+sNum;
	}


	//임시비밀번호를 만드는 알고리즘(비밀번호찾기 시에만 발급이 될것임)
	public  String getTempPw() {
		List<String> lowerList = Arrays.asList
				("a" ,"b" ,"c" ,"d" ,"e" ,"f" ,"g" ,"h" ,"i" ,"j" ,"k" 
						,"l" ,"m" ,"n" ,"o" ,"p","q","r","s","t","z");

		List<String> upperList = new ArrayList<>();
		for (String string : lowerList) {
			upperList.add(string.toUpperCase());
		}	
		List<String> specialList = Arrays.asList("%","@","#","^","&","*","!");

		List<Object> combineList = new ArrayList<>();
		combineList.addAll(specialList);
		combineList.addAll(lowerList);
		combineList.addAll(upperList);
		for (int i = 0; i < 15; i++) { //랜덤한0~9 숫자 10개집어넣기
			combineList.add(new Random().nextInt(10)); 
		}
		//무작위 섞기
		Collections.shuffle(combineList);
		String tempNum = "";
		for (int i = 0; i < 6; i++) {
			int num = new Random().nextInt(combineList.size());
			tempNum += combineList.get(num);
		}
		return tempNum;
	}



	public void registerNumChk(RegisterUserDto dto, HttpServletRequest request) {
		//LocalDate -> Date
		//LocalDate birth = dto.getBirth();
		//Date date = Date.from(birth.atStartOfDay(ZoneId.systemDefault()).toInstant());

		//position에 따른 id를 만들어줌(중복방지 로직추가)
		String id = IdChk(dto.getPosition());
		System.out.println("id : "+id);

		//pass -> hashPass
		String hashpw = BCrypt.hashpw(dto.getPassword(), BCrypt.gensalt());
		System.out.println("hashPass : "+hashpw );
		if(id.contains("S")) {
			Student st = new Student();
			st.setStudentId(id);
			st.setStudentNum(id.substring(1));
			st.setDeptId(dto.getDeptId());
			st.setStudentBirthday(dto.getBirth());
			st.setStudentEmail(dto.getEmail());
			st.setStudentImg(dto.getPicture());
			st.setStudentName(dto.getName());
			st.setStudentPassword(hashpw);
			st.setStudentPhone(dto.getPhone());
			st.setStudentStatus("재학");
			System.out.println(st);
			request.setAttribute("id", st.getStudentId());
			request.getSession().setAttribute("m", st);
			String num = EmailUtil.sendNum(st.getStudentEmail(), st.getStudentName(), st.getStudentId());
			request.setAttribute("num", num);
			System.out.println("num : "+num);
		}
		else {
			Professor pro = new Professor();
			pro.setProfessorId(id);
			pro.setDeptId(dto.getDeptId());
			pro.setProfessorBirthday(dto.getBirth());
			pro.setProfessorEmail(dto.getEmail());
			pro.setProfessorImg(dto.getPicture());
			pro.setProfessorName(dto.getName());
			pro.setProfessorPassword(hashpw);
			pro.setProfessorPhone(dto.getPhone());
			request.setAttribute("id", pro.getProfessorId());
			request.getSession().setAttribute("m", pro);
			String num = EmailUtil.sendNum(pro.getProfessorEmail(), pro.getProfessorName(), pro.getProfessorId());
			request.setAttribute("num", num);
			System.out.println("num : "+num);
		}


	}


	public boolean registerSuccess(HttpServletRequest request) {
		String id = request.getParameter("id");
		if(id.contains("S")) {
			Student stu = (Student)request.getSession().getAttribute("m");

			if(studentMapper.insert(stu)<1) { //DB에오류발생시
				request.getSession().invalidate();
				request.setAttribute("msg", "회원가입 오류발생!");
				return false;
			}
			else {
				//회원가입성공 시 해당email로 발급된 id를 보내줄거임
				String email = stu.getStudentEmail();
				String name = stu.getStudentName();
				EmailUtil.sendIdEmail(email, name, id);
				request.getSession().invalidate();
				request.setAttribute("msg", email+"로 아이디 전송완료!");
				return true;
			}
		}
		else {
			Professor pro = (Professor)request.getSession().getAttribute("m");

			if(professroMapper.insert(pro)<1) {
				request.getSession().invalidate();
				request.setAttribute("msg", "회원가입 오류발생!");
				return false;
			}
			else {
				String email = pro.getProfessorEmail();
				String name = pro.getProfessorName();
				EmailUtil.sendIdEmail(email, name, id);
				request.getSession().invalidate();
				request.setAttribute("msg", email+"로 아이디 전송완료!");
				return true;
			}
		}

	}


	public boolean index(HttpServletRequest request) {
		HttpSession session = request.getSession();
		String dbId = (String)session.getAttribute("login");
		if(dbId==null) {
			return false;
		}
		if(dbId.contains("S")) {
			Student student = studentMapper.selectOne(dbId);
			String deptName = deptMapper.selectName(student.getDeptId());
			session.setAttribute("deptName", deptName);
			session.setAttribute("m", student);
			return true;
		}
		else if(dbId.contains("P")){
			Professor professor = professroMapper.selectOne(dbId);
			String deptName = deptMapper.selectName(professor.getDeptId());
			session.setAttribute("deptName", deptName);
			session.setAttribute("m", professor);	
			return true;
		}
		return false;

	}


	public void logout(HttpServletRequest request) {
		HttpSession session = request.getSession(false);
        if (session != null) {
            session.invalidate();
            System.out.println("Session invalidated");
        }
	}


	public void findId(FindIdDto dto,HttpServletRequest request) {
		String id = proStuMapper.findId(dto);
		if(id==null) {
			request.setAttribute("msg", "아이디를 찾을 수 없어요");
		}
		else {
			request.setAttribute("msg", dto.getName()+"님의 id : "+id);
		}
	}


	public void findPwProcess(FindPwDto dto, HttpServletRequest request) {
		String pass = proStuMapper.findPw(dto);
		String id = dto.getId();
		String email = dto.getEmail();
		if(pass==null) {
			request.setAttribute("msg", "입력하신정보가 맞지않아요");
			return;
		}
		else {
			//임시비번
			String tempPw = getTempPw(); 
			//암호화 된 임시비번
			String hashPw = BCrypt.hashpw(tempPw, BCrypt.gensalt());
			System.out.println("임시비번 : "+tempPw);
			
			if(id.contains("S")) {
				UpdatePwDto sDto = new UpdatePwDto();
				sDto.setId(id);
				sDto.setNewPw(hashPw);
				if(proStuMapper.updateStuPw(sDto)<1) {
					request.setAttribute("msg", "임시비밀번호 업데이트실패");
					return;
				}
				else {
					//업데이트성공시 (해쉬처리 전의 임시비밀번호를 이메일로 보내준다)
					EmailUtil.sendTempPw(email, id, tempPw);
					request.setAttribute("id", id);
					request.setAttribute("email", email);//updatePw에서사용
					request.setAttribute("msg", "success");
					return ;
				}
			}
			else if(id.contains("P")) {
				UpdatePwDto pDto = new UpdatePwDto();
				pDto.setId(id);
				pDto.setNewPw(hashPw);
				if(proStuMapper.updateProPw(pDto)<1) {
					request.setAttribute("msg", "임시비밀번호 업데이트실패");
					return;
				}
				else {
					//업데이트성공시 (해쉬처리 전의 임시비밀번호를 이메일로 보내준다)
					EmailUtil.sendTempPw(email, id, tempPw);
					request.setAttribute("id", id);
					request.setAttribute("email", email);
					request.setAttribute("msg", "success");
					return;
				}
			}
			return;
		}
	}


	public void changePw(UpdatePwDto dto,HttpServletRequest request) {
		String id = dto.getId();
		String newPw = dto.getNewPw();
		String hashpw = BCrypt.hashpw(newPw, BCrypt.gensalt());
		dto.setNewPw(hashpw); //암호화된 비밀번호로 업데이트
		if(id.contains("S")) {
			if(proStuMapper.updateStuPw(dto)<1) {
				request.setAttribute("chg", "비밀번호변경 실패");
			}
			else {
				request.getSession().invalidate();//완료했으면 세션지우기
				request.setAttribute("chg", "비밀번호변경 완료");
			}
		}
		else {
			if(proStuMapper.updateProPw(dto)<1) {
				request.setAttribute("chg", "비밀번호변경 실패");
			}
			else {
				request.getSession().invalidate();//완료했으면 세션지우기
				request.setAttribute("chg", "비밀번호변경 완료");
			}
		}
	}


	public boolean userUpdate(UpdateInfoDto dto,HttpServletRequest request) {
		System.out.println("dto :::: "+dto);
		if(dto.getId().contains("S")) {
			int updateStuInfo = proStuMapper.updateStuInfo(dto);
			System.out.println("dtoUpdate L "+updateStuInfo);
			if(updateStuInfo<1) {
				return false;	
			}
			else {
				return true;
			}
		}
		else if(dto.getId().contains("P")) {
			if(proStuMapper.updateProInfo(dto)<1) {
				return false;
			}
			else {
				return true;
			}
		}
		else {
			return false;
		}
	}
}
