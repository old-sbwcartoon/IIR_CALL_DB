package com.iirtech.chatbot.dto;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

import com.iirtech.common.enums.DialogStatus;

public class MessageInfo {

	private String statusCd;
	private String filePath;
	private String[] messages;
	private int statusSize;
	private String nextStatusCd;
	
	public MessageInfo(String statusCd) {
		
		setStatusCd(statusCd);
		setFilePath(statusCd);
		setMessages(getFilePath());
		setStatusSize(getMessages().length-1); //마지막 줄에는 next statusCd 입력됐다고 가정함. 따라서 대화 시퀀스 개수는 length-1
		setNextStatusCd(statusCd);
		
	}
	
	public String getStatusCd() {
		return statusCd;
	}
	private void setStatusCd(String statusCd) {
		this.statusCd = statusCd;
	}
	public String getFilePath() {
		return filePath;
	}

	private void setFilePath(String statusCd) {
		String fileName = DialogStatus.get(statusCd).toString()+".txt";
		String path = "/Users/rnder_007/git/IIR_CALL/src/main/webapp/resources/file/script/bot/" + fileName;
		this.filePath = path;
	}
	public String[] getMessages() {
		return messages;
	}
	private void setMessages(String filePath) {
		
		BufferedReader br = null;
		//message 저장할 배열
		ArrayList<String> messagesArr = new ArrayList<String>();
		try {
			br = new BufferedReader(new FileReader(filePath));

			String str = "";
			while ((str=br.readLine()) != null) {
				messagesArr.add(str);
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try { br.close();} catch (IOException e) {e.printStackTrace();}
		}
		this.messages = messagesArr.toArray(new String[messagesArr.size()]);
		
	}
	public int getStatusSize() {
		return statusSize;
	}
	private void setStatusSize(int statusSize) {
		this.statusSize = statusSize;
	}
	public String getNextStatusCd() {
		return nextStatusCd;
	}
	public void setNextStatusCd(String nextStatusCd) {
		//마지막 줄에는 next statusCd 입력됐다고 가정함
		this.nextStatusCd = getMessages()[getMessages().length-1];
	}

	
	/************************************************************ util ************************************************************/
	
	/*
	 * index로 statusCd 속 메시지를 찾는다
	 * @param	statusCd 속 row index
	 * @return	찾았을때: String[]: 토막글 하나일 경우 String.length=1, 토막글 여러개일 경우 "|"로 분리된 String 어레이
	 * 			못찾았을 때: null
	 */
	public String getMessagesByIdx(int idx) {
		
		//index는 0부터 시작, statusSize는 1부터 시작이므로 statusSize-1 == idx
		//index가 해당 status의 최대 index보다 크다면
//		String[] sepOneLine = null;
		String oneLine = null;
		if (idx > getStatusSize()-1) {
			// sepOneLine = null;
		} else {
			oneLine = getMessages()[idx];
			// ("|") 분리는 앞단에서 ajax > doInput()
//			if (oneLine.contains("|")) {
//				sepOneLine = oneLine.split("|");
//			} else {
//				//하나일 경우에도 배열에 할당
//				sepOneLine = new String[1];
//				sepOneLine[0] = oneLine;
//			}
		}
		return oneLine;
	}

	
}
