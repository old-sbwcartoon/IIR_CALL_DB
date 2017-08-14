package com.iirtech.chatbot.service.impl;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.iirtech.chatbot.dto.MessageInfo;
import com.iirtech.chatbot.service.ChatbotScriptService;
import com.iirtech.common.enums.DialogStatus;
import com.iirtech.common.utils.ChatbotUtil;

/**
 * @Package   : com.iirtech.chatbot.service.impl
 * @FileName  : ChatbotScriptServiceImpl.java
 * @작성일       : 2017. 8. 13. 
 * @작성자       : choikino
 * @explain : 이르테크 챗봇 스크립트 매핑과 관련된 메소드들을 실제 구현 
 */
@Service
public class ChatbotScriptServiceImpl implements ChatbotScriptService {

	private Logger log = Logger.getLogger(this.getClass());
	
	@Autowired
	ChatbotUtil cbu;

	@Override
	public Map<String, Object> getMessageInfo(String statusCd, String inputText, String messageIdx) {
		log.debug("*************************getMessageInfo*************************");
		//message 는 null 값이 들어올 수 있음에 유의! null이면 bot이 말할 메시지를 찾는 것임 
		Map<String, Object> resultMap = new HashMap<String, Object>();
		

//		if(inputText != null) {
//			//input 메시지가 있는 상황은 언어처리 필수 
//			
//		}

		MessageInfo info = new MessageInfo(statusCd);
		int nextIdx = Integer.parseInt(messageIdx) + 1;
		String[] nextMessages = info.getMessagesByIdx(nextIdx);
		
		if (nextMessages == null) {
			//index 해당 문장이 없다면 (해당 statusCd의 봇 발화 모두 진행했다면)
			//다음 statusCd 데이터 불러옴
			info = new MessageInfo(info.getNextStatusCd());
			nextMessages = info.getMessagesByIdx(0);
			nextIdx = 0;
		}
//		
//		
//		
//		//나머지 로직(메시지 패턴 스코어링 후 매칭)은 공통 
//		switch (DialogStatus.get(statusCd)) {
//		case SYSTEM_ON:
//			//스텝별로 발화자, 다음상태, 발화문을 결정하는 판별식이 다르게 적용되어야 함 
//			returnStatus = DialogStatus.START_DIALOG.getStatusCd();
//			//지금은 하드코딩인데 스크립트 파일에서 찾아오는 로직 구현해야함 
//			returnMessage = "CALL 시스템에 오신것을 환영합니다.\n**한문장씩 쓰세요.\n**문장 끝에 . ? 를 써 주세요.";
//			resultMap.put("returnSpeecher", "bot"); //첫발화는 bot으로 발화자 고정 
//			
//			break;
//		case START_DIALOG:
//			//js에서 특정 조건에 따라 타이머 맞추고 스크립트 메소드 실행시킬 수 있는지 확인 
//			//스텝별로 발화자, 다음상태, 발화문을 결정하는 판별식이 다르게 적용되어야 함 
//			returnStatus = DialogStatus.START_DIALOG.getStatusCd();
//			//지금은 하드코딩인데 스크립트 파일에서 찾아오는 로직 구현해야함 
//			returnMessage = ChatbotUtil.getRightMessage("");
//			resultMap.put("returnSpeecher", "user"); //첫발화는 bot으로 발화자 고정
//			
//			break;
//		case GREETING:
//			
//			break;
//		case APPROACH_TOPIC:
//			
//			break;
//		//아직도 엄청많이 남음 
//		default:
//			break;
//		}
//		

		resultMap.put("returnStatus", info.getStatusCd());
		resultMap.put("returnMessage", nextMessages);
		resultMap.put("returnMessageIdx", nextIdx);
		
		return resultMap;
	}

}
