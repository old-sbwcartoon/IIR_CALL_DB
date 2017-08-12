package com.iirtech.common.enums;

//대화 단계절 상태값 enum, 각 enum들의 계층도 고려하여 작성 
public enum DialogStatus {

	START_DIALOG("S000")
	,GREETING("S010")
	,APPROACH_TOPIC("S011")
	,START_TOPIC("S020")
	,LEAD_TOPIC("S021")
	,SHOW_EXPRESSION("S022")
	,TRAIN_EXPRESSION("S023")
	,FEEDBACK("S024")
	,END_TOPIC("S025")
	,REMIND("S030")
	,END_DIALOG("S001")
	
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
	
	DialogStatus(String code){
		this.statusCd = code;
	}
	
	public String getStatusCd() {
		return statusCd;
	}
	
}
