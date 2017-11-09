//var idSeq = 0;

$(document).ready(function() {
	$('#idSeq').val(0);

	// resetChat();

	// $('.img-circle').attr("src", $('#imgSrc').val()+bot.avatar);
	$('#img-bot').attr("src", $('#imgSrc').val() + bot.avatar);
	// init SYSTEM_ON
	doInput("0000", 0, 0, 0);

	btnEvent();
});

/**
 * 
 */
var bot = {};
// bot.avatar = "/resources/img/bot.jpeg";
bot.avatar = "bot.jpeg";

var usr = {};
// usr.avatar = "/resources/img/usr.jpeg";
usr.avatar = "usr.jpeg";

function btnEvent() {
	$('#btnInput').on('click', function(e) {

		var inputMsg = $('#userInput').val()

		insertUser($('#userInput').val(), $('#imgSrc').val()); // user
		doInput($('#statusCd').val(), $('#exStatusCd').val(), $('#messageIdx').val(), $('#subMessageIdx').val()); // bot
		$('#userInput').val('');
		// 스크롤바 focusing
		$("html, body").animate({
			scrollTop : '+=400'
		}, 'slow');
		$(".dialog-ul").animate({
			scrollTop : '+=400'
		}, 'slow');

		$(this).attr("disabled", true);
		$(this).addClass("btn_disabled");
	});

	$('#userInput').on({
		
		'keyup' : function(e) {
	
			var inputMsg = $(this).val();
			if (inputMsg == null || inputMsg.trim() == "") {
				$('#btnInput').attr("disabled", true);
				$('#btnInput').addClass("btn_disabled");
				if (e.which == 13) {
					event.preventDefault();
				}
				return;
			} else {
				$('#btnInput').attr("disabled", false);
				$('#btnInput').removeClass("btn_disabled");
			}
		}
	
		, 'keydown' : function(e) {
			
			var inputMsg = $(this).val();
			if (inputMsg == null || inputMsg.trim() == "") {
				$('#btnInput').attr("disabled", true);
				$('#btnInput').addClass("btn_disabled");
				if (e.which == 13) {
					event.preventDefault();
				}
				return;
			} else {
				$('#btnInput').attr("disabled", false);
				$('#btnInput').removeClass("btn_disabled");
			}
			
			if (e.which == 13) {
				event.preventDefault();
				insertUser($(this).val(), $('#imgSrc').val());
				doInput($('#statusCd').val(), $('#exStatusCd').val(), $('#messageIdx').val(), $('#subMessageIdx').val());
				$(this).val('');
				// 스크롤바 focusing
				$("html, body").animate({
					scrollTop : '+=400'
				}, 'slow');
				$(".dialog-ul").animate({
					scrollTop : '+=400'
				}, 'slow');
				
	//			$('#exStatusCd').val($('#statusCd').val());
			}
		}
		, 'cut' : function() {
			$.ajax({
				url : btnOnOff()
				, success : function() {
					
					var inputMsg = $('#userInput').val();
					if (inputMsg == null || inputMsg.trim() == "") {
						$('#btnInput').attr("disabled", true);
						$('#btnInput').addClass("btn_disabled");
						return;
					} else {
						$('#btnInput').attr("disabled", false);
						$('#btnInput').removeClass("btn_disabled");
					}
				}
			});
		}
		, 'paste' : function() {
			$.ajax({
				url : btnOnOff()
				, success : function() {
					
					var inputMsg = $('#userInput').val();
					if (inputMsg == null || inputMsg.trim() == "") {
						$('#btnInput').attr("disabled", true);
						$('#btnInput').addClass("btn_disabled");
						return;
					} else {
						$('#btnInput').attr("disabled", false);
						$('#btnInput').removeClass("btn_disabled");
					}
				}
			});
		}
	});
}

function btnOnOff() {
	//do nothing
	//dummy function
}

