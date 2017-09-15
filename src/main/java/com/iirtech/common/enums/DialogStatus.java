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
	
	,START_DIALOG_EXCEPTION("E000")
	,GREETING_EXCEPTION("E010")
	,APPROACH_TOPIC_EXCEPTION("E011")
	,START_TOPIC_EXCEPTION("E020")
	,LEAD_TOPIC_EXCEPTION("E021")
	,SHOW_EXPRESSION_EXCEPTION("E022")
	,TRAIN_EXPRESSION_EXCEPTION("E023")
	,FEEDBACK_EXCEPTION("E024")
	,END_TOPIC_EXCEPTION("E025")
	,REMIND_EXCEPTION("E030")
	,END_DIALOG_EXCEPTION("E001")
	;
	
	private final String statusCd;
	
	//enum에 value 값 부여하고 가져올 수 있게 하는 메소드 
	DialogStatus(String statusCd){
		this.statusCd = statusCd;
	}
	public String getStatusCd() {
		return statusCd;
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
