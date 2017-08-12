/**
 * 
 */
function validatinCheck(){
	var userName = $('.userName').val()
	var userPwd = $('.userPassword').val()
	if (userName === '' || userPwd === '') {
		alert("아이디와 비밀번호를 모두 입력하세요!");
		return false;
	} 
	else {
		return true;
	}
}

function doLogin(){
	var userName = $('.userName').val()
	var userPwd = $('.userPassword').val()
	if(!validatinCheck()){
		$(".formLogin").submit();
	}else{
		return;
	}
}