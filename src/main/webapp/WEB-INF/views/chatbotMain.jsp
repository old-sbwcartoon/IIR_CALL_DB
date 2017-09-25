<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ page session="false" %>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<html>
<head>
	<title>CALL SYSTEM</title>
	<link href="resources/css/chatbotMain.css?ver=1" rel="stylesheet" type="text/css">
	<meta http-equiv="Content-Type" content="text/html; charset=utf-8" />
    <script src="resources/js/jquery-1.10.2.min.js"></script>
    <script type="text/javascript" charset="utf-8" src="resources/js/chatbotMain.js?ver=3"></script>
    
    <!-- Animate.css -->
	<link rel="stylesheet" href="resources/css/animate.css">
	<!-- Bootstrap  -->
	<link rel="stylesheet" href="resources/css/bootstrap.css">
	<!-- Magnific Popup -->
	<link rel="stylesheet" href="resources/css/magnific-popup.css">
	<!-- Theme style  -->
	<link rel="stylesheet" href="resources/css/style.css">

	<!-- Modernizr JS -->
	<script src="resources/js/modernizr-2.6.2.min.js"></script>
	<!-- FOR IE9 below -->
	<!--[if lt IE 9]>
	<script src="js/respond.min.js"></script>
	<![endif]-->
	
	<script type="text/javascript">
		function resizeFt() {
			if($('.js-gtco-nav-toggle').css('display') == 'none') {
				$('#toggle').show();
			} else {
				$('#toggle').css('display', 'none');
			}
		}
	</script>
	
</head>
<body onresize="resizeFt()">

	<br>
		<center><h1>CALL</h1><center>
	
	<!-- navigator -->
	<nav class="gtco-nav" role="navigation" style="background-color: black; display: none;">
		<div class="container">
			<div class="row">
				<div class="col-xs-10 text-right menu-1 main-nav">
					<ul>
						<div class="dialog-div" id="dialogShowBox" style="float: left; overflow: auto; color: white;">
							${dialogLogStr}
						</div>
					</ul>
				</div>
			</div>
			
		</div>
	</nav>
	
		<div class="container">
			<div class="row">
				<div class="col-md-6" style="border-color: black;">
					<div class="dialog-div" style="float: left;">
						<ul class="dialog-ul" style="height: 600px; overflow:auto">
							<li>
								<div class="msj macro">
									<div class="avatar"><img alt="avatar" style="width:100%;" class="img-circle" src=""></div>
									<div class="text text-l" style="text-align: left;">
										<p>
										${initInfo}
										</p>
										<p></p>
									</div>
								</div>
							</li>
						</ul>
						<div class="macro" style="width:475; background:whitesmoke;">
							대화를 입력하세요:   
							<textarea id="userInput" rows="4" cols="50"></textarea>
							<button id="btnInput">입력</button>
						</div>
					</div>
				</div>
				
				<div id="toggle" class="col-md-6">
					<div class="dialog-div" id="dialogShowBox" style="float: left; overflow: auto;">
						${dialogLogStr}
					</div>
				</div>
			</div>
		</div>
	
		<input type="hidden" id="imgSrc" value='${imgSrc}'>
		<input type="hidden" id="statusCd" value='${statusCd}'>
		<input type="hidden" id="exStatusCd" value='${statusCd}'>
		<input type="hidden" id="messageIdx" value='${messageIdx}'>
		<input type="hidden" id="conditionInfos" value='${conditionInfoMap}'>
		<input type="hidden" id="loginTime" value='${loginTime}'>
	
	<!-- jQuery -->
	<script src="resources/js/jquery.min.js"></script>
	<!-- jQuery Easing -->
	<script src="resources/js/jquery.easing.1.3.js"></script>
	<!-- Bootstrap -->
	<script src="resources/js/bootstrap.min.js"></script>
	<!-- Waypoints -->
	<script src="resources/js/jquery.waypoints.min.js"></script>
	<!-- Stellar -->
	<script src="resources/js/jquery.stellar.min.js"></script>
	<!-- Magnific Popup -->
	<script src="resources/js/jquery.magnific-popup.min.js"></script>
	<script src="resources/js/magnific-popup-options.js"></script>
	<!-- Main -->
	<script src="resources/js/nav.js"></script>
</body>
</html>
