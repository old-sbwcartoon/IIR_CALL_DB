/**
 * 
 */
package com.iirtech.common.utils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.snu.ids.ha.ma.MorphemeAnalyzer;
import org.snu.ids.ha.ma.Sentence;

import com.iirtech.chatbot.dto.CompareDomain;
import com.iirtech.chatbot.dto.CompareOrderNumAsc;
import com.iirtech.common.enums.ClassificationConstants;
import com.iirtech.common.enums.MorphTypes;
import com.iirtech.common.enums.SimilarityWeights;

import info.debatty.java.stringsimilarity.JaroWinkler;

/**
 * @Package   : com.iir.chatbot
 * @FileName  : UtilsForGGMA.java
 * @작성일       : 2017. 10. 26. 
 * @작성자       : choikino
 * @explain : 
 */

public class UtilsForGGMA {

	public int doClassification(String expectedMsg, String inputMsg) throws Exception {
		int result = -1;
		UtilsForGGMA ufg = new UtilsForGGMA();
		int mode = ClassificationConstants.FOCUS_ACTION; //유사도 구할 때 중점적으로 보는 사항 별 모드를 지정해줌 
		//int mode = ClassificationConstants.FOCUS_NOUN; //유사도 구할 때 중점적으로 보는 사항 별 모드를 지정해줌 
		
		//1. 꼬꼬마 형태소 분석기 - 형태소분석 ** 시스템에 답안으로 가지고 있는 문장들은 미리 분석결과를 저장해 놓아서 성능 개선을 노린다.
		//expected Msg Analyze
		List<List<String>> expectedSentence = ufg.morphAnalyze(expectedMsg);
		//input Msg Analyze
		List<List<String>> inputSentence = ufg.morphAnalyze(inputMsg);
		
		//2. 명사와 동사에 한해서 최소 등장 횟수를 계산한다. 최소 횟수만큼만 각 형태소별 유사도 점수를 누적해야함
		Map<String, Integer> addScoreLimitPerMorph = ufg.getAddScoreLimitPerMorph(expectedSentence, inputSentence);
		
		//3. 유사도 구할 어휘쌍 추출하기
		List<CompareDomain> wordPairs = ufg.getCompareWordPair(expectedSentence, inputSentence, mode);
		//4. 유사도 계산
		double maximumSimilarityScore = 0;
		if(!wordPairs.isEmpty()) {//비교할 대상이 있을 때만 태운다.
			maximumSimilarityScore = ufg.getSimilarityScore(wordPairs, addScoreLimitPerMorph, mode);
		}
		System.out.print(maximumSimilarityScore);
		//5. 획득한 유사도 점수가 기준점 보다 크면 정상 낮으면 오류
		if(maximumSimilarityScore >= ClassificationConstants.THRESHOLD) {
			result = ClassificationConstants.NORMAL;
		}else {
			result = ClassificationConstants.ERROR;
		}
		
		return result;
	}
	
	
	/**
	 * @Method   : getAddScoreLimitPerMorph
	 * @작성일     : 2017. 10. 27. 
	 * @작성자     : choikino
	 * @explain : 형태소별 최소갯수를 통해 유사도 점수 누적 횟수를 제한한다.
	 * @param :
	 * @return :
	 */
	
