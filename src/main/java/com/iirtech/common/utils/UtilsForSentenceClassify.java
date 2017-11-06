/**
 * 
 */
package com.iirtech.common.utils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.iirtech.common.enums.SentenceClassifyConstants;


/**
 * @Package   : com.iir.chatbot
 * @FileName  : UtilsForSentenceClassify.java
 * @작성일       : 2017. 11. 2. 
 * @작성자       : choikino
 * @explain : 
 */

public class UtilsForSentenceClassify {
	
	//특수문자 제거, 형태소 분석
	
	
	//사용자 입력문을 받았을 때 아래 몇가지케이스로 분류하는 방법 
	//isSentence isPositive isNegative isUndo isDo isReask isReject isYN isAskSysAttr isAskInfo
	
	//모든 필터에 걸리지 않는 경우, 서브테마가 없는 경우(준비된 테마를 벗어나는 대화일 경우), 멈블링하는 대화문일 경우, 오타있는 경우
	
	public boolean isMoreThanTwoSentences(String inputStr) throws Exception {
		boolean result = false;
		UtilsForGGMA ufg = new UtilsForGGMA();
		List<List<String>> morphAnalyzeResult = ufg.morphAnalyze(inputStr);
		if(morphAnalyzeResult.size() >= 2) {
			result = true;
		}
		return result;
	}
	
	//특수문자 및 앞뒤 공백 및 ㅋㅋ ㅎㅎ 제거
	public String removeSpecialLetters(String inputStr) {
		String result = "";
		String match = "[^ㄱ-ㅎㅏ-ㅣ가-힣\\w\\s\\?\\.]";
		result =inputStr.replaceAll(match, "");
		//웃음과 관련되어 자주 사용하는 ㅋㅋㅋ ㅎㅎㅎ 도 제거 따라서 문장에 ㅋㅋㅋ ㅎㅎㅎ가 있을 때는 오타인식에 걸리지 않음
		match = "[ㅋ|ㅎ]";
		result =result.replaceAll(match, "");
		result = result.trim();

		return result;
	}
	
	//오타 있는 경우 다시 올바르게 입력하도록 유도
	public boolean isTypoTypeOfSentence(String inputStr) {
		boolean result = false;
		String typoRegex = "([ㄱ-ㅎㅏ-ㅣ|a-z])";//한글 자모 분리된 입력문 혹은 영어 문자 
		Pattern pattern = Pattern.compile(typoRegex);
		Matcher match = pattern.matcher(inputStr);
		
		if(match.find()) {
			result = true;
		}
		
		return result;
	}
	
