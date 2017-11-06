package com.iirtech.common.enums;

import java.util.HashMap;
import java.util.Map;

//대화 단계절 상태값 enum, 각 enum들의 계층도 고려하여 작성 
public enum DialogStatus {

	SYSTEM_ON("0000")
//	,START_DIALOG("S000")
	,START_CONVERSATION("S000")
	,APPROACH_TOPIC("S010")
	,START_TOPIC("S020")
	,ONGOING_TOPIC("S030")
	,PRACTICE("S040")
	,PRISE("S050")
	,REMIND("S060")
	,CLOSE("S070")
	,SYSTEM_OFF("9999")
	
	,SUB_MEAL("T000")
//	,SUB_VEHICLE_BUS("T010")
	,SUB_VEHICLE("T010")
//	,SUB_VEHICLE_TAXI("T020")
	,SUB_MOVE("T030")
//	,SUB_INN("T040")
//	,SUB_GETLUGGAGE("T050")
//	,SUB_CARRENTAL("T060")
	,SUB_SHOPPING("T070")
//	,SUB_MEETFRIEND("T080")
//	,SUB_EXCHANGEMONEY("T090")
//	,SUB_LOSEMYWAY("T100")
	
	
	,ENTER_ERROR_STATUS("E000") //오류로 진입
	,WAIT_FOR_INPUT("E001") //다음 입력이 올때까지 대기
	,FORCED_LEARNING("E002") //강제로 혼내고 다시 입력하게 함 2회반복되면 다음 상태로 그냥 넘어감
	,EXIT_ERROR_STATUS("E999") //오류에서 나옴 - 다시 입력문 체크대상이 됨
	;
	private final String statusCd;
	
	//enum에 value 값 부여하고 가져올 수 있게 하는 메소드 
	DialogStatus(String statusCd){
		this.statusCd = statusCd;
	}
	public String getStatusCd() {
		return statusCd;
	}
	
	/**
	 * 문자열과 동일한 Enum name을 가져온다
	 * @param String statusName
	 * @return DialogStatus statusName
	 */
	public static DialogStatus getStatusName(String statusName) {
		DialogStatus status = null;
		
		if      (statusName.toUpperCase().equals("SYSTEM_ON")) 					{ status = SYSTEM_ON; }
		else if (statusName.toUpperCase().equals("START_CONVERSATION"))			{ status = START_CONVERSATION; }
		else if (statusName.toUpperCase().equals("APPROACH_TOPIC"))				{ status = APPROACH_TOPIC; }
		else if (statusName.toUpperCase().equals("START_TOPIC"))					{ status = START_TOPIC; }
		else if (statusName.toUpperCase().equals("ONGOING_TOPIC"))				{ status = ONGOING_TOPIC; }
		else if (statusName.toUpperCase().equals("PRACTICE"))						{ status = PRACTICE; }
		else if (statusName.toUpperCase().equals("PRISE"))						{ status = PRISE; }
		else if (statusName.toUpperCase().equals("REMIND"))						{ status = REMIND; }
		else if (statusName.toUpperCase().equals("SYSTEM_OFF"))					{ status = SYSTEM_OFF; }
		
		else if (statusName.toUpperCase().equals("SUB_MEAL"))						{ status = SUB_MEAL; }
		else if (statusName.toUpperCase().equals("SUB_VEHICLE"))					{ status = SUB_VEHICLE; }
//		else if (statusName.toUpperCase().equals("SUB_VEHICLE_TAXI"))				{ status = SUB_VEHICLE_TAXI; }
		else if (statusName.toUpperCase().equals("SUB_MOVE"))						{ status = SUB_MOVE; }
//		else if (statusName.toUpperCase().equals("SUB_INN"))						{ status = SUB_INN; }
//		else if (statusName.toUpperCase().equals("SUB_GETLUGGAGE"))				{ status = SUB_GETLUGGAGE; }
//		else if (statusName.toUpperCase().equals("SUB_CARRENTAL"))				{ status = SUB_CARRENTAL; }
		else if (statusName.toUpperCase().equals("SUB_SHOPPING"))					{ status = SUB_SHOPPING; }
//		else if (statusName.toUpperCase().equals("SUB_MEETFRIEND"))				{ status = SUB_MEETFRIEND; }
//		else if (statusName.toUpperCase().equals("SUB_EXCHANGEMONEY"))			{ status = SUB_EXCHANGEMONEY; }
//		else if (statusName.toUpperCase().equals("SUB_LOSEMYWAY"))				{ status = SUB_LOSEMYWAY; }

		else if (statusName.toUpperCase().equals("ENTER_ERROR_STATUS"))			{ status = ENTER_ERROR_STATUS; }
		else if (statusName.toUpperCase().equals("WAIT_FOR_INPUT"))				{ status = WAIT_FOR_INPUT; }
		else if (statusName.toUpperCase().equals("FORCED_LEARNING"))				{ status = FORCED_LEARNING; }
		else if (statusName.toUpperCase().equals("EXIT_ERROR_STATUS"))			{ status = EXIT_ERROR_STATUS; }

		return status;
	}
	//역으로 enum의 value를 가지고 enum name을 찾기위한 메소드 
    private static final Map<String, DialogStatus> lookup = new HashMap<String, DialogStatus>();
    
    static {
        for (DialogStatus d : DialogStatus.values()) {
            lookup.put(d.getStatusCd(), d);
        }
    }
    public static DialogStatus get(String statusCd) {
        return lookup.get(statusCd);
    }
	
}
