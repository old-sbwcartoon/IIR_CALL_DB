<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ page session="false" %>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<html>
<head>
	<title>CALL SYSTEM</title>
	<meta http-equiv="Content-Type" content="text/html; charset=utf-8" />
	<link href="<c:url value="/resources/css/chatbotMain.css" />" rel="stylesheet" type="text/css">
    <script src="<c:url value="/resources/js/jquery-1.10.2.min.js" />"></script>
    <script type="text/javascript" charset="utf-8" src="<c:url value="/resources/js/chatbotMain.js" />"></script>
</head>
<body>
	<div class="macro" style="background:whitesmoke">
	대화를 입력하세요:   
		<input class="user-input" type="text" style="width:300px;">
		<button onclick="doInput()">입력</button>
	</div>
	<div>
	    <ul class="dialog-ul" style="width: 600px; height: 700px; overflow: auto"></ul>
	</div>        
</body>
</html>
