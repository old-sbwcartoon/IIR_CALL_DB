<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
	<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
	<title>CALL SYSTEM</title>
	<link href="/resources/css/index.css" rel="stylesheet" type="text/css">
    <script src="/resources/js/jquery-1.10.2.min.js"></script>
    <script type="text/javascript" charset="utf-8" src="/resources/js/index.js"></script>
</head>
<body>
<div class="container" align="center">
    <div class="row" style="width:300px;" align="center">
        <div class="col-md-offset-5 col-md-3" align="center">
            <div align="center"  class="form-login">
	            <h4>대화형 한국어 학습 시스템</h4>
	            <form method="post" class="formLogin" action="chatbotMain.do" >
		            <input type="text" name="id" class="userName" style="width:200px;" placeholder="아아디(Id)" /><br>
		            <input type="password" name="password" class="userPassword" style="width:200px;" placeholder="비밀번호(Password)" /><br><br>
		            <input type="button" class="btnLogin" value="입장" />
	            </form>
            </div>
        </div>
    </div>
</div>
</body>
</html>