	//인사하는 문장인지 판별
	public boolean isGreetingTypeOfSentence(String inputStr, List<List<String>> morphAnalyzeResult) {
		boolean result = false;
		String[] greetingExpressions = {
				"/안녕/","/반갑/"
		};
		for (int i = 0; i < morphAnalyzeResult.size(); i++) {
			for (int j = 0; j < morphAnalyzeResult.get(i).size(); j++) {
				String morphline = morphAnalyzeResult.get(i).get(j);
				for (int k = 0; k < greetingExpressions.length; k++) {
					if(morphline.contains(greetingExpressions[k])) {
						result = true;
					}
				}
			}
		}
		
		return result;
	}
	
	
	//무의미한 입력문 잡기 - 멈블링 
	public boolean isMumbleTypeOfSentence(String inputStr) {
		boolean result = false;
		if(inputStr.equals("")) return true;//특수문자만 입력한 경우 바로 무의미한 입력문판정 
		
		String[] mumbleExpressions = {
				"아","흠","음","오","하","케","흐","허"
				};
		String mubleRegex = "";
		for (int i = 0; i < mumbleExpressions.length; i++) {
			if(i != mumbleExpressions.length - 1) {
				mubleRegex += "(^" + mumbleExpressions[i] + "{1,}$)|";
			}
			else {
				mubleRegex += "(^" + mumbleExpressions[i] + "{1,}$)";
			}
		}
		if (inputStr.matches(mubleRegex)) {
			result = true;
		}
		
		return result;
	}
	
	
	//isYN: 네 아니오 응답하는 문장인가 체크한다.
	//대화턴 자체에서 네 아니오 류의 응답을 원하는 상황이 아니라면 이는 거부 오류다.
	//형태소분석없이 돌린다.
	//return: YES
	public String isYNTypeOfSentence(String inputStr) {
		String result = SentenceClassifyConstants.IS_NOT_YN;
		String[] yesExpressions = {
			"어","응","네","예","좋아","좋아요","좋습니다","맞아","맞아요","맞습니다","맞았어","맞았어요","맞았습니다"
				};
		String[] noExpressions = {
			"아니","아니요","아닙니다","싫어","싫어요","싫습니다","틀려","틀려요","틀립니다","틀렸어","틀렸어요","틀렸습니다"
		};
		for (int i = 0; i < yesExpressions.length; i++) {
			if(yesExpressions[i].equals(inputStr)) {
				result = SentenceClassifyConstants.IS_ANSWER_YES;
			}
		}
		for (int i = 0; i < noExpressions.length; i++) {
			if(noExpressions[i].equals(inputStr)) {
				result = SentenceClassifyConstants.IS_ANSWER_NO;
			}
		}
		return result;
	}
	
	//****************여기서부터는 형태소 분석된 문장을 입력받음******************
	
	//isSentence: 용언을 입력했는지 여부를 체크한다. 용언류가 없을 경우 false
	//네 아니오로 대답하는 문장이 아닌경우에만 적용
	public boolean isSentence(String inputStr, List<List<String>> morphAnalyzeResult) {
		boolean result = false;
		String sentenceRegex = "(V.{0,2}|XR)";
		Pattern pattern = Pattern.compile(sentenceRegex);
		
		String[] exceptionWords = {//형태소 분석기에서 용언으로 분류하지 못하지만 정상으로 인식해야하는 경우
				"안녕"
				};
		for (int i = 0; i < exceptionWords.length; i++) {
			if (exceptionWords[i].equals(inputStr)) {
				return true;
			}
		}
		
		for (int i = 0; i < morphAnalyzeResult.size(); i++) {
			for (int j = 0; j < morphAnalyzeResult.get(i).size(); j++) {
				String morphLine = morphAnalyzeResult.get(i).get(j);
				Matcher match = pattern.matcher(morphLine);
				if (match.find()) {
					result = true;
				}
			}
		}
		return result;
	}
	
