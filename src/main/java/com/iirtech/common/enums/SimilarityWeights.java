/**
 * 
 */
package com.iirtech.common.enums;

/**
 * @Package   : com.iir.chatbot
 * @FileName  : SimilarityWeights.java
 * @작성일       : 2017. 10. 26. 
 * @작성자       : choikino
 * @explain : 
 */

public final class SimilarityWeights {

	//가중치 : N*(NOUN), V*(VERB), J*(POSTPOSITION), M*(ADVERB), E*(EOW)
	int mode;
	public double NOUN;
	public double VERB;
	public double POSTPOSITION;
	public double ADVERB;
	public double EOW;
	
	public SimilarityWeights(int mode) {
		this.mode = mode;
		switch (mode) {
		case ClassificationConstants.FOCUS_ACTION:
			this.NOUN = 0.8;
			this.VERB = 5.0;
			this.POSTPOSITION = 0.1;
			this.ADVERB = 0.5;
			this.EOW = 0.1;
			break;
		case ClassificationConstants.FOCUS_NOUN:
			this.NOUN = 5.0;
			this.VERB = 1.0;
			this.POSTPOSITION = 0.1;
			this.ADVERB = 0.5;
			this.EOW = 0.1;
			break;
		default:
			break;
		}
		
	}
	
	
	
}
