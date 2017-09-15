package com.iirtech.chatbot.service.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import javax.servlet.http.HttpSession;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.iirtech.chatbot.dao.ChatbotDao;
import com.iirtech.chatbot.service.ChatbotService;
import com.iirtech.common.enums.DialogStatus;
import com.iirtech.common.utils.ChatbotUtil;

/**
 * @Package   : com.iirtech.chatbot.service.impl
 * @FileName  : ChatbotServiceImpl.java
 * @작성일       : 2017. 8. 13. 
 * @작성자       : choikino
 * @explain : 이르테크 챗봇 메인 서비스 코드 구현파트 
 */
@Service
public class ChatbotServiceImpl implements ChatbotService{

	private Logger log = Logger.getLogger(this.getClass());
	
	@Autowired
	private ChatbotDao cbd;
	
	@Autowired
	private ChatbotUtil cbu;
	
	/**
	 * 시스템 프로퍼티 변수 
	 * systemFilePath: 시스템 파일 저장경로 
	 * userFilePath: 사용자별 파일 저장경로 
	 * userSeqFileName: 사용자 id pwd 별 seq 정보 저장파일명  
	 */
	@Value("#{systemProp['filepath']}") 
	String filePath;
	@Value("#{systemProp['userseqfilename']}") 
	String userSeqFileName;
	@Value("#{systemProp['systemdelimeter']}") 
	String systemDelimeter;

	@Override
	public Map<String, Object> mergeSystemFile(Map<String, Object> param, HttpSession session) {
		log.debug("***********************mergeUserFile**********************");
		Map<String,Object> resultMap = new HashMap<String, Object>();
		try {
			String id = String.valueOf(param.get("id"));
			String password = String.valueOf(param.get("password"));		
			//비밀번호 암호화 
			String encPassword = cbu.encryptPwd(password);
			String userType = "newUser";
			// userInfos : ["userSeq|id|pwd", "userSeq|id|pwd" ...]
			
			String systemFilePath = System.getProperty("user.home") + "/Documents/chatbot/systemfile/";
//			String systemFilePath = session.getServletContext().getRealPath("resources/file/systemfile");
			log.debug("path>>>"+systemFilePath);
			List<String> userInfos = cbu.ReadFileByLine(systemFilePath, userSeqFileName);

			String userSeq = "";
			if(userInfos.isEmpty()) {
				//unique한 유저시퀀스생성 
				userSeq = UUID.randomUUID().toString().replace("-", "").replace(systemDelimeter, "");
				//시스템 최초로딩시 사용자관련 파일 없으므로 생성
				String userSeqContent = userSeq + systemDelimeter + id + systemDelimeter + encPassword;
				userInfos.add(userSeqContent);
				cbu.WriteFile(systemFilePath, userSeqFileName, userInfos);
				
			}else {
				//사용자정보 파일이 있는 경우 꺼내서 id & pwd 비교 후 없으면 추가 
				for (String userInfo : userInfos) {
					String[] userData = userInfo.split("\\"+systemDelimeter);
					String fileUserSeq = userData[0];
					String fileUserId = userData[1];
					String fileUserPassword = userData[2];
					log.debug(fileUserId+":"+id);
					log.debug(fileUserPassword+":"+encPassword);
					if((fileUserId.equals(id)) && (fileUserPassword.equals(encPassword))) {
						userType = "oldUser";
						userSeq = fileUserSeq;
					}
				}
				//없는 회원이므로 추가
				if(userType.equals("newUser")) {
					userSeq = UUID.randomUUID().toString().replace("-", "").replace(systemDelimeter, "");
					//시스템 파일 폴더의 userSeq.txt 에 정보추가 
					String content = ""; 
					content = userSeq + systemDelimeter + id + systemDelimeter + encPassword;
					userInfos.add(content);
					cbu.WriteFile(systemFilePath, userSeqFileName, userInfos);
				}
			}
			resultMap.put("id", id);
			resultMap.put("password", encPassword);
			resultMap.put("userSeq", userSeq);
			resultMap.put("userType", userType);
			resultMap.put("loginTime", cbu.getYYYYMMDDhhmmssTime(	System.currentTimeMillis()));
		} catch (Exception e) {
			e.printStackTrace();		
		}
		
		return resultMap;
	}