	public Map<String, Integer> getAddScoreLimitPerMorph(List<List<String>> expectedSentence, List<List<String>> inputSentence) {
		Map<String, Integer> resultMap = new HashMap<String, Integer>();
		//[[0/학교/NNG+2/에서/JKM, 5/밥/NNG+6/을/JKO, 8/먹/VV+9/습니다/EFN+12/./SF]]
		int numOfExpectedSentenceNoun = 0;
		int numOfExpectedSentenceVerb = 0;
		int numOfExpectedSentenceAdverb = 0;
		
		int numOfInputSentenceNoun = 0;
		int numOfInputSentenceVerb = 0;
		int numOfInputSentenceAdverb = 0;
		
		//루프돌면서 N으로 시작하는거랑 V로 시작하는거 갯수 세기
		for (int i = 0; i < expectedSentence.size(); i++) {
			for (int j = 0; j < expectedSentence.get(i).size(); j++) {
				String token = expectedSentence.get(i).get(j);
				//명사 찾기 정규식 객체
				Pattern nounPattern = Pattern.compile("N{1,2}\\w");
				Matcher nounMatcher = nounPattern.matcher(token);
				//동사 찾기 정규식 객체
				Pattern verbPattern = Pattern.compile("V{1}\\w{1,2}");
				Matcher verbMatcher = verbPattern.matcher(token);
				//부사 및 관형사 찾기 정규식 객체
				Pattern adverbPattern = Pattern.compile("M{1}\\w{1,2}");
				Matcher adverbMatcher = adverbPattern.matcher(token);
				if(nounMatcher.find()) {
					numOfExpectedSentenceNoun++;
				}
				if(verbMatcher.find()) {
					numOfExpectedSentenceVerb++;
				}
				if(adverbMatcher.find()) {
					numOfExpectedSentenceAdverb++;
				}
			}
		}
		//루프돌면서 N으로 시작하는거랑 V로 시작하는거 갯수 세기
		for (int i = 0; i < inputSentence.size(); i++) {
			for (int j = 0; j < inputSentence.get(i).size(); j++) {
				String token = inputSentence.get(i).get(j);
				//명사 찾기 정규식 객체
				Pattern nounPattern = Pattern.compile("N{1,2}\\w");
				Matcher nounMatcher = nounPattern.matcher(token);
				//동사 찾기 정규식 객체
				Pattern verbPattern = Pattern.compile("V{1}\\w{1,2}");
				Matcher verbMatcher = verbPattern.matcher(token);
				//부사 및 관형사 찾기 정규식 객체
				Pattern adverbPattern = Pattern.compile("M{1}\\w{1,2}");
				Matcher adverbMatcher = adverbPattern.matcher(token);
				if(nounMatcher.find()) {
					numOfInputSentenceNoun++;
				}
				if(verbMatcher.find()) {
					numOfInputSentenceVerb++;
				}
				if(adverbMatcher.find()) {
					numOfInputSentenceAdverb++;
				}
			}
		}
		
		int finalNounCnt = numOfExpectedSentenceNoun > numOfInputSentenceNoun ? numOfInputSentenceNoun : numOfExpectedSentenceNoun;
		int finalVerbCnt = numOfExpectedSentenceVerb > numOfInputSentenceVerb ? numOfInputSentenceVerb : numOfExpectedSentenceVerb;
		int finalAdverbCnt = numOfExpectedSentenceAdverb > numOfInputSentenceAdverb ? numOfInputSentenceAdverb : numOfExpectedSentenceAdverb;
		
		resultMap.put("NOUN", finalNounCnt);
		resultMap.put("VERB", finalVerbCnt);
		resultMap.put("ADVERB", finalAdverbCnt);
		
		return resultMap;
	}


