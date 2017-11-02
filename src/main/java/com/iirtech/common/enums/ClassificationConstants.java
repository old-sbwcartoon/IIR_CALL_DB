/**
 * 
 */
package com.iirtech.common.enums;

/**
 * @Package   : com.iir.chatbot
 * @FileName  : ClassificationConstants.java
 * @작성일       : 2017. 10. 26. 
 * @작성자       : choikino
 * @explain : 
 */

public final class ClassificationConstants {

	//input Msg 분류
	public static final int ERROR = 0;
	public static final int NORMAL = 1;
	public static final double THRESHOLD = 5.0;
	
	//유사도 중요하게 생각하는 포인트 분류
	public static final int FOCUS_ACTION = 0;
	public static final int FOCUS_FORM = 1;
	public static final int FOCUS_NOUN = 2;
	
}
