package com.iirtech.chatbot.service;

import java.util.Map;



public interface ChatbotService {

	Map<String, Object> searchUserFile(Map<String, Object> param);

	void mkUsrFiles(Map<String, Object> param);

}
