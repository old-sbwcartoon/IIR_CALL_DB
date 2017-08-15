$(document).ready(function() {

	$('.img-circle').attr("src", $('#imgSrc').val()+bot.avatar);
	//init SYSTEM_ON
	doInput("0000", 0);
	
	btnEvent();
	
});


/**
 * 
 */
var bot = {};
//bot.avatar = "/resources/img/bot.jpeg";
bot.avatar = "bot.jpeg";

var usr = {};
//usr.avatar = "/resources/img/usr.jpeg";
usr.avatar = "usr.jpeg";

function formatAMPM(date) {
    var hours = date.getHours();
    var minutes = date.getMinutes();
    var ampm = hours >= 12 ? 'PM' : 'AM';
    hours = hours % 12;
    hours = hours ? hours : 12;
    minutes = minutes < 10 ? '0'+minutes : minutes;
    var strTime = hours + ':' + minutes + ' ' + ampm;
    return strTime;
}            

//function insertChat(who, text, imgfilepath){
//    var control = "";
//    var date = formatAMPM(new Date());
//    
//    if (who == "bot"){
//        
//        control = '<li style="width:100%">' +
//                        '<div class="msj macro">' +
//                        '<div class="avatar"><img class="img-circle" style="width:100%;" src="'+ imgfilepath + bot.avatar +'" /></div>' +
//                            '<div class="text text-l">' +
//                                '<p>'+ text +'</p>' +
//                                '<p><small>'+date+'</small></p>' +
//                            '</div>' +
//                        '</div>' +
//                    '</li>';              
//        
//        
//    }else{
//        control = '<li style="width:100%;">' +
//                        '<div class="msj-rta macro">' +
//                            '<div class="text text-r">' +
//                                '<p>'+text+'</p>' +
//                                '<p><small>'+date+'</small></p>' +
//                            '</div>' +
//                        '<div class="avatar" style="padding:0px 0px 0px 10px !important"><img class="img-circle" style="width:100%;" src="'+ imgfilepath + usr.avatar+'" /></div>' +                                
//                  '</li>'; 
//    }
//    setTimeout(
//        function(){                        
//            $(".dialog-ul").append(control);
//        }
//    );
//    
//}

function insertBot(text, imgfilepath){
    var control = "";
    var date = formatAMPM(new Date());
    		//sleep(text.length * 100); //사용자 입력과 동시에 나오지 않도록 잠시 정지. 글자 수에 따라 정지 시간 길어짐. 버벅댐.
        control = '<li style="width:100%">' +
                        '<div class="msj macro">' +
                        '<div class="avatar"><img class="img-circle" style="width:100%;" src="'+ imgfilepath + bot.avatar +'" /></div>' +
                            '<div class="text text-l">' +
                                '<p>'+ text +'</p>' +
                                '<p><small>'+date+'</small></p>' +
                            '</div>' +
                        '</div>' +
                    '</li>';
        
        
    setTimeout(
        function(){                        
            $(".dialog-ul").append(control);
        }
    );
    
}

function insertUser(text, imgfilepath){
    var control = "";
    var date = formatAMPM(new Date());
    
        control = '<li style="width:100%;">' +
                        '<div class="msj-rta macro">' +
                            '<div class="text text-r">' +
                                '<p>'+text+'</p>' +
                                '<p><small>'+date+'</small></p>' +
                            '</div>' +
                        '<div class="avatar" style="padding:0px 0px 0px 10px !important"><img class="img-circle" style="width:100%;" src="'+ imgfilepath + usr.avatar+'" /></div>' +                                
                  '</li>';
    setTimeout(
        function(){                        
            $(".dialog-ul").append(control);
        }
    );
    
}

function resetChat(){
    $(".dialog-ul").empty();
    $("#speecher").val('');
    $("#message").val('');
    $("#imgSrc").val('');
}

$(".mytext").on("keyup", function(e){
    if (e.which == 13){
        var text = $(this).val();
        if (text !== ""){
            insertChat("bot", text);              
            $(this).val('');
        }
    }
});

function doInput(statusCd, messageIdx){
	var userText = $('.userInput').val();
	var statusCd = statusCd;
	var messageIdx = messageIdx;
	
	$.ajax({
		   url: 'messageInput.json'
		   ,async: false
		   ,type: 'POST'
		   ,data: {
		     userText : userText
		     , statusCd : statusCd
		     , messageIdx : messageIdx
		   }
		   ,error: function() {
		      $('#info').html('<p>An error has occurred</p>');
		   }
		   ,dataType: 'text'
		   ,success: function(data) {
			   //-- 채팅창 대화 쓰기
			   // read only 속성을 봇이 계속 발화해야 하는 상황이면 추가한다.
			   var jsonObj = JSON.parse(data);
			   $('#statusCd').val(jsonObj.statusCd);
			   $('#messageIdx').val(jsonObj.messageIdx);
			   
			   //message 약속된 기호로 나누기
			   var message = jsonObj.message;
//			   var messageArr = new Array();
//			   if (message.includes("|")) {
//				   messageArr = message.split("|");
//			   } else {
//				   messageArr[0] = message;
//			   }
//			   //message 갯수만큼 뿌리기
//			   for (var i= 0; i < messageArr.length; i++) {
				   insertBot(message, jsonObj.imgSrc);  
				   //너무 곧바로 대답하니 부자연스러움 
//			   }
		   	}
		});
}

function btnEvent() {
	$('#btnInput').on('click',function(e) {
		
		insertUser($('#userInput').val(), $('#imgSrc').val());
		doInput($('#statusCd').val(), $('#messageIdx').val());
	        
	});
}

//***************************** util *****************************//
function sleep(ms){
	  ts1 = new Date().getTime() + ms;
	  do ts2 = new Date().getTime(); while (ts2<ts1);
}