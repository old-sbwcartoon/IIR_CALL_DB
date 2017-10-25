package com.iirtech.chatbot;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.snu.ids.ha.ma.MorphemeAnalyzer;
import org.snu.ids.ha.ma.Sentence;

import info.debatty.java.stringsimilarity.JaroWinkler;

public class Test {
	
	public static void main(String[] args) throws Exception {
		//어떻게 유사도를 검사하지?
		//각 대답 유형별로 다를것이다.
		//
		//1. 명사만 사용해서 단답형으로 대답할 수 있는 경우
		//2. 연습문제라서 대부분의 문자열에 일치가 되어야하는경우
		//3. 싫다/좋다 맞다/틀리다 의 바이너리 정보로 인풋이 들어오는 경우
		//4. 대답이 필요없는 상황에서 대답을 하는 경우의 처리
		//5. 입력문에 외국어가 사용된 경우
		
		//문장유사도가 일정 수준 이하일 경우 다시 말하도록 유도 
		//문장 유사도가 있어야 시스템 명령어도 인식이 가능하지 ("너 누구야? 너 이름이 뭐야? 이건 무슨 뜻이야? 등등 ")

		System.out.println("Test process start!");
		//문장유사도에 대해서 테스트 진행함
		BufferedReader in = new BufferedReader(new InputStreamReader(System.in));
		String expectedMsg = "학교에서 밥을 먹습니다.";
		
		//input Msg 입력받기
		System.out.print("please input messege: ");
		String inputMsg = in.readLine();
		//분류결과 도출
		UtilsForGGMA ufg = new UtilsForGGMA();
		int classifyResult = ufg.doClassification(expectedMsg, inputMsg);
		
		switch (classifyResult) {
		case ClassificationConstants.ERROR:
			//오류 케이스 
			break;
		case ClassificationConstants.NORMAL:
			//정상 케이스
			break;
		default:
			//에러
			break;
		}
	}
	
}

final class ClassificationConstants{
	//input Msg 분류
	public static final int ERROR = 0;
	public static final int NORMAL = 1;
	public static final double THRESHOLD = 0.5;
}

final class SimilarityWeights{
	//가중치 : N*(NOUN), V*(VERB), J*(POSTPOSITION), M*(ADVERB), E*(EOW)
	public static final double NOUN = 0.5;
	public static final double VERB = 0.7;
	public static final double POSTPOSITION = 0.2;
	public static final double ADVERB = 0.1;
	public static final double EOW = 0.1;
}


class UtilsForGGMA {
	
