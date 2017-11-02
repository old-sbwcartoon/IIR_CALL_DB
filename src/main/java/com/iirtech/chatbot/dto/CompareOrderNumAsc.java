/**
 * 
 */
package com.iirtech.chatbot.dto;

import java.util.Comparator;


/**
 * @Package   : com.iir.chatbot
 * @FileName  : CompareOrderNumAsc.java
 * @작성일       : 2017. 10. 26. 
 * @작성자       : choikino
 * @explain : 
 */

public class CompareOrderNumAsc implements Comparator<CompareDomain> {

	/* (non-Javadoc)
	 * @see java.util.Comparator#compare(java.lang.Object, java.lang.Object)
	 */
	@Override
	public int compare(CompareDomain o1, CompareDomain o2) {
		return o1.getOrderNum() < o2.getOrderNum() ? -1 : o1.getOrderNum() > o2.getOrderNum() ? 1 : 0;
	}
	
}