function doInput(statusCd, exStatusCd, messageIdx, subMessageIdx) {

	var msg = {
		userText : $('#userInput').val(),
		statusCd : statusCd,
		exStatusCd : exStatusCd,
		messageIdx : messageIdx,
		subMessageIdx : subMessageIdx,
		conditionInfoMap : $('#conditionInfos').val(),
		shortTermInfoMap : $('#shortTermInfos').val()
	}

	socketHandler(JSON.stringify(msg));
}

function socketHandler(clientMessage) {

	// var sock = new WebSocket("ws://106.255.230.162:1148/sockethandler.do");
	var sock = new WebSocket("ws://localhost:7090/sockethandler.do");
	// var sock = new WebSocket("ws://localhost:8090/sockethandler.do");
	/* server 연결시 바로 */
	sock.onopen = function() {
		/* server 연결시 바로 message 보내기 */
		sock.send(clientMessage);
	};

	/* message 받아옴 */
	sock.onmessage = function(serverMessage) {
		var data = JSON.parse(serverMessage.data);
		
		$('#statusCd').val(data.statusCd);
		$('#exStatusCd').val(data.exStatusCd);
		$('#messageIdx').val(data.messageIdx);
		$('#subMessageIdx').val(data.subMessageIdx);
		$('#conditionInfos').val(data.conditionInfoMap);
		$('#shortTermInfos').val(data.shortTermInfoMap);
		// script path hidden 기록
		$('#scriptPath').val(data.scriptFilePath);
		
		var dialogUlHtml = $('.dialog-ul').html();
		
		insertBot(data.message, data.imgSrc, data.messageIdx, data.subMessageIdx, data.statusCd, data.exStatusCd);
		

		// 시각화 부분
		$('#dialogShowBoxText').remove();
		$('#dialogShowBox-navigation').html(data.dialogLogStr);
		$('#dialogShowBox-frame').html(data.dialogLogStr);
		
	};

	/* server 연결 단절 */
	sock.onclose = function(event) {
		isSocketClosed = true;
		sock.close();
	};
}


function redirectPage(controller){
	//location.href='http://106.255.230.125:11480/' + controller;
	if(controller == 'index.do'){
		if(confirm('정말 나가겠습니까?')==true){
			location.href='http://localhost:7090/' + controller;
		}else{
			return;
		}
	}else{
		location.reload();
	}
}


function formatAMPM(date) {
	var hours = date.getHours();
	var minutes = date.getMinutes();
	var ampm = hours >= 12 ? 'PM' : 'AM';
	hours = hours % 12;
	hours = hours ? hours : 12;
	minutes = minutes < 10 ? '0' + minutes : minutes;
	var strTime = hours + ':' + minutes + ' ' + ampm;
	return strTime;
}

