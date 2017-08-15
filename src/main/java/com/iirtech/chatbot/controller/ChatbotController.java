package com.iirtech.chatbot.controller;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpSession;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import com.iirtech.chatbot.dto.MessageInfo;
import com.iirtech.chatbot.service.ChatbotNLPService;
import com.iirtech.chatbot.service.ChatbotScriptService;
import com.iirtech.chatbot.service.ChatbotService;
import com.iirtech.common.utils.ChatbotUtil;


/**
 * @Package   : com.iirtech.chatbot.controller
 * @FileName  : ChatbotController.java
 * @작성일       : 2017. 8. 13. 
 * @작성자       : choikino
 * @explain : 이르테크 챗봇 시스템의 메인 컨트롤러
 */
@Controller
public class ChatbotController {
	
	Logger log = Logger.getLogger(this.getClass());

	@Autowired
	private ChatbotService cbs;
	@Autowired
	private ChatbotScriptService cbss;
	@Autowired
	private ChatbotNLPService cbns;
	@Autowired
	private ChatbotUtil cbu;
	
	/**
	 * @Method   : Index
	 * @작성일     : 2017. 8. 13. 
	 * @작성자     : choikino
	 * @explain : 로그인 하는 시작페이지로 리다이렉트
	 */
	@RequestMapping(value = "index.do")
	public void Index() {
		log.debug("*************************index.do*************************");
	}
	
	/**
	 * @Method   : chatbotMain
	 * @작성일     : 2017. 8. 13. 
	 * @작성자     : choikino
	 * @explain : 로그인 후 사용자 정보 조회 및 파일 생성하여 채팅페이지로 이동 
	 * @param  id, password
	 * @return session(userSeq, userType, dialogStatus, dialogTime), resultmap(speecher, message, imgsrc)
	 */
	@Value("#{systemProp['imgfilepath']}") 
	String systemImgFilePath;
	@Value("#{systemProp['scriptfilepath']}") 
	String scriptFilePath;
	@RequestMapping(value = "chatbotMain.do")
	public ModelAndView chatbotMain(@RequestParam Map<String, Object> param, HttpSession session) {
		log.debug("*************************chatbotMain.do*************************");
		ModelAndView mv = new ModelAndView("chatbotMain");
		try {
			//사용자 정보 조회 후 없으면 create 있으면 update 
			//userInfoMap : id, password, userSeq, isOldUserYN, loginTime
			Map<String, Object> userInfoMap = cbs.mergeSystemFile(param);
			//사용자 대화접속 이력 파일 생성 
			cbs.mergeUserHistFile(userInfoMap);
			
			//Session에 기억할 정보들(userSeq,userHistFile,userDialogFile) 저장!
			String userSeq = String.valueOf(userInfoMap.get("userSeq"));
			String userType = String.valueOf(userInfoMap.get("userType"));
			session.setAttribute("userSeq", userSeq);
			//훗날 문장 최적화에 사용될 조건 정보들을 담는 객체 선언
			Map<String,Object> conditionInfoMap = new HashMap<String, Object>();
			conditionInfoMap.put("userType", userType);//사용자 타입 세팅 : oldUser, newUser
			session.setAttribute("conditionInfoMap", conditionInfoMap);
			
			MessageInfo info = new MessageInfo("0000",scriptFilePath);
			String initInfo = info.getMessagesByIdx(0).replace("\\n", "<br>");
			mv.addObject("initInfo", initInfo);
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return mv;
	}
	
	/**
	 * @Method   : inputPreprocess
	 * @작성일     : 2017. 8. 13. 
	 * @작성자     : choikino
	 * @explain : 실제로 봇과 사용자의 대화가 진행되는 메인 메소드 
	 * @param session(userSeq,userType, dialogStatus, dialogTime), param(userText)
	 * @return session(userSeq, userType, dialogStatus, dialogTime), resultmap(speecher, message, imgsrc)
	 */
	@RequestMapping(value = "messageInput.json")
	public ModelAndView inputPreprocess(@RequestParam Map<String, Object> param, HttpSession session) {
		log.debug("*************************messageInput.json*************************");
		ModelAndView mv = new ModelAndView("jsonView");
        Map<String, Object> resultMap = new HashMap<String, Object>();
        
		// 1. 상태정보를 확인한다.
		// 2. 입력문장을 언어처리(전처리+언어분석)한다.
		// 3. 현재 상태에서 처리해야 하는 입력데이터 처리 -> 다음 상태 결정, 출력여부, 입력데이터의 처리/가공
		// 4. 처리결과 리턴
		// 5. 입력대기
        
        try {
            //세션에서 가장 최근의 대화정보 가져오기 
	    		String userSeq = session.getAttribute("userSeq").toString();
	    		String statusCd = String.valueOf(param.get("statusCd"));
	    		Map<String,Object> conditionInfoMap = (Map<String, Object>) session.getAttribute("conditionInfoMap");
	    		String userType = String.valueOf(conditionInfoMap.get("userType"));
	    		
	    		//입력문 넘겨받아 메시지 생성 메소드에 넘겨줌, inputText 는 null 값이 될 수도 있다.
	    		String inputText = String.valueOf(param.get("userText"));
	    		String procText = inputText; //오리지널 문자열 보존 차원
	    		if(procText != null && procText != "") {
	    			//전처리 메소드는 preprocess안에서 다시 여러 단계로 확장될 예정
	    			conditionInfoMap = cbns.preProcess(procText);//List textTypes, String procText
	    			conditionInfoMap.put("userType", userType);
	    			procText = String.valueOf(conditionInfoMap.get("procText"));
	    		}
	    		String messageIdx = String.valueOf(param.get("messageIdx"));
	    		
	    		//세션의 lastDialogStatus 값, 입력문장 등 정보를 가지고 발화자, 상태코드, 메시지 생성
	    		//conditionInfoMap 에는 String userType, List textTypes 이 들어있음.
	    		Map<String, Object> messageInfo = cbss.getMessageInfo(statusCd, procText, messageIdx, conditionInfoMap);
	    		String returnStatus = messageInfo.get("returnStatus").toString();
	    		String returnMessage = messageInfo.get("returnMessage").toString();
	    		String returnMessageIdx = messageInfo.get("returnMessageIdx").toString();
	    		
	    		String dialogTime = cbu.getYYYYMMDDhhmmssTime(System.currentTimeMillis());
	    		
	    		//사용자 대화내용 로그 파일 업데이트!!
	    		Map<String,Object> userDialogInfoMap = new HashMap<String, Object>();
	    		userDialogInfoMap.put("userSeq", userSeq);
	    		userDialogInfoMap.put("orglMessage", inputText);
	    		userDialogInfoMap.put("procMessage", procText);
	    		userDialogInfoMap.put("dialogTime", dialogTime);
	    		
	    		// 상태체크해서 최초 시스템 시작이 아닌경우에는 updateUserDialogFile 실행 
	    		cbs.makeUserDialogFile(userDialogInfoMap);
	    		
	    		//화면에 뿌릴 데이터 세팅 
	    		resultMap.put("message", returnMessage);
			resultMap.put("messageIdx", returnMessageIdx);
			resultMap.put("statusCd", returnStatus);
	    		resultMap.put("imgSrc", systemImgFilePath);
	    		mv.addAllObjects(resultMap);

		} catch (Exception e) {
			e.printStackTrace();
		}
        
	    return mv;
	}
	
}
