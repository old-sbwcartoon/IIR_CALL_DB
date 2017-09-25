var idSeq = 0;

$(document).ready(function() {
	resetChat();
	
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


function btnEvent() {
	$('#btnInput').on('click',function(e) {
		insertUser($('#userInput').val(), $('#imgSrc').val()); //user
		doInput($('#statusCd').val(), $('#messageIdx').val()); //bot
		$('#userInput').val('');
		//스크롤바 focusing
		$("html, body").animate({scrollTop:'+=400'},'slow');
		$(".dialog-ul").animate({scrollTop:'+=400'},'slow');
		
		$('#exStatusCd').val($('#statusCd').val());
	});
	
	$('#userInput').keypress(function(e) {
		if ( e.which == 13 ) {
			event.preventDefault();
			insertUser($('#userInput').val(), $('#imgSrc').val());
			doInput($('#statusCd').val(), $('#messageIdx').val());
			$('#userInput').val('');
			//스크롤바 focusing
			$("html, body").animate({scrollTop:'+=400'},'slow');
			$(".dialog-ul").animate({scrollTop:'+=400'},'slow');
			
			$('#exStatusCd').val($('#statusCd').val());
		}
	});
}


function doInput(statusCd, messageIdx){
	
	var msg = {
			userText					: $('#userInput').val()
			, statusCd				: statusCd
			, messageIdx				: messageIdx
			, conditionInfoMap		: $('#conditionInfos').val()
	}
	
	socketHandler(JSON.stringify(msg));
}


function socketHandler(clientMessage) {

	//var sock = new WebSocket("ws://106.255.230.162:1148/sockethandler.do");
	var sock = new WebSocket("ws://localhost:7080/sockethandler.do");
	/* server 연결시 바로 */
    sock.onopen = function() {
		/* server 연결시 바로 message 보내기 */
		sock.send(clientMessage);
	};

	/* message 받아옴 */
	sock.onmessage = function(serverMessage) {
		var data = JSON.parse(serverMessage.data);
		$('#statusCd').val(data.statusCd);
		$('#messageIdx').val(data.messageIdx);
		$('#conditionInfos').val(data.conditionInfoMap);
		//script path hidden 기록
		$('#scriptPath').val(data.scriptFilePath);
		insertBot(data.message, data.imgSrc, data.messageIdx, data.statusCd);
		
		//시각화 부분 
		$('#dialogShowBoxText').remove();		
		$('#dialogShowBox').append(data.dialogLogStr);
	};

	/* server 연결 단절 */
	sock.onclose = function(event) {
		isSocketClosed = true;
		sock.close();
	};
}

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


function insertBot(text, imgfilepath, messageIdx, statusCd){
    var control = "";
    var date = formatAMPM(new Date());
    var seq = idSeq;
	var messageIdx = $('#messageIdx').val();
	
    		//sleep(text.length * 100); //사용자 입력과 동시에 나오지 않도록 잠시 정지. 글자 수에 따라 정지 시간 길어짐. 버벅댐.
        control = '<li>' +
                        '<div class="msj macro">' +
                        '<div class="avatar"><img class="img-circle" style="width:100%;" src="'+ imgfilepath + bot.avatar +'" /></div>' +
                            '<div class="text text-l">' +
                                '<p>'+ text +'<button onclick="activeFixBox('+seq+')" class="btnFix">수정</button></p>' +
                                '<div class="fixBox" style="display:none">' + 
                                '<textarea class="fixText" rows="3" cols="30"></textarea><br>' +
                                '<button onclick="doFixText('+seq+',\''+statusCd+'\',\''+messageIdx+'\')" class="btnFixInput">입력</button>'+
                                '<button onclick="cancleFixText('+seq+')" class="cancleFixInput">취소</button>' +
                                '</div>' +
                                '<p><small>'+date+'</small></p>' +
                            '</div>' +
                        '</div>' +
                    '</li>';
        
    setTimeout(
        function(){                        
            $(".dialog-ul").append(control);
        }
    );
    
    idSeq++;
}

function insertUser(text, imgfilepath){
    var control = "";
    var date = formatAMPM(new Date());
    
        control = '<li>' +
                        '<div class="msj-rta macro">' +
                            '<div class="text text-r" style="text-align: left;">' +
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
    //$(".dialog-ul").empty();
    $("#speecher").val('');
    $("#message").val('');
    //$("#imgSrc").val('');
    idSeq = 0;
}


function activeFixBox(seq){
	myFunction(seq);
	$('.fixText').eq(seq).focus();
}

function doFixText(seq, statusCd, messageIdx, scriptPath){
	var fixedText = $('.fixText').eq(seq).val();
	var loginTime = $('#loginTime').val();
	if(fixedText == null || fixedText == ""){
		alert("수정할 문장을 입력하세요!");
		return;
	}else{
		$.ajax({
		   url: 'createNewScriptFile.do'
		   ,async: false
		   ,type: 'POST'
		   ,data: {
			   fixedText : fixedText
		     , messageIdx : messageIdx
		     , statusCd : statusCd
		     , loginTime : loginTime
		   }
		   ,error: function() {
		      $('#info').html('<p>An error has occurred</p>');
		   }
		   ,dataType: 'text'
		   ,success: function(data) {
			   var jsonObj = JSON.parse(data)
			   alert(jsonObj.result);
			   
			   //시각화 부분 
			   $('#dialogShowBoxText').remove();
			   $('#dialogShowBox').append(jsonObj.dialogLogStr);
		   	}
		});
	}
	myFunction(seq);
}

function cancleFixText(seq){
	myFunction(seq);
}


function myFunction(seq) {
	$(".fixBox").eq(seq).toggle();
}

//***************************** util *****************************//
function sleep(ms){
	  ts1 = new Date().getTime() + ms;
	  do ts2 = new Date().getTime(); while (ts2<ts1);
}