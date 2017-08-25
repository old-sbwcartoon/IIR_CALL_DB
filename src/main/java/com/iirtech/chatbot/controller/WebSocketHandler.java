package com.iirtech.chatbot.controller;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.iirtech.chatbot.service.ChatbotNLPService;
import com.iirtech.chatbot.service.ChatbotScriptService;
import com.iirtech.chatbot.service.ChatbotService;
import com.iirtech.common.enums.DialogStatus;
import com.iirtech.common.utils.ChatbotUtil;


//@Component
//@ServerEndpoint("/websocket")
public class WebSocketHandler extends TextWebSocketHandler {

	@Autowired
	private ChatbotService cbs;
	@Autowired
	private ChatbotScriptService cbss;
	@Autowired
	private ChatbotNLPService cbns;
	@Autowired
	private ChatbotUtil cbu;
	
	@Value("#{systemProp['imgfilepath']}")
	String urlSystemImgFilePath;
	@Value("#{systemProp['filepath']}")
	String urlFilePath;
	@Value("#{systemProp['redirectpath']}") 
	String redirectPath;
	
	private static final Logger logger = LoggerFactory.getLogger(WebSocketHandler.class);
	
	/**
	 * client 연결시
	 */
	@Override
	public void afterConnectionEstablished(WebSocketSession session) throws Exception {
		logger.info(session.getUri().toString());
		super.afterConnectionEstablished(session);
	}
	
	/**
	 * 
	 */
	@Override
	public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
		logger.info("webSocketHandler 종료");
		super.afterConnectionClosed(session, status);
	}
	
	/**
	 * message 받았을 때
	 */
	@Override