	//문장 구성 판별
	//동사와 부정사 결합유무, 부사가 있으면 긍정인지 부정인지, 형용사면 긍정인지 부정인지
	public List<Integer> analyzeIntenseOfSentence(List<List<String>> morphAnalyzeResult, List<String> negativeWords){
		List<Integer> result = new ArrayList<Integer>();
		// **VV VA가 동시에 둘 이상 등장할 경우에는 무조건 뒤에 등장한 것으로 기준삼는다.
		List<Integer> targetPredicateIdxesOfI = new ArrayList<Integer>();
		List<Integer> targetPredicateIdxesOfJ = new ArrayList<Integer>();
		
		int predicateCnt = 0;
		for (int i = 0; i < morphAnalyzeResult.size(); i++) {
			predicateCnt = 0;
			for (int j = 0; j < morphAnalyzeResult.get(i).size(); j++) {
				String token = morphAnalyzeResult.get(i).get(j);
				if(token.contains("VV") || token.contains("VA")) {
					//System.out.println(token);
					targetPredicateIdxesOfI.add(i);
					targetPredicateIdxesOfJ.add(j);
					predicateCnt++;
				}
			}
		}

		List<String> targetTokens = new ArrayList<String>();
		//만약 용언이 두개 이상일 경우에만 뒤에것을 제외한 나머지를 제거
		if(predicateCnt >= 2) {
			//지울 기준이 되는 i,j의 idx값 구하기 
			int targetPredicateIdxOfI = targetPredicateIdxesOfI.get(targetPredicateIdxesOfI.size()-1);
			int targetPredicateIdxOfJ = targetPredicateIdxesOfJ.get(targetPredicateIdxesOfJ.size()-2);
			
			for (int i = 0; i < morphAnalyzeResult.size(); i++) {
				if (targetPredicateIdxOfI <= i) {
					for (int j = 0; j < morphAnalyzeResult.get(i).size(); j++) {
						if (targetPredicateIdxOfJ < j) {
							targetTokens.add(morphAnalyzeResult.get(i).get(j));
						}
					}
				}
			}
		}
		else if(!targetPredicateIdxesOfI.isEmpty()){
			int targetPredicateIdxOfI = targetPredicateIdxesOfI.get(targetPredicateIdxesOfI.size()-1);
			for (int i = 0; i < morphAnalyzeResult.size(); i++) {
				if (targetPredicateIdxOfI <= i) {
					for (int j = 0; j < morphAnalyzeResult.get(i).size(); j++) {
						targetTokens.add(morphAnalyzeResult.get(i).get(j));
					}
				}
			}
		}
		/*
			0/밥/NNG+1/을/JKO/3/안/MAG/5/먹/VV+6/었/EPT+7/어요/EFN+9/./SF    
			0/밥/NNG+1/을/JKO/3/먹/VV+4/지/ECD/6/않/VXV+7/았/EPT+8/어요/EFN+10/./SF    
			0/밥/NNG+1/을/JKO/3/안/MAG/5/좋아하/VV+8/어요/EFN+9/./SF    
			0/밥/NNG+1/을/JKO/3/좋아하/VV+6/지/ECD/8/않/VXV+9/아요/EFN+11/./SF    
			0/음식/NNG+2/이/JKS/4/맛없/VA+6/어요/EFN+8/./SF    
			0/음식/NNG+2/이/JKS/4/맛있/VA+6/어요/EFN+8/./SF    
			0/음식/NNG+2/이/JKS/4/맛있/VA+6/지/ECD/8/않/VXV+9/아요/EFN+11/./SF    
			0/음식/NNG+2/이/JKS/4/안/MAG/6/맛있/VA+8/어요/EFN+10/./SF  
			0/맛/NNG+1/이/JKS/3/없/VA+4/어요/EFN+6/./SF    
			0/맛/NNG+1/이/JKS/3/있/VV+4/어요/EFN+6/./SF  
		 */
		String[] negativeEndingExpressions = {
				"/않/V","/못/V","/없/V","/못하/V"
		};
		String[] negativeAdverbExpressions = {
				"/안/M","/못/M"
		};
		//분류작업 시작!
		String targetLine = "";
		for (int i = 0; i < targetTokens.size(); i++) {
			targetLine += targetTokens.get(i) + "/";
		}
		//2.용언에 부정 어미 결합유무 판단 
		for (int j = 0; j < negativeEndingExpressions.length; j++) {
			if(targetLine.contains(negativeEndingExpressions[j]) && (targetLine.contains("VA") || targetLine.contains("XR"))) {
				result.add(SentenceClassifyConstants.NEGATIVE_ADJECTIVE_END);
			}
			else if(targetLine.contains(negativeEndingExpressions[j]) && targetLine.contains("VV")) {
				result.add(SentenceClassifyConstants.NEGATIVE_VERB_END);
			}
		}
		//3.부사가 부정의 의미를 표현하는지 판단
		for (int j = 0; j < negativeAdverbExpressions.length; j++) {
			if(targetLine.contains(negativeAdverbExpressions[j])) {
				result.add(SentenceClassifyConstants.NEGATIVE_ADVERB);
			}
		}
		//4.형용사,동사,어근이 부정형인지 판단 & 용언이 형용사인지 동사인지도 판단
		for (int i = 0; i < targetTokens.size(); i++) {
			String token = targetTokens.get(i);
			String[] tempElmnts = token.split("\\+");
			for (int j = 0; j < tempElmnts.length; j++) {
				String morphWord = tempElmnts[j];
				if (morphWord.contains("VA") || morphWord.contains("XR")) {
					String word = morphWord.split("/")[1];
					if (negativeWords.contains(word)) {
						result.add(SentenceClassifyConstants.NEGATIVE_ADJECTIVE);
					}
					else {
						result.add(SentenceClassifyConstants.POSITIVE_ADJECTIVE);
					}
				}
				else if(morphWord.contains("VV")) {
					result.add(SentenceClassifyConstants.POSITIVE_VERB);
				}
			}
		}
		return result;
	}
	