	@Override
	public void mergeUserHistFile(Map<String, Object> userInfoMap, HttpSession session) {
		log.debug("*************************mergeUserHistFile*************************");
		//사용자 파일 폴더의 {userSeq}_hist.txt 파일 생성하고 정보추가 
		//line0: logintime|logouttime|usingtime
		try {
			String userSeq = userInfoMap.get("userSeq").toString();
			String userHistFileName = userSeq + "_hist.txt";
//			String userFilePath = filePath + "userfile/";
			String userFilePath = session.getServletContext().getRealPath("resources/file/userfile");
			List<String> userHistInfos = cbu.ReadFileByLine(userFilePath, userHistFileName);

			String content = ""; 
			Long loginTime = Long.valueOf(userInfoMap.get("loginTime").toString());
			Long logoutTime = Long.valueOf(cbu.getYYYYMMDDhhmmssTime(System.currentTimeMillis()));
			Long usingTime = (logoutTime - loginTime)/1000; //sec
			content = loginTime + systemDelimeter + logoutTime + systemDelimeter + usingTime;
			userHistInfos.add(content);
			cbu.WriteFile(userFilePath, userHistFileName, userHistInfos);
			
		} catch (Exception e) {
			e.printStackTrace();		
		}

	}

	/*
	 * make, update 통합
	 * @see com.iirtech.chatbot.service.ChatbotService#makeUserDialogFile(java.util.Map)
	 */
	@Override
	public void makeUserDialogFile(Map<String, Object> userInfoMap, String rootPath) {
		log.debug("*************************makeUserDialogFile*************************");
		//사용자 파일 폴더의 {currentTimeMillis}_dialog.txt 파일 생성하고 정보추가 
		//line0: topic
		//line1: time|speacker|orgnl_text|prcssd_text
		List<String> userDialogContents = new ArrayList<String>();
		String content = ""; 
		try {
			String userSeq = userInfoMap.get("userSeq").toString();
//			String userFilePath = filePath + "userfile/";
//			String userFilePath = session.getServletContext().getRealPath("resources/file/userfile");
			String userFilePath = rootPath + "/file/userfile/";
			String userDialogFileDir = userFilePath + userSeq + "/";
			log.debug(userDialogFileDir);
//			File targetDir = new File(userDialogFileDir);
			
			//File이 존재하지 않을 경우 만들고 "topic" 문자열 쓰기
//			if (!targetDir.exists()) {
//				targetDir.mkdirs();
//				content = "topic";
//				userDialogContents.add(content);
//			}
			String userDialogFileName = userInfoMap.get("loginTime").toString() + "_dialog.txt";
			log.debug(userDialogFileName);
			
			String orglMessage = userInfoMap.get("orglMessage").toString().replaceAll("\n", "<br>");
			String speecher = "";
			if (Boolean.valueOf( String.valueOf((userInfoMap.get("isUser"))) )) {
				speecher = "User";
			} else {
				speecher = "Bot";
			}

			//파일내용은 하기와 같이 쓰여져야한다.
			//statusCd|msgIdx|Bot|BotText|time|seq
			//statusCd|msgIdx|Fix|fixedText|time|seq
			//statusCd|msgIdx|User|UserText|time|seq
			String dialogTime = userInfoMap.get("dialogTime").toString();
			String statusCd = userInfoMap.get("statusCd").toString();
			String messageIdx = userInfoMap.get("messageIdx").toString();
			
			//파일의 마지막 라인에서 seq값 읽어오기 
			List<String> dialogs = cbu.ReadFileByLine(userDialogFileDir,userDialogFileName);
			int dialogSeq = 0;//초기값 
			if(!dialogs.isEmpty()) {
				String lastDialogLine = dialogs.get(dialogs.size()-1);
				dialogSeq = Integer.parseInt(lastDialogLine.split("\\|")[5]) + 1;
			}
			
			content = statusCd + systemDelimeter + messageIdx + systemDelimeter + speecher 
					+ systemDelimeter + orglMessage + systemDelimeter + dialogTime 
					+ systemDelimeter + dialogSeq;
			
			userDialogContents.add(content);
			cbu.WriteFile(userDialogFileDir, userDialogFileName, userDialogContents);
			
		} catch (Exception e) {
			e.printStackTrace();		
		}

	}