	/**
	 * @Method   : morphAnalyze
	 * @작성일     : 2017. 10. 27. 
	 * @작성자     : choikino
	 * @explain : 형태소 분석하여 원하는 문자열 리스트로 리턴 
	 * @param :
	 * @return :
	 */
	public List<List<String>> morphAnalyze(String msg) throws Exception{
		UtilsForGGMA ufg = new UtilsForGGMA();
		MorphemeAnalyzer ma = new MorphemeAnalyzer();
		List ret = ma.analyze(msg);//expectedMsg!!
		ret = ma.postProcess(ret);
		ret = ma.leaveJustBest(ret);
		List stl = ma.divideToSentences(ret);
		List<List<String>> sentence = new ArrayList<List<String>>();
		for( int i = 0; i < stl.size(); i++ ) {//문장단위로 루프
			Sentence st = (Sentence) stl.get(i);
			//[[0/학교/NNG+2/에서/JKM, 5/밥/NNG+6/을/JKO, 8/먹/VV+9/습니다/EFN+12/./SF]]
			sentence.add(ufg.getSplittedWords(st));//getSplittedWords 어절단위로 루프 
		}
		return sentence;
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
	public List<CompareDomain>  getCompareWordPair(List<List<String>> expectedSentence, List<List<String>> inputSentence, int mode){
		UtilsForGGMA ufg = new UtilsForGGMA();

		List<String> compareWordPairs = new ArrayList<String>();//inputSentenceSeq|exptecdWord|inputWord|morpheme
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
		List<List<String>> expectedCompareStrings = ufg.getCompareStrings(expectedSentence, mode);//[[NNG|학교NNG에서JKM, NNG|밥NNG을JKO, VV|먹VV습니다EFN.SF]]
		List<List<String>> inputCompareStrings = ufg.getCompareStrings(inputSentence, mode);//[[NNG|학교NNG에JKM, VV|가VV었EPT어요EFN.SF], [MAG|그리고MAG, NNG|밥NNG을JKO, VV|먹VV었EPT어요EFN.SF]]
		for(int i = 0; i < expectedCompareStrings.size(); i++) {
			for (int j = 0; j < expectedCompareStrings.get(i).size(); j++) {
				String expectedLine = expectedCompareStrings.get(i).get(j);
				String expectedMorph = expectedLine.split("\\|")[0];
				String expectedWord = expectedLine.split("\\|")[1];
				for (int k = 0; k < inputCompareStrings.size(); k++) {
					for (int l = 0; l < inputCompareStrings.get(k).size(); l++) {
						String inputLine = inputCompareStrings.get(k).get(l);
						String inputMorph = inputLine.split("\\|")[0];
						String inputWord = inputLine.split("\\|")[1];
						
						String expectedMorphForMatch = expectedMorph;
						if(expectedMorphForMatch.startsWith("N")) {
							expectedMorphForMatch = MorphTypes.NOUN;
						}
						else if(expectedMorphForMatch.startsWith("V")) {
							expectedMorphForMatch = MorphTypes.VERB;
						}
						else if(expectedMorphForMatch.startsWith("J")) {
							expectedMorphForMatch = MorphTypes.POSTPOSITION;
						}
						else if(expectedMorphForMatch.startsWith("M")) {
							expectedMorphForMatch = MorphTypes.ADVERB;
						}
						else if(expectedMorphForMatch.startsWith("E")) {
							expectedMorphForMatch = MorphTypes.EOW;
						}
						
						String inputMorphForMatch = inputMorph;
						if(inputMorphForMatch.startsWith("N")) {
							inputMorphForMatch = MorphTypes.NOUN;
						}
						else if(inputMorphForMatch.startsWith("V")) {
							inputMorphForMatch = MorphTypes.VERB;
						}
						else if(inputMorphForMatch.startsWith("J")) {
							inputMorphForMatch = MorphTypes.POSTPOSITION;
						}
						else if(inputMorphForMatch.startsWith("M")) {
							inputMorphForMatch = MorphTypes.ADVERB;
						}
						else if(inputMorphForMatch.startsWith("E")) {
							inputMorphForMatch = MorphTypes.EOW;
						}
						
						if(expectedMorphForMatch.equals(inputMorphForMatch)) {
							String compareStr = k + "|" + expectedWord + "|" + inputWord + "|" + expectedMorph;
							compareWordPairs.add(compareStr);
						}
					}
				}
			}
		}
		
		//[0|학교NNG에서JKM|학교NNG에JKM|NNG, 1|학교NNG에서JKM|밥NNG을JKO|NNG, 0|밥NNG을JKO|학교NNG에JKM|NNG, 1|밥NNG을JKO|밥NNG을JKO|NNG, 0|먹VV습니다EFN.SF|가VV었EPT습니다EFN.SF|VV, 1|먹VV습니다EFN.SF|먹VV었EPT습니다EFN.SF|VV]
		List<CompareDomain> sortedCompareList = new ArrayList<CompareDomain>();
		for (int i = 0; i < compareWordPairs.size(); i++) {
			CompareDomain cd = new CompareDomain(); //expectedMsg, inputMsg, representativMorph
			//inputSentenceSeq|exptecdWord|inputWord|morpheme
			String compareLine = compareWordPairs.get(i);//0|학교NNG에서JKM|학교NNG에JKM|NNG
			String[] compareElmnts = compareLine.split("\\|");
			int lineSeq = Integer.parseInt(compareElmnts[0]);
			cd.setOrderNum(lineSeq);
			cd.setExpectedMsg(compareElmnts[1]);
			cd.setInputMsg(compareElmnts[2]);
			cd.setRepresentativeMorph(compareElmnts[3]);
			sortedCompareList.add(cd);
		}
		//order num 으로 오름차순 정렬
		Collections.sort(sortedCompareList, new CompareOrderNumAsc());
		return sortedCompareList;
	}
	
	
	/**
	 * @param addScoreLimitPerMorph 
	 * @Method   : getSimilarityScore
	 * @작성일     : 2017. 10. 25. 
	 * @작성자     : choikino
	 * @explain : 유사도를 구할 단어쌍들을 인자로 받아서 형태소별 가중치를 적용한 최종 스코어를 리턴한다.
	 * @param :
	 * @return :
	 */
	public Double getSimilarityScore(List<CompareDomain> sortedCompareList, Map<String, Integer> addScoreLimitPerMorph, int mode){
		UtilsForGGMA ufg = new UtilsForGGMA();
		//i 리스트 idx 마다 j개의 대응쌍이 있을테니 다시 루프돌면서 유사도 구하고 max값을 선택 
		/*
		어순이 바뀔 수 있고 한문장에 동일한 형태소의 다른 낱말이 등장 할 수 있으므로 모두 비교 후 유사도가 높은값을 선택한다.
		동일한 형태소 끼리 비교하고 서로 같거나 다를때 주는 가중치를 명사 > 동사 = 형용사 > 부사 > 조사 > 어미 순서대로 준다.
		숫자? 기호? 외국어?
		두 문장일때 처리? 둘다 유사도를 구하고 높은 유사도에 해당하는 문장을 기준으로 분류를 진행해준다.
		*/
		JaroWinkler jw = new JaroWinkler();
		List<List<CompareDomain>> similarityScores = new ArrayList<List<CompareDomain>>();
		List<CompareDomain> tempScores = new ArrayList<CompareDomain>();
		
		double similarityScore = 0;
		int exOrderNum = 0;
		int loopCnt = 0;
		for (CompareDomain compareDomain : sortedCompareList) {
			int orderNum = compareDomain.getOrderNum();
			String exportedMsg = compareDomain.getExpectedMsg();
			String inputMsg = compareDomain.getInputMsg();
			String representativeMorph = compareDomain.getRepresentativeMorph();
			
			//System.out.print("sorted >>>>>>>>>: " + orderNum);
			//System.out.print(", " + exportedMsg);
			//System.out.print(", " + inputMsg);
			//System.out.println(", " + representativeMorph);
			SimilarityWeights sw = new SimilarityWeights(mode);
			double weight = 0;
			if(representativeMorph.startsWith("N")) {
				weight = sw.NOUN;
			}
			else if(representativeMorph.startsWith("V")) {
				weight = sw.VERB;
			}
			else if(representativeMorph.startsWith("J")) {
				weight = sw.POSTPOSITION;
			}
			else if(representativeMorph.startsWith("M")) {
				weight = sw.ADVERB;
			}
			else if(representativeMorph.startsWith("E")) {
				weight = sw.EOW;
			}
			/*
			sorted >>>>>>>>>: 0, 학교NNG에서JKM, 학교NNG에JKM, NNG
			sorted >>>>>>>>>: 0, 밥NNG을JKO, 학교NNG에JKM, NNG
			sorted >>>>>>>>>: 0, 먹VV습니다EFN.SF, 가VV었EPT어요EFN.SF, VV
			sorted >>>>>>>>>: 1, 학교NNG에서JKM, 밥NNG을JKO, NNG
			sorted >>>>>>>>>: 1, 밥NNG을JKO, 밥NNG을JKO, NNG
			sorted >>>>>>>>>: 1, 먹VV습니다EFN.SF, 먹VV었EPT어요EFN.SF, VV
			*/
			similarityScore = jw.similarity(exportedMsg, inputMsg) * weight;
			compareDomain.setSimilarityScore(similarityScore);
			tempScores.add(compareDomain);
			if((exOrderNum != orderNum) || (loopCnt == sortedCompareList.size()-1)) {
				similarityScores.add(tempScores);
				tempScores = new ArrayList<CompareDomain>();//초기화 중요!!!
				//System.out.println("total_simillarityScore: " + simillarityScore);
			}
			exOrderNum = orderNum;
			loopCnt ++;
		}
		
		List<List<Double>> nounScores = new ArrayList<List<Double>>();
		List<List<Double>> verbScores = new ArrayList<List<Double>>();
		List<List<Double>> adverbScores = new ArrayList<List<Double>>();
		List<List<Double>> etcScores = new ArrayList<List<Double>>();
		
		//필요한 유사도 값만 형태소 품사별로 모으기
		for (int i = 0; i < similarityScores.size(); i++) {
			List<Double> nounTemp = new ArrayList<Double>();
			List<Double> verbTemp = new ArrayList<Double>();
			List<Double> adverbTemp = new ArrayList<Double>();
			List<Double> etcTemp = new ArrayList<Double>();
			for (int j = 0; j < similarityScores.get(i).size(); j++) {
				CompareDomain cd = similarityScores.get(i).get(j);
				String morphType = cd.getRepresentativeMorph();
				if (morphType.startsWith("N")) {
					nounTemp.add(cd.getSimilarityScore());
				}
				else if(morphType.startsWith("V")) {
					verbTemp.add(cd.getSimilarityScore());
				}
				else if(morphType.startsWith("M")) {
					adverbTemp.add(cd.getSimilarityScore());
				}
				else {
					etcTemp.add(cd.getSimilarityScore());
				}
			}
			nounScores.add(nounTemp);
			verbScores.add(verbTemp);
			adverbScores.add(adverbTemp);
			etcScores.add(etcTemp);//1
		}
		
		// 크기별로 정렬하고 다시 limit 갯수만큼 더해서 문장별 토탈 스코어 구하기 
		int nounLimit = addScoreLimitPerMorph.get("NOUN");
		int verbLimit = addScoreLimitPerMorph.get("VERB");
		int adverbLimit = addScoreLimitPerMorph.get("ADVERB");
		List<Double> sortedNounTotalScores = ufg.getSortedListTotalScore(nounScores, nounLimit);
		List<Double> sortedVerbTotalScores = ufg.getSortedListTotalScore(verbScores, verbLimit);
		List<Double> sortedAdverbTotalScores = ufg.getSortedListTotalScore(adverbScores, adverbLimit);
		List<Double> sortedEtcTotalScores = ufg.getSortedListTotalScore(etcScores, 1);
		
		List<Integer> listSizes = new ArrayList<Integer>();
		int nounSize = sortedNounTotalScores.size();
		int verbSize = sortedVerbTotalScores.size();
		int adverbSize = sortedAdverbTotalScores.size();
		int etcSize = sortedEtcTotalScores.size();
		listSizes.add(nounSize);
		listSizes.add(verbSize);
		listSizes.add(adverbSize);
		listSizes.add(etcSize);
		
		int maxSize = Collections.max(listSizes);
		// 문장별 토탈 스코어 구해서 가장 큰 값을 리턴
		List<Double> totalScoresPerSentence = new ArrayList<Double>();
		for (int i = 0; i < maxSize; i++) {
			double nounScore = 0;
			if(i < nounSize) nounScore = sortedNounTotalScores.get(i);
			double verbScore = 0;
			if(i < verbSize) verbScore = sortedVerbTotalScores.get(i);
			double adverbScore = 0;
			if(i < adverbSize) adverbScore = sortedAdverbTotalScores.get(i);
			double etcScore = 0;
			if(i < etcSize) etcScore = sortedEtcTotalScores.get(i);
			double totalScorePerSentence = nounScore + verbScore + adverbScore + etcScore;
			totalScoresPerSentence.add(totalScorePerSentence);
		}
		
		double maximumSimillarityScore = Collections.max(totalScoresPerSentence);
		return (int)(maximumSimillarityScore*1000)/1000.0; //소수점 4째자리 이하 버림
	}
	

	/**
	 * @param limitCnt 
	 * @param addScoreLimitPerMorph 
	 * @Method   : getSortedList
	 * @작성일     : 2017. 10. 28. 
	 * @작성자     : choikino
	 * @explain : 중첩 리스트 안의 값을 정렬시키고 제한 수만큼만 남긴다.
	 * @param :
	 * @return :
	 */
	public List<Double> getSortedListTotalScore(List<List<Double>> morphScores, int limitCnt) {
		List<Double> result = new ArrayList<Double>();
		List<List<Double>> tempResult = new ArrayList<List<Double>>();
		//정렬 
		for (int i = 0; i < morphScores.size(); i++) {
			if(!morphScores.get(i).isEmpty()) {
				Collections.sort(morphScores.get(i), Collections.reverseOrder());
			}
			tempResult.add(morphScores.get(i));
		}
		//제한수 만큼만 더해서 값 넣기
		for (int i = 0; i < tempResult.size(); i++) {
			double score = 0;
			if(!tempResult.get(i).isEmpty()) {
				int idx = tempResult.get(i).size() > limitCnt ? limitCnt : tempResult.get(i).size();
				for (int j = 0; j < idx; j++) {
					score += tempResult.get(i).get(j);
				}
			}
			result.add(score);
		}
		return result;
	}


	/**
	 * @Method   : getCompareStrings
	 * @작성일     : 2017. 10. 26. 
	 * @작성자     : choikino
	 * @explain : 대응쌍을 뽑기위한 스트링형태로 가공한다.
	 * @param :
	 * @return :
	 */
	public List<List<String>> getCompareStrings(List<List<String>> Sentence, int mode){
		List<List<String>> result = new ArrayList<List<String>>();
		for (int i = 0; i < Sentence.size(); i++) {//[[0/제주도/NNP/C+3/요/JX]]
			List<String> tempList = new ArrayList<String>();
			for (int j = 0; j < Sentence.get(i).size(); j++) {//[0/제주도/NNP/C+3/요/JX]
				String expectedTokenStr = Sentence.get(i).get(j);//0/제주도/NNP/C+3/요/JX
				String compareStr = "";
				String morphType = "";
				String[] temp1 = expectedTokenStr.split("\\+");//[8/먹/VV, 9/었/EPT, 10/습니다/EFN, 13/./SF]
				for (int k = 0; k < temp1.length; k++) {
					String[] temp2 = temp1[k].split("/");//0,제주도,NNP,C
					if(k == 0) {
						morphType = temp2[2];//NNP
					}else {
						if(temp2[2].startsWith("V")) {
							morphType = temp2[2];
						}
						else if(temp2[2].startsWith("N") && !morphType.startsWith("V")) {
							morphType = temp2[2];
						}
						else if(temp2[2].startsWith("E") && mode == ClassificationConstants.FOCUS_ACTION) {
							//액션을 중시하는 유사도 계산에서는 동사의 경우 시제, 어말 어미 등 문법요소 때문에 판별력 저하되어서 어근,문장부호 빼고 제거 
							temp2[1] = ""; 
						}
					}
					compareStr += temp2[1];
				}
				compareStr = morphType + "|" + compareStr;
				tempList.add(compareStr);//[NNG|학교에서, NNG|밥을, VV|먹.]
			}
			result.add(tempList);
		}
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
	public List<String> getSplittedWords(Sentence st){
		List<String> result = new ArrayList<String>();
		String regex = "\\[.*\\]";
		for (int i = 0; i < st.size(); i++) {
			String userStr = String.valueOf(st.get(i));
			Pattern pattern = Pattern.compile(regex);
			Matcher match = pattern.matcher(userStr);
			if(match.find()) {
				//System.out.println("Matched::" + match.group(0));
				//0/안녕/NNG+2/하/XSV+3/세요/EFN+5/./SF
				result.add(match.group(0).substring(1,match.group(0).length()-1)); 
			}
		}
		return result;
	}
	
	
}
