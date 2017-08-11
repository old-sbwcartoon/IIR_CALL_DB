/**
 * 
 */
var bot = {};
bot.avatar = "/resources/img/bot.jpeg";

var usr = {};
usr.avatar = "/resources/img/usr.jpeg";

function formatAMPM(date) {
    var hours = date.getHours();
    var minutes = date.getMinutes();
    var ampm = hours >= 12 ? 'PM' : 'AM';
    hours = hours % 12;
    hours = hours ? hours : 12; // the hour '0' should be '12'
    minutes = minutes < 10 ? '0'+minutes : minutes;
    var strTime = hours + ':' + minutes + ' ' + ampm;
    return strTime;
}            

//-- No use time. It is a javaScript effect.
function insertChat(who, text, time = 0){
    var control = "";
    var date = formatAMPM(new Date());
    
    if (who == "bot"){
        
        control = '<li style="width:100%">' +
                        '<div class="msj macro">' +
                        '<div class="avatar"><img class="img-circle" style="width:100%;" src="'+ bot.avatar +'" /></div>' +
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
                        '<div class="avatar" style="padding:0px 0px 0px 10px !important"><img class="img-circle" style="width:100%;" src="'+usr.avatar+'" /></div>' +                                
                  '</li>'; 
    }
    setTimeout(
        function(){                        
            $(".dialog-ul").append(control);

        }, time);
    
}

function resetChat(){
    $(".dialog-ul").empty();
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

//-- 시작하면 채팅창 클리
resetChat();

function doInput(){
	var userText = $('.user-input').val()
	$.ajax({
		   url: 'inputPreprocess.json'
		   ,async: false
		   ,type: 'POST'
		   ,data: {
		     userText : userText
		     ,statusCd : userText //임시 
		   }
		   ,error: function() {
		      $('#info').html('<p>An error has occurred</p>');
		   }
		   ,dataType: 'text'
		   ,success: function(data) {
			   //-- 채팅창 대화 쓰기
			   // read only 속성을 봇이 계속 발화해야 하는 상황이면 추가한다.
			   var jsonObj = JSON.parse(data);
			   insertChat(jsonObj.Speacker,jsonObj.Message);  
		   }
		});
}

