$(document).ready(function() {
	
	//init SYSTEM_ON
	doInput("0000");
	
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

function insertChat(who, text, imgfilepath){
    var control = "";
    var date = formatAMPM(new Date());
    
    if (who == "bot"){
        
        control = '<li style="width:100%">' +
                        '<div class="msj macro">' +
                        '<div class="avatar"><img class="img-circle" style="width:100%;" src="'+ imgfilepath + bot.avatar +'" /></div>' +
                            '<div class="text text-l">' +
                                '<p>'+ text +'</p>' +
                                '<p><small>'+date+'</small></p>' +
                            '</div>' +
                        '</div>' +
                    '</li>';              
        
        
    }else{
        control = '<li style="width:100%;">' +
                        '<div class="msj-rta macro">' +
                            '<div class="text text-r">' +
                                '<p>'+text+'</p>' +
                                '<p><small>'+date+'</small></p>' +
                            '</div>' +
                        '<div class="avatar" style="padding:0px 0px 0px 10px !important"><img class="img-circle" style="width:100%;" src="'+ imgfilepath + usr.avatar+'" /></div>' +                                
                  '</li>'; 
    }
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

function doInput(statusCd, statusCdSeq){
	var userText = $('.userInput').val();
	var statusCd = statusCd;
	var statusCdSeq = statusCdSeq;
	
	$.ajax({
		   url: 'messageInput.json'
		   ,async: false
		   ,type: 'POST'
		   ,data: {
		     userText : userText
		     , statusCd : statusCd
		     , statusCdSeq : statusCdSeq
		   }
		   ,error: function() {
		      $('#info').html('<p>An error has occurred</p>');
		   }
		   ,dataType: 'text'
		   ,success: function(data) {
			   //-- 채팅창 대화 쓰기
			   // read only 속성을 봇이 계속 발화해야 하는 상황이면 추가한다.
			   var jsonObj = JSON.parse(data);
			   $('#messageNo').val(jsonObj.messageNo);
			   //message 갯수만큼 뿌리기
			   for (var i= 0; i < jsonObj.message.length; i++) {
				   insertChat(jsonObj.speecher,jsonObj.message,jsonObj.imgSrc);  
			   }
		   	}
		});
}

function btnEvent() {
$('#btn-input').on('click', function() {
		
			doInput();

	});
}