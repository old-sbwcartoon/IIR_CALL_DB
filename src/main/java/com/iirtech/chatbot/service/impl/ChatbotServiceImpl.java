package com.iirtech.chatbot.service.impl;

import java.util.Map;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.iirtech.chatbot.dao.ChatbotDao;
import com.iirtech.chatbot.service.ChatbotService;

@Service
public class ChatbotServiceImpl implements ChatbotService{

	private Logger log = Logger.getLogger(this.getClass());
	
	@Autowired
	private ChatbotDao cbd;
	
	@Value("#{systemProp['filepath']}") 
	String systemFilePath;

	@Override
	public Map<String, Object> searchUserFile(Map<String, Object> param) {
		log.debug("test system properties>>>"+systemFilePath);
		return null;
	}

	@Override
	public void mkUsrFiles(Map<String, Object> param) {
		// TODO Auto-generated method stub
		
	}
	
}
