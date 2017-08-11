package com.iirtech.common.enums;

//대화 단계절 상태값 enum, 각 enum들의 계층도 고려하여 작성 
public enum DialogStatus {

	START_DIALOG("S000")
	,GREETING("S010")
	,APPROACH_TOPIC("S020")
	,START_TOPIC("S021")
	,LEAD_TOPIC("S022")
	,SHOW_EXPRESSION("S023")
	,TRAIN_EXPRESSION("S024")
	,CONVRT_TOPIC("S025")
	,END_TOPIC("S026")
	,FEEDBACK("S030")
	,END_DIALOG("S001")
	;
	
	private final String statusCd;
	
	DialogStatus(String code){
		this.statusCd = code;
	}
	
	public String getStatusCd() {
		return statusCd;
	}
	
}
