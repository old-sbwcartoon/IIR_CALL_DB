/**
 * 
 */

$(document).ready(function(){
	
	function validatinCheck(){
		var userName = $('#loginId').val()
		var userPwd = $('#loginPassword').val()
		if (userName === '' || userPwd === '') {
			alert("아이디와 비밀번호를 모두 입력하세요!");
			return false;
		} 
		else {
			return true;
		}
	}
	
	//로그인
	$(".btnLogin").click(function(){
		var userName = $('#loginId').val()
		var userPwd = $('#loginPassword').val()
		if(!validatinCheck()){
			return;
		}else{
			//id가 있는지 우선 확인
			var action = "checkLogin.do";
			var param  = "&id="+userName+"&password="+userPwd;
			$.ajax({
				type: "POST", 
				url: action, 
				data: param,
				cache: false,
				dataType: "json",
				success: function(isLoginOk){
					if (isLoginOk == true) {
						//id와 비밀번호가 일치하면 chatbotMain.do를 실행
						$("#formLogin").submit();
					} else {
						alert("아이디 혹은 비밀번호를 확인해 주세요.");
					}
				}
			});
		}
	});
	
	/******************** 회원 가입 ********************/
	//아이디체크
	$(".btnChkId").click(function(){
		
		var userName = $('#signupId').val()
		var userPwd = $('#signupPassword').val()
		if(!userName){
			return;
		}else{
			var action = "checkId.do";
			var param  = "&id="+userName;
			$.ajax({
				type: "POST", 
				url: action, 
				data: param,
				cache: false,
				dataType: "json",
				success: function(isNewId){
					if (isNewId == true) {
						btnShow("btnSignup");
					} else {
						//안내문 추가
						var chkIdInfo = '<div id="chkIdInfo" style="font-size:13px; margin:13px; padding:10px; position:absolute; right:40px;">같은 아이디가 존재합니다</div>';
						$("#tab-body-signup .form-group").eq(0).prepend(chkIdInfo);
					}
				}
			});
		}
	});
	
	//회원가입
	$(".btnSignup").click(function(){
		
		var userName = $('#signupId').val()
		var userPwd = $('#signupPassword').val()
		if(!userName && !userPwd){
			alert("아이디와 암호를 모두 입력해 주세요!");
			return;
		}else{
			var action = "signup.do";
			var param  = "&id="+userName+"&password="+userPwd;
			$.ajax({
				type: "POST", 
				url: action, 
				data: param,
				cache: false,
				dataType: "json",
				success: function(result){
					if (result == true) {
						alert("회원 가입되었습니다.");
						resetSignup();
						
						switchTab('login');
					} else {
						alert("인터넷 연결 상태를 확인해 주십시오.");
					}
				}
			});
		}
	})


	//수정시 다시 체크 버튼 보이기
	$("#signupId").on("click keydown", function() {
		//안내문 존재시 삭제
		$('#chkIdInfo').remove();
		
		btnShow("btnChkId");
	});
	
	$("#signupId").on({
		
		"keydown": function(e) {
			$('#chkIdInfo').remove();
			btnShow("btnChkId");
		},
		"mousedown": function(e) {
			if (e.which == 1 || e.which == 2) {
				$('#chkIdInfo').remove();
				btnShow("btnChkId");
			}
		}
		
	});
});

function resetSignup() {
	btnShow("btnChkId");
	$('#signupId').val('');
	$('#signupPassword').val('');
	$('#chkIdInfo').remove();
}

function btnShow(btn) {
	var otherBtn = '';
	if (btn == 'btnChkId') {
		otherBtn = 'btnSignup';
	} else if (btn == 'btnSignup') {
		otherBtn = 'btnChkId';
	}
	$("."+btn).css("display", "block");
	$("."+otherBtn).css("display", "none");
}


function switchTab(index) { 
	$("[id^=tab-head-").removeClass("active"); 
	$("#tab-head-"+index).addClass("active"); 
	$("[id^=tab-body-").hide(); 
	$("#tab-body-"+index).show(); 
	
	resetSignup();
}