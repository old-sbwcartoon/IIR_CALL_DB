/**
 * 
 */
package com.iirtech.chatbot.service.impl;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.iirtech.common.enums.SentenceClassifyConstants;
import com.iirtech.common.utils.ChatbotUtil;
import com.iirtech.common.utils.UtilsForGGMA;
import com.iirtech.common.utils.UtilsForSentenceClassify;


/**
 * @Package   : com.iir.chatbot.classify
 * @FileName  : ClassifyRulePerAutomata.java
 * @작성일       : 2017. 11. 5. 
 * @작성자       : choikino
 * @explain : 대화 오토마타 별로 서로다른 규칙이 적용되어 입력문의 정상, 오류를 판별하고 대응한다.
 */

public class ReactRulePerAutomata {

	//생성자로 입력된 오토마타 변수값을 통해 결정된 규칙을 사용하여 정상인지 오류인지를 판단하고
	//정상일 경우에는 판별 결과를 리턴하며 오류일 경우에는 오류 스크립트를 태운다.
	//오류스크립트는 거의 yes no 형태로만 응답이 나올 수 있도록 발화문을 구성하며 오토마타 별로 별개의 폴더에 위치한다.
	public List<String> normalSentenceTypeList;
	
	//생성자로 넣어준 변수값(오토마타 상태코드)에 따라서 오류와 정상 문장 타입이 바뀜
	public ReactRulePerAutomata(String dialogStatusCd) {
		if (dialogStatusCd.equals("S000")) {	//안녕하세요? 반갑습니다.
			System.out.println("*******START_CONVERSATION*******");
			String[] normalSentenceTypes = {
					SentenceClassifyConstants.IS_POSITIVE_FEEDBACK
					,SentenceClassifyConstants.IS_DO
					,SentenceClassifyConstants.IS_ANSWER_YES
			};
			normalSentenceTypeList = Arrays.asList(normalSentenceTypes);
		}
		else if(dialogStatusCd.equals("S010")) { //오늘은 어떤 주제로 이야기?
			System.out.println("*******APPROACH_TOPIC*******");
			String[] normalSentenceTypes = {
					SentenceClassifyConstants.IS_DO
			};
			normalSentenceTypeList = Arrays.asList(normalSentenceTypes);
		}
		//나머지 오토마타들에 대해서도 작업 필요
		else if(dialogStatusCd.equals("E000")) { //오늘은 어떤 주제로 이야기?
			System.out.println("*******APPROACH_TOPIC*******");
			String[] normalSentenceTypes = {
					SentenceClassifyConstants.IS_NOT_YN
			};
			normalSentenceTypeList = Arrays.asList(normalSentenceTypes);
		}
	}
	
