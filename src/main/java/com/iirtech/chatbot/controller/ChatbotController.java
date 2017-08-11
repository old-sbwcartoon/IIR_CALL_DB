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
    static int state = -1;
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
	//    int state = -1;
	    Map<String, Object> resultMap = new HashMap();
	    ChatbotModel cbm = new ChatbotModel();
	    DialogueState ds;
	    String inputText = "";
	    int prestate = state;
	    Boolean isUserTurn = false;
	    try {
	    		inputText = (String)param.get("userText");
	    		state = Integer.parseInt((String)param.get("statusCd"));
	    		//getState();
			switch(state) {
			case 0 : //Greeting
				//Greeting's Operation
				isUserTurn = false;//cbm.decideSpeakerTurn(param);
				if(!isUserTurn) {
					//bot
					resultMap.put("Speacker", "bot");
					resultMap.put("Message", "안녕하세요.");
					mv.addAllObjects(resultMap);
				}else {
					//user
					resultMap.put("Speacker", "usr");
					resultMap.put("Message", "반갑습니다.");
					
				}
				//다음 상태결정
				state = 1; //1=Greeting_Wait				
				break;
			case 1: //Greeting_Wait
				//Greeting_Wait's Operation
				// isGreeting(String usr_speak){//입력인 인사인지?}
				// int indenfy_userSpeak(String UserSpeak, int tmp){return 0}
				
				if(prestate == 1) {//1=="hello, too!!!"
					resultMap.put("Speacker", "bot");
					resultMap.put("Message", "오늘은 무엇을 공부해 볼가요?");
					state = 2; //2=topicstart
					break;
				}
                //다음 상태결정
				resultMap.put("Speacker", "bot");
				resultMap.put("Message", "이런 싸가지를 봤");
			   state = 3; //4=징
			   break;
			case 2: // TopicStart
				//TopicStart's Op
				resultMap.put("Speacker", "bot");
				resultMap.put("Message", "오늘은 여행에 대해 이야기합시.");
			   state = 4; //approach topic
			   break;
			case 3: //punishment
				if(state == 3) {//3==no answer
				resultMap.put("Speacker", "bot");
				resultMap.put("Message", "맞을래 말할");
				}
			    state = 2;//5==2
				break;
			case 4: //
				resultMap.put("Speacker", "bot");
				resultMap.put("Message", "한국에서 가보고 싶었던 곳이 있나요?");
			}
			log.debug("this is received integer: " + state);
			log.debug("this is received text: " + inputText);
			mv.addAllObjects(resultMap);
		} catch (Exception e) {
			e.printStackTrace();
		}
	    return mv;
	}
	
}
