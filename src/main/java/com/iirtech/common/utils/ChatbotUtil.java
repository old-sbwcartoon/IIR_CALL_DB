package com.iirtech.common.utils;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.security.MessageDigest;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * @Package   : com.iirtech.common.utils
 * @FileName  : ChatbotUtil.java
 * @작성일       : 2017. 8. 13. 
 * @작성자       : choikino
 * @explain : 각종 쓸모있는 메소드들을 모아놓은 클래스
 * 			  encrpy/decrypt, parser, encoder/decoder, fileIO
 */
public class ChatbotUtil {
	
	/////////////////////////////////////
	//1. encrypt decrypt
	/////////////////////////////////////
	public String encryptPwd(String password) {
		//단방향 복호화 sha256 으로 pwd 비교시에는 암호화된 값 끼리 비교한다.
		String encrpytString = "";
        try{
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(password.getBytes("UTF-8"));
            StringBuffer hexString = new StringBuffer();
 
            for (int i = 0; i < hash.length; i++) {
                String hex = Integer.toHexString(0xff & hash[i]);
                if(hex.length() == 1) hexString.append('0');
                hexString.append(hex);
            }
            
            encrpytString = hexString.toString();
 
        } catch(Exception e){
            e.printStackTrace();
        }
        
        return encrpytString;
 
	}
	
	
	/////////////////////////////////////
	//2. parser
	/////////////////////////////////////
	public String getYYYYMMDDhhmmssTime(Long time) {
		String result = "";
		SimpleDateFormat sdf = new SimpleDateFormat("YYYYMMDDhhmmss");
		result = sdf.format(new Date(time));
		return result;
	}
	
	/////////////////////////////////////
	//3. decoder encoder
	/////////////////////////////////////
	
	
	
	/////////////////////////////////////
	//4. file read write
	/////////////////////////////////////
	public List<String> ReadFileByLine(String filePath, String fileName) {
		File targetFile = new File(filePath, fileName);
		List<String> contents = new ArrayList<String>();
		if(!targetFile.exists()) {
			//아무 처리도 하지 않고 null을 리턴함 
		}else {
			BufferedReader br = null;
			try {
				br = new BufferedReader(new FileReader(targetFile));
				String line;
				while ((line = br.readLine()) != null) {
					contents.add(line);
				}
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();	
			} finally {
				if (br != null)
					try {
						br.close();
					} catch (IOException e) {
						e.printStackTrace();
					}
			}
		}
		return contents;
	}
	
	public void WriteFile(String filePath, String fileName, List<String> contents) {
		File targetDir = new File(filePath);
		if(!targetDir.exists()) {
			targetDir.mkdirs();
		}
		
		File targetFile = new File(filePath,fileName);
		BufferedWriter bw = null;
        try {
            bw = new BufferedWriter(new FileWriter(targetFile));
            //list 형태의 자료를 루프돌면서 write
            for (String content : contents) {
	            	bw.write(content);
	            	bw.newLine();
			}
            bw.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }finally {
            if(bw != null) {
	            	try {
	            		bw.close(); 
	            	} catch (IOException e) {
	            		e.printStackTrace();
	            	}
            }
        }
	}

	
	
}
