package com.iirtech.chatbot.dao;

import org.apache.ibatis.session.SqlSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

@Repository
public class ChatbotDao {

	@Autowired
	private SqlSession sqlSession;
}