//	protected void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
//		logger.info(message.getPayload());
//		// session에 담음
//		session.sendMessage(new TextMessage(message.getPayload() + "ddsfsdf"));
//		super.handleTextMessage(session, message);
//	}
	public void handleMessage(WebSocketSession session, WebSocketMessage<?> message) throws Exception {

		logger.info(message.getPayload().toString());
		
		String rootPath = System.getProperty("user.home") + "/Documents/chatbot";
		logger.info(session.getAttributes().toString());
		Map<String, Object> resultMap = new HashMap<String, Object>();
		
		Map<String, Object> httpSession = session.getAttributes();
//		HttpSession httpSession = getHttpSession(session);

		ObjectMapper jacksonMapper = new ObjectMapper();
		
		Map<String, Object> param = new HashMap<String, Object>();
		param = jacksonMapper.readValue(message.getPayload().toString(), new TypeReference<Map<String, Object>>(){});
		// 1. 상태정보를 확인한다.
		// 2. 입력문장을 언어처리(전처리+언어분석)한다.
		// 3. 현재 상태에서 처리해야 하는 입력데이터 처리 -> 다음 상태 결정, 출력여부, 입력데이터의 처리/가공
		// 4. 처리결과 리턴
		// 5. 입력대기
        
        try {
            //세션에서 가장 최근의 대화정보 가져오기 
	    		String userSeq  = httpSession.get("userSeq").toString();
	    		String statusCd = String.valueOf(param.get("statusCd"));
//	    		Map<String,Object> conditionInfoMap = (Map<String, Object>) httpSession.get("conditionInfoMap");
	    		Map<String,Object> conditionInfoMap =
	    				jacksonMapper.readValue(String.valueOf(param.get("conditionInfoMap")), new TypeReference<Map<String, Object>>(){}); // session 안되므로 임시
	    		
	    		String userType = String.valueOf(conditionInfoMap.get("userType"));

	    		//대화가 끝난경우 현재는 채팅페이지로 리다이렉트 시킴
	    		if(statusCd.equals(DialogStatus.END_DIALOG.getStatusCd())) {
//			    	return (ModelAndView)new ModelAndView("redirect:/" + redirectPath );
	    			resultMap = null;
//	    			session.close(); //session 종료
	    		} else {
	    			//입력문 넘겨받아 메시지 생성 메소드에 넘겨줌, inputText 는 null 값이 될 수도 있다.
		    		String inputText = String.valueOf(param.get("userText"));
		    		String procText = inputText; //오리지널 문자열 보존 차원
		    		
	    			
//		    		if(procText != null && procText != "") { //문장을 입력하지 않아도 작동시킴
		    			//전처리 메소드는 preprocess안에서 다시 여러 단계로 확장될 예정
		    			//CIT값(사용자 인풋텍스트에서 체크해야되는 키워드keywordType)가 있다면 판별 후 추출
//		    			conditionInfoMap = cbns.preProcess(procText);//List textTypes, String procText
		    			conditionInfoMap = addDifferentData(conditionInfoMap, cbns.preProcess(procText));
//		    			if(httpSession.get("CIT")!=null) {
		    			if(conditionInfoMap.get("CIT")!=null) {
//		    				String keywordType = httpSession.get("CIT").toString();
		    				String keywordType = String.valueOf(conditionInfoMap.get("CIT"));
		    				String keyword = cbns.selectKeyword(keywordType, conditionInfoMap);
		    				conditionInfoMap.put("CITKeyword", keyword);
		    			}
		    			conditionInfoMap.put("userType", userType);
		    			procText = String.valueOf(conditionInfoMap.get("procText"));
//		    		}
		    		String messageIdx = String.valueOf(param.get("messageIdx"));
		    		
		    		//세션의 lastDialogStatus 값, 입력문장 등 정보를 가지고 발화자, 상태코드, 메시지 생성
		    		//conditionInfoMap 에는 String userType, List textTypes, List CITKeywords(사용자인풋텍스트에서 추출된 키워드)
		    		//statusCd,message,messageIdx(string)-not null, CIT(map)-nullable 이 들어있음
		    		Map<String, Object> messageInfo = cbss.getMessageInfo(statusCd, procText, messageIdx, conditionInfoMap);
		    		
		    		
		    		
		    		//세션에 저장된 시스템 변수값을 제거해야하는 경우인지 체크
//		    		if(messageInfo.get("CITDelete")!=null && httpSession.get("CIT")!=null) {
		    		if(messageInfo.get("CITDelete")!=null && conditionInfoMap.get("CIT")!=null) {
		    			String deleteType = messageInfo.get("CITDelete").toString();
//		    			String sessionType = httpSession.get("CIT").toString();
		    			String sessionType = conditionInfoMap.get("CIT").toString();
		    			if (deleteType.equals("TOPIC") && sessionType.equals("TOPIC")) {
		    				//topic을 지워야하는 경우 세션에서 제거
//		    				httpSession.remove("CIT");
		    				conditionInfoMap.remove("CIT");
					}
		    		}

		    		//CIT가 있으면 세션에 값넣기 CIT:type으로 넣고 사용자 input 처리하는 부분에서 항상 체크하여 처리 
		    		if(messageInfo.get("CIT") != null) {
//		    			httpSession.put("CIT", messageInfo.get("CIT"));
		    			conditionInfoMap.put("CIT", messageInfo.get("CIT"));
		    		}
		    		String dialogTime = cbu.getYYYYMMDDhhmmssTime(System.currentTimeMillis());
		    		
		    		//사용자 대화내용 로그 파일 업데이트!!
		    		Map<String,Object> userDialogInfoMap = new HashMap<String, Object>();
		    		userDialogInfoMap.put("userSeq", userSeq);
		    		userDialogInfoMap.put("orglMessage", inputText);
		    		userDialogInfoMap.put("procMessage", procText);
		    		userDialogInfoMap.put("dialogTime", dialogTime);
		    		
		    		// 상태체크해서 최초 시스템 시작이 아닌경우에는 updateUserDialogFile 실행 
		    		cbs.makeUserDialogFile(userDialogInfoMap, rootPath);
		    		
		    		//화면에 뿌릴 데이터 세팅 
		    		resultMap = messageInfo;
		    		resultMap.put("imgSrc", urlSystemImgFilePath);
		    		resultMap.put("conditionInfoMap", jacksonMapper.writeValueAsString(conditionInfoMap));

	    		}
		} catch (Exception e) {
			e.printStackTrace();
		}
	    		
		
		/**
		 * client로 보낼 message session에 담음
		 */
		session.sendMessage(new TextMessage(jacksonMapper.writeValueAsString(resultMap)));
		super.handleMessage(session, message);
	}

	
//	public HttpSession getHttpSession(WebSocketSession session) {
//		
//		Map<String, Object> map = session.getAttributes();
//		
//		Iterator<String> keys = map.keySet().iterator();
//		
//		HttpSession result = null;
//		while (keys.hasNext()) {
//			result.setAttribute(keys.next(), map.get(keys.next()));
//		}
//		return result;
//	}
	
	/**
	 * 원본 맵에 새 맵의 값을 추가. 키가 같은 경우 원본 맵의 해당 값을 새 맵의 값으로 바꿈. 
	 * @param originMap
	 * @param newMap
	 * @return
	 */
	public Map<String, Object> addDifferentData(Map<String, Object> originMap, Map<String, Object> newMap) {
		
		Map<String, Object> resultMap = originMap;
		
		Iterator<String> newKeys = newMap.keySet().iterator();
		
		while (newKeys.hasNext()) {
			String key = newKeys.next();
			resultMap.put(key, newMap.get(key));
		}
		
		return resultMap;
	}
}
