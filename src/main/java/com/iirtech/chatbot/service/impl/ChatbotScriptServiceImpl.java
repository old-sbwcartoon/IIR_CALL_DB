package com.iirtech.chatbot.service.impl;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import org.apache.log4j.Logger;
import org.snu.ids.ha.ma.MorphemeAnalyzer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.iirtech.chatbot.dto.MessageInfo;
import com.iirtech.chatbot.service.ChatbotNLPService;
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
	private ChatbotNLPService cbns;
	@Autowired
	ChatbotUtil cbu;
	
	@Value("#{systemProp['systemdelimeter']}")
	String systemDelimeter;
	@Value("#{systemProp['imgfilepath']}")
	String urlSystemImgFilePath;
	@Value("#{systemProp['filepath']}")
	String urlFilePath;
	
	@Override
	public Map<String, Object> getMessageInfo(String statusCd, String exStatusCd, String procInputText
			, String messageIdx, String subMessageIdx, Map<String,Object> conditionInfoMap, Map<String,Object> shortTermInfoMap
			,MorphemeAnalyzer ma) {
		log.debug("*************************getMessageInfo*************************");
		
		//컨트롤러로 리턴할 리턴 값들을 담는 맵객체 
		Map<String, Object> resultMap = new HashMap<String, Object>();
		String scriptFilePath = urlFilePath + "script/iitp_bot/";
		if (statusCd != DialogStatus.CLOSE.getStatusCd()) {//마지막이 아닐 때
			//여기서 스크립트 파일 읽어옴 
			//APPROACH_TOPIC에서 TOPIC이 정해진 후 해당 스크립트를 다 탄 시점부터 topic경로 적용
			//이 정해졌을 때 트리거를 받아 스크립트 경로를 토픽으로 변환해주기
			

			// 서브 테마 찾기
	    		if(statusCd.equals(DialogStatus.ONGOING_TOPIC.getStatusCd()) && messageIdx.equals("0")) {
				
				exStatusCd = statusCd;
				statusCd = cbns.getSubThemeStatusCd(procInputText, ma);
    				
	    		}

			int nextIdx = Integer.parseInt(messageIdx); //한 파일(statusCd) 내부에서의 index=한 줄
			int nextSubIdx = Integer.parseInt(subMessageIdx);
			int idx = 0;
			
			
			boolean hasReturnToScript = false; // 본래 스크립트 이외 질문, 오류 등 스크립트가 끝났을 경우 true
    			HashMap<String, String> pauseInfo = cbns.getPauseCondition(procInputText);
			
    			// statusCd가 서브 테마가 아니라면
			if ( !DialogStatus.get(statusCd).name().contains("SUB_") ) {
				
				// 질문이나 오류 발생시 pauseInfo 속성 존재함
				// 새 질문
				if (!pauseInfo.isEmpty()) {
					// 새 질문이 질문 속 질문일 경우
					if (shortTermInfoMap.containsKey("pause")) {
						// do nothing
					} else {
						// 새 질문이 스크립트 첫 질문일 경우
						shortTermInfoMap.put("pause", true);
						
						// 서브 테마가 끝난 경우 다시 ongoing_topic으로 진행시
						if (!(statusCd.equals(DialogStatus.ONGOING_TOPIC.getStatusCd()) && messageIdx.equals("1"))) {
							nextIdx--;
							
						}
						if (nextIdx < 0) {
							hasReturnToScript = true;
							nextIdx = -1;
						}
					}
				} else {
					// 일반 응답문
					if (shortTermInfoMap.containsKey("pause")) {
						// 질문 속 일반 응답문일 경우
						shortTermInfoMap.remove("pause");
						
						hasReturnToScript = true;
					} else {
						// 일반 상황의 일반 응답문일 경우
					}
					
					nextIdx++;
				}
				
				idx = nextIdx;
			} else {

				// 새 질문
				if (!pauseInfo.isEmpty()) {
					// 새 질문이 질문 속 질문일 경우
					if (shortTermInfoMap.containsKey("pause")) {
						// do nothing
					} else {
						// 새 질문이 스크립트 첫 질문일 경우
						shortTermInfoMap.put("pause", true);
						nextSubIdx--;
					}
					if (nextSubIdx < 0) {
						hasReturnToScript = true;
						statusCd = exStatusCd;
						conditionInfoMap.put("CIT", "TOPIC");
						conditionInfoMap.put("CITKeyword", "travel|TOPIC");
						nextSubIdx++;
						nextIdx = -1;
						idx = nextIdx;
					} else {
						// 새 질문이 스크립트 첫 질문일 경우
						conditionInfoMap.put("CIT", "SUB");
						conditionInfoMap.put("CITKeyword", "travel/sub|TOPIC");
						idx = nextSubIdx;
					}
					
				} else {
					// 일반 응답문
					idx = nextSubIdx;
					
					if (shortTermInfoMap.containsKey("pause")) {
						// 질문 속 일반 응답문일 경우
						shortTermInfoMap.remove("pause");
						hasReturnToScript = true;
					} else {
						// 일반 상황의 일반 응답문일 경우
						conditionInfoMap.put("CIT", "SUB");
						conditionInfoMap.put("CITKeyword", "travel/sub|TOPIC");
					}
					
					nextSubIdx++;
				}
				
				
			}
				
			
			if (statusCd.equals(DialogStatus.START_CONVERSATION.getStatusCd()) && conditionInfoMap.containsKey("CIT")) {
				conditionInfoMap.remove("CITKeyword");
				conditionInfoMap.remove("CIT");
			}
			
	    		scriptFilePath = this.addTopicScriptPath(statusCd, urlFilePath + "script/iitp_bot/", conditionInfoMap);
			MessageInfo info = new MessageInfo(statusCd, scriptFilePath);
			

			String nextMessage = "";
			if (!pauseInfo.isEmpty()) {
				
				String infoType = pauseInfo.get("infoType");
				
				if (infoType.equals("systemAsk") || infoType.equals("translation")) {
					nextMessage = pauseInfo.get("data");
					
				} else {
					// 사용자 입력문 = 오류 체크일 경우
					
				}
				
				
			} else {

//				scriptFilePath = this.addTopicScriptPath(statusCd, urlFilePath + "script/iitp_bot/", conditionInfoMap);
//				info = new MessageInfo(statusCd, scriptFilePath);
				String nextMessages = info.getMessageByIdx(idx);
				Map<String,Object> applySysOprtResultMap = null;
				
				// 질문이 아닌 경우 또는 해당 스크립트에서 메시지가 없는 경우
				if (nextMessages == null && !hasReturnToScript) {
					int nextAutomataIdx = 0;
					//index 해당 문장이 없다면 (해당 statusCd의 봇 발화 모두 진행했다면)
					//다음 statusCd 데이터 불러옴
					//APPROACH_TOPIC 주제 진입 스크립트의 마지막줄일때만 스크립트 파일경로에 주제경로 구하기 
					if(statusCd.equals(DialogStatus.APPROACH_TOPIC.getStatusCd())) {
						scriptFilePath = this.addTopicScriptPath(info.getNextStatusCd(), scriptFilePath, conditionInfoMap);
					}
					//END_TOPIC 주제 종료 스크립트의 마지막줄일때만 스크립트 파일경로의 주제경로 제거
					if(statusCd.equals(DialogStatus.REMIND.getStatusCd())) {
						scriptFilePath = urlFilePath + "script/iitp_bot/";
						//컨트롤러 세션에서 CIT TOPIC 값 제거할 트리거 담아줌
						//CITDelete/deleteType TOPIC이면 세션에서 CIT
						resultMap.put("CITDelete", "TOPIC");
					}
					//SUB 테마가 끝나고 돌아올 때 폴더를 CIT|TOPIC용으로 돌림
					if(DialogStatus.get(statusCd).name().contains("SUB_")) {
						conditionInfoMap.put("CIT", "TOPIC");
						conditionInfoMap.put("CITKeyword", "travel|TOPIC");
						scriptFilePath = this.addTopicScriptPath(info.getNextStatusCd(), urlFilePath + "script/iitp_bot/", conditionInfoMap);
						if (pauseInfo.isEmpty()) {
							nextIdx++;
						}
						nextAutomataIdx = nextIdx;
					}
					info = new MessageInfo(info.getNextStatusCd(), scriptFilePath);
					nextMessages = info.getMessageByIdx(nextAutomataIdx);
					nextIdx = nextAutomataIdx;
					exStatusCd = statusCd;
				}
				//현재 nextMessages 는 | 가 붙은 상태임. 따라서 optimize할때는 짤라내고 루프돌면서 한개씩 처리
				//인자값으로 conditionInfoMap(oldUser, newUser , isPositive/isNegative/isAsking)등의 정보들을 넣어줌 
				applySysOprtResultMap = (Map<String, Object>) this.applySysOprt(nextMessages, conditionInfoMap);
				//applySysOprtResultMap 안에는 optmzMessage-not null 와 CIT(keywordType) -nullable
				String optmzMessage = (String) applySysOprtResultMap.get("optmzMessage");
				//blank 채우기 //본래 스크립트로 돌아갈 경우에, 직전 사용한 스크립트를 다시 사용
				if (!hasReturnToScript) {
					shortTermInfoMap.put("procInputText", procInputText);
					shortTermInfoMap = getCompleteMap(optmzMessage, statusCd, shortTermInfoMap, ma);
				}
				
				if(applySysOprtResultMap.get("CIT")!=null) {
					String CIT = (String)applySysOprtResultMap.get("CIT");
					resultMap.put("CIT", CIT);//TOPIC
				}
				
				nextMessage = this.parseForHtml(String.valueOf(shortTermInfoMap.get("nextMessage")));
			}
			
			
			log.debug("optimizedMsg>>>>>>>>>"+nextMessage);
			
			resultMap.put("statusCd", info.getStatusCd());
			log.debug("statusCd>>>>>>>>>"+info.getStatusCd());
			resultMap.put("exStatusCd", exStatusCd);
			resultMap.put("message", nextMessage);
			resultMap.put("messageIdx", nextIdx);
			resultMap.put("subMessageIdx", nextSubIdx);
			resultMap.put("scriptFilePath", scriptFilePath);
			// 단기 기억
			resultMap.put("shortTermInfoMap", shortTermInfoMap);
		}
		return resultMap;
	}
	



	private String addTopicScriptPath(String statusCd, String scriptFilePath,Map<String, Object>conditionInfoMap) {
		if(conditionInfoMap.get("CITKeyword")!=null) {
			String CITKeyword = (String) conditionInfoMap.get("CITKeyword");
			String[] CITs = CITKeyword.split("\\"+systemDelimeter);
			String CITValue = CITs[0]; //dict에서 여행에 매핑되는 값인 travel or travel/sub
			String keywordType = CITs[1]; //TOPIC or SUB
			//만약 키워드타입이 TOPIC이면 파일경로를 /CITValue/ 만큼 depth를 더 파고듦  
			if (keywordType.equals("TOPIC") && !statusCd.equals(DialogStatus.APPROACH_TOPIC.getStatusCd())) {
				scriptFilePath += CITValue + "/";
				log.debug("CITKeywordPath>>>>>>>>>>>"+scriptFilePath);
			} else if (keywordType.equals("SUB") /* && !statusCd.equals(DialogStatus.ONGOING_TOPIC.getStatusCd()) */) {
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

			candidateMessage = initPropMessage(candidateMessage,conditionInfoMap, resultMap);
			if (!candidateMessage.equals("")&&!candidateMessage.equals(" ")) {
				candidateNextMessages.add(candidateMessage);
			}
		}
		
		resultMap.put("optmzMessage", this.selectOneNextMessage(candidateNextMessages));
//			
//			
//			List<String> sysOprts = this.findAllOperationStrings(candidateMessage);
//			//원래 찾아낸 operation String 과 parsing 완료된 String 의 key:value 셑
//			Map<String,Object> oprtStrParsStrSet = new HashMap<String, Object>();
//			
//			int keyIdx = 0; //같은 operation, parser가 겹칠 경우 덮어쓰지 않도록 하는 보조 index
//			if(!sysOprts.isEmpty()) {//시스템 명령어가 존재하지 않는 문장은 그대로 nextMessagesArr[i]가 입력되어있음
//				
//				for (String operationString : sysOprts) {
//					
//						//enum매칭할 키워드 추출
//						String tempStr = operationString.substring(1, operationString.length()-1);
//						String key = tempStr.split("\\"+systemDelimeter)[0];
//	
//						
//						//중첩 중괄호 parser 위한 반복문 작업
//						
//						String operrationStringWithKeyIdx = "operationString" + keyIdx;
//						String parserStringWithKeyIdx = "parserString" + keyIdx;
//						if (!key.contains("NNP") && !key.contains("IF") && !key.contains("IMG") && !key.contains("STYL") && !key.contains("CIT")) {
//							// do nothing
//						} else {
//
//							// operationString = {NNP|location|헬싱키} , parserString = __헬싱키(location)__
//							oprtStrParsStrSet.put(operrationStringWithKeyIdx, operationString);
//							
//							switch (Operation.get(key)) {
//								case NNP:	operationString = String.valueOf(Operation.NNP.doParse(operationString,null,null));						break;
//								case IF:	operationString = String.valueOf(Operation.IF.doParse(operationString,null,conditionInfoMap));			break; //conditionInfoMap 에는 String userType, List textTypes 이 들어있음.
//								case IMG:	operationString = String.valueOf(Operation.IMG.doParse(operationString,urlSystemImgFilePath,null));		break;
//								case STYL:	operationString = String.valueOf(Operation.STYL.doParse(operationString,null,null));					break;
//								case CIT:	//{CIT|TOPIC} >> TOPIC.dict 사전을 찾아서 안에 있는 어휘들을 key:value set으로 만들어 리턴
//									String dictName = (String) Operation.CIT.doParse(operationString,null,null);
//									resultMap.put("CIT", dictName);
//									//처리됐으니 시스템명령어 null string 처리해서 없앰
//									operationString = "";																							break;
//								default:																											break; //현재까지 시스템 명령어 목록에 없는 내용. 업데이트 내용이 누락되거나 오타일 가능성 체크
//							}//switch_case
//
//							oprtStrParsStrSet.put(parserStringWithKeyIdx,operationString);
//
//							candidateMessage = this.applyParserString(candidateMessage,oprtStrParsStrSet);
//							
//							
//							int keyIdxInner = 0;
//							Map<String,Object> oprtStrParsStrSetInner = new HashMap<String, Object>();
//							List<String> sysOprtsInner = this.findAllOperationStrings(operationString);
//							for (String operationStringInner : sysOprtsInner) {
//								String tempStrInner = operationStringInner.substring(1, operationStringInner.length()-1);
//								String keyInner = tempStrInner.split("\\"+systemDelimeter)[0];
//			
//								
//								//중첩 중괄호 parser 위한 반복문 작업
//								
//								String operrationStringWithKeyIdxInner = "operationString" + keyIdxInner;
//								String parserStringWithKeyIdxInner = "parserString" + keyIdxInner;
//								if (!keyInner.contains("NNP") && !keyInner.contains("IF") && !keyInner.contains("IMG") && !keyInner.contains("STYL") && !keyInner.contains("CIT")) {
//									// do nothing
//								} else {
//
//									// operationString = {NNP|location|헬싱키} , parserString = __헬싱키(location)__
//									oprtStrParsStrSetInner.put(operrationStringWithKeyIdxInner, operationStringInner);
//									
//									switch (Operation.get(keyInner)) {
//										case NNP:	operationStringInner = String.valueOf(Operation.NNP.doParse(operationStringInner,null,null));					break;
//										case IF:	operationStringInner = String.valueOf(Operation.IF.doParse(operationStringInner,null,conditionInfoMap));		break; //conditionInfoMap 에는 String userType, List textTypes 이 들어있음.
//										case IMG:	operationStringInner = String.valueOf(Operation.IMG.doParse(operationStringInner,urlSystemImgFilePath,null));	break;
//										case STYL:	operationStringInner = String.valueOf(Operation.STYL.doParse(operationStringInner,null,null));					break;
//										case CIT:	//{CIT|TOPIC} >> TOPIC.dict 사전을 찾아서 안에 있는 어휘들을 key:value set으로 만들어 리턴
//											String dictName = (String) Operation.CIT.doParse(operationStringInner,null,null);
//											resultMap.put("CIT", dictName);
//											//처리됐으니 시스템명령어 null string 처리해서 없앰
//											operationStringInner = "";																								break;
//										default:																													break; //현재까지 시스템 명령어 목록에 없는 내용. 업데이트 내용이 누락되거나 오타일 가능성 체크
//									}//switch_case
//									oprtStrParsStrSetInner.put(parserStringWithKeyIdxInner,operationStringInner);
//									keyIdxInner++;
//								}
//							}
//							candidateMessage = this.applyParserString(candidateMessage,oprtStrParsStrSetInner);
////							oprtStrParsStrSet.put(operrationStringWithKeyIdx, operationString);
//							keyIdx++;
//						}
//						candidateMessage = this.applyParserString(candidateMessage,oprtStrParsStrSet);
						//파싱결과를 적용 
//						candidateMessage = this.applyParserString(candidateMessage,oprtStrParsStrSet);
			
						//if조건에 의해 걸러져 ""이 된 값들을 걸러낸다.
//						if (!candidateMessage.equals("")&&!candidateMessage.equals(" ")) {
//							candidateNextMessages.add(candidateMessage);
//						}
//					}// |으로 자른 문장 수 만큼 도는 for문 
					//candidateNextMessages 값이 1이상일 경우 랜덤하게 한문장을 초이스하여 리턴
//				
//		}
		return resultMap;
	}
	
	public String initPropMessage(String str, Map<String, Object> conditionInfoMap, Map<String, Object> resultMap) {
		String candidateMessage = str;
		
		List<String> sysOprts = this.findAllOperationStrings(str);
		
		//원래 찾아낸 operation String 과 parsing 완료된 String 의 key:value 셑
		Map<String,Object> oprtStrParsStrSet = new HashMap<String, Object>();
		
		int keyIdx = 0; //같은 operation, parser가 겹칠 경우 덮어쓰지 않도록 하는 보조 index
		if(!sysOprts.isEmpty()) {//시스템 명령어가 존재하지 않는 문장은 그대로 nextMessagesArr[i]가 입력되어있음
			
			for (String operationString : sysOprts) {
				
					//enum매칭할 키워드 추출
					String tempStr = operationString.substring(1, operationString.length()-1);
					String key = tempStr.split("\\"+systemDelimeter)[0];

					//중첩 중괄호 parser 위한 반복문 작업
					String operrationStringWithKeyIdx = "operationString" + keyIdx;
					String parserStringWithKeyIdx = "parserString" + keyIdx;
					if (!key.contains("NNP") && !key.contains("IF") && !key.contains("IMG") && !key.contains("STYL") && !key.contains("CIT")) {
						// do nothing
					} else {

						// operationString = {NNP|location|헬싱키} , parserString = __헬싱키(location)__
						oprtStrParsStrSet.put(operrationStringWithKeyIdx, operationString);
						
						switch (Operation.get(key)) {
							case NNP:	operationString = String.valueOf(Operation.NNP.doParse(operationString,null,null));					break;
							case IF:		operationString = String.valueOf(Operation.IF.doParse(operationString,null,conditionInfoMap));			break; //conditionInfoMap 에는 String userType, List textTypes 이 들어있음.
							case IMG:	operationString = String.valueOf(Operation.IMG.doParse(operationString,urlSystemImgFilePath,null));	break;
							case STYL:	operationString = String.valueOf(Operation.STYL.doParse(operationString,null,null));					break;
							case CIT:	//{CIT|TOPIC} >> TOPIC.dict 사전을 찾아서 안에 있는 어휘들을 key:value set으로 만들어 리턴
								String dictName = (String) Operation.CIT.doParse(operationString,null,null);
								resultMap.put("CIT", dictName);
								//처리됐으니 시스템명령어 null string 처리해서 없앰
								operationString = "";																						break;
							default:																											break; //현재까지 시스템 명령어 목록에 없는 내용. 업데이트 내용이 누락되거나 오타일 가능성 체크
						}//switch_case

						oprtStrParsStrSet.put(parserStringWithKeyIdx,operationString);
						candidateMessage = this.applyParserString(candidateMessage,oprtStrParsStrSet);
						
						candidateMessage = initPropMessage(candidateMessage, conditionInfoMap, resultMap);
						keyIdx++;
					}
			}
		}
		return candidateMessage;
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
//		String regex = "\\{(.*?)\\}";
//		Pattern pattern = Pattern.compile(regex);
//		Matcher matcher = pattern.matcher(message);
		
		int bracketOpenCnt  = 0;
		int bracketCloseCnt = 0;
		String tmpStrSlice  = message;
		String tmpStrStart  = message;
		int sliceEndIdx     = 0;
		
		while (tmpStrSlice.contains("{")) {
			bracketOpenCnt++;
			tmpStrStart = tmpStrSlice.substring(tmpStrSlice.indexOf("{"), tmpStrSlice.length());
			tmpStrSlice = tmpStrStart.substring(1);
			while (bracketOpenCnt != bracketCloseCnt) {
				
				if (tmpStrSlice.contains("{") && tmpStrSlice.indexOf("{") < tmpStrSlice.indexOf("}")) {
					tmpStrSlice = tmpStrSlice.substring(tmpStrSlice.indexOf("{"), tmpStrSlice.length());
					bracketOpenCnt++;
				}
				if (tmpStrSlice.contains("}")) {
					tmpStrSlice = tmpStrSlice.substring(tmpStrSlice.indexOf("}")+1, tmpStrSlice.length());
					bracketCloseCnt++;
				}
			}
			
			if (!tmpStrSlice.isEmpty()) {
				sliceEndIdx = message.indexOf(tmpStrSlice);
			} else {
				sliceEndIdx = message.lastIndexOf("}") + 1;
			}
			
			if (sliceEndIdx < message.indexOf(tmpStrStart)) {
				sliceEndIdx = message.lastIndexOf(tmpStrSlice);
			}
			operations.add(message.substring(message.indexOf(tmpStrStart), sliceEndIdx));
			bracketOpenCnt  = 0;
			bracketCloseCnt = 0;
		}
//		while (matcher.find()) {
//			operations.add(matcher.group());
//		}
		return operations;
	}
	
	/**
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
	
	
	/**
	 * script의 blank ({where}, {name} 등등) 채우고 적합한 조사를 선택해서 완성된 단기 기억 정보를 리턴
	 * @param nextMessage
	 * @param statusCd
	 * @param shortTermInfoMap
	 * @return 완성된 단기 기억 맵
	 */
	private Map<String, Object> getCompleteMap(String nextMessage, String statusCd, Map<String, Object> shortTermInfoMap, MorphemeAnalyzer ma) {
		
		Map<String, Object> resultMap = shortTermInfoMap;
		
		String inputStr = String.valueOf(shortTermInfoMap.get("procInputText"));
		if (!statusCd.equals(DialogStatus.SYSTEM_ON.getStatusCd()) && inputStr != "") {
			
			boolean hasTarget = false;
			String[] sysKeywordArr = {"food_0", "food_1", "where_0", "where_1", "what_0"};
			
			
			for (String sysKeyword : sysKeywordArr) {
				if (nextMessage.contains("{"+sysKeyword+"}")) {
					hasTarget = true;
					break;
				}
			}
			// name 따로 추가
			if (!hasTarget) {
				if (nextMessage.contains("{name}")) {
					hasTarget = true;
				}
			}
			
			if (hasTarget) {
				resultMap = fillBlank(nextMessage, statusCd, shortTermInfoMap, sysKeywordArr, ma);
				nextMessage = getMessageWithRightJosa((String)resultMap.get("nextMessage"));
			}
			
		} else {
			// 예외 처리: 입력문이 존재 않을 경우
			
		}
		resultMap.put("nextMessage", nextMessage);

		return resultMap;
	}
	

	private Map<String, Object> fillBlank(String nextMessage, String statusCd, Map<String, Object> shortTermInfoMap, String[] sysKeywordArr, MorphemeAnalyzer ma) {
		
		Map<String, Object> resultMap = shortTermInfoMap;
		
		String inputStr = String.valueOf(shortTermInfoMap.get("procInputText"));
			
		HashMap<String, String> map = new HashMap<String, String>();
		// 대응 사전 이름 저장
		HashMap<String, ArrayList<String>> dictNameListInBlank = new HashMap<String, ArrayList<String>>();
		
		String[] dictNameArr = {"company", "drink", "entertainer", "food", "hotel", "korea_location", "music", "nation", "restaurant"
				, "school", "transport", "travel_place", "TV_drama_program", "TV_movie_program", "TV_show_program"};
		
		dictNameListInBlank.put("food",  new ArrayList<String>(Arrays.asList(new String[]{dictNameArr[1], dictNameArr[3]})));
		dictNameListInBlank.put("where", new ArrayList<String>(Arrays.asList(new String[]{dictNameArr[5], dictNameArr[7], dictNameArr[11]})));
		dictNameListInBlank.put("what",  new ArrayList<String>(Arrays.asList(dictNameArr))); // what일 경우 모든 사전찾음
		
		HashMap<String, ArrayList<String>> inputMorpListMap = cbns.getMorpListMap(inputStr, ma);
		if (!(inputMorpListMap.get("jList").isEmpty() && inputMorpListMap.get("vList").isEmpty() && inputMorpListMap.get("nList").isEmpty())) {
			
			double minSimilarityScore = 0.8;
			ArrayList<String> nList = inputMorpListMap.get("nList");
			String filePath = urlFilePath + "dictionary/WikiDictionary/";
			
			for (String sysKeyword : sysKeywordArr) {
				
				// {~!@#_index} //index 순차적이지 않음. where_0과 where_1은 완전히 별개
				if (nextMessage.contains("{"+sysKeyword+"}")) {
						
//						if (nextMessage.contains("{food_")) { // {~!@#_save}, {~!@#_load} 사용시
//							if (nextMessage.contains("save}")) {
						HashMap<String, ArrayList<?>> similarityMap = cbns.getMaxSimilarityAndFileName(filePath, new ArrayList<String>(Arrays.asList(new String[]{sysKeyword.split("_")[0]})), dictNameListInBlank, nList, minSimilarityScore);
//							ArrayList<String> fileNameList   = (ArrayList<String>)similarityMap.get("fileNameList");
						ArrayList<Double> similarityList = (ArrayList<Double>)similarityMap.get("similarityList");
						ArrayList<String> keywordList    = (ArrayList<String>)similarityMap.get("keywordList");
						
						// 이전에 저장해 놨던 sysKeyword 데이터를 다시 사용
						if (shortTermInfoMap.containsKey(sysKeyword)) {
							map.put(sysKeyword, (String)shortTermInfoMap.get(sysKeyword));
							
							
						} else {
							
							double maxSimilarity = 0;
							for (int i = 0; i < similarityList.size(); i++) {
								if (similarityList.get(i) > maxSimilarity) {
									maxSimilarity = similarityList.get(i);
								}
							}
							
							if (maxSimilarity > minSimilarityScore) {
								for (int i = 0; i < similarityList.size(); i++) {
									if (similarityList.get(i) == maxSimilarity) {
										
										map.put(sysKeyword, keywordList.get(i));
										resultMap.put(sysKeyword, keywordList.get(i));
										break;
									}
									
								}
							} else {
								// 봇 발화와 유사성이 0일 때
								// 임시 조치
								if (nList.size() == 0) {
									// 명사가 없다면 입력문 전체를 blank 대체용으로 사용
									map.put(sysKeyword, inputStr);
									resultMap.put(sysKeyword, inputStr);
								} else {
									int maxLen = 0;
									for (String n : nList) {
										if (n.length() > maxLen) {
											map.put(sysKeyword, n);
											resultMap.put(sysKeyword, n); // 우선은 가장 길이가 긴 (원본 문자열) 문자열을 매핑
											maxLen = n.length();
										}
									}
								}
								
								
							}
						}
						
					}
				
				
			}
			
			
			
		} else {
			// 예외 처리: 입력문에서 명사, 동사, 조사가 분석되지 않았을 경우
			
		}
		// {name}은 오류문이라도 교체
		if (nextMessage.contains("{name}")) {
			map.put("name", String.valueOf(shortTermInfoMap.get("name")));
		}
	
		for (String key : map.keySet()) {
			nextMessage = nextMessage.replace("{"+key+"}", map.get(key));
		}
		
		resultMap.put("nextMessage", nextMessage);
		
		return resultMap;
	}
	
	/**
	 * 문자열의 조사 을/를, 이/가, 은/는 을 앞 단어에 맞게 변환해서 반환
	 * @param str
	 * @return 조사 선택이 완료된 문자열
	 */
	private String getMessageWithRightJosa(String str) {
		String resultMsg = str;
		
		boolean hasTarget = false;
		// 앞 단어 마지막 문자에 받침이 있을 경우/없을 경우
		String[] josaArr = {"이/가", "은/는", "을/를", "으로/로", "이라는/라는"};
		for (String josa : josaArr) {
			if (resultMsg.contains(josa)) {
				hasTarget = true;
				break;
			}
		}
		
		if (hasTarget) {
			
			String strBeforeJosa = null;
			String selectedJosa = null;
			String replaceJosa = null;
			
			for (String josa : josaArr) {
				strBeforeJosa = resultMsg.split(josa)[0];
				selectedJosa = cbns.getJosaByJongsung(strBeforeJosa, josa.split("/")[0], josa.split("/")[1]);
				if (selectedJosa == null) {
					replaceJosa = josa;
				} else {
					replaceJosa = selectedJosa;
				}
				resultMsg = resultMsg.replaceFirst(josa, replaceJosa);
			}
			// 조사 선택이 완료될 때까지 재귀
			if (selectedJosa != null) {
				resultMsg = getMessageWithRightJosa(resultMsg);
			} else {
				// 각 조사의 앞 단어 마지막 문자가 모두 한글이 아닐 경우= do nothing
			}
		}
		
		return resultMsg;
	}
	
	
	/**
	 * code에 따라 질문의 종류를 판별한 후, 질문글에 대한 답변 문자열을 반환한다.<br>
	 * @param code
	 * @param korContent
	 * @param engContent
	 * @return 문자열
	 */
	@Override
	public String getAnswerSentence(int code, String korContent, String engContent) {
		String result = null;
		
		switch (code) {

			case 0:
				if (korContent != null && engContent != null && !korContent.equals("") && !engContent.equals("")) {
					result = "\"" + korContent + "\"" + "은/는 " + "\"" + engContent + "\"이라는 뜻이에요.";
					result = getMessageWithRightJosa(result);
				} else {
					if (korContent == null) {
						korContent = "";
					}
					result = "\"" + korContent + "\"" + "의 뜻은 잘 모르겠어요. 공부해야겠어요.";
				}
				break;
			
			case 1:
				result = "저는 이르입니다.";
				break;
				
			case 2:
				String CALLInitDate = "2017-08-09 18:02:21";
				SimpleDateFormat dayTime = new SimpleDateFormat("yyyy-mm-dd hh:mm:ss");
				Date date;
				long start = 0;
				long now   = 0;
				try {
					now    = System.currentTimeMillis();
					date   = dayTime.parse(CALLInitDate);
					start  = date.getTime();
				} catch (ParseException e) {
					e.printStackTrace();
				}
				long ageInSecond = (now - start) / 1000;
				String ageStr = "초";
				if (ageInSecond == 0) {
					// 계산 오류 발생시
					ageInSecond = 4;
					ageStr = "달";
				}
				
				result = "제 나이는 " + ageInSecond + ageStr + "입니다.";
				break;
		}
		
		result += "<br>궁금한게 있으면 언제든 물어보세요.";
		
		
		return result;
	}


}

