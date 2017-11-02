/**
 * 
 */
package com.iirtech.chatbot.dto;

/**
 * @Package   : com.iir.chatbot
 * @FileName  : CompareDomain.java
 * @작성일       : 2017. 10. 26. 
 * @작성자       : choikino
 * @explain : 
 */

public class CompareDomain {

	private int orderNum;
	private String expectedMsg;
	private String inputMsg;
	private String representativeMorph;
	
	private double similarityScore;
	
	/**
	 * @return the orderNum
	 */
	public int getOrderNum() {
		return orderNum;
	}
	/**
	 * @param orderNum the orderNum to set
	 */
	public void setOrderNum(int orderNum) {
		this.orderNum = orderNum;
	}
	/**
	 * @return the expectedMsg
	 */
	public String getExpectedMsg() {
		return expectedMsg;
	}
	/**
	 * @param expectedMsg the expectedMsg to set
	 */
	public void setExpectedMsg(String expectedMsg) {
		this.expectedMsg = expectedMsg;
	}
	/**
	 * @return the inputMsg
	 */
	public String getInputMsg() {
		return inputMsg;
	}
	/**
	 * @param inputMsg the inputMsg to set
	 */
	public void setInputMsg(String inputMsg) {
		this.inputMsg = inputMsg;
	}
	/**
	 * @return the representativeMorph
	 */
	public String getRepresentativeMorph() {
		return representativeMorph;
	}
	/**
	 * @param representativeMorph the representativeMorph to set
	 */
	public void setRepresentativeMorph(String representativeMorph) {
		this.representativeMorph = representativeMorph;
	}
	/**
	 * @return the similarityScore
	 */
	public double getSimilarityScore() {
		return similarityScore;
	}
	/**
	 * @param similarityScore the similarityScore to set
	 */
	public void setSimilarityScore(double similarityScore) {
		this.similarityScore = similarityScore;
	}
	
}
