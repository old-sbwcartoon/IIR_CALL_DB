package com.iirtech.common.enums;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @Package : com.iirtech.common.enums
 * @FileName : Operation.java
 * @작성일 : 2017. 8. 15.
 * @작성자 : choikino
 * @explain : 메시지 문장에 포함된 시스템 명령어들을 파싱하기 위한 enum
 * 
 *          시스템 명령어 : NNP,IF,VAR,IMG,STYL
 * 
 *          IF의 조건들 : oldUser/newUser , isPositive/isNegative, isAsking,
 *          hasName, isCorrect/isWrong VAR 종류들 : name, location, correctAnswer
 * 
 */
public enum Operation { //VAR 에 대한 처리를 고민중...
	
	// {NNP|Type|word} >> {NNP|location|헬싱키} >> __헬싱키(location)__
	NNP("NNP") {
		public String doParse(String operationString, String y, Object z) {
			operationString = operationString.substring(1, operationString.length()-1);
			String[] operationVals = operationString.split("\\|");
			String result = "";
			result = "__" + operationVals[2] + "(" + operationVals[1] + ")" + "__" ;
			return result;
		}
	},
	// {IF|condition|sentence} >> {IF|oldUser|또 왔네?} >> true : 또 왔네 false : ""
	IF("IF") {
		public String doParse(String operationString, String y, Object conditionInfoMap) {
			operationString = operationString.substring(1, operationString.length()-1);
			String[] operationVals = operationString.split("\\|");
			String result = "";
			//conditionInfoMap 에는 String userType, List textTypes 이 들어있음.
			Map<String,Object> tempMap = (Map<String, Object>) conditionInfoMap;
			String userType = String.valueOf(tempMap.get("userType"));
			List<String> textTypes = (List<String>) tempMap.get("textTypes");
			//userType 이든 textType이든 현재까지 누적된 조건 어느거에 하나만이라도 스크립트 상의 조건이 걸리면 true
			List<String> tempList = textTypes;
			tempList.add(userType); // 기존의 textTypes 에 userType까지 추가한 리스트 
			for (String condition : tempList) {
				if(operationVals[1].equalsIgnoreCase(condition)) {
					//조건이 참일 때
					result = operationVals[2];
				}
			}
			return result;
		}
	},
	// {IMG|imgName} >> <img src="시스템이미지파일경로//imgName" height="300" width="300"/>
	IMG("IMG") {
		public String doParse(String operationString, String imgPath, Object z) {
			operationString = operationString.substring(1, operationString.length()-1);
			String[] operationVals = operationString.split("\\|");
			String result = "";
			result = "<img src=\""+imgPath + operationVals[1] +"\" height=\"300\" width=\"300\"";
			return result;
		}
	},
	// {STYL|green|인분} >> <font color="green">인분</font>
	STYL("STYL") {
		public String doParse(String operationString, String y, Object z) {
			operationString = operationString.substring(1, operationString.length()-1);
			String[] operationVals = operationString.split("\\|");
			String result = "";
			result = "<font color=\""+operationVals[1]+"\">"+operationVals[2]+"</font>";
			return result;
		}
	};
	
	
	//enum 내부 연산관련 메소드
	public abstract String doParse(String x, String y, Object z);
	
	//enum 생성자  
	private final String operationString;
	
	Operation(String operationString){
		this.operationString = operationString;
	}
	public String getOperationString() {
		return operationString;
	}
	
	//역으로 enum의 value를 가지고 enum name을 찾기위한 메소드 
    private static final Map<String, Operation> lookup = new HashMap<String, Operation>();
    
    static {
        for (Operation o : Operation.values()) {
            lookup.put(o.getOperationString(), o);
        }
    }
    public static Operation get(String operationKey) {
        return lookup.get(operationKey);
    }

}
