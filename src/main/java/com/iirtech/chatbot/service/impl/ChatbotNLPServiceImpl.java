package com.iirtech.chatbot.service.impl;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.snu.ids.ha.ma.Eojeol;
import org.snu.ids.ha.ma.MExpression;
import org.snu.ids.ha.ma.Morpheme;
import org.snu.ids.ha.ma.MorphemeAnalyzer;
import org.snu.ids.ha.ma.Sentence;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.iirtech.chatbot.service.ChatbotNLPService;
import com.iirtech.common.enums.DialogStatus;
import com.iirtech.common.utils.ChatbotUtil;

import info.debatty.java.stringsimilarity.JaroWinkler;

/**
 * @Package   : com.iirtech.chatbot.service.impl
 * @FileName  : ChatbotNLPServiceImpl.java
 * @작성일       : 2017. 8. 14. 
 * @작성자       : choikino
 * @explain : 이르테크 챗봇 시스템 입력 메시지 전처리와 관련된 메소드들을 실제로 구현  
 */
@Service
public class ChatbotNLPServiceImpl implements ChatbotNLPService {

	private Logger log = Logger.getLogger(this.getClass());
	
	@Autowired
	ChatbotUtil cbu;
	
	@Value("#{systemProp['filepath']}") 
	String urlFilePath;
	@Value("#{systemProp['systemdelimeter']}") 
	String systemDelimeter;
	@Override
	public Map<String,Object> preProcess(String procInputText) {
		log.debug("*************************preProcess*************************");
		Map<String,Object> resultMap = new HashMap<String, Object>();
		String procText = procInputText;
		//전처리 ~
		
		List<String> textTypes = new ArrayList<String>();
		//문장정보 파악~ isPositive/isNegative/isAsking
		textTypes.add("isPositive");//일단 긍정으로 하드코딩 
		textTypes.add("isCorrect");//일단 맞음으로 하드코딩 
		
		//전처리된 문장과 전처리 후 파악된 문장정보를 맵객체에 담아서 리턴
		resultMap.put("procText", procText);
		resultMap.put("textTypes", textTypes);
		return resultMap;
	}

	@Override
	public String selectKeyword(String keywordType, Map<String, Object> conditionInfoMap) {
		String result = "";
		//1.keywordType을 가지고 dict파일에서 뒤져서 키워드 판단-구현
		String dictFilePath = urlFilePath + "dictionary/";
		String fileName = keywordType + ".dict"; //TOPIC.dict
		String procText = conditionInfoMap.get("procText").toString();
		//사전파일읽음 
		List<String> lines = cbu.readFileByLine(dictFilePath, fileName);
		List<String> keywordCandidates = new ArrayList<String>();
		for (String line : lines) {
			//line >> 여행|travel
			String[] lineArr = line.split("\\"+systemDelimeter);
			String CITKey = lineArr[0]; //TOPIC:여행 or SUB:여행 서브 테마
			String CITValue = lineArr[1]; //TOPIC:travel or SUB:travel/sub
			if(line.contains(CITKey) && !keywordCandidates.contains(CITValue)) {
				keywordCandidates.add(CITValue);
			}
		}
		
		//2.전처리 정보를 가지고 판별식을 통해 키워드 판단-미구현 
		
		
		//3.추출된 키워드가 한개 이상일 경우 한개만 선택하기(사용자가 고의로 여러개 입력-에러)로 봄
		//예외처리이므로 미구현
		
		//일단 임시로 첫번째 것만 리턴하는거로 TOPIC:string "travel|TOPIC" or SUB:string "travel/sub|SUB"
		result = keywordCandidates.get(0) + systemDelimeter + keywordType;
		return result;
	}

