package com.iirtech.chatbot.controller;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.Logger;
import org.codehaus.jackson.JsonParseException;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import com.iirtech.chatbot.model.ChatbotModel;
import com.iirtech.common.utils.UtilClass;

/**
 * Handles requests for the application home page.
 */
@Controller
public class ChatbotController {
	
	Logger log = Logger.getLogger(this.getClass());
	
	/**
	 * Simply selects the home view to render by returning its name.
	 */
	@RequestMapping(value = "chatbotMain.do")
	public ModelAndView chatbotMain() {
		ModelAndView mv = new ModelAndView("chatbotMain");
		log.debug("logger test!!");
		return mv;
	}
	
	enum DialogueState {ClassStart, TopicStart, Greeting, Greeting_Wait, ConvStart, ApproachTopic, ConvTopic, StartTopic, LeadTopic, TrainExpression, EndTopoic, EndConv, EndClass }
	@RequestMapping(value = "inputPreprocess.json")
	public ModelAndView inputPreprocess(@RequestParam Map<String, Object> param) {
        ModelAndView mv = new ModelAndView("jsonView");
		//getState
		// 1. 상태정보를 확인한다.
		// 2. 입력문장을 언어처리(전처리+언어석)한다.
		// 3. 현재 상태에서 처리해야 하는 입력데이터 처 -> 다음 상태 결정, 출력여부, 입력데이터의 처리/가공
		// 4. 처리결과 리턴
		// 5. 입력대기
	    int state = -1;
	    Map<String, Object> resultMap = new HashMap();
	    ChatbotModel cbm = new ChatbotModel();  
	    String inputText = "";
	    String speacker = "";
	    try {
	    		inputText = (String)param.get("userText");
	    		state = Integer.parseInt((String)param.get("statusCd"));
			switch(state) {
			case 1: 
//				isUserTurn = cbm.decideSpeakerTurn(param);
//				resultMap = cbm.makeSentence();
				break;
			case 2:
			}
			log.debug("this is received integer: " + state);
			log.debug("this is received text: " + inputText);
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	    return mv;
	}
	
}
