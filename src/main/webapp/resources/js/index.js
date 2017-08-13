/**
 * 
 */

$(document).ready(function(){
	
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
	
	
	$(".btnLogin").click(function(){
		var userName = $('.userName').val()
		var userPwd = $('.userPassword').val()
		if(!validatinCheck()){
			return;
		}else{
			$(".formLogin").submit();
		}
	})
	
	
	
})
