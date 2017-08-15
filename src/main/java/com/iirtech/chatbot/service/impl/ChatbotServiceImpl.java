package com.iirtech.chatbot.service.impl;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.iirtech.chatbot.dao.ChatbotDao;
import com.iirtech.chatbot.service.ChatbotService;
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
	@Value("#{systemProp['systemfilepath']}") 
	String systemFilePath;
	@Value("#{systemProp['userfilepath']}") 
	String userFilePath;
	@Value("#{systemProp['userseqfilename']}") 
	String userSeqFileName;
	@Value("#{systemProp['systemdelimeter']}") 
	String systemDelimeter;
	@Value("#{systemProp['botname']}") 
	String botName;

	@Override
	public Map<String, Object> mergeSystemFile(Map<String, Object> param) {
		log.debug("***********************mergeUserFile**********************");
		Map<String,Object> resultMap = new HashMap<String, Object>();
		try {
			String id = String.valueOf(param.get("id"));
			String password = String.valueOf(param.get("password"));		
			//비밀번호 암호화 
			String encPassword = cbu.encryptPwd(password);
			String userType = "newUser";
			
			// userInfos : ["userSeq|id|pwd", "userSeq|id|pwd" ...]
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
	public void mergeUserHistFile(Map<String, Object> userInfoMap) {
		log.debug("*************************mergeUserHistFile*************************");
		//사용자 파일 폴더의 {userSeq}_hist.txt 파일 생성하고 정보추가 
		//line0: logintime|logouttime|usingtime
		try {
			String userSeq = userInfoMap.get("userSeq").toString();
			String userHistFileName = userSeq + "_hist.txt";
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
	public void makeUserDialogFile(Map<String, Object> userInfoMap) {
		log.debug("*************************makeUserDialogFile*************************");
		//사용자 파일 폴더의 {currentTimeMillis}_dialog.txt 파일 생성하고 정보추가 
		//line0: topic
		//line1: time|speacker|orgnl_text|prcssd_text
		List<String> userDialogContents = new ArrayList<String>();
		String content = ""; 
		try {
			String userSeq = userInfoMap.get("userSeq").toString();
			String userDialogFileDir = userFilePath + userSeq + "/"; 
			File targetDir = new File(userDialogFileDir);
			
			//File이 존재하지 않을 경우 만들고 "topic" 문자열 쓰기
			if (!targetDir.exists()) {
				targetDir.mkdirs();
				content = "topic";
				userDialogContents.add(content);
			}
			String dialogTime = userInfoMap.get("dialogTime").toString();
			String userDialogFileName = dialogTime + "_dialog.txt";
			
			String orglMessage = userInfoMap.get("orglMessage").toString();
			
			content = dialogTime + systemDelimeter + botName + systemDelimeter + orglMessage + systemDelimeter + orglMessage;
			
			userDialogContents.add(content);
			cbu.WriteFile(userDialogFileDir, userDialogFileName, userDialogContents);
			
		} catch (Exception e) {
			e.printStackTrace();		
		}

	}

//	@Override
//	public void updateUserDialogFile(Map<String, Object> param) {
//		log.debug("*************************updateUserDialogFile*************************");
//		//line0: topic
//		//line1: time|speacker|orgnl_content|prcssd_content
//		//read 하고 마지막에 add해서 write!
//	}
	
}
