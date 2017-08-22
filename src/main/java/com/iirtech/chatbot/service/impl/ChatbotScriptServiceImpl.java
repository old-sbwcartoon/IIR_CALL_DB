package com.iirtech.chatbot.service.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Random;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.iirtech.chatbot.dto.MessageInfo;
import com.iirtech.chatbot.service.ChatbotScriptService;
import com.iirtech.common.enums.DialogStatus;
import com.iirtech.common.enums.Operation;
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

	@Value("#{systemProp['systemdelimeter']}") 
	String systemDelimeter;
	@Value("#{systemProp['imgfilepath']}") 
	String urlSystemImgFilePath;
	@Value("#{systemProp['filepath']}") 
	String urlFilePath;
	
	@Override
	public Map<String, Object> getMessageInfo(String statusCd, String procInputText
			, String messageIdx, Map<String,Object> conditionInfoMap) {
		log.debug("*************************getMessageInfo*************************");
		
		//컨트롤러로 리턴할 리턴 값들을 담는 맵객체 
		Map<String, Object> resultMap = new HashMap<String, Object>();
		String scriptFilePath = urlFilePath + "script/bot/";
		if (statusCd != DialogStatus.END_DIALOG.getStatusCd()) {//마지막이 아닐 때
			//여기서 스크립트 파일 읽어옴 
			//APPROACH_TOPIC에서 TOPIC이 정해진 후 해당 스크립트를 다 탄 시점부터 topic경로 적용
			//이 정해졌을 때 트리거를 받아 스크립트 경로를 토픽으로 변환해주기 
			scriptFilePath = this.addTopicScriptPath(statusCd, scriptFilePath, conditionInfoMap);
			MessageInfo info = new MessageInfo(statusCd, scriptFilePath);
			
			int nextIdx = Integer.parseInt(messageIdx) + 1; //한 파일(statusCd) 내부에서의 index=한 줄
			String nextMessages = info.getMessageByIdx(nextIdx);
			Map<String,Object> applySysOprtResultMap = null;
			String nextMessage = "";
			
			if (nextMessages == null ) {
				//index 해당 문장이 없다면 (해당 statusCd의 봇 발화 모두 진행했다면)
				//다음 statusCd 데이터 불러옴
				//APPROACH_TOPIC 주제 진입 스크립트의 마지막줄일때만 스크립트 파일경로에 주제경로 구하기 
				if(statusCd.equals(DialogStatus.APPROACH_TOPIC.getStatusCd())) {
					scriptFilePath = this.addTopicScriptPath(info.getNextStatusCd(), scriptFilePath, conditionInfoMap);
				}
				//END_TOPIC 주제 종료 스크립트의 마지막줄일때만 스크립트 파일경로의 주제경로 제거
				if(statusCd.equals(DialogStatus.REMIND.getStatusCd())) {
					scriptFilePath = urlFilePath + "script/bot/";
					//컨트롤러 세션에서 CIT TOPIC 값 제거할 트리거 담아줌
					//CITDelete/deleteType TOPIC이면 세션에서 CIT
					resultMap.put("CITDelete", "TOPIC");
				}
				info = new MessageInfo(info.getNextStatusCd(), scriptFilePath);
				nextMessages = info.getMessageByIdx(0);
				nextIdx = 0;
			}
			//현재 nextMessages 는 | 가 붙은 상태임. 따라서 optimize할때는 짤라내고 루프돌면서 한개씩 처리
			//인자값으로 conditionInfoMap(oldUser, newUser , isPositive/isNegative/isAsking)등의 정보들을 넣어줌 
			applySysOprtResultMap = (Map<String, Object>) this.applySysOprt(nextMessages, conditionInfoMap);
			//applySysOprtResultMap 안에는 optmzMessage-not null 와 CIT(keywordType) -nullable
			String optmzMessage = (String) applySysOprtResultMap.get("optmzMessage");
			if(applySysOprtResultMap.get("CIT")!=null) {
				String CIT = (String)applySysOprtResultMap.get("CIT");
				resultMap.put("CIT", CIT);//TOPIC
			}
			nextMessage = this.parseForHtml(optmzMessage);
			log.debug("statusCd>>>>>>>>>"+statusCd);
			log.debug("optimizedMsg>>>>>>>>>"+nextMessage);
			
			resultMap.put("statusCd", info.getStatusCd());
			resultMap.put("message", nextMessage);
			resultMap.put("messageIdx", nextIdx);
		}
		return resultMap;
	}

	private String addTopicScriptPath(String statusCd, String scriptFilePath,Map<String, Object>conditionInfoMap) {
		if(conditionInfoMap.get("CITKeyword")!=null) {
			String CITKeyword = (String) conditionInfoMap.get("CITKeyword");
			String[] CITs = CITKeyword.split("\\"+systemDelimeter);
			String CITValue = CITs[0]; //dict에서 여행에 매핑되는 값인 travel
			String keywordType = CITs[1]; //TOPIC
			//만약 키워드타입이 TOPIC이면 파일경로를 /CITValue/ 만큼 depth를 더 파고듦  
			if(keywordType.equals("TOPIC") && !statusCd.equals(DialogStatus.APPROACH_TOPIC.getStatusCd())) {
				scriptFilePath += CITValue + "/";
				log.debug("CITKeywordPath>>>>>>>>>>>"+scriptFilePath);
			}
		}
		return scriptFilePath;
	}
	
	//메시지의 시스템 명령어들을 operation enum을 사용해 파싱하고 한개의 문장만을 골라서 리턴함 
	@Override
	public Object applySysOprt(String nextMessage, Map<String, Object> conditionInfoMap) {
		log.debug("*************************optmzMessage*************************");
		Map<String, Object> resultMap = new HashMap<String, Object>();
		//현재 nextMessages 는 | 가 붙은 상태임. 따라서 optimize할때는 짤라내고 루프돌면서 한개씩 처리
		String[] nextMessagesArr = nextMessage.split("\\|\\|");
		//후보 문장들을 담아둘 리스트 객체: 나중에 이중에서 한개만 랜덤하게 추출 
		List<String> candidateNextMessages = new ArrayList<String>();
		for(int i=0; i<nextMessagesArr.length; i++) {
			//정규표현식으로 한개 문장안의 모든 시스템명령문구를 찾음 
			String candidateMessage = nextMessagesArr[i];
			List<String> sysOprts = this.findAllOperationStrings(candidateMessage);
			//원래 찾아낸 operation String 과 parsing 완료된 String 의 key:value 셑
			Map<String,Object> oprtStrParsStrSet = new HashMap<String, Object>();
			
			int keyIdx = 0; //같은 operation, parser가 겹칠 경우 덮어쓰지 않도록 하는 보조 index
			if(!sysOprts.isEmpty()) {//시스템 명령어가 존재하지 않는 문장은 그대로 nextMessagesArr[i]가 입력되어있음
				for (String operationString : sysOprts) {
					//enum매칭할 키워드 추출
					String tempStr = operationString.substring(1, operationString.length()-1);
					String key = tempStr.split("\\"+systemDelimeter)[0];
					
					String operrationStringWithKeyIdx = "operationString" + keyIdx;
					String parserStringWithKeyIdx = "parserString" + keyIdx;
					// operationString = {NNP|location|헬싱키} , parserString = __헬싱키(location)__
					oprtStrParsStrSet.put(operrationStringWithKeyIdx, operationString);
					switch (Operation.get(key)) {
					case NNP:
						oprtStrParsStrSet.put(parserStringWithKeyIdx,Operation.NNP.doParse(operationString,null,null));
						break;
					case IF:
						//conditionInfoMap 에는 String userType, List textTypes 이 들어있음.
						oprtStrParsStrSet.put(parserStringWithKeyIdx,Operation.IF.doParse(operationString,null,conditionInfoMap));
						break;
					case IMG:
						oprtStrParsStrSet.put(parserStringWithKeyIdx,Operation.IMG.doParse(operationString,urlSystemImgFilePath,null));
						break;
					case STYL:
						oprtStrParsStrSet.put(parserStringWithKeyIdx,Operation.STYL.doParse(operationString,null,null));
						break;
					case CIT:
						//{CIT|TOPIC} >> TOPIC.dict 사전을 찾아서 안에 있는 어휘들을 key:value set으로 만들어 리턴
						String dictName = (String) Operation.CIT.doParse(operationString,null,null);
						resultMap.put("CIT", dictName);
						oprtStrParsStrSet.put(parserStringWithKeyIdx,"");//처리됐으니 시스템명령어 null string 처리해서 없앰
						break;
					default:
						//현재까지 시스템 명령어 목록에 없는 내용. 업데이트 내용이 누락되거나 오타일 가능성 체크 
						break;
					}//switch_case
					keyIdx++;
				}//switch-case 도는 for문 
				//파싱결과를 적용 
				candidateMessage = this.applyParserString(candidateMessage,oprtStrParsStrSet);
			}
			//if조건에 의해 걸러져 ""이 된 값들을 걸러낸다.
			if (!candidateMessage.equals("")&&!candidateMessage.equals(" ")) {
				candidateNextMessages.add(candidateMessage);
			}
		}// |으로 자른 문장 수 만큼 도는 for문 
		//candidateNextMessages 값이 1이상일 경우 랜덤하게 한문장을 초이스하여 리턴 
		resultMap.put("optmzMessage", this.selectOneNextMessage(candidateNextMessages));
		return resultMap;
	}
	
	private String selectOneNextMessage(List<String> candidateNextMessages) {
		String result = "";
		if(!candidateNextMessages.isEmpty()) {
			Random rd = new Random();
			int randomIdx = rd.nextInt((candidateNextMessages.size()-1)+1);
			result = candidateNextMessages.get(randomIdx);
			if(result.equals("")) {
				this.selectOneNextMessage(candidateNextMessages);
			}
		}
		return result;
	}
	
	//파싱결과를 적용 
	private String applyParserString(String candidateMessage, Map<String, Object> oprtStrParsStrSet) {
		String result = "";
		
		//key : operationString parserString , value : {IF|oldUser|또왔네요.} 또왔네요.
		String chgMessage = candidateMessage;
		String targetStr  = "";
		String parsingStr = "";
		for (int i = 0; i < oprtStrParsStrSet.size() / 2; i++) {
			targetStr  = (String)oprtStrParsStrSet.get("operationString"+i);
			parsingStr = (String)oprtStrParsStrSet.get("parserString"+i);
			chgMessage = chgMessage.replace(targetStr, parsingStr);
		}
		result = chgMessage;

		return result;
	}

	//문장에서 모든 시스템 명령어들을 찾아서 넘겨주는 메소드 
	@Override
	public List<String> findAllOperationStrings(String message) {
		List<String> operations = new ArrayList<String>();
		String regex = "\\{(.*?)\\}";
		Pattern pattern = Pattern.compile(regex);
		Matcher matcher = pattern.matcher(message);
		while (matcher.find()) {
			operations.add(matcher.group());
		}
		return operations;
	}
	
	/*
	 * java 문법 기호를 html 문법 기호로 변환
	 */
	public String parseForHtml(String message) {
		String result = "";
		String chgMessage = message;
		ArrayList<String> javaArr = new ArrayList<String>();
		HashMap<String, String> htmlMap = new HashMap<String, String>();
		
		javaArr.add("\\n");
		htmlMap.put("\\n", "<br>");

		String origin   = "";
		String replaced = "";
		for (int i = 0; i < javaArr.size(); i++) {
			
			origin   = javaArr.get(i);
			replaced = htmlMap.get(origin);
			chgMessage = chgMessage.replace(origin, replaced);
		}
		result = chgMessage;

		return result;
	}
}