	int doClassification(String expectedMsg, String inputMsg) throws Exception {
		int result = -1;
		UtilsForGGMA ufg = new UtilsForGGMA();

		//1. 꼬꼬마 형태소 분석기
		//형태소분석 ** 시스템에 답안으로 가지고 있는 문장들은 미리 분석결과를 저장해 놓아서 성능 개선을 노린다.
		//expected Msg Analyze
		MorphemeAnalyzer expectedMa = new MorphemeAnalyzer();
		expectedMa.createLogger(null);
		List expectedRet = expectedMa.analyze(expectedMsg);//expectedMsg!!
		expectedRet = expectedMa.postProcess(expectedRet);
		expectedRet = expectedMa.leaveJustBest(expectedRet);
		List expectedStl = expectedMa.divideToSentences(expectedRet);
		List<List<String>> expectedSentence = new ArrayList<List<String>>();
		for( int i = 0; i < expectedStl.size(); i++ ) {//문장단위로 루프
			Sentence st = (Sentence) expectedStl.get(i);
			//[[0/학교/NNG+2/에서/JKM, 5/밥/NNG+6/을/JKO, 8/먹/VV+9/습니다/EFN+12/./SF]]
			expectedSentence.add(ufg.getSplittedWords(st));//getSplittedWords 어절단위로 루프 
		}
		expectedMa.closeLogger();
		//input Msg Analyze
		MorphemeAnalyzer inputMa = new MorphemeAnalyzer();
		inputMa.createLogger(null);
		List inputRet = inputMa.analyze(inputMsg);//inputMsg!!
		inputRet = inputMa.postProcess(inputRet);
		inputRet = inputMa.leaveJustBest(inputRet);
		List inputStl = inputMa.divideToSentences(inputRet);
		List<List<String>> inputSentence = new ArrayList<List<String>>();
		for( int i = 0; i < inputStl.size(); i++ ) {
			Sentence st = (Sentence) inputStl.get(i);
			inputSentence.add(ufg.getSplittedWords(st));
		}
		inputMa.closeLogger();
		
		//2. 유사도 구할 어휘쌍 추출하기
		List<List<String>> wordPairs = ufg.getCompareWordPair(expectedSentence, inputSentence);
		//3. 유사도 계산
		double similarityScore = ufg.getSimilarityScore(wordPairs);
		
		//4. 획득한 유사도 점수가 기준점 보다 크면 정상 낮으면 오류
		if(similarityScore >= ClassificationConstants.THRESHOLD) {
			result = ClassificationConstants.NORMAL;
		}else {
			result = ClassificationConstants.ERROR;
		}
		
		return result;
	}
	
	
	/**
	 * @Method   : getCompareWordPair
	 * @작성일     : 2017. 10. 25. 
	 * @작성자     : choikino
	 * @explain : 이어지는 요소, 위치 등 정보를 참고하여 비교할 대상끼리 묶어서 리스트형태로 리턴한다. 
	 * @param :
	 * @return :
	 * @throws Exception 
	 */
	List<List<String>> getCompareWordPair(List<List<String>> expectedSentence, List<List<String>> inputSentence){
		List<List<String>> result = new ArrayList<List<String>>();//exptecdWord|inputWord|morpheme
		/*
		test case
		1. 제주도요. vs 제주요.
		[[0/제주도/NNP/C+3/요/JX]]
		2. 밥을 먹었어요. vs 갈치 먹었어요.
		3. 밥을 먹었어요. vs 밥과 갈치 먹었어요.
		3. 밥을 학교에서 먹고 도서관에 갔어요. vs 식당에서 밥 먹고 학교 갔어요.
		[[0/밥/NNG+1/을/JKO, 3/학교/NNG+5/에서/JKM, 8/먹/VV+9/고/ECE, 11/도서관/NNG+14/에/JKM, 16/가/VV+17/었/EPT+17/어요/EFN+19/./SF]]
		4. 밥을 먹고 갔어요. vs 식당에서 밥을 먹고 학교에 갔어요.
		5. 롯데 마트에 갔어요. vs 로떼 마트에 갔어요.
		6. 밥을 먹었어요. vs 학교에 갔어요. 그리고 밥을 먹었어요.
		[[0/학교/NNG+2/에/JKM, 4/가/VV+5/었/EPT+5/어요/EFN+7/./SF], [9/그리고/MAG, 13/밥/NNG+14/을/JKO, 16/먹/VV+17/었/EPT+18/어요/EFN+20/./SF]]
		*/
		
		/*
		어려운 규칙보다 간단하게. 대표 형태소(enum쓰기) 우선순위는 has verb or noun? if yes >> "V > N > etc"
		inputSentence 의 size를 먼저 체크해야함 2문장 이상일 경우에 대비해서 ... 2문장 이상이면 아래 과정을 size만큼 실행하고 최종 max값을 구해야함 
		1. [0/제주도/NNP/C+3/요/JX] >> 대표형태소_제주도NNP요JX 형태로 변환을 expected & input 의 모든 어절에 대해서 진행
		2. 루프돌면서 대표형태소가 같은 것에 대해서만 쌍 만들기 expected 어절 수만큼 리스트 i size 나오겠지
		3. i 리스트 idx 마다 j개의 대응쌍이 있을테니 다시 루프돌면서 유사도 구하고 max값을 선택 
		*/
		//expectedSentence는 한개문장 
		for (int i = 0; i < expectedSentence.size(); i++) {
			for (int j = 0; j < expectedSentence.get(i).size(); j++) {
				String expectedTokenStr = expectedSentence.get(i).get(j);
				String compareStr = "";
				String morphType = "";
				String[] temp1 = expectedTokenStr.split("\\+");
				List<String> representMorph = new ArrayList<String>();
				for (int k = 0; k < temp1.length; k++) {
					
				}
			}
		}
		
		return result;
	}
	
	
	/**
	 * @Method   : getSimilarityScore
	 * @작성일     : 2017. 10. 25. 
	 * @작성자     : choikino
	 * @explain : 유사도를 구할 단어쌍들을 인자로 받아서 형태소별 가중치를 적용한 최종 스코어를 리턴한다.
	 * @param :
	 * @return :
	 */
	Double getSimilarityScore(List<List<String>> compareWordPairs){
		double result = 0;
			
		//i 리스트 idx 마다 j개의 대응쌍이 있을테니 다시 루프돌면서 유사도 구하고 max값을 선택 
		/*
		어순이 바뀔 수 있고 한문장에 동일한 형태소의 다른 낱말이 등장 할 수 있으므로 모두 비교 후 유사도가 높은값을 선택한다.
		동일한 형태소 끼리 비교하고 서로 같거나 다를때 주는 가중치를 명사 > 동사 = 형용사 > 부사 > 조사 > 어미 순서대로 준다.
		숫자? 기호? 외국어?
		두 문장일때 처리? 둘다 유사도를 구하고 높은 유사도에 해당하는 문장을 기준으로 분류를 진행해준다.
		*/
		
		for (int i = 0; i < compareWordPairs.size(); i++) {
			//exptecdWord|inputWord|morpheme
		}
		
		
		
		JaroWinkler jw = new JaroWinkler();
		
//		double simillarity = jw.similarity(targetMsg, inputMsg);
//		System.out.println("input messege: " + inputMsg);
//		System.out.println("simillarity: " + simillarity);
		
		return result;
	}
	
	
	
	/**
	 * @Method   : getSplittedWords
	 * @작성일     : 2017. 10. 25. 
	 * @작성자     : choikino
	 * @explain : 꼬꼬마 분석결과를 받아서 어절단위 분석결과 문자열만 뽑아냄 
	 * @param :
	 * @return :
	 */
	List<String> getSplittedWords(Sentence st){
		List<String> result = new ArrayList<String>();
		String regex = "\\[.*\\]";
		for (int i = 0; i < st.size(); i++) {
			String userStr = String.valueOf(st.get(i));
			Pattern pattern = Pattern.compile(regex);
			Matcher match = pattern.matcher(userStr);
			if(match.find()) {
				System.out.println("Matched::" + match.group(0));
				//0/안녕/NNG+2/하/XSV+3/세요/EFN+5/./SF
				result.add(match.group(0).substring(1,match.group(0).length()-1)); 
			}
		}
		return result;
	}
	
}
