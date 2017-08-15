package com.iirtech.chatbot.service.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.springframework.stereotype.Service;

import com.iirtech.chatbot.service.ChatbotNLPService;

/**
 * @Package   : com.iirtech.chatbot.service.impl
 * @FileName  : ChatbotNLPServiceImpl.java
 * @작성일       : 2017. 8. 14. 
 * @작성자       : choikino
 * @explain : 이르테크 챗봇 시스템 입력 메시지 전처리와 관련된 메소드들을 실제로 구현  
 */
@Service
public class ChatbotNLPServiceImpl implements ChatbotNLPService {

	private Logger log = Logger.getLogger(this.getClass());
	
	@Override
	public Map<String,Object> preProcess(String procInputText) {
		log.debug("*************************preProcess*************************");
		Map<String,Object> resultMap = new HashMap<String, Object>();
		String procText = procInputText;
		//전처리 ~
		
		List<String> textTypes = new ArrayList<String>();
		//문장정보 파악~ isPositive/isNegative/isAsking
		textTypes.add("isPositive");//일단 긍정으로 하드코딩 
		textTypes.add("isCorrect");//일단 맞음으로 하드코딩 
		
		//전처리된 문장과 전처리 후 파악된 문장정보를 맵객체에 담아서 리턴
		resultMap.put("procText", procText);
		resultMap.put("textTypes", textTypes);
		return resultMap;
	}

}
