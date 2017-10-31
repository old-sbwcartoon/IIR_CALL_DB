package com.iirtech.chatbot.controller;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import javax.servlet.http.HttpSession;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.iirtech.chatbot.dto.MessageInfo;
import com.iirtech.chatbot.service.ChatbotNLPService;
import com.iirtech.chatbot.service.ChatbotScriptService;
import com.iirtech.chatbot.service.ChatbotService;
import com.iirtech.common.enums.DialogStatus;
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
	 * 시스템 프로퍼티 변수 
	 * systemFilePath: 시스템 파일 저장경로 
	 * userFilePath: 사용자별 파일 저장경로 
	 * userSeqFileName: 사용자 id pwd 별 seq 정보 저장파일명  
	 */
	@Value("#{systemProp['filepath']}") 
	String filePath;
	@Value("#{systemProp['userseqfilename']}") 
	String userSeqFileName;
	@Value("#{systemProp['systemdelimeter']}") 
	String systemDelimeter;
	
	/**
	 * @Method   : Index
	 * @작성일     : 2017. 8. 13. 
	 * @작성자     : choikino
	 * @explain : 로그인 하는 시작페이지로 리다이렉트
	 */
	@RequestMapping(value = "index.do")
	public void index() {
		log.debug("************************index.do*************************");
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
	String urlSystemImgFilePath;
	@Value("#{systemProp['filepath']}")
	String urlFilePath;
	@Value("#{systemProp['redirectpath']}") 
	String redirectPath;
	@RequestMapping(value = "chatbotMain.do")
	public ModelAndView chatbotMain(@RequestParam Map<String, Object> param, HttpSession session) {
		log.debug("*************************chatbotMain.do*************************");
		ModelAndView mv = new ModelAndView("chatbotMain");
		try {
			
			log.debug("session_id>>>>>>" + session.getAttribute("id"));
			if(param.isEmpty()) {
				String errorMsg = "잘못된 접근입니다. 다시 로그인 하세요.";
				try {
					errorMsg = URLEncoder.encode(errorMsg, "utf8");
				} catch (UnsupportedEncodingException e) {
					e.printStackTrace();
				}
				mv = new ModelAndView("redirect:/error.do?errorMsg="+errorMsg);
				return mv;
			}
			
			//사용자 정보 조회 후 없으면 create 있으면 update 
			//userInfoMap : id, password, userSeq, isOldUserYN, loginTime
			Map<String, Object> userInfoMap = cbs.mergeSystemFile(param, session);
			//사용자 대화접속 이력 파일 생성 
			cbs.mergeUserHistFile(userInfoMap, session);
			
			//Session에 기억할 정보들(userSeq,userHistFile,userDialogFile) 저장!
			String userSeq = String.valueOf(userInfoMap.get("userSeq"));
			String userType = String.valueOf(userInfoMap.get("userType"));
			String loginTime = String.valueOf(userInfoMap.get("loginTime"));
			session.setAttribute("userSeq", userSeq);
			session.setAttribute("id", String.valueOf(param.get("id")));
			
			//훗날 문장 최적화에 사용될 조건 정보들을 담는 객체 선언
			Map<String,Object> conditionInfoMap = new HashMap<String, Object>();
			conditionInfoMap.put("userType", userType);//사용자 타입 세팅 : oldUser, newUser
			conditionInfoMap.put("loginTime", loginTime);
			session.setAttribute("conditionInfoMap", conditionInfoMap);
			mv.addObject("conditionInfoMap", new ObjectMapper().writeValueAsString(conditionInfoMap));
			
			String scriptFilePath = urlFilePath + "script/bot/";
			String initStatusCd = "0000";
			String initMsgIdx = "0";
			String initSubMsgIdx = "0";
			MessageInfo info = new MessageInfo(initStatusCd,scriptFilePath);
			String initInfo = info.getMessageByIdx(0).replace("\\n", "<br>");
			
			// loginTime으로 파일 초기화
			String rootPath = System.getProperty("user.home") + "/Documents/chatbot";
			log.debug("rootPath>>>>>>"+rootPath);
			userInfoMap.put("orglMessage", info.getMessageByIdx(Integer.parseInt(initMsgIdx)).replace("\\n","<br>")); //로그 기록하기 위해 tag 변환
			userInfoMap.put("id", String.valueOf(param.get("id")));
			userInfoMap.put("isUser", false);
			userInfoMap.put("dialogTime", userInfoMap.get("loginTime"));
			userInfoMap.put("statusCd", initStatusCd);
			userInfoMap.put("messageIdx", initMsgIdx);
			userInfoMap.put("subMessageIdx", initSubMsgIdx);
			cbs.makeUserDialogFile(userInfoMap, rootPath);
			
			//시각화 
			String dialogLogStr = cbs.makeDialogLogString(userInfoMap,rootPath);
			
			mv.addObject("statusCd", initStatusCd);
			mv.addObject("messageIdx", initMsgIdx);
			mv.addObject("subMessageIdx", initSubMsgIdx);
			mv.addObject("initInfo", initInfo);
			mv.addObject("imgSrc", urlSystemImgFilePath);
			mv.addObject("loginTime", loginTime);
			mv.addObject("dialogLogStr", dialogLogStr);
			
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
	
	//봇발화 수정하는 경우 대화로그 파일 수정로직  fixedText  messageIdx  statusCd
	@RequestMapping(value = "createNewScriptFile.do")
	public ModelAndView createNewScriptFile(@RequestParam Map<String, Object> param, HttpSession session) {
		log.debug("*************************createNewScriptFile.do*************************");
		ModelAndView mv = new ModelAndView("jsonView");
		//본래 저장된 파일을 읽어서 수정하여 저장
		//statusCd|msgIdx|Bot|BotText|time|seq
		//statusCd|msgIdx|Fix|fixedText|time|seq
		//statusCd|msgIdx|User|UserText|time|seq
//		String userSeq = session.getAttribute("userSeq").toString();
//		param.put("userSeq", userSeq);
		String userId = session.getAttribute("id").toString();
		param.put("id", userId);
		String rootPath = System.getProperty("user.home") + "/Documents/chatbot";
		cbs.addFixedTextToDialogFile(param, rootPath);
		
		//시각화 
		String dialogLogStr = cbs.makeDialogLogString(param,rootPath);
		
		mv.addObject("result", "success");
		mv.addObject("dialogLogStr", dialogLogStr);
		return mv;
	}

	@RequestMapping(value = "checkId.do", produces="text/plain;")
	@ResponseBody
	public String isNewId(String id) {
		boolean isNewId = true;
		
		String systemFilePath = System.getProperty("user.home") + "/Documents/chatbot/systemfile/";
//		String systemFilePath = session.getServletContext().getRealPath("resources/file/systemfile");
		List<String> userInfos = cbu.readFileByLine(systemFilePath, userSeqFileName);

		for (String userInfo : userInfos) {
			String fileUserId = userInfo.split("\\"+systemDelimeter)[1];
			if (fileUserId.equals(id)) {
				isNewId = false;
				break;
			}
		}
		return isNewId ? Boolean.TRUE.toString() : Boolean.FALSE.toString();
	}
	
	@RequestMapping(value = "checkLogin.do", produces="text/plain;")
	@ResponseBody
	public String checkIdAndPassword(String id, String password) {
		boolean isLoginOk = false;
		
		String systemFilePath = System.getProperty("user.home") + "/Documents/chatbot/systemfile/";
		List<String> userInfos = cbu.readFileByLine(systemFilePath, userSeqFileName);

		if (!Boolean.getBoolean(isNewId(id))) {
			for (String userInfo : userInfos) {
				String fileUserId = userInfo.split("\\"+systemDelimeter)[1];
				String fileUserPwd = userInfo.split("\\"+systemDelimeter)[2];
				String encPassword = cbu.encryptPwd(password);
				if (fileUserId.equals(id) && fileUserPwd.equals(encPassword)) {
					isLoginOk = true;
					break;
				}
			}
		} else {
			isLoginOk = false;
		}
		
		return isLoginOk ? Boolean.TRUE.toString() : Boolean.FALSE.toString();
	}
	
	@RequestMapping(value = "signup.do", produces="text/plain;")
	@ResponseBody
	public String signup(String id, String password) {
		boolean isSucceed = true;
		
		String systemFilePath = System.getProperty("user.home") + "/Documents/chatbot/systemfile/";
		
		String userSeq = UUID.randomUUID().toString().replace("-", "").replace(systemDelimeter, "");
		String encPassword = cbu.encryptPwd(password);
		
		String content = userSeq + systemDelimeter + id + systemDelimeter + encPassword;
		List<String> contents = new ArrayList<String>();
		contents.add(content);
		cbu.writeFile(systemFilePath, userSeqFileName, contents, true);

		return isSucceed ? Boolean.TRUE.toString() : Boolean.FALSE.toString();
	}
	
	//자신이 작업한 내용을 확인하고 다운라드 받을 수 있는 페이지
	@RequestMapping(value = "error.do")
	public ModelAndView error(@RequestParam Map<String, Object> param, HttpSession session) {
		log.debug("*************************error.do*************************");
		ModelAndView mv = new ModelAndView("error");
		try {
			String errorMsg;
			errorMsg = URLDecoder.decode(param.get("errorMsg").toString(),"utf8");
			mv.addObject("errorMsg", errorMsg);
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		return mv;
	}
	
}