	//숫자를 곱해서 긍정 부정 동작함 동작안함 여부를 결정하고 문자열로 리턴해줌 
	// 1,3:isDo -1,-3:isUndo -4,-6:isNegativeFeedback 8,12:isPositiveFeedback
	// 양수 홀수는 동작함, 음수 홀수는 동작안함, 양수 짝수는 긍정피드백, 음수 짝수는 부정피드백
	public String decideIntenseString(List<Integer> intenseList) {
		String intense = "";
		int intenseNum = 1;
		for (int i = 0; i < intenseList.size(); i++) {
			intenseNum = intenseNum * intenseList.get(i);
		}
		if (intenseNum > 0) {
			if ((intenseNum % 2) == 0) {//양수짝수
				intense = SentenceClassifyConstants.IS_POSITIVE_FEEDBACK;
			} else {//양수홀수
				intense = SentenceClassifyConstants.IS_DO;
			}
		} else {
			if ((intenseNum % 2) == 0) {//음수짝수
				intense = SentenceClassifyConstants.IS_NEGATIVE_FEEDBACK;
			} else {//음수홀수
				intense = SentenceClassifyConstants.IS_UNDO;
			}
		}
		return intense;
	}
	
	//의문문인지 여부를 판별함
	public boolean isAskSentence(String inputStr, List<List<String>> morphAnalyzeResult) {
		boolean result = false;
		String[] askingEnds = {
			"?","냐","니","냐.","니."
		};
		String[] askingElmnts = {
			"/누구/", "/언제/", "/어디/", "/무엇/", "/왜/", "/뭐/", "/어째서/", "/몇/", "/얼마/", "/얼마나/", "/며칠/"
		};
		String[] askingExpressions = {
			"왜요","누가"
		};
		for (int i = 0; i < askingEnds.length; i++) {
			if(inputStr.endsWith(askingEnds[i])) {
				return true;
			};
		}
		for (int i = 0; i < askingExpressions.length; i++) {
			if(inputStr.contains(askingExpressions[i])) {
				return true;
			};
		}
		for (int i = 0; i < morphAnalyzeResult.size(); i++) {
			for (int j = 0; j < morphAnalyzeResult.get(i).size(); j++) {
				String token = morphAnalyzeResult.get(i).get(j);
				for (int k = 0; k < askingElmnts.length; k++) {
					if(token.contains(askingElmnts[k])) {
						return true;
					}
				}
			}
		}
		
		return result;
	}
	