function insertBot(text, imgfilepath, messageIdx, subMessageIDx, statusCd, exStatusCd) {
	var control = "";
	var date = formatAMPM(new Date());
	var seq = $('#idSeq').val();
	var messageIdx = $('#messageIdx').val();
	var subMessageIdx = $('#subMessageIdx').val();

	
	var dialogUlHtml = $(".dialog-ul").html();
	// sleep(text.length * 100); //사용자 입력과 동시에 나오지 않도록 잠시 정지. 글자 수에 따라 정지 시간
	if(statusCd == "S070" && exStatusCd == "S060"){ //마지막 발화일 경우 페이지 아웃할 수 있게
		control = '<li>' + '<div class="msj macro">' +
		// '<div class="avatar"><img class="img-circle" style="width:100%;" src="'+
		// imgfilepath + bot.avatar +'" /></div>' +
		'<div class="avatar"><img style="width:46%;" src="'
				+ imgfilepath
				+ bot.avatar
				+ '" /></div>'
				+ '<div class="text text-l">'
				+ '<p>'
				+ text
				+ '<br>대화가 끝났어요.<br>다시 같은 내용으로 공부할래요?'
				+ '<button onclick="redirectPage(\'chatbotMain.do\')" class="btnFix btnSmall btnUpper align-right">네</button>'
				+ '<button onclick="redirectPage(\'index.do\')" class="btnFix btnSmall btnUpper align-right">아니오</button>'
				+ '</p>'
				+ '<div class="fixBox" style="display:none">'
				+ '<textarea class="fixText" rows="3" cols="30"></textarea><br>'
				+ '<button onclick="addFixText('
				+ seq
				+ ',\''
				+ statusCd
				+ '\',\''
				+ messageIdx
				+ '\',\''
				+ subMessageIdx
				+ '\',\'ADD\',\'0\', this)" class="btnFixInput btnSmall">입력</button>'
				+ '<button onclick="cancleFixText(' + seq
				+ ')" class="cancleFixInput btnSmall">취소</button>' + '</div>'
				+ '<p><div class="date">' + date + '</div></p>' + '</div>'
				+ '</div>' + '</li>';
	}
	else{
		control = '<li>' + '<div class="msj macro">' +
		// '<div class="avatar"><img class="img-circle" style="width:100%;" src="'+
		// imgfilepath + bot.avatar +'" /></div>' +
		'<div class="avatar"><img style="width:46%;" src="'
				+ imgfilepath
				+ bot.avatar
				+ '" /></div>'
				+ '<div class="text text-l">'
				+ '<p id = "text_'+seq+'">'
				+ text
				+ '<select id="fromLang_'+seq+'" class="btnFix btnSmall btnUpper align-right" onChange="javascript:translateLang('+seq+ ',\'' +text+'\')">'
				+ '<option value="" selected>language</option>'
				+ '<option value="KOR">English</option>'
				+ '<option value="JP">日本語</option>'
				+ '<option value="CN">中文</option></select>'
				+ '</p>'
				+ '<div class="fixBox" style="display:none">'
				+ '<textarea class="fixText" rows="3" cols="30"></textarea><br>'
				+ '<button onclick="addFixText('
				+ seq
				+ ',\''
				+ statusCd
				+ '\',\''
				+ messageIdx
				+ '\',\''
				+ subMessageIdx
				+ '\',\'ADD\',\'0\', this)" class="btnFixInput btnSmall">입력</button>'
				+ '<button onclick="cancleFixText(' + seq
				+ ')" class="cancleFixInput btnSmall">취소</button>' + '</div>'
				+ '<p><div class="date">' + date + '</div></p>' + '</div>'
				+ '</div>' + '</li>';
	}
	
	setTimeout(function() {
		$(".dialog-ul").append(control);
	});

	var preIdSeq = parseInt($('#idSeq').val());
	$('#idSeq').val(preIdSeq + 1);
}

function translateLang(seq, text){
	var lang = $("#fromLang_"+seq+" option:selected").val();
	
	$.ajax({
		url : 'translateLang.do',
		async : false,
		type : 'POST',
		data : {
			fromLang : lang
			,text : text
		},
		error : function() {
			$('#info').html('<p>An error has occurred</p>');
		},
		dataType : 'text',
		success : function(data) {
			var jsonObj = JSON.parse(data)
//			alert(jsonObj.result);
			var translatedStr = jsonObj.translatedStr;
			var newText = text + '<br>[' + translatedStr + ']' 
			+ '<select id="fromLang_'+seq+'" class="btnFix btnSmall btnUpper align-right" onChange="javascript:translateLang('+seq+ ',\'' +text+'\')">'
			+ '<option value="" selected>language</option>'
			+ '<option value="KOR">English</option>'
			+ '<option value="JP">日本語</option>'
			+ '<option value="CN">中文</option></select>';
			$('#text_'+seq+'').html(newText);
			$('.fixText').val('');
			// 시각화 부분
			$('#dialogShowBoxText').remove();
			$('#dialogShowBox-navigation').html(jsonObj.dialogLogStr);
			// $('#dialogShowBox1 #dialogShowBoxText').remove();
			$('#dialogShowBox-frame').html(jsonObj.dialogLogStr);
		}
	});
}


