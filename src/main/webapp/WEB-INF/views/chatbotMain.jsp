<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ page session="false" %>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<html>
<head>
	<title>CALL SYSTEM</title>
	<link href="/resources/css/chatbotMain.css" rel="stylesheet" type="text/css">
	<meta http-equiv="Content-Type" content="text/html; charset=utf-8" />
    <script src="/resources/js/jquery-1.10.2.min.js"></script>
    <script type="text/javascript" charset="utf-8" src="/resources/js/chatbotMain.js"></script>
</head>
<body>
	<div class="macro" style="background:whitesmoke">
	대화를 입력하세요:   
		<input class="userInput" type="text" style="width:300px;">
		<button id="btn-input">입력</button>
	</div>
	<div>
	    <ul class="dialog-ul" style="width: 600px; height: 700px; overflow: auto"></ul>
	</div>        
	<%-- <input type="hidden" id="speecher" value="${speecher}">
	<input type="hidden" id="message" value="${message}">
	<input type="hidden" id="imgSrc" value="${imgSrc}"> --%>
	<input type="hidden" id="statusCd" value="${statusCd}">
	<input type="hidden" id="messageIdx" value="${messageIdx}">
</body>

<!-- <script type="text/javascript">
	/* resetChat(); */
	
</script> -->
</html>