	//의문문으로 판명된 상태에서 isReask, isAskSysAttr, isAskInfo 를 가르는 요소는 무엇일까?
	public String getTypeOfAskSentence(String inputStr, List<List<String>> morphAnalyzeResult) {
		String result = SentenceClassifyConstants.IS_REASK;
		
		String[] sysAttrExpressions = {
			"/이름/", "/세/", "/살/", "/나이/", "/성별/", "/당신/", "/너/"
		};
		
		String[] mkNonInfoExpressions = {
			"이것","이거","이건","저것","저거","저건","그것","그거","그건"
		};
		
		String[] infoExpressions = {
			"뭐","무엇","뜻","무슨"
		};
		//시스템에 관련된 물음이 아닌경우가 반문 -> 현재 챗봇은 질문에 답하지 않음
		
		boolean hasNounYN = false;
		boolean hasVerbYN = false;
		boolean hasSysAttrExpressionYN = false;
		//물어보는 대상 있는지 체크
		for (int i = 0; i < morphAnalyzeResult.size(); i++) {
			for (int j = 0; j < morphAnalyzeResult.get(i).size(); j++) {
				String token = morphAnalyzeResult.get(i).get(j);
				if (token.contains("NN")) {
					hasNounYN = true;
				}
				if (token.contains("VV")||token.contains("VA")) {
					hasVerbYN = true;
				}
				for (int k = 0; k < sysAttrExpressions.length; k++) {
					if(token.contains(sysAttrExpressions[k])) {
						hasSysAttrExpressionYN = true;
					}
				}
			}
		}
		
		boolean hasNonInfoExpressions = false;
		for (int i = 0; i < infoExpressions.length; i++) {
			if(inputStr.contains(infoExpressions[i])) {
				for (int j = 0; j < mkNonInfoExpressions.length; j++) {
					if(inputStr.contains(mkNonInfoExpressions[j])) {
						hasNonInfoExpressions = true;
					}
				}
			}
		}

		if(hasNounYN && !hasNonInfoExpressions &&!hasVerbYN&&!hasSysAttrExpressionYN) {
			result = SentenceClassifyConstants.IS_ASK_INFO;
		}
		else if(hasSysAttrExpressionYN) {
			result = SentenceClassifyConstants.IS_ASK_SYSTEM_ATTRIBUTE;
		}
		
		return result;
	}
	
	
	//끝으로 isReject
	public boolean isReject(List<Integer> intenseList, List<List<String>> morphAnalyzeResult) {
		boolean result = false;
		//형태로 판단 - 못/MAG + 하다/VV 안/MAG + 하다/VV 기/ETN 싫/VA
		String tokenLine = "";
		for (int i = 0; i < morphAnalyzeResult.size(); i++) {
			for (int j = 0; j < morphAnalyzeResult.get(i).size(); j++) {
				String token = morphAnalyzeResult.get(i).get(j);
				tokenLine += token + "/";
			}
		}
		if ((tokenLine.contains("못/MAG") && tokenLine.contains("하/VV")) || 
				(tokenLine.contains("안/MAG") && tokenLine.contains("하/VV")) || 
				(tokenLine.contains("기/ETN") && tokenLine.contains("싫/VA"))) {
			result = true;
		}
		//긍정베이스(음수 없음) - 그만MAG 동사VV(말하다 쓰다 연습하다 ...)
		//긍정베이스(음수 없음) - 나가VV, 멈추VV,몰VV,모르VV,그만두VV,짜증나VV, 지겹VA,중지NN
		//부정이 되는 경우는 intenseList의 값에서 -1 이거나 -2 가 있는데 -3 이 없으면 부정
		boolean isNegativeBase = false;
		String[] rejectWords = {
				"/나가/VV","/멈추/VV","/몰/VV","/모르/VV","/그만두/VV","/짜증나/VV","/지겹/VA","/중지/NN","/안해/NN","/못해/NN"
		};
		if((intenseList.contains(-1) || intenseList.contains(-2)) && (!intenseList.contains(-3))) {
			isNegativeBase = true;
		}
		if (!isNegativeBase) {
			if (tokenLine.contains("그만/MAG") && tokenLine.contains("/VV")){
				result = true;
			}
			for (int i = 0; i < rejectWords.length; i++) {
				if(tokenLine.contains(rejectWords[i])){
					result = true;
				}
			}
		}
		
		return result;
	}
	
	
}
