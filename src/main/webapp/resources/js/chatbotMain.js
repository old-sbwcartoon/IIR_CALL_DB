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
		if (inputMsg == null || inputMsg == "") {
			alert("문장을 입력하세요!");
			return;
		}

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

	});

	$('#userInput').keypress(function(e) {
		if (e.which == 13) {
			event.preventDefault();
			insertUser($('#userInput').val(), $('#imgSrc').val());
			doInput($('#statusCd').val(), $('#exStatusCd').val(), $('#messageIdx').val(), $('#subMessageIdx').val());
			$('#userInput').val('');
			// 스크롤바 focusing
			$("html, body").animate({
				scrollTop : '+=400'
			}, 'slow');
			$(".dialog-ul").animate({
				scrollTop : '+=400'
			}, 'slow');

//			$('#exStatusCd').val($('#statusCd').val());
		}
	});
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
	var sock = new WebSocket("ws://localhost:7080/sockethandler.do");
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
		insertBot(data.message, data.imgSrc, data.messageIdx, data.subMessageIdx, data.statusCd);

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

function insertBot(text, imgfilepath, messageIdx, subMessageIDx, statusCd) {
	var control = "";
	var date = formatAMPM(new Date());
	var seq = $('#idSeq').val();
	var messageIdx = $('#messageIdx').val();
	var subMessageIdx = $('#subMessageIdx').val();

	// sleep(text.length * 100); //사용자 입력과 동시에 나오지 않도록 잠시 정지. 글자 수에 따라 정지 시간
	// 길어짐. 버벅댐.
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
			+ '<button onclick="activeFixBox('
			+ seq
			+ ')" class="btnFix btnSmall btnUpper align-right">추가</button></p>'
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

	setTimeout(function() {
		$(".dialog-ul").append(control);
	});

	var preIdSeq = parseInt($('#idSeq').val());
	$('#idSeq').val(preIdSeq + 1);
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