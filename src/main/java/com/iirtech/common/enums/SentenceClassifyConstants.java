/**
 * 
 */
package com.iirtech.common.enums;

/**
 * @Package   : com.iir.chatbot
 * @FileName  : SentenceClassifyConstants.java
 * @작성일       : 2017. 11. 4. 
 * @작성자       : choikino
 * @explain : 
 */

public class SentenceClassifyConstants {
	//input Msg 타입 분류 결과 

	// 1,3:isDo -1,-3:isUndo -4,-6:isNegativeFeedback 8,12:isPositiveFeedback
	//동사류는 홀수(-1,1,-3,3), 형용사류는 짝수(-4,-6,8,12) 
	//곱해서 음수는 부정, 양수는 긍정

	public static final int POSITIVE_VERB = 1;
	public static final int POSITIVE_ADJECTIVE = 2;
	
	public static final int NEGATIVE_VERB_END = -1;
	public static final int NEGATIVE_ADJECTIVE_END = -2;
	public static final int NEGATIVE_ADVERB = -3;
	public static final int NEGATIVE_ADJECTIVE = -4;
	
	public static final String IS_POSITIVE_FEEDBACK = "positiveFeedback";
	public static final String IS_NEGATIVE_FEEDBACK = "negativeFeedback";
	public static final String IS_UNDO = "undo";
	public static final String IS_DO = "do";
	public static final String IS_MORE_THAN_TWO_SENTENCE = "moreThanTwoSentence";
	public static final String IS_TYPO_SENTENCE = "typoSentence";
	public static final String IS_MUMBLE_SENTENCE = "mumbleSentence";
	public static final String IS_NOT_SENTENCE = "isNotSentence";
	public static final String IS_ANSWER_NO = "no";
	public static final String IS_ANSWER_YES = "yes";
	public static final String IS_NOT_YN = "notYN";
	public static final String IS_REASK = "reask";
	public static final String IS_ASK_SYSTEM_ATTRIBUTE = "askSysAttr";
	public static final String IS_ASK_INFO = "askInfo";
	public static final String IS_REJECT = "reject";
	public static final String IS_GREETING = "greeting";

	
	public static final String IS_NORMAL_SENTENCE = "normalSentence";
	public static final String IS_ERROR_SENTENCE = "errorSentence";
	
	
}
