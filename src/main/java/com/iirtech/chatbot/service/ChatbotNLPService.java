package com.iirtech.chatbot.service;

/**
 * @Package   : com.iirtech.chatbot.service.impl
 * @FileName  : ChatbotNLPService.java
 * @작성일       : 2017. 8. 14. 
 * @작성자       : choikino
 * @explain : 이르테크 챗봇 시스템 입력 메시지 전처리 서비스 인터페이스 
 */
public interface ChatbotNLPService {

	String preProcess(String procInputText);

}
