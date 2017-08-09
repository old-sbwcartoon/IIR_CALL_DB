package com.iirtech.chatbot.controller;

import java.util.Map;

import org.apache.log4j.Logger;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

/**
 * Handles requests for the application home page.
 */
@Controller
public class ChatbotController {
	
	Logger log = Logger.getLogger(this.getClass());
	
	/**
	 * Simply selects the home view to render by returning its name.
	 */
	@RequestMapping(value = "chatbotMain.do")
	public ModelAndView chatbotMain(Map<String,Object> param) {
		ModelAndView mv = new ModelAndView("chatbotMain");
		log.debug("logger test!!");
		return mv;
	}
	
}