	/**
	 * 서브테마 체크 후 해당하는 코드를 반환
	 * 
	 */
	@Override
	public String getSubThemeStatusCd(String procText) {
		String subThemeStatusCd = null;
		String strToExtrtKwrd = procText;
		
		
		// extract keywords
		if (strToExtrtKwrd != null && strToExtrtKwrd != "") {
			
			
			// 식사하다, 탈것을 타다, 이동하다, 숙소로 가다, 짐을 찾다, 차 렌트하다, 쇼핑하다, 친구 만나다, 환전하다, 길잃다
			String[] subThemeArr = {"meal", "vehicle_bus", "vehicle_taxi", "move", "inn", "getLuggage", "carRental", "shopping", "meetFriend", "exchangeMoney", "loseMyWay"};
			
			String[] dictNameArr = {"company", "drink", "entertainer", "food", "hotel", "korea_location", "music", "nation", "restaurant"
					, "school", "transport", "travel_place", "TV_drama_program", "TV_movie_program", "TV_show_program"};
			
			// 주제별 대응 사전 이름 저장
			HashMap<String, ArrayList<String>> dictNameListInSubTheme = new HashMap<String, ArrayList<String>>();
			dictNameListInSubTheme.put("meal", new ArrayList<String>(Arrays.asList(new String[]{dictNameArr[1], dictNameArr[3], dictNameArr[8]})));
			dictNameListInSubTheme.put("vehicle_bus", new ArrayList<String>(Arrays.asList(new String[]{dictNameArr[10]})));
			dictNameListInSubTheme.put("vehicle_taxi", new ArrayList<String>(Arrays.asList(new String[]{dictNameArr[10]})));
			dictNameListInSubTheme.put("move", new ArrayList<String>(Arrays.asList(new String[]{dictNameArr[5], dictNameArr[7], dictNameArr[9], dictNameArr[11]})));
			dictNameListInSubTheme.put("inn", new ArrayList<String>(Arrays.asList(new String[]{dictNameArr[4]})));
			dictNameListInSubTheme.put("getLuggage", new ArrayList<String>(Arrays.asList(new String[]{null})));
			dictNameListInSubTheme.put("carRental", new ArrayList<String>(Arrays.asList(new String[]{dictNameArr[10]})));
			dictNameListInSubTheme.put("shopping", new ArrayList<String>(Arrays.asList(new String[]{null})));
			dictNameListInSubTheme.put("meetFriend", new ArrayList<String>(Arrays.asList(new String[]{null})));
			dictNameListInSubTheme.put("exchangeMoney", new ArrayList<String>(Arrays.asList(new String[]{null})));
			dictNameListInSubTheme.put("loseMyWay", new ArrayList<String>(Arrays.asList(new String[]{null})));
			
			// 예문 문장 저장
			HashMap<String, ArrayList<String>> expInput = new HashMap<String, ArrayList<String>>();
			expInput.put("meal", new ArrayList<String>(Arrays.asList(new String[]{"도착하자마자 밥을 먹었어요~"})));
			expInput.put("vehicle_bus", new ArrayList<String>(Arrays.asList(new String[]{"도착하자마자 버스를 탔어요~"})));
			expInput.put("vehicle_taxi", new ArrayList<String>(Arrays.asList(new String[]{"도착하자마자 택시를 탔어요~"})));
			expInput.put("move", new ArrayList<String>(Arrays.asList(new String[]{"도착하자마자 {where}으로/로 갔어요~"})));
			expInput.put("inn", new ArrayList<String>(Arrays.asList(new String[]{"도착하자마자 숙소로 갔어요~"})));
			expInput.put("getLuggage", new ArrayList<String>(Arrays.asList(new String[]{"도착하자마자 짐을 찾았어요~"})));
			expInput.put("carRental", new ArrayList<String>(Arrays.asList(new String[]{"도착하자마자 차를 빌렸어요~"})));
			expInput.put("shopping", new ArrayList<String>(Arrays.asList(new String[]{"도착하자마자 쇼핑했어요~"})));
			expInput.put("meetFriend", new ArrayList<String>(Arrays.asList(new String[]{"도착하자마자 친구를/가족을 만났어요~"})));
			expInput.put("exchangeMoney", new ArrayList<String>(Arrays.asList(new String[]{"도착하자마자 환전을 했어요~"})));
			expInput.put("loseMyWay", new ArrayList<String>(Arrays.asList(new String[]{"도착하자마자 길을 잃어버렸어요~"})));
			
			// 조사 + 동사 = 사용자 문장 : 예문 문장
			// 명사 = 사용자 키워드 단어 : 사전 단어
			
			try {
			
				ArrayList<String> inputVList = new ArrayList<String>(); // 입력 문장 속 동사 리스트
				ArrayList<String> inputJList = new ArrayList<String>(); // 입력 문장 속 조사 리스트
				ArrayList<String> inputNList = new ArrayList<String>(); // 입력 문장 속 명사 리스트
				
				// 사용자 입력 문장 형태소 분석
				MorphemeAnalyzer ma = null;
				
				ma = new MorphemeAnalyzer();
//				ma.createLogger(null);
			
				List<MExpression> ret = ma.analyze(strToExtrtKwrd);
				ret = ma.postProcess(ret);
				ret = ma.leaveJustBest(ret);
	
				List<Sentence> stl = ma.divideToSentences(ret);
				
				for( int i = 0; i < stl.size(); i++ ) {
					Sentence st = stl.get(i);
//					System.out.println("=============================================  " + st.getSentence());
					
					for( int j = 0; j < st.size(); j++ ) {
						
						Eojeol eojeol = st.get(j);
//						log.debug(eojeol);
						
						for (Morpheme morp : eojeol) {
							if (morp.getTag().contains("JK")) {
								inputJList.add(morp.getString());
							} else if (morp.getTag().equals("VV")) {
								inputVList.add(morp.getString());
							} else if (morp.getTag().contains("NN")) {
								inputNList.add(morp.getString());
							}
						}
					}
				}
				
				// subThemeScore = (vCnt * vWeight) + (jCnt * jWeight) + (nCnt * nWeight)
				double jWeight = 0.5;
				double vWeight = 1;
				double nWeight = 0.5;
				Double[] subThemeScoreArr = new Double[subThemeArr.length]; // 서브테마별 점수 저장
				
				// 예문 문장 형태소 분석
				for (int i = 0; i < subThemeArr.length; i++) {
					
					String subTheme = subThemeArr[i];
					ArrayList<String> expInputList = expInput.get(subTheme); // 예문 리스트
					int jCnt = 0;
					int vCnt = 0;
					int nCnt = 0;
					for (String expStr : expInputList) {
//						expStr; 예문 문장
						ma = null;
						
						ma = new MorphemeAnalyzer();
//						ma.createLogger(null);
					
						ret = ma.analyze(expStr);
						ret = ma.postProcess(ret);
						ret = ma.leaveJustBest(ret);
	
						stl = ma.divideToSentences(ret);
						
						for( int j = 0; j < stl.size(); j++ ) {
							Sentence st = stl.get(j);
//							System.out.println("=============================================  " + st.getSentence());
							
							for( int k = 0; k < st.size(); k++ ) {
								
								Eojeol eojeol = st.get(k);
								log.debug(eojeol);
								for (Morpheme morp : eojeol) {
									if (morp.getTag().contains("JK")) {
										for (String inputJ : inputJList) {
											if (inputJ.equals(morp.getString())) {
												jCnt++;
											}
										}
									} else if (morp.getTag().equals("VV")) {
										for (String inputV : inputVList) {
											if (inputV.equals(morp.getString())) {
												vCnt++;
											}
										}
									} else if (morp.getTag().contains("NN")) {
										for (String inputN : inputNList) {
											if (inputN.equals(morp.getString())) {
												nCnt++;
											}
										}
									}
								}
							}
						}
					}
					subThemeScoreArr[i] = (vCnt * vWeight) + (jCnt * jWeight) + (nCnt * nWeight);
				}
			
				// 사용자 입력 동사, 조사의 점수가 0 넘는 주제 모으기
				ArrayList<String> candidateSubThemeList      = new ArrayList<String>();
				ArrayList<Double> candidateSubThemeScoreList = new ArrayList<Double>();
				double maxScore = 0;
				int maxScoreCnt = 0;
				for (int i = 0; i < subThemeArr.length; i++) {
					if (subThemeScoreArr[i] > 0) {
						candidateSubThemeList.add(subThemeArr[i]);
						candidateSubThemeScoreList.add(subThemeScoreArr[i]);
						if (subThemeScoreArr[i] >= maxScore ) {
							maxScore = subThemeScoreArr[i];
							maxScoreCnt++;
						}
					}
				}
				
				// 예문 조사+동사와 일치하는 사용자 입력 조사+동사가 존재하지 않거나, 주제별 조사+동사 점수가 동점이 존재할 경우, 사전에서 명사 유사도 구하기
				if (candidateSubThemeList.isEmpty() || maxScoreCnt > 1) {
					
					// 후보 서브 테마가 없을 경우, 모든 사전을 후보 서브 테마 지정
					if (candidateSubThemeList.isEmpty()) {
						candidateSubThemeList.addAll(new ArrayList<String>(Arrays.asList(subThemeArr)));
						candidateSubThemeScoreList.addAll(new ArrayList<Double>(Arrays.asList(subThemeScoreArr)));
					}
					ArrayList<String> keywordList = inputNList;
					double minSimilarityScore = 0.7;

					String filePath = urlFilePath + "dictionary/WikiDictionary/";
					
					ArrayList<Double> maxSimilarityPerKeyword  = new ArrayList<Double>();
					ArrayList<String> maxSimilarDictPerKeyword = new ArrayList<String>();
					for (int i = 0; i < keywordList.size(); i++) { // 입력문 키워드 수만큼

						for (String candidateSubTheme : candidateSubThemeList) {
							ArrayList<String> candidateDictNameList = dictNameListInSubTheme.get(candidateSubTheme);
								
							for (int j = 0; j < candidateDictNameList.size(); j++) {
								String themeName = candidateDictNameList.get(j);
								String fileName  = themeName + ".txt";
								List<String> lines = cbu.readFileByLine(filePath, fileName);
								
								String dict = "";
								for (String line : lines) {
									dict += line;
								}
								
								double oneWordSimilarityScore = 0;
								double oneDictSimilarityScore = 0;
								int overThresholdCnt = 0;
								// 유사도 
								String[] dictWordArr = dict.split(",");
								JaroWinkler jw = new JaroWinkler();
								for (int k = 0; k < dictWordArr.length; k++) { // 
									String dictWord = dictWordArr[k];
									oneWordSimilarityScore = jw.similarity(dictWord, keywordList.get(i));
									// min유사도 점수를 넘는 유효 유사도 값 더하기
									if (oneWordSimilarityScore > minSimilarityScore) {
										oneDictSimilarityScore += oneWordSimilarityScore;
										overThresholdCnt++;
									}
								}
								if (overThresholdCnt == 0) {
									overThresholdCnt = 1;
								}
								// 한 사전 내 유효 유사도 값들의 평균값 저장
								oneDictSimilarityScore = oneDictSimilarityScore / overThresholdCnt;
								
//								log.debug(themeName + ": " + oneDictSimilarityScore);
								// 유사도 점수 최대값 파일 이름 구하기
								if (!maxSimilarDictPerKeyword.contains(themeName)) {
									maxSimilarityPerKeyword.add(oneDictSimilarityScore);
									maxSimilarDictPerKeyword.add(themeName);
								} else {
									maxSimilarityPerKeyword.set(j, maxSimilarityPerKeyword.get(j) + oneDictSimilarityScore);
								}
							
							}
							
						}
								
					}
					
					// 각 키워드의 각 사전별 유사도 평균 셋팅
					for (int i = 0; i < maxSimilarityPerKeyword.size(); i++) {
						maxSimilarityPerKeyword.set(i, maxSimilarityPerKeyword.get(i) / keywordList.size());
					}
					
					// 사용자 입력문 키워드마다, 사전과 가장 높은 유사도 수치를 대응 서브 테마의 score에 더함
					for (int i = 0; i < candidateSubThemeList.size(); i++) {
						
						for (int j = 0; j < maxSimilarDictPerKeyword.size(); j++) {
//							log.debug(maxSimilarDictPerKeyword.get(i) + ":" + keywordList.get(i) + " = " + maxSimilarityPerKeyword.get(i));
							ArrayList<String> subThemeKeyList = getKeyFromValue(dictNameListInSubTheme, maxSimilarDictPerKeyword.get(j));
							
							for (int k = 0; k < subThemeKeyList.size(); k++) {
								if (candidateSubThemeList.get(i).equals(subThemeKeyList.get(k))) {
									candidateSubThemeScoreList.set(i, candidateSubThemeScoreList.get(i) + maxSimilarityPerKeyword.get(j));
								}
							}
						}
							
							
					}
					
					
				}
				
				// 가장 높은 점수를 받은 서브 테마 이름 구하기
				double maxSimilarityScore = 0;
				String maxSimiarSubTheme  = null;
				for (int i = 0; i < candidateSubThemeScoreList.size(); i++) {
					if (candidateSubThemeScoreList.get(i) > maxSimilarityScore) {
						maxSimilarityScore = candidateSubThemeScoreList.get(i);
						maxSimiarSubTheme = candidateSubThemeList.get(i);
					}
				}
			
				subThemeStatusCd = DialogStatus.getStatusName("sub_"+maxSimiarSubTheme).getStatusCd();
				log.debug("subTheme=" + subThemeStatusCd);
				
//				ma.closeLogger();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		
		return subThemeStatusCd;
	}
	
	/**
	 * Map에서 value를 가진 key를 찾는다
	 * @param hm
	 * @param value
	 * @return keyList
	 */
	public ArrayList<String> getKeyFromValue(Map hm, Object value) {
		ArrayList<String> keyList = new ArrayList<String>();
		for (Object o : hm.keySet()) {
			ArrayList<String> valueList = (ArrayList<String>)hm.get(o);
			for (String s : valueList) {
				if (s != null && s.equals(value)) {
					keyList.add(String.valueOf(o));
				}
			}
		}
		return keyList;
	}
}