	@Override
	public void addFixedTextToDialogFile(Map<String, Object> param, String rootPath) {
		//1. 기존 대화로그파일 읽어비교 
		String userFilePath = rootPath + "/file/userfile/";
		String userDialogFileDir = userFilePath + param.get("userSeq").toString() + "/";
		String userDialogFileName = param.get("loginTime").toString() + "_dialog.txt";
		String statusCd = param.get("statusCd").toString();
		String messageIdx = param.get("messageIdx").toString();
		List<String> dialogs = cbu.ReadFileByLine(userDialogFileDir, userDialogFileName);
		//삽입할 위치 구하기 //정규표현식 사용 (S000[|]0[|]Bot[|].*)
		String botMatchingStr = statusCd + "[|]" + messageIdx + "[|]Bot[|]";
		String fixMatchingStr = statusCd + "[|]" + messageIdx + "[|]Fix[|]";
		int insertPoint = 0;
		boolean isChangeFix = false;
		for (int i = 0; i < dialogs.size(); i++) {
			String dialog = dialogs.get(i);
			if(dialog.matches("("+ botMatchingStr +".*)")) {
				insertPoint = i + 1;
			}else if(dialog.matches("("+ fixMatchingStr +".*)")) {
				insertPoint = i;
				isChangeFix = true;
			}
		}
		//삽입할 문자열 만들어서 삽입위치에 삽입
		String speecher = "Fix";
		String fixedText = param.get("fixedText").toString().replaceAll("\r\n", "<br>").replaceAll("\n", "<br>");
		String dialogTime = cbu.getYYYYMMDDhhmmssTime(System.currentTimeMillis());
		String content = statusCd + systemDelimeter + messageIdx + systemDelimeter + speecher 
				+ systemDelimeter + fixedText + systemDelimeter + dialogTime 
				+ systemDelimeter + insertPoint;
		if(!isChangeFix) {
			dialogs.add(insertPoint, content);//추가 
		}else {
			dialogs.set(insertPoint, content);//교체 
		}
		
		//루프돌면서 dialogSeq 재 정렬시키기
		List<String> newDialogs = new ArrayList<String>();
		for (int i = 0; i < dialogs.size(); i++) {
			String dialog = dialogs.get(i);
			String[] elmnts = dialog.split("\\|");
			//statusCd|msgIdx|Bot|BotText|time|seq
			String newDialog = elmnts[0] + systemDelimeter + elmnts[1] + systemDelimeter + elmnts[2] + systemDelimeter + elmnts[3]
					 + systemDelimeter + elmnts[4] + systemDelimeter + i;
			newDialogs.add(newDialog);
		}
		//기존 파일 지우고 새로 쓰기 
		cbu.DeleteFile(userDialogFileDir, userDialogFileName);
		cbu.WriteFile(userDialogFileDir, userDialogFileName, newDialogs);
	}
	
	//시각화 
	@Override
	public String makeDialogLogString(Map<String, Object> param, String rootPath) {
		String result = "<p id='dialogShowBoxText'>";
		
		String userFilePath = rootPath + "/file/userfile/";
		String userDialogFileDir = userFilePath + param.get("userSeq").toString() + "/";
		String userDialogFileName = param.get("loginTime").toString() + "_dialog.txt";
		List<String> dialogs = cbu.ReadFileByLine(userDialogFileDir, userDialogFileName);
		//현재는 <br>을 \t으로 바꿔주고 line 별로는 <br>태그 붙여준다.
		//statusCd 가 달라지면 <br><br> 붙임 
		for (int i = 0; i < dialogs.size(); i++) {
			String newLineStr = "<br>";
			String dialog = dialogs.get(i).replaceAll("<br>", "\t");//기존 개행 표시<br>를 \t 으로 변경 
			String[] elmnts = dialog.split("\\|");
			String statusCd = elmnts[0];
			String targetStatusCd = statusCd;
			if(i+1 < dialogs.size()) {
				String nextDialog = dialogs.get(i+1).replaceAll("<br>", "\t");//기존 개행 표시<br>를 \t 으로 변경 
				targetStatusCd = nextDialog.split("\\|")[0];
			}
			String msgIdx = elmnts[1];
			if(!targetStatusCd.equals(statusCd)) {
				newLineStr = "<br><br>";
			}
			//statusCd|msgIdx|Bot|BotText|time|seq
			//[Bot]: BotText
			String otomata = DialogStatus.get(statusCd).toString();
			result += "[" + otomata + "(" + msgIdx + ")] " + elmnts[2] + ": " + elmnts[3] + newLineStr;
		}
		return result + "</p>";
	}

//	@Override
//	public void updateUserDialogFile(Map<String, Object> param) {
//		log.debug("*************************updateUserDialogFile*************************");
//		//line0: topic
//		//line1: time|speacker|orgnl_content|prcssd_content
//		//read 하고 마지막에 add해서 write!
//	}
	
}