function insertUser(text, imgfilepath) {
	var control = "";
	var date = formatAMPM(new Date());

	control = '<li>'
			+ '<div class="msj-rta macro">'
			+ '<div class="text text-r" style="text-align: left;">'
			+ '<p>'
			+ text
			+ '</p>'
			+ '<p><div class="date">'
			+ date
			+ '</div></p>'
			+ '</div>'
			+
			// '<div class="avatar" style="padding:0px 0px 0px 10px
			// !important"><img class="img-circle" style="width:100%;" src="'+
			// imgfilepath + usr.avatar+'" /></div>' +
			'<div class="avatar" style="padding:0px 0px 0px 10px !important"><img style="width:46%;" src="'
			+ imgfilepath + usr.avatar + '" /></div>' + '</li>';
	setTimeout(function() {
		$(".dialog-ul").append(control);
	});

}

function activeFixBox(seq) {
	toggleFixBox(seq);
	// $('.fixText').eq(seq).focus();
}

function activeFixFixedBox(obj) {
//	toggleFixFixedBox(statusCd, messageIdx, fixedTextIdx);
	toggleFixFixedBox(obj);
}

function addFixText(seq, statusCd, messageIdx, subMessageIdx, workType, fixedTextIdx, obj) {
	// var objBoxName = $(obj).parent().parent().attr('id');
	// var fixedText = $('.fixText').eq(seq).val();
	var fixedText = '';
	if (workType != "DELETE") {
		fixedText = $(obj).parent().find('textarea').val();
	} else {
		fixedText = $(obj).parent().parent().html();
		fixedText = fixedText.substring(fixedText.indexOf('Fix: ')+5, fixedText.indexOf('<div'));
	}
	
	var loginTime = $('#loginTime').val();

	if ((fixedText == null || fixedText == "") && workType != "DELETE") {
		alert("수정할 문장을 입력하세요!");
		return;
	} else {
		$.ajax({
			url : 'createNewScriptFile.do',
			async : false,
			type : 'POST',
			data : {
				fixedText : fixedText,
				workType : workType,
				fixedTextIdx : fixedTextIdx,
				messageIdx : messageIdx,
				subMessageIdx : subMessageIdx,
				statusCd : statusCd,
				loginTime : loginTime
			},
			error : function() {
				$('#info').html('<p>An error has occurred</p>');
			},
			dataType : 'text',
			success : function(data) {
				var jsonObj = JSON.parse(data)
//				alert(jsonObj.result);
				$('.fixText').val('');
				// 시각화 부분
				$('#dialogShowBoxText').remove();
				$('#dialogShowBox-navigation').html(jsonObj.dialogLogStr);
				// $('#dialogShowBox1 #dialogShowBoxText').remove();
				$('#dialogShowBox-frame').html(jsonObj.dialogLogStr);
			}
		});
	}
	toggleFixBox(seq);
}

function cancleFixText(seq) {
	toggleFixBox(seq);
}
function cancleFixFixedText(obj) {
	// var seq = statusCd + ':' + messageIdx + ':' + fixedTextIdx;
//	toggleFixFixedBox(statusCd, messageIdx, fixedTextIdx);
	toggleFixFixedBox(obj);
}

function toggleFixBox(seq) {
	$(".fixBox").eq(seq).toggle();
}
function toggleFixFixedBox(obj) {
//	$("#dialogShowBox-navigation .fixFixedBox").each(
//			function() {
//				if ($(this).attr('data-statusCd') == statusCd
//						&& $(this).attr('data-msgIdx') == msgIdx
//						&& $(this).attr('data-fixedTextidx') == fixedTextIdx) {
//					$(this).toggle();
//				}
//			});
//	$("#dialogShowBox-frame .fixFixedBox").each(
//			function() {
//				if ($(this).attr('data-statusCd') == statusCd
//						&& $(this).attr('data-msgIdx') == msgIdx
//						&& $(this).attr('data-fixedTextidx') == fixedTextIdx) {
//					$(this).toggle();
//				}
//			});
	$(obj).parent().next().toggle();
}

// ***************************** util *****************************//
function sleep(ms) {
	ts1 = new Date().getTime() + ms;
	do
		ts2 = new Date().getTime();
	while (ts2 < ts1);
}