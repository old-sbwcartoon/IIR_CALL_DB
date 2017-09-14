package com.iirtech.chatbot.service;

import java.util.Map;

import javax.servlet.http.HttpSession;


/**
 * @Package   : com.iirtech.chatbot.service
 * @FileName  : ChatbotService.java
 * @작성일       : 2017. 8. 13. 
 * @작성자       : choikino
 * @explain : 이르테크 챗봇 메인 서비스 인터페이스 
 */
public interface ChatbotService {

	Map<String, Object> mergeSystemFile(Map<String, Object> param, HttpSession session);

	void mergeUserHistFile(Map<String, Object> param, HttpSession session);

//	void makeUserDialogFile(Map<String, Object> userInfoMap, HttpSession session);
	void makeUserDialogFile(Map<String, Object> userInfoMap, String rootPath);

	void addFixedTextToDialogFile(Map<String, Object> param, String rootPath);

//	void updateUserDialogFile(Map<String, Object> param);

}
