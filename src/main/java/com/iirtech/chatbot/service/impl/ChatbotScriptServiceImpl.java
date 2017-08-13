package com.iirtech.chatbot.service.impl;

import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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
	public Map<String, Object> getMessageInfo(String statusCd, String message) {
		log.debug("*************************getMessageInfo*************************");
		//message 는 null 값이 들어올 수 있음에 유의! null이면 bot이 말할 메시지를 찾는 것임 
		Map<String, Object> resultMap = new HashMap<String, Object>();
		if(message != null) {
			//input 메시지가 있는 상황은 언어처리 필수 
			
		}
		//나머지 로직(메시지 패턴 스코어링 후 매칭)은 공통 
		switch (DialogStatus.get(statusCd)) {
		case SYSTEM_ON:
			//스텝별로 발화자, 다음상태, 발화문을 결정하는 판별식이 다르게 적용되어야 함 
			String returnStatus = DialogStatus.START_DIALOG.getStatusCd();
			//지금은 하드코딩인데 스크립트 파일에서 찾아오는 로직 구현해야함 
			String returnMessage = "CALL 시스템에 오신것을 환영합니다.\n**한문장씩 쓰세요.\n**문장 끝에 . ? 를 써 주세요.";
			
			resultMap.put("returnSpeecher", "bot"); //첫발화는 bot으로 발화자 고정 
			resultMap.put("returnStatus", returnStatus);
			resultMap.put("returnMessage",returnMessage );
			break;
		case START_DIALOG:
			//js에서 특정 조건에 따라 타이머 맞추고 스크립트 메소드 실행시킬 수 있는지 확인 
			//스텝별로 발화자, 다음상태, 발화문을 결정하는 판별식이 다르게 적용되어야 함 
			returnStatus = DialogStatus.START_DIALOG.getStatusCd();
			//지금은 하드코딩인데 스크립트 파일에서 찾아오는 로직 구현해야함 
			
			resultMap.put("returnSpeecher", "user"); //첫발화는 bot으로 발화자 고정 
			resultMap.put("returnStatus", returnStatus);
			resultMap.put("returnMessage",message );
			break;
		case GREETING:
			
			break;
		case APPROACH_TOPIC:
			
			break;
		//아직도 엄청많이 남음 
		default:
			break;
		}
		
		return resultMap;
	}

}
