package com.iirtech.chatbot.controller;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.springframework.http.converter.FormHttpMessageConverter;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.servlet.ModelAndView;



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
	
	//for test
	//REST API
	@ResponseBody
	@RequestMapping(value = "/KAIST")
	public String KAIST(@RequestParam String jsonStr) {
		return "hi "+ jsonStr;
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
	    // parameter 세팅
	    MultiValueMap<String, String> map = new LinkedMultiValueMap<String, String>();
	 
	    // REST API 호출
	    String result = restTemplate.postForObject("http://localhost:8090/KAIST", map, String.class);
	    log.debug("------------------ TEST 결과 ------------------");
	    log.debug(result);
	    mav.addObject("result",result);

		return mav;
	}

	

}
