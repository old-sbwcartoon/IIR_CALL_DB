package com.iirtech.chatbot.service;

import java.util.List;
import java.util.Map;

/**
 * @Package   : com.iirtech.chatbot.service
 * @FileName  : ChatbotScriptService.java
 * @작성일       : 2017. 8. 13. 
 * @작성자       : choikino
 * @explain : 이르테크 챗봇 스크립트 매핑 서비스 인터페이스 
 */
public interface ChatbotScriptService {

	Map<String, Object> getMessageInfo(String statusCd, String inputText, String messageIdx, Map<String,Object> conditionInfoMap);

	List<String> findAllOperationStrings(String message);
	
	Object applySysOprt(String message, Map<String, Object> conditionInfos);
}
