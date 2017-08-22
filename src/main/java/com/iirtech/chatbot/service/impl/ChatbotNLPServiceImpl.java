package com.iirtech.chatbot.service.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.iirtech.chatbot.service.ChatbotNLPService;
import com.iirtech.common.utils.ChatbotUtil;

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
	
	@Autowired
	ChatbotUtil cbu;
	
	@Value("#{systemProp['filepath']}") 
	String urlFilePath;
	@Value("#{systemProp['systemdelimeter']}") 
	String systemDelimeter;
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

	@Override
	public String selectKeyword(String keywordType, Map<String, Object> conditionInfoMap) {
		String result = "";
		//1.keywordType을 가지고 dict파일에서 뒤져서 키워드 판단-구현
		String dictFilePath = urlFilePath + "dictionary/";
		String fileName = keywordType + ".dict"; //TOPIC.dict
		String procText = conditionInfoMap.get("procText").toString();
		//사전파일읽음 
		List<String> lines = cbu.ReadFileByLine(dictFilePath, fileName);
		List<String> keywordCandidates = new ArrayList<String>();
		for (String line : lines) {
			//line >> 여행|travel
			String[] lineArr = line.split("\\"+systemDelimeter);
			String CITKey = lineArr[0]; //여행
			String CITValue = lineArr[1]; //travel
			if(line.contains(CITKey) && !keywordCandidates.contains(CITValue)) {
				keywordCandidates.add(CITValue);
			}
		}
		
		//2.전처리 정보를 가지고 판별식을 통해 키워드 판단-미구현 
		
		
		//3.추출된 키워드가 한개 이상일 경우 한개만 선택하기(사용자가 고의로 여러개 입력-에러)로 봄
		//예외처리이므로 미구현
		
		//일단 임시로 첫번째 것만 리턴하는거로 string "travel|TOPIC"
		result = keywordCandidates.get(0) + systemDelimeter + keywordType;
		return result;
	}

}
