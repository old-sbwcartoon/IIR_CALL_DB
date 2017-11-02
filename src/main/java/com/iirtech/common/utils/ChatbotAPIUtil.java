package com.iirtech.common.utils;

import java.io.IOException;
import java.io.StringReader;
import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.Logger;

import com.fasterxml.jackson.core.JsonGenerationException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * @Package   : com.iirtech.common.utils
 * @FileName  : ChatbotAPIUtil.java
 * @작성일       : 2017. 8. 24. 
 * @작성자       : choikino
 * @explain : 외부 서버와 데이터를 주고 받기 위한 Chatbot API 관련 작업에서 사용되는 Utils
 * 				JsonParser, ScriptRuleParser
 */
public class ChatbotAPIUtil {

	private Logger log = Logger.getLogger(this.getClass());
	
	
	//node 형태의 중첩 json에서 inner node의 값을 꺼내오기 
	public String getInnerJsonValueFromJsonNodes(String jsonStr) {
		//{"message":{"@type":"response","@service":"naverservice.nmt.proxy","@version":"1.0.0","result":{"srcLangType":"ko","tarLangType":"en","translatedText":"Hello."}}}
		JsonNode rootNode;
		String result = "";
		try {
			rootNode = new ObjectMapper().readTree(new StringReader(jsonStr));
			JsonNode innerNode = rootNode.get("message");
			JsonNode test = innerNode.get("result");
			result = test.toString();
		} catch (JsonProcessingException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return result;
	}
	
	// json parser
	// json_string >> map_object
	public Map<String,Object> jsonStrToMap(String jsonStr){
		Map<String, Object> resultMap = new HashMap<String, Object>();
		try {
			ObjectMapper mapper = new ObjectMapper();
			// convert JSON string to Map
			resultMap = mapper.readValue(jsonStr, new TypeReference<Map<String, String>>(){});
			log.debug("JsonToMap>>>"+resultMap);
		} catch (JsonGenerationException e) {
			e.printStackTrace();
		} catch (JsonMappingException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return resultMap;
	}
	
	
	// map_object >> json_string
	public String mapToJsonStr(Map<String, Object> paramMap) {
		String jsonStrResult = "";
		try {

			ObjectMapper mapper = new ObjectMapper();
			// pretty print
			jsonStrResult = mapper.writerWithDefaultPrettyPrinter().writeValueAsString(paramMap);
			log.debug("MapToJson>>>"+jsonStrResult);
			// convert map to JSON string
			jsonStrResult = mapper.writeValueAsString(paramMap);
			
		} catch (JsonGenerationException e) {
			e.printStackTrace();
		} catch (JsonMappingException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return jsonStrResult;
	}
	
	//script rule parser
	// script rules >> json_string
	
}
