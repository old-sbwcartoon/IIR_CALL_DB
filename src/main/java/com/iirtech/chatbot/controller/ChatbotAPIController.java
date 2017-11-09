package com.iirtech.chatbot.controller;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.json.simple.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.converter.FormHttpMessageConverter;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.servlet.ModelAndView;

import com.iirtech.common.utils.ChatbotAPIUtil;
import com.iirtech.common.utils.UtilsForPPGO;



/**
 * @Package   : com.iirtech.chatbot.controller
 * @FileName  : ChatbotAPIController.java
 * @작성일       : 2017. 8. 25. 
 * @작성자       : choikino
 * @explain : REST API test를 위한 클래스 
 */
@RestController
public class ChatbotAPIController {
	
	private Logger log = Logger.getLogger(this.getClass());
	@Autowired
	ChatbotAPIUtil cbau;
	
	@Value("#{systemProp['papagoclientId']}") 
	String clientId;
	@Value("#{systemProp['papagoclientPwd']}") 
	String clientPwd;
	
	//for test
	//REST API
	@ResponseBody
	@RequestMapping(value = "/IIR.do")
	public String KAIST(@RequestParam String jsonStr) {
		//jsonStr을 MAP으로 변환해 받기로 한 param명인 name의 value를 꺼내서 hi 문자열을 붙여서 리턴해준다.
		Map<String,Object> paramMap = cbau.jsonStrToMap(jsonStr);
		String name = String.valueOf(paramMap.get("name"));
		String greetingMsg = "hi~~~!! "+ name;
		
		//json string 형식의 parameter 세팅
	    JSONObject jo = new JSONObject();
	    jo.put("msg", greetingMsg); //{msg:"hi~~~!! KINO"}
		//
	    return jo.toJSONString();
	}
	
	// kaist에 request 날리고, response를 돌려받는 test용도 
	@RequestMapping(value = "URLConnTest.do")
	public ModelAndView URLConnTest() {
		ModelAndView mav = new ModelAndView();
	    // RestTemplate 에 MessageConverter 세팅
	    List<HttpMessageConverter<?>> converters = new ArrayList<HttpMessageConverter<?>>();
	    converters.add(new FormHttpMessageConverter());
	    converters.add(new StringHttpMessageConverter());
	    
	    RestTemplate restTemplate = new RestTemplate();
	    restTemplate.setMessageConverters(converters);
	    
	    //json string 형식의 parameter 세팅
	    JSONObject jo = new JSONObject();
	    jo.put("name", "KINO"); //{name:"KINO"}
	    MultiValueMap<String, String> map = new LinkedMultiValueMap<String, String>();
	    map.add("jsonStr", jo.toJSONString());//jsonStr={"name":"KINO"}
	    
	    // REST API 호출: 약속된 패러미터 전달 url(http://localhost:8090/KAIST)을 호출 
	    String jsonStrResult = restTemplate.postForObject("http://localhost:7080/IIR.do", map, String.class);
	    String oopsResult = restTemplate.getForObject("http://chatbotaip.azurewebsites.net/api/morph",String.class, map);	    
	    //jsonString 형식의 response param을 Map객체로 변환 후 이미 약속된 msg 변수의 값을 추출하여 result에 세팅 후 view page로 전달 
	    Map<String,Object> tempMap = cbau.jsonStrToMap(jsonStrResult);
	    String result = String.valueOf(tempMap.get("msg"));
	    log.debug("==================[KAIST]=====================");
	    log.debug(result);
	    log.debug("===================[OOPS]====================");
	    log.debug(oopsResult);
	    mav.addObject("result",result);

		return mav;
	}

	
	//문장을 번역하는 ajax 호출에 응답한다.
	@RequestMapping(value = "translateLang.do")
	public ModelAndView translateLang(@RequestParam Map<String, Object> param) {
		ModelAndView mav = new ModelAndView("jsonView");
		String fromLang = (String) param.get("fromLang");
	    String korStr = (String) param.get("text");
		log.debug("translate language: " + fromLang + ", " + korStr);
		UtilsForPPGO ufp = new UtilsForPPGO();
		
		String translatedStr = ufp.getTranslation(korStr, clientId, clientPwd, fromLang);
		mav.addObject("translatedStr", translatedStr);
		return mav;
	}
	

}
