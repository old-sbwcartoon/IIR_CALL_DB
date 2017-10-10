package com.iirtech.chatbot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Map.Entry;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Test {
	
	public static void main(String[] args) {

//		Map<String, Object> oprtStrParsStrSet = new HashMap<String, Object>();
//		oprtStrParsStrSet.put("SPARTA", "SSPPAARRTTAA!!!");
//		String candidateMessage = "This is SPARTA!!";
//		Test test = new Test();
//		System.out.println(test.applyParserString(candidateMessage, oprtStrParsStrSet));
		
//		List<String> candidateNextMessages = new ArrayList<String>();
//		candidateNextMessages.add("Hello");
//		candidateNextMessages.add("Hi");
//		candidateNextMessages.add("How are you?");
//		Random rd = new Random();
//		System.out.println(candidateNextMessages.size());
//		int randomIdx = rd.nextInt((candidateNextMessages.size()-1)+1);
//		System.out.println(candidateNextMessages.get(randomIdx));
		String message = "{sdfsdf}sdfsdf{4534535}sdfsdf{한글한글}입니다{";
		List<String> operations = new ArrayList<String>();
		String regex = "\\{(.*?)\\}";
		Pattern pattern = Pattern.compile(regex);
		Matcher matcher = pattern.matcher(message);
		while (matcher.find()) {
			operations.add(matcher.group());
		}
		for (int i = 0; i < operations.size(); i++) {
			System.out.println(operations.get(i));
		}
		
	}
	private String applyParserString(String candidateMessage, Map<String, Object> oprtStrParsStrSet) {
		String result = "";
		for(Entry<String, Object> me : oprtStrParsStrSet.entrySet()) {
			String targetStr = me.getKey();
			String parsingStr = (String) me.getValue();
			result = candidateMessage.replace(targetStr, parsingStr);
		}
		return result;
	}
}
