package com.iirtech.chatbot.service.impl;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.snu.ids.ha.index.Keyword;
import org.snu.ids.ha.index.KeywordExtractor;
import org.snu.ids.ha.index.KeywordList;
import org.snu.ids.ha.ma.Eojeol;
import org.snu.ids.ha.ma.MExpression;
import org.snu.ids.ha.ma.Morpheme;
import org.snu.ids.ha.ma.MorphemeAnalyzer;
import org.snu.ids.ha.ma.Sentence;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.iirtech.chatbot.service.ChatbotNLPService;
import com.iirtech.chatbot.service.ChatbotScriptService;
import com.iirtech.common.enums.DialogStatus;
import com.iirtech.common.utils.ChatbotUtil;
import com.iirtech.common.utils.UtilsForPPGO;

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
	@Autowired
	private ChatbotScriptService cbss;
	@Autowired
	private ChatbotNLPService cbns;
	
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
	 * @param procText
	 * @return 서브 테마 code
	 */
	@Override
	public String getSubThemeStatusCd(String procText, MorphemeAnalyzer ma) {
		String subThemeStatusCd = null;
		String strToExtrtKwrd = procText;
		
		
		// extract keywords
		if (strToExtrtKwrd != null && strToExtrtKwrd != "") {
			
			
			// 식사하다, 탈것을 타다, 이동하다, 숙소로 가다, 짐을 찾다, 차 렌트하다, 쇼핑하다, 친구 만나다, 환전하다, 길잃다
			String[] subThemeArr = {"meal", "vehicle" /*, "vehicle_taxi" */, "move" /* , "inn", "getLuggage", "carRental" */,  "shopping" /*, "meetFriend", "exchangeMoney", "loseMyWay" */ };
			
			String[] dictNameArr = {"company", "drink", "entertainer", "food", "hotel", "korea_location", "music", "nation", "restaurant"
					, "school", "transport", "travel_place", "TV_drama_program", "TV_movie_program", "TV_show_program"};
			
			// 주제별 대응 사전 이름 저장
			HashMap<String, ArrayList<String>> dictNameListInSubTheme = new HashMap<String, ArrayList<String>>();
			dictNameListInSubTheme.put("meal", new ArrayList<String>(Arrays.asList(new String[]{dictNameArr[1], dictNameArr[3], dictNameArr[8]})));
			dictNameListInSubTheme.put("vehicle", new ArrayList<String>(Arrays.asList(new String[]{dictNameArr[10]})));
//			dictNameListInSubTheme.put("vehicle_taxi", new ArrayList<String>(Arrays.asList(new String[]{dictNameArr[10]})));
			dictNameListInSubTheme.put("move", new ArrayList<String>(Arrays.asList(new String[]{dictNameArr[5], dictNameArr[7], dictNameArr[9], dictNameArr[11]})));
//			dictNameListInSubTheme.put("inn", new ArrayList<String>(Arrays.asList(new String[]{dictNameArr[4]})));
//			dictNameListInSubTheme.put("getLuggage", new ArrayList<String>(Arrays.asList(new String[]{})));
//			dictNameListInSubTheme.put("carRental", new ArrayList<String>(Arrays.asList(new String[]{dictNameArr[10]})));
			dictNameListInSubTheme.put("shopping", new ArrayList<String>(Arrays.asList(new String[]{})));
//			dictNameListInSubTheme.put("meetFriend", new ArrayList<String>(Arrays.asList(new String[]{})));
//			dictNameListInSubTheme.put("exchangeMoney", new ArrayList<String>(Arrays.asList(new String[]{})));
//			dictNameListInSubTheme.put("loseMyWay", new ArrayList<String>(Arrays.asList(new String[]{})));
			
			// 예문 문장 저장
			HashMap<String, ArrayList<String>> expInput = new HashMap<String, ArrayList<String>>();
			expInput.put("meal", new ArrayList<String>(Arrays.asList(new String[]{"도착하자마자 {food_1}을/를 먹었어요~"})));
			expInput.put("vehicle", new ArrayList<String>(Arrays.asList(new String[]{"도착하자마자 {what_0}을/를 탔어요~"})));
//			expInput.put("vehicle_taxi", new ArrayList<String>(Arrays.asList(new String[]{"도착하자마자 택시를 탔어요~"})));
			expInput.put("move", new ArrayList<String>(Arrays.asList(new String[]{"도착하자마자 {where_1}으로/로 갔어요~"})));
//			expInput.put("inn", new ArrayList<String>(Arrays.asList(new String[]{"도착하자마자 숙소로 갔어요~"})));
//			expInput.put("getLuggage", new ArrayList<String>(Arrays.asList(new String[]{"도착하자마자 짐을 찾았어요~"})));
//			expInput.put("carRental", new ArrayList<String>(Arrays.asList(new String[]{"도착하자마자 차를 빌렸어요~"})));
			expInput.put("shopping", new ArrayList<String>(Arrays.asList(new String[]{"도착하자마자 쇼핑했어요~" /*, "도착하자마자 옷을 샀어요~", "도착하자마자 물건을 샀어요~", "도착하자마자 기념품을 샀어요~" */})));
//			expInput.put("meetFriend", new ArrayList<String>(Arrays.asList(new String[]{"도착하자마자 친구를/가족을 만났어요~"})));
//			expInput.put("exchangeMoney", new ArrayList<String>(Arrays.asList(new String[]{"도착하자마자 환전을 했어요~"})));
//			expInput.put("loseMyWay", new ArrayList<String>(Arrays.asList(new String[]{"도착하자마자 길을 잃어버렸어요~"})));
			
			// 조사 + 동사 = 사용자 문장 : 예문 문장
			// 명사 = 사용자 키워드 단어 : 사전 단어
			
				HashMap<String, ArrayList<String>> inputMorpMap = getMorpListMap(strToExtrtKwrd, ma);
				ArrayList<String> inputJList = inputMorpMap.get("jList");
				ArrayList<String> inputVList = inputMorpMap.get("vList");
				ArrayList<String> inputNList = inputMorpMap.get("nList");
				
				HashMap<String, ArrayList<String>> expMorpMap = null;
				
				// subThemeScore = (vCnt * vWeight) + (jCnt * jWeight) + (nCnt * nWeight)
				double jWeight = 0.5;
				double vWeight = 1;
				double nWeight = 0.5;
				Double[] subThemeScoreArr = new Double[subThemeArr.length]; // 서브테마별 점수 저장
				
				// 예문 문장 형태소 분석
				for (int i = 0; i < subThemeArr.length; i++) {
					
					String subTheme = subThemeArr[i];
					ArrayList<String> expInputList = expInput.get(subTheme); // 예문 리스트
					double jCnt = 0;
					double vCnt = 0;
					double nCnt = 0;
					for (String expStr : expInputList) {
//						expStr; 예문 문장
						expMorpMap = getMorpListMap(expStr, ma);
						
						ArrayList<String> expJList = expMorpMap.get("jList");
						ArrayList<String> expVList = expMorpMap.get("vList");
						ArrayList<String> expNList = expMorpMap.get("nList");
						
						for (int j = 0; j < expJList.size(); j++) {
							for (String inputJ : inputJList) {
								if (inputJ.equalsIgnoreCase(expJList.get(j))) {
									jCnt += 1 / expJList.size();
								}
							}
						}
						
						for (int j = 0; j < expVList.size(); j++) {
							for (String inputV : inputVList) {
								if (inputV.equalsIgnoreCase(expVList.get(j))) {
									vCnt += 1 / expVList.size();
								}
							}
						}
						
						for (int j = 0; j < expNList.size(); j++) {
							for (String inputN : inputNList) {
								if (inputN.equalsIgnoreCase(expNList.get(j))) {
									nCnt += 1 / expNList.size();
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
					

					double minSimilarityScore = 0.7;
					String filePath = urlFilePath + "dictionary/WikiDictionary/";
					HashMap<String, ArrayList<?>> similarityMap = getMaxSimilarityAndFileName(filePath, candidateSubThemeList, dictNameListInSubTheme, inputNList, minSimilarityScore);
					
					ArrayList<String> fileNameList   = (ArrayList<String>)similarityMap.get("fileNameList");
					ArrayList<Double> similarityList = (ArrayList<Double>)similarityMap.get("similarityList");
					// 사용자 입력문 키워드마다, 사전과 가장 높은 유사도 수치를 대응 서브 테마의 score에 더함
					for (int i = 0; i < candidateSubThemeList.size(); i++) {
						
						for (int j = 0; j < fileNameList.size(); j++) {
//							log.debug(maxSimilarDictPerKeyword.get(i) + ":" + keywordList.get(i) + " = " + maxSimilarityPerKeyword.get(i));
							ArrayList<String> subThemeKeyList = getKeyFromValue(dictNameListInSubTheme, fileNameList.get(j));
							
							for (int k = 0; k < subThemeKeyList.size(); k++) {
								if (candidateSubThemeList.get(i).equals(subThemeKeyList.get(k))) {
									candidateSubThemeScoreList.set(i, candidateSubThemeScoreList.get(i) + similarityList.get(j));
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
				
				// maxSimilarityScore가 0일 때 // 오류로 분류해야 함
				if (maxSimilarityScore == 0) {
					maxSimiarSubTheme = subThemeArr[0]; // 임시로 무조건 subTheme 첫번째("meal")로 매핑
				}
				/*
				 * 
				 * 
				 * 
				 * 
				 * 
				 * 
				 * 
				 */
				
				subThemeStatusCd = DialogStatus.getStatusName("sub_"+maxSimiarSubTheme).getStatusCd();
				log.debug("subTheme=" + subThemeStatusCd);
				
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
	
	/**
	 * 문자열을 형태소 (JK, VV, NN)로 분류해서 각각 리스트로 변환 후 맵으로 반환한다
	 * @param String str
	 * @return HashMap("jList", jlist), HashMap("vList", vlist), HashMap("nList", nlist)
	 */
	@Override
	public HashMap<String, ArrayList<String>> getMorpListMap(String str, MorphemeAnalyzer ma) {
		HashMap<String, ArrayList<String>> resultMap = null;
		
		if (str != null && str != "") {
			resultMap = new HashMap<String, ArrayList<String>>();
			
			ArrayList<String> vList = new ArrayList<String>(); // 입력 문장 속 동사 리스트
			ArrayList<String> jList = new ArrayList<String>(); // 입력 문장 속 조사 리스트
			ArrayList<String> nList = new ArrayList<String>(); // 입력 문장 속 명사 리스트
			
			// 사용자 입력 문장 형태소 분석
			
//					ma.createLogger(null);
		
			try {
				List<MExpression> ret = ma.analyze(str);
				ret = ma.postProcess(ret);
				ret = ma.leaveJustBest(ret);
		
				List<Sentence> stl = ma.divideToSentences(ret);
				
				for( int i = 0; i < stl.size(); i++ ) {
					Sentence st = stl.get(i);
					
					for( int j = 0; j < st.size(); j++ ) {
						
						Eojeol eojeol = st.get(j);
						log.debug(eojeol);
						for (Morpheme morp : eojeol) {
							
							log.debug(morp);
							if (morp.getTag().contains("JK")) {
								jList.add(morp.getString());
							} else if (morp.getTag().equals("VV")) {
								vList.add(morp.getString());
							} 
//							else if (morp.getTag().contains("NN")) {
//								n    = morp.getString();
//								// 유효 키워드가 한 글자일 경우 그 글자를 포함한 어절에서 조사를 제외한 명사의 합으로 대치
//								if (nIdx == morp.getIndex()) {
//									if (nLen < morp.getString().length()) {
//										
//									}
//								}
//								nList.add(n);
//								nIdx = morp.getIndex();
//								nLen = morp.getString().length();
//							}
						}
					}
				}
				
				// string to extract keywords
				String strToExtrtKwrd = str;

				// init KeywordExtractor
				KeywordExtractor ke = new KeywordExtractor();

				// extract keywords
				KeywordList kl = ke.extractKeyword(strToExtrtKwrd, true);

				// print result
				String n = "";
				int nIdx = 0;
				int nLen = 0;
				int nCnt = 0;
				
				// 키워드의 어절 내 idx가 겹칠 경우, 등장 수가 가장 많거나 문자 수가 가장 많은 키워드를 nList 추가함
				for( int i = 0; i < kl.size(); i++ ) {
					Keyword kwrd = kl.get(i);
					
					if (i == 0) {
						nIdx = kwrd.getIndex();
					}
					
					// 새 문자열의 어절 내 idx가 이전과 같다면
					if (nIdx == kwrd.getIndex()) {
						
						// 새 문자열의 등장 수가 이전과 같거나 더 크다면, 또는 길이가 더 길다면
						if (nCnt <= kwrd.getCnt() || nLen < kwrd.getString().length()) {
							n = kwrd.getString();
							
							if (i == kl.size() -1) {
								nList.add(n);
							}
						}
					
						// 이전의 문자열 길이와 지금 문자열의 index가 같다면 (이전 문자열 다음 문자열이라면)
					} else if (i == kl.size() -1 || kwrd.getIndex() == n.length() + nIdx){
//						if (i == kl.size() -1) {
//							nList.add(kwrd.getString());
//						} else {
							nList.add(n);
							nIdx = kwrd.getIndex();
						
					}
					nLen = kwrd.getString().length();
					nCnt = kwrd.getCnt();
				}
				

				// 명사를 못찾으면
				if (nList.isEmpty()) {
					
					// 조사가 있다면
					if (!jList.isEmpty()) {
						// 첫 번째 조사 앞까지를 명사로 저장
						nList.add(strToExtrtKwrd.split(jList.get(0))[0]);
					} else if (!vList.isEmpty()) {
						// 동사가 있다면
						String tmpN = strToExtrtKwrd.split(vList.get(0))[0];
						// 동사 앞까지 중 마지막 공백 제거하고 명사로 저장
						if (tmpN.lastIndexOf(" ") == tmpN.length() - 1) {
							nList.add(tmpN.substring(0, tmpN.length() - 1));
						}
					}
				}
				
			} catch(Exception e) {
				e.printStackTrace();
			}
			
			resultMap.put("jList", jList);
			resultMap.put("vList", vList);
			resultMap.put("nList", nList);
		}
		
		return resultMap;
	}
	
	
	/**
	 * list 포함된 키워드 별 최대 유사도에 해당하는 수치 및 파일이름을 가져온다.<br>
	 * 결과값으로 리턴되는 fileNameList와 similarityList의 index는 같이 움직인다.
	 * @param candidateList
	 * @param dictNameListMap
	 * @param keywordList
	 * @return HashMap("fileNameList", (String)maxSimilarFileNameList)<br>HashMap("similarityList", (Double)maxSimilarityList)
	 * 
	 */
	@Override
	public HashMap<String, ArrayList<?>> getMaxSimilarityAndFileName(String filePath, ArrayList<String> sysKeywordList, HashMap<String, ArrayList<String>> dictNameListMap, ArrayList<String> inputKeywordList, double minSimilarityScore) {
		
		HashMap<String, ArrayList<?>> resultMap = new HashMap<String, ArrayList<?>>();

		
		ArrayList<Double> maxSimilarityList  = new ArrayList<Double>();
		ArrayList<String> maxSimilarFileNameList = new ArrayList<String>();
		ArrayList<String> targetInputKeywordList = new ArrayList<String>();
		for (int i = 0; i < inputKeywordList.size(); i++) { // 입력문 키워드 수만큼

			for (String sysKeyword : sysKeywordList) {
				
				if (dictNameListMap.containsKey(sysKeyword) && !dictNameListMap.get(sysKeyword).isEmpty()) {
					ArrayList<String> candidateDictNameList = dictNameListMap.get(sysKeyword);
					
					for (int j = 0; j < candidateDictNameList.size(); j++) {
						String dictName = candidateDictNameList.get(j);
						String fileName  = dictName + ".txt";
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
							oneWordSimilarityScore = jw.similarity(dictWord, inputKeywordList.get(i));
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
						
//						log.debug(dictName + ": " + oneDictSimilarityScore);
						// 유사도 점수 최대값 파일 이름 구하기
//						if (!maxSimilarFileNameList.contains(dictName)) {
							maxSimilarityList.add(oneDictSimilarityScore);
							maxSimilarFileNameList.add(dictName);
							targetInputKeywordList.add(inputKeywordList.get(i));
//						} else {
//							maxSimilarityList.set(j, maxSimilarityList.get(j) + oneDictSimilarityScore);
//						}
					
					}
				}
				
				
			}
					
		}
		
		// 각 키워드의 각 사전별 유사도 평균 셋팅
		for (int i = 0; i < maxSimilarityList.size(); i++) {
			maxSimilarityList.set(i, maxSimilarityList.get(i) / inputKeywordList.size());
		}
		resultMap.put("fileNameList", maxSimilarFileNameList);
		resultMap.put("similarityList", maxSimilarityList);
		resultMap.put("keywordList", targetInputKeywordList);
		
		return resultMap;
	}
	
	
	/**
	 * 조사 을/를, 이/가, 은/는 을 앞 단어에 따라 선택해서 반환한다
	 * @param str
	 * @param josaWithJongsung
	 * @param josaWithoutJongsung
	 * @return 을 or 를, 이 or 가, 은 or 는
	 */
	@Override
	public String getJosaByJongsung(String str, String josaWithJongsung, String josaWithoutJongsung) {
		String result = null;
		
		char lastWord = str.charAt(str.length() - 1);
		if (lastWord == 34) { // 마지막 문자가 "일 경우. ascii code 34 == "
			lastWord = str.charAt(str.length() - 2);
		}
		// 한글이 아닐 경우
		if (lastWord < 0xAC00 || lastWord > 0xD7A3) {
			result = null;
		} else {
			if (hasLastKoreanWordJongsung(lastWord)) {
				result = josaWithJongsung;
			} else {
				result = josaWithoutJongsung;
			}
		}
		
		return result;
	}
	
	
	/**
	 * 한글 문자열의 마지막 글자가 받침을 가졌는지 알려준다
	 * @param str
	 * @return boolean
	 */
	@Override
	public boolean hasLastKoreanWordJongsung(char lastWord) {
		boolean hasJongsung = true;
		hasJongsung = (lastWord - 0xAC00) % 28 > 0 ? true : false;

		return hasJongsung;
	}
	
	
	/**
	 * 한글을 입력하면 영문으로 번역해서 반환
	 * @param korStr
	 * @return engStr
	 */
	@Override
	public String getEngByKor(String korStr) {
		String engStr = null;
		
		UtilsForPPGO ufp = new UtilsForPPGO();
		
		String clientId = "v_norw0FYk6gNwbDHt7Q";	//후보아이디1: S3PJoLLxPOJUy9hNLtv7,	후보아이디2: JeP6rRJ4lQfBmEndNrMd
		String clientPwd = "CxWLhAMS5C";				//후보암호1: NVKI_JDMU3,				후보암호2: WfUqaWRsRU
		String fromLang = "KOR";
		
		if (korStr != null) {
			engStr = ufp.getTranslation(korStr, clientId, clientPwd, fromLang);
		}
		
		return engStr;
	}

	
	/**
	 * 스크립트 진행을 멈추고 도움말(번역을 요청한 단어, 오류 주석) 정보를 가져온다<br>
	 * 입력문이 질문이 아닐 경우 오류 체크를 한다
	 * @param procInputText
	 * @return hashMap.infoType(translation 또는 errorCode), hashMap.data(번역을 요청한 단어 또는 오류 주석 코드)
	 */
	@Override
	public HashMap<String, String> getPauseCondition(String procInputText, MorphemeAnalyzer ma) {
		HashMap<String, String> resultMap = new HashMap<String, String>();
		
		String askContent = getAskContent(procInputText, ma);
		
		if (askContent != null) {
			int askCode = getContentCode(askContent);
			
			// 챗봇 시스템에 관한 질문일 경우
			if (askCode > 0) {
				resultMap.put("infoType", "systemAsk");
				resultMap.put("data", cbss.getAnswerSentence(askCode, null, null));
				
			} else {
				// 번역에 관한 질문일 경우
				String korContent = askContent;
				String engContent = cbns.getEngByKor(korContent);
				// 번역되었을 때 마지막 글자 은, 는, 이, 가 삭제 후 표출
				String lastWord = korContent.substring(korContent.length() - 1, korContent.length());
				
				if (lastWord.equals(" ")) {
					korContent = korContent.substring(0, korContent.length() - 1);
				} else {
					// 마지막 문자가 공백이 아닐 경우(형태소 분석기에서 조사를 지우지 않았을 경우) 마지막 문자 은, 는, 이, 가 삭제
					String[] chkArr = {"은", "는", "이", "가"};
					for (String chk : chkArr) {
						if (lastWord.equals(chk)) {
							korContent = korContent.substring(0, korContent.length() - 1);
							break;
						}
					}
				}
				
				// 질문한 내용이 번역되지 않았을 경우 -- 마지막 글자 은, 는, 이, 가 삭제 후 다시 번역 
				if (engContent == null || engContent.equals("")) {
					engContent = cbns.getEngByKor(korContent);
				}
				resultMap.put("infoType", "translation");
				resultMap.put("data", cbss.getAnswerSentence(askCode, korContent, engContent));
				
			}
			
			
		} else {
//			String errorCode  = 오류체크메서드(procInputText); 오류 판별시 errorCode, 정상 문장은 null
//			if (errorCode != null) {
//				resultMap.put("infoType", errorCode);
//				resultMap.put("data", errorCode);
//			}
		}

		return resultMap;
	}
	

	/**
	 * 컨텐츠의 종류 판별해서 반환
	 * @param askContent
	 * @return 0: 뜻 질문, 1: 이름, 2: 나이 
	 */
	private int getContentCode(String askContent) {
		int code = 0;
		
		String[] chkArr1 = {"너", "넌", "너는", "니", "닌", "니는", "네", "너의", "니의"};
		String[] chkArr2 = {"이름", "나이", "연세", "몇살", ""};
		
		
		for (int i = 0; i < chkArr1.length; i++) {
			
			for (int j = 0; j < chkArr2.length; j++) {
				String askKey  = chkArr1[i] + chkArr2[j];
				String trimStr = askContent.replaceAll(" ", "");
				// 공백을 제거한 문자가 askKey의 길이 + 1(문자 마지막의 "은, 는, 이, 가" 붙었을 경우를 포함)보다 작거나 같을 때, 서로 같다면,
				if (trimStr.length() <= askKey.length() + 1 && trimStr.contains(askKey)) {
					if ( j == chkArr2.length - 1) { // chkArr2 == ""
						if (i > 5) {
							code = 0;
						} else {
							code = 1;
						}
					} else if (j == 0) {
						code = 1;
					} else {
						code = 2;
					}
				}
			}
		}
		
		return code;
	}

	/**
	 * 사용자 입력문이 질문인지 확인해서 결과 반환
	 * @param procInputText
	 * @return 질문이라면 문장에서 질문글 제거한 문장, 일반 응답문이라면 null<br>
	 * !질문글 제거한 문장에서 조사를 지웠을 경우 마지막에 공백 추가!
	 */
	@Override
	public String getAskContent(String procInputText, MorphemeAnalyzer ma) {
		boolean isAsk = false;
		String askContent = null;
		String[] wordArr  = null;
		
		if (procInputText != null) {
			
			String str = procInputText;
			String askKey     = "";
			String trimAskKey = "";
			
			String[] chkArr1 = {"뭐", "무엇", "뜻이 뭐", "무슨 뜻", "무슨", "무엇 뜻", "뜻이 무엇", "뜻 뭐", "뭔", "뭔 뜻", "뭡", "뜻이 뭡", "뜻 뭡"};
			String[] chkArr2 = {"야", "니", "냐", "이냐", "라고", "이라고", "요", "이요", "이오", "이야", "지", "이지", "죠", "지요", "데", "인데", "인가", "인가요", "가", "가요", "에요", "예요", "이에요", "이예요", "니까", "입니까"};	
			String[] chkArr3 = {"?"};
			
			for (int i = 0; i < chkArr1.length; i++) {
				
				if (!isAsk) {
					for (int j = 0; j < chkArr2.length; j++) {
						
						if (!isAsk) {
							for (int k = 0; k < chkArr3.length; k++) {
								askKey = chkArr1[i] + chkArr2[j] + chkArr3[k];
								// 질문 키워드 제외하고 다른 문자열이 있을 때, 질문 키워드를 가지고 있다면
								if (str.contains(askKey) && str.length() > askKey.length() ) {
									askContent = str.replace(askKey, "");
								}
								trimAskKey = chkArr1[i].replaceAll(" ", "") + chkArr2[j] + chkArr3[k];
								// askKey에서 공백 제거한 문자열로도 대조
								if (str.contains(trimAskKey) && str.replaceAll(" ", "").length() > trimAskKey.length() ) {
									askContent = str.replace(trimAskKey, "");
								}
								
								if (askContent != null) {
									
									// 마지막 공백만 제거
									wordArr = askContent.split(" ");
									askContent = "";
									for (int h = 0; h < wordArr.length; h++) {
										askContent += wordArr[h];
										if (h < wordArr.length - 1) {
											askContent += " ";
										}
									}
									
									// 시간 오래 걸리므로 getEngByKor를 여러 번 돌려서 제어
									// 사용자 입력 문장 형태소 분석
//									MorphemeAnalyzer ma = new MorphemeAnalyzer();

									try {
										List<MExpression> ret = ma.analyze(str);
										ret = ma.postProcess(ret);
										ret = ma.leaveJustBest(ret);
								
										List<Sentence> stl = ma.divideToSentences(ret);
										
										for( Sentence st : stl ) {
											
											for( Eojeol eojeol : st ) {
												
												for (Morpheme morp : eojeol) {
													
													if (morp.getIndex() == askContent.length() - 1) {
														if (morp.getTag().contains("JK") || morp.getTag().equals("JX")) {
															askContent = askContent.substring(0, askContent.length() - 1) + " "; // 조사를 지웠을 경우 마지막에 공백 추가
														}
													}
												}
											}
										}
									} catch (Exception e) {
										e.printStackTrace();
									}
									
									isAsk = true;
									break;
								}
							}
						} else {
							break;
						}
						
					}
				} else {
					break;
				}
				
			}
			
			
		}
		
		return askContent;
	}


	
}

