/**
 * 
 */
package com.iirtech.chatbot;


/**
 * @Package   : com.iir.chatbot
 * @FileName  : TestMain.java
 * @작성일       : 2017. 10. 26. 
 * @작성자       : choikino
 * @explain : 
 */

public class Test {

	/**
	 * @Method   : main
	 * @작성일     : 2017. 10. 26. 
	 * @작성자     : choikino
	 * @explain :
	 * @param :
	 * @return :
	 * @throws Exception 
	 */

	public static void main(String[] args) throws Exception {
		
		System.out.println("Hello this is test page!");
		//==================================[문장분류하기]===================================
		
		
		

		//==================================[번역문구하기]===================================
		//papago 사용하고 실제 플젝에 넣을때는 system.properties에 넣어야한다.
//		UtilsForPPGO ufp = new UtilsForPPGO();
//		
//		String clientId = "v_norw0FYk6gNwbDHt7Q";
//		String clientPwd = "CxWLhAMS5C";
//		String fromLang = "KOR";
//		
//		String korStr = "우리집";
//		
//		String engStr = ufp.getTranslation(korStr, clientId, clientPwd, fromLang);
//		System.out.println(">>>>>>>>papago result: " + engStr);
		
		
		//==================================[실제 사용할 어휘 리스트 만들어 파일에 쓰기]===================================
//		String fromPath = "/Users/rnder_004/Kino/iir/chatbot/800_refer/900_020 materials/negative_positive_dictionary/korNegDic.txt";
//		String toPath = "/Users/rnder_004/Kino/iir/chatbot/800_refer/900_020 materials/negative_positive_dictionary/filterdKorNegDic.txt";
////		String fromPath = "/Users/rnder_004/Kino/iir/chatbot/800_refer/900_020 materials/negative_positive_dictionary/korPosDic.txt";
////		String toPath = "/Users/rnder_004/Kino/iir/chatbot/800_refer/900_020 materials/negative_positive_dictionary/filterdKorPosDic.txt";
//		
//		ufp.mkSentimentWords(fromPath, toPath);
		
		
//		//==================================[유사도구하기]===================================
//		//제주도에서는 무엇이 가장 맛있었어요? 질문은 무엇 언제 어디서 
//		//
//		//동작 함: 음식이 맛있었어요. 음식이요. 음식이 가장 맛있었어요.
//		//동작 안함: 아무것도 안 먹었어요. 맛없었어요. (부정 + 동작)
//		//거부: 모르겠어요. 싫어요. 아니요.
//		//긍정피드백: 음식이 좋았어요. 음식이 최고였어요. 음식이 제일이었어요.
//		//부정피드백: 음식이 좋지 않았어요. 음식이 안 좋았어요. 음식이 나빴어요. 음식이 최악이었어요.
//		//반문: 왜요?
//		//시스템질문: 
//		//무응답: 
//		//문장형태 아님: 동사가 없는 경우. 
//		
//		//예/아니오 싫어요/좋아요 응답문은 별도 처리해야함
//		
//		//문장유사도가 일정 수준 이하일 경우 다시 말하도록 유도 
//		//문장 유사도가 있어야 시스템 명령어도 인식이 가능하지 ("너 누구야? 너 이름이 뭐야? 이건 무슨 뜻이야? 등등 ")
//		UtilsForGGMA ufg = new UtilsForGGMA();
//
//		System.out.println("Test process start!");
//		//문장유사도에 대해서 테스트 진행함
//		String expectedMsg = "미스터 피자에서 밥을 먹었습니다.";
//		
//		//input Msg 입력받기
//		String[] inputMsgs = {
//			"집에 또 갔습니다. 그래서 또 밥을 먹습니다."
//			,"배가 고픕니다."
//			,"저는 학교에서 밥 먹습니다."
//			,"어제 학교에 가서 요리를 먹었습니다."
//			,"친구랑 게임을 했습니다."
//			,"저는 선생님입니다."
//			,"집에서 갈치를 먹습니다."
//			,"학교에 갔습니다. 그리고 밥을 먹었습니다."
//			,"우리집에 왜 왔습니까?"
//			,"선물을 주세요."
//			,"주말에는 회사에 가지 않고 집에서 쉽니다."
//			,"제주도에서 맛있는 음식을 먹었어요."
//			,"학교에서 진행하는 프로젝트는 잘 진행되고 있어요."
//			,"제주도에서 친구랑 회를 먹었어요."
//		};
//		
//		for (int i = 0; i < inputMsgs.length; i++) {
//			String inputMsg = inputMsgs[i];
//			//유사도 계산하여 분류결과 도출!
//			int classifyResult = ufg.doClassification(expectedMsg, inputMsg);
//			
//			switch (classifyResult) {
//			case ClassificationConstants.ERROR:
//				//오류 케이스 
//				System.out.println(" \t[ERROR] " + inputMsg);
//				break;
//			case ClassificationConstants.NORMAL:
//				//정상 케이스
//				System.out.println(" \t[NORMAL] " + inputMsg);
//				break;
//			default:
//				//에러
//				System.out.println("SYSTEM ERROR");
//				break;
//			}
//		}
//		System.out.println("==========" + expectedMsg + "==========");
//		
//		/*
//		
//		[[test case result]]
//		>>> 동사가 포인트인 경우 noun = 0.8 verb = 5.0 postposition = 0.1 adverb = 0.5 eow = 0.1 threshold = 5.0
//			5.8  	[NORMAL] 집에 또 갔습니다. 그래서 또 밥을 먹습니다.
//			0.0  	[ERROR] 배가 고픕니다.
//			6.479 	[NORMAL] 저는 학교에서 밥 먹습니다.
//			5.753 	[NORMAL] 어제 학교에 가서 요리를 먹었습니다.
//			3.333 	[ERROR] 친구랑 게임을 했습니다.
//			0.0  	[ERROR] 저는 선생님입니다.
//			5.577 	[NORMAL] 집에서 갈치를 먹습니다.
//			5.8  	[NORMAL] 학교에 갔습니다. 그리고 밥을 먹었습니다.
//			0.4  	[ERROR] 우리집에 왜 왔습니까?
//			3.333 	[ERROR] 선물을 주세요.
//			4.333 	[ERROR] 주말에는 회사에 가지 않고 집에서 쉽니다.
//			5.506 	[NORMAL] 제주도에서 맛있는 음식을 먹었어요.
//			4.133 	[ERROR] 학교에서 진행하는 프로젝트는 잘 진행되고 있어요.
//			0.0  	[ERROR] 이제
//		>>> 명사가 포인트인 경우	noun = 5.0 verb = 1.0 postposition = 0.1 adverb = 0.5 eow = 0.1 threshold = 5.0
//			5.949 	[NORMAL] 집에 또 갔습니다. 그래서 또 밥을 먹습니다.
//			0.666 	[ERROR] 배가 고픕니다.
//			10.199 	[NORMAL] 저는 학교에서 밥 먹습니다.
//			5.708 	[NORMAL] 어제 학교에 가서 요리를 먹었습니다.
//			0.888 	[ERROR] 친구랑 게임을 했습니다.
//			0.625 	[ERROR] 저는 선생님입니다.
//			4.561 	[ERROR] 집에서 갈치를 먹습니다.
//			6.0 		[NORMAL] 학교에 갔습니다. 그리고 밥을 먹었습니다.
//			3.055 	[ERROR] 우리집에 왜 왔습니까?
//			0.472 	[ERROR] 선물을 주세요.
//			6.95 	[NORMAL] 주말에는 회사에 가지 않고 집에서 쉽니다.
//			3.866 	[ERROR] 제주도에서 맛있는 음식을 먹었어요.
//			5.472 	[NORMAL] 학교에서 진행하는 프로젝트는 잘 진행되고 있어요.
//			3.866 	[ERROR] 제주도에서 친구랑 회를 먹었어요.
//		*/
	}

}
