/**
 * 
 */
package com.iirtech.common.utils;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.core.JsonProcessingException;

/**
 * @Package   : com.iir.chatbot
 * @FileName  : UtilsForPPGO.java
 * @작성일       : 2017. 10. 31. 
 * @작성자       : choikino
 * @explain : 
 */

public class UtilsForPPGO {
	
	
	public String getTranslation(String korStr, String clientId, String clientPwd, String fromLang) {
		String result = "";
		
	        try {
	            String text = URLEncoder.encode(korStr, "UTF-8");
	            String apiURL = "https://openapi.naver.com/v1/papago/n2mt";
	            URL url = new URL(apiURL);
	            HttpURLConnection con = (HttpURLConnection)url.openConnection();
	            con.setRequestMethod("POST");
	            con.setRequestProperty("X-Naver-Client-Id", clientId);
	            con.setRequestProperty("X-Naver-Client-Secret", clientPwd);
	            // post request
	            String postParams = "";
	            if(fromLang.equals("ENG")) {
	            		postParams = "source=en&target=ko&text=" + text;
	            }else if(fromLang.equals("KOR")) {
		            	postParams = "source=ko&target=en&text=" + text;
	            }
	            con.setDoOutput(true);
	            DataOutputStream wr = new DataOutputStream(con.getOutputStream());
	            wr.writeBytes(postParams);
	            wr.flush();
	            wr.close();
	            int responseCode = con.getResponseCode();
	            BufferedReader br;
	            if(responseCode==200) { // 정상 호출
	                br = new BufferedReader(new InputStreamReader(con.getInputStream()));
	            } else {  // 에러 발생
	                br = new BufferedReader(new InputStreamReader(con.getErrorStream()));
	            }
	            String inputLine;
	            StringBuffer response = new StringBuffer();
	            while ((inputLine = br.readLine()) != null) {
	                response.append(inputLine);
	            }
	            br.close();
	            String jsonStr = response.toString();
	            result = this.getWordFromJsonStr(jsonStr);
	        } catch (Exception e) {
	            System.out.println(e);
	        }
		
		return result;
	}
	
	public String getWordFromJsonStr(String jsonStr) throws JsonProcessingException, IOException {
		//>>>>>>>>papago result: {"message":{"@type":"response","@service":"naverservice.nmt.proxy","@version":"1.0.0","result":{"srcLangType":"ko","tarLangType":"en","translatedText":"Hello."}}}
		String result = "";
		ChatbotAPIUtil cau = new ChatbotAPIUtil();
		String tempJsonStr = cau.getInnerJsonValueFromJsonNodes(jsonStr);
		Map<String, Object> resultMap = cau.jsonStrToMap(tempJsonStr);//{srcLangType=ko, tarLangType=en, translatedText=Hello.}
		result = (String) resultMap.get("translatedText");
		return result;
	}
	
	public void translateAllWords(String clientId, String clientPwd, String fromLang, String fromPath, String toPath) throws Exception {
		
		UtilsForPPGO ufp = new UtilsForPPGO();
		
		//파일 읽어서 모든 단어를 한글로 번역하기
		BufferedReader br = new BufferedReader(new FileReader(fromPath));
		List<String> engwords = new ArrayList<String>();
		List<String> korwords = new ArrayList<String>();
		
		while (true) {
			String word = br.readLine();
			if(word == null) break;
			engwords.add(word);
		}
		br.close();

		for (int i = 0; i < engwords.size(); i++) {
			String word = engwords.get(i);
			String newWord = ufp.getTranslation(word, clientId, clientPwd, fromLang);
			System.out.println("[" + i + "]" + newWord);
			korwords.add(newWord);
			Thread.sleep(1000);
		}
		
		String content = "";
		for (int i = 0; i < korwords.size(); i++) {
			content += korwords.get(i) + "\n";
		}
		FileWriter fw = new FileWriter(toPath);
		fw.write(content);
		fw.close();
		
	}
	
	
	public void mkSentimentWords(String fromPath, String toPath) throws Exception{
		//1.리스트에서 중복단어 제거하고 형태소 분석결과가 체언 혹은 용언 혹은 어근인 대상만 추출하여 최종 감정사전 생성

		UtilsForGGMA ufg = new UtilsForGGMA();
		BufferedReader br = new BufferedReader(new FileReader(fromPath));
		
		List<String> targetWords = new ArrayList<String>();
		while (true) {
			String word = br.readLine();
			if(word == null) break;
			targetWords.add(word);
		}
		br.close();
		
		List<String> filteredWords = new ArrayList<String>();
		for (int i = 0; i < targetWords.size(); i++) {
			String word = targetWords.get(i);
			List<List<String>> morphResult = ufg.morphAnalyze(word);
			for (int j = 0; j < morphResult.size(); j++) {
				for (int k = 0; k < morphResult.get(j).size(); k++) {
					//NNG, VV(하 제외), VA, XR(하 제외)   
					String targetLine = morphResult.get(j).get(k);//[[0/정확/NNG+2/하/XSV+3/게/ECD]]
					String[] eumjeol = targetLine.split("\\+");
					for (int l = 0; l < eumjeol.length; l++) {//0/정확/NNG
						String[] eojeol = eumjeol[l].split("\\/");
						String targetWord = eojeol[1];
						String targetMorph = eojeol[2];
						String elmntOfFilteredWords = targetWord + "|" + targetMorph; 
						if (targetMorph.equals("NNG") || targetMorph.equals("VA") || targetMorph.equals("VV") || (targetMorph.equals("XR") && !targetWord.equals("하"))) {
							filteredWords.add(elmntOfFilteredWords);// 정확|NNG
						}
					}
				}
			}
		}
		
		filteredWords = new ArrayList<String>(new HashSet<String>(filteredWords));
		String content = "";
		for (int i = 0; i < filteredWords.size(); i++) {
			content += filteredWords.get(i) + "\n";
		}
		
		FileWriter fw = new FileWriter(toPath);
		fw.write(content);
		fw.close();
	}

}
