<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<html>
<head>
	<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
	<meta name="viewport"content="width=device-width, initial-scale=1.0, maximum-scale=1.0, user-scalable=0"/>
	<title>CALL SYSTEM</title>
	<link   href="resources/css/index.css" rel="stylesheet" type="text/css">
    <script  src="resources/js/jquery-1.10.2.min.js"></script>
    <!-- <script  src="/resources/js/sockjs.min.js"></script> socket -->
    <script type="text/javascript" charset="utf-8" src="resources/js/index.js"></script>

    <!-- Animate.css -->
	<link rel="stylesheet" href="resources/css/animate.css">
	<!-- Bootstrap  -->
	<link rel="stylesheet" href="resources/css/bootstrap.css">
	<!-- Theme style  -->
	<link rel="stylesheet" href="resources/css/style.css">

	<!-- Modernizr JS -->
	<script src="resources/js/modernizr-2.6.2.min.js"></script>
	<!-- FOR IE9 below -->
	<!--[if lt IE 9]>
	<script src="js/respond.min.js"></script>
	<![endif]-->
    
</head>
<body>
<section id="gtco-contact" data-section="contact">
	<div class="container">
		<div class="row row-pb-md">
			<div class="col-md-8 col-md-offset-2 heading animate-box" data-animate-effect="fadeIn">
				<h1>대화형 한국어 학습 시스템</h1>
				<p class="sub">Computer Assistant Language Learning ( CALL )</p>
				<p class="subtle-text animate-box" data-animate-effect="fadeIn">IIRTECH</p>
			</div>
		</div>
		<div class="row">
			<div class="col-md-6 col-md-push-3 animate-box">
			
				<div class="tab">
					<a href="javascript:switchTab('login')" id="tab-head-login" class="active" style="margin-left:0;">로그인</a>
					<a href="javascript:switchTab('signup')" id="tab-head-signup"  >회원 가입</a>
				</div>
				<div class="loginbox">
					<div id="tab-body-login" style="display:block">
						<form method="post" id="formLogin" action="chatbotMain.do" >
							<div class="form-group">
								<input type="text" id="loginId" name="id" class="userName form-control" placeholder="아이디(ID)">
							</div>
							<div class="form-group">
								<input type="password" id="loginPassword" name="password" class="userPassword form-control" placeholder="비밀번호(Password)">
							</div>
							<div class="form-group">
								<center>
									<input type="button" value="입장" class="btnLogin btn btn-primary" style=>
								</center>
							</div>
						</form>
					</div>
					
					<div id="tab-body-signup" style="display:none">
						<!-- <form method="post" id="formSignup"> -->
							<div class="form-group">
								<input type="text" id="signupId" name="id" class="userName form-control" placeholder="가입 아이디(ID)">
							</div>
							<div class="form-group">
								<input type="password" id="signupPassword" name="password" class="userPassword form-control" placeholder="비밀번호(Password)">
							</div>
							<div class="form-group">
								<center>
									<input type="button" value="아이디 중복 확인" class="btnChkId btn btn-warning" style="display:block;">
									<input type="button" value="회원 가입" class="btnSignup btn btn-primary" style="display:none;">
								</center>
							</div>
						<!-- </form> -->
					</div>
				</div>
			</div>
		</div>
	</div>
</section>

	<!-- jQuery -->
	<script src="resources/js/jquery.min.js"></script>
	<!-- Waypoints -->
	<script src="resources/js/jquery.waypoints.min.js"></script>
	<!-- Stellar -->
	<script src="resources/js/jquery.stellar.min.js"></script>
	<!-- Main -->
	<script src="resources/js/main.js"></script>

	
<script type="text/javascript">
	
</script>
</body>
</html>