	//입력문이 오류인지 정상인지를 리턴한다. 
	//key:IS_NORMAL_SENTENCE, IS_ERROR_SENTENCE
	public Map<String, String> getClassifyResult(Map<String, Object> paramInfo, String inputStr, List<String> normalSentenceTypeList) throws Exception{
		Map<String, String> result = new HashMap<String, String>();
		UtilsForSentenceClassify ufsc = new UtilsForSentenceClassify();
		String resultKeyStr = "";
		// space . ? 제외한 특수문자 제거(ㅋㅋㅋ,ㅎㅎㅎ 포함)
		inputStr = ufsc.removeSpecialLetters(inputStr);
		//두문장 이상 입력했는지 검사
		if(ufsc.isMoreThanTwoSentences(inputStr)) {
			resultKeyStr = normalSentenceTypeList.contains(SentenceClassifyConstants.IS_MORE_THAN_TWO_SENTENCE) ? 
					SentenceClassifyConstants.IS_NORMAL_SENTENCE : SentenceClassifyConstants.IS_ERROR_SENTENCE; 
			result.put(resultKeyStr, SentenceClassifyConstants.IS_MORE_THAN_TWO_SENTENCE);
			return result;
		}
		
		//무의미하게 얼버무리는 문장인지 판별 
		if(ufsc.isMumbleTypeOfSentence(inputStr)) {
			resultKeyStr = normalSentenceTypeList.contains(SentenceClassifyConstants.IS_MORE_THAN_TWO_SENTENCE) ? 
					SentenceClassifyConstants.IS_NORMAL_SENTENCE : SentenceClassifyConstants.IS_ERROR_SENTENCE; 
			result.put(resultKeyStr, SentenceClassifyConstants.IS_MORE_THAN_TWO_SENTENCE);
			return result;
		}
		
		//오타있는 문장인지 판별
		if(ufsc.isTypoTypeOfSentence(inputStr)) {
			resultKeyStr = normalSentenceTypeList.contains(SentenceClassifyConstants.IS_TYPO_SENTENCE) ? 
					SentenceClassifyConstants.IS_NORMAL_SENTENCE : SentenceClassifyConstants.IS_ERROR_SENTENCE; 
			result.put(resultKeyStr, SentenceClassifyConstants.IS_TYPO_SENTENCE);
			return result;
		}
		
		UtilsForGGMA ufg = new UtilsForGGMA();
		List<List<String>> morphAnalyzeResult = ufg.morphAnalyze(inputStr);
		//문장형태인지 확인
		if(!ufsc.isSentence(inputStr, morphAnalyzeResult)) {
			resultKeyStr = normalSentenceTypeList.contains(SentenceClassifyConstants.IS_NOT_SENTENCE) ? 
					SentenceClassifyConstants.IS_NORMAL_SENTENCE : SentenceClassifyConstants.IS_ERROR_SENTENCE; 
			result.put(resultKeyStr, SentenceClassifyConstants.IS_NOT_SENTENCE);
		}
		
		//의문문일때 질문유형 판별 : 시스템 신변질문? 모르는 어휘 질문? 단순 반문?
		if(ufsc.isAskSentence(inputStr, morphAnalyzeResult)) {
			if(ufsc.getTypeOfAskSentence(inputStr, morphAnalyzeResult).equals(SentenceClassifyConstants.IS_ASK_INFO)) {
				resultKeyStr = normalSentenceTypeList.contains(SentenceClassifyConstants.IS_ASK_INFO) ? 
						SentenceClassifyConstants.IS_NORMAL_SENTENCE : SentenceClassifyConstants.IS_ERROR_SENTENCE; 
				result.put(resultKeyStr, SentenceClassifyConstants.IS_ASK_INFO);
			}
			else if(ufsc.getTypeOfAskSentence(inputStr, morphAnalyzeResult).equals(SentenceClassifyConstants.IS_ASK_SYSTEM_ATTRIBUTE)) {
				resultKeyStr = normalSentenceTypeList.contains(SentenceClassifyConstants.IS_ASK_SYSTEM_ATTRIBUTE) ? 
						SentenceClassifyConstants.IS_NORMAL_SENTENCE : SentenceClassifyConstants.IS_ERROR_SENTENCE; 
				result.put(resultKeyStr, SentenceClassifyConstants.IS_ASK_SYSTEM_ATTRIBUTE);
			}
			else if(ufsc.getTypeOfAskSentence(inputStr, morphAnalyzeResult).equals(SentenceClassifyConstants.IS_REASK)){
				resultKeyStr = normalSentenceTypeList.contains(SentenceClassifyConstants.IS_REASK) ? 
						SentenceClassifyConstants.IS_NORMAL_SENTENCE : SentenceClassifyConstants.IS_ERROR_SENTENCE; 
				result.put(resultKeyStr, SentenceClassifyConstants.IS_REASK);
			}
		}
		
		ChatbotUtil cu = new ChatbotUtil();
		String filePath = "/Users/rnder_004/Kino/iir/chatbot/800_refer/900_020 materials/negative_positive_dictionary/";
		String negativeFileName = "filterdKorNegDic.txt";
		List<String> negativeWords = cu.readFileByLine(filePath, negativeFileName);
		List<Integer> intenseList = ufsc.analyzeIntenseOfSentence(morphAnalyzeResult, negativeWords);
		String intense = ufsc.decideIntenseString(intenseList);
		
		//거부문장 판별 - 반드시 do 판단보다 먼저 위치해야함
		if(ufsc.isReject(intenseList, morphAnalyzeResult)) {
			resultKeyStr = normalSentenceTypeList.contains(SentenceClassifyConstants.IS_REJECT) ? 
					SentenceClassifyConstants.IS_NORMAL_SENTENCE : SentenceClassifyConstants.IS_ERROR_SENTENCE; 
			result.put(resultKeyStr, SentenceClassifyConstants.IS_REJECT);
		}
		
		//**제일 마지막에 map paramInfo 변수에서 정보를 뽑아 현재 상태가 에러(E*)면 do,undo,negativeFeedback,positiveFeedbak 대신에 is_not_yn으로 대체
		String status = paramInfo.get("dialogStatus").toString();
		if (status.startsWith("E")) {//E999는 타면 안됨
			//yes 인지 no 인지 판별
			if(ufsc.isYNTypeOfSentence(inputStr).equals(SentenceClassifyConstants.IS_ANSWER_NO)) {
				resultKeyStr = normalSentenceTypeList.contains(SentenceClassifyConstants.IS_ANSWER_NO) ? 
						SentenceClassifyConstants.IS_NORMAL_SENTENCE : SentenceClassifyConstants.IS_ERROR_SENTENCE; 
				result.put(resultKeyStr, SentenceClassifyConstants.IS_ANSWER_NO);
			}
			else if(ufsc.isYNTypeOfSentence(inputStr).equals(SentenceClassifyConstants.IS_ANSWER_YES)) {
				resultKeyStr = normalSentenceTypeList.contains(SentenceClassifyConstants.IS_ANSWER_YES) ? 
						SentenceClassifyConstants.IS_NORMAL_SENTENCE : SentenceClassifyConstants.IS_ERROR_SENTENCE; 
				result.put(resultKeyStr, SentenceClassifyConstants.IS_ANSWER_YES);
			}
			else {
				resultKeyStr = normalSentenceTypeList.contains(SentenceClassifyConstants.IS_NOT_YN) ? 
						SentenceClassifyConstants.IS_NORMAL_SENTENCE : SentenceClassifyConstants.IS_ERROR_SENTENCE; 
				result.put(resultKeyStr, SentenceClassifyConstants.IS_NOT_YN);
			}
		}
		else {
			if(ufsc.isYNTypeOfSentence(inputStr).equals(SentenceClassifyConstants.IS_ANSWER_NO)) {
				resultKeyStr = normalSentenceTypeList.contains(SentenceClassifyConstants.IS_ANSWER_NO) ? 
						SentenceClassifyConstants.IS_NORMAL_SENTENCE : SentenceClassifyConstants.IS_ERROR_SENTENCE; 
				result.put(resultKeyStr, SentenceClassifyConstants.IS_ANSWER_NO);
			}
			else if(ufsc.isYNTypeOfSentence(inputStr).equals(SentenceClassifyConstants.IS_ANSWER_YES)) {
				resultKeyStr = normalSentenceTypeList.contains(SentenceClassifyConstants.IS_ANSWER_YES) ? 
						SentenceClassifyConstants.IS_NORMAL_SENTENCE : SentenceClassifyConstants.IS_ERROR_SENTENCE; 
				result.put(resultKeyStr, SentenceClassifyConstants.IS_ANSWER_YES);
			}
			
			//do undo negative positive 극성판별! 이부분이 오토마타마다 다른 오류대응 스크립트가 나가야 하는 부분
			if (!ufsc.isReject(intenseList, morphAnalyzeResult) && intense.equals(SentenceClassifyConstants.IS_DO)) {
				resultKeyStr = normalSentenceTypeList.contains(SentenceClassifyConstants.IS_DO) ? 
						SentenceClassifyConstants.IS_NORMAL_SENTENCE : SentenceClassifyConstants.IS_ERROR_SENTENCE; 
				result.put(resultKeyStr, SentenceClassifyConstants.IS_DO);
			} 
			else if(intense.equals(SentenceClassifyConstants.IS_UNDO)) {
				resultKeyStr = normalSentenceTypeList.contains(SentenceClassifyConstants.IS_UNDO) ? 
						SentenceClassifyConstants.IS_NORMAL_SENTENCE : SentenceClassifyConstants.IS_ERROR_SENTENCE; 
				result.put(resultKeyStr, SentenceClassifyConstants.IS_UNDO);
			}
			else if(intense.equals(SentenceClassifyConstants.IS_NEGATIVE_FEEDBACK)) {
				resultKeyStr = normalSentenceTypeList.contains(SentenceClassifyConstants.IS_NEGATIVE_FEEDBACK) ? 
						SentenceClassifyConstants.IS_NORMAL_SENTENCE : SentenceClassifyConstants.IS_ERROR_SENTENCE; 
				result.put(resultKeyStr, SentenceClassifyConstants.IS_NEGATIVE_FEEDBACK);
			}
			else if(intense.equals(SentenceClassifyConstants.IS_POSITIVE_FEEDBACK)) {
				resultKeyStr = normalSentenceTypeList.contains(SentenceClassifyConstants.IS_POSITIVE_FEEDBACK) ? 
						SentenceClassifyConstants.IS_NORMAL_SENTENCE : SentenceClassifyConstants.IS_ERROR_SENTENCE; 
				result.put(resultKeyStr, SentenceClassifyConstants.IS_POSITIVE_FEEDBACK);
			}
		}
		
		return result;
	}
	
	
	
	
}
