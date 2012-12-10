var ws = null,Socket=WebSocket||MozWebSocket,sending = false,stautsInv,onlStautsInv;
/**
 * 建立信息服务通道
 */
function startWebSocket() {
	if(Socket){
		ws = new Socket(window.wsSer+"wsServices?nk="+window.nickName+"&key="+window.key);
		ws.onmessage = function(evt) {
			var data = eval("("+evt.data+")");
			switch (data.type) {
			case 'msg':
				writeMsg(data.msg);
				break;
			case 'vt':
				bulidTree(data.msg);
				break;
			default:
				break;
			}
			//保证不掉线
			if(stautsInv){
				clearInterval(stautsInv);
			}
			stautsInv = setInterval(function(){
				ws.send("");
			},4*60*1000);
		};

		ws.onopen = function(evt) {
			$('#sendMsgBtn').linkbutton({ text: '发送(ctrl+enter)'}).click(sendMsg);
			if(window.localStorage){
				var msgData = localStorage.getItem("sk_loc_msg_data") ? eval(localStorage.getItem("sk_loc_msg_data")) : [];
				if(msgData){
					var msg = [];
					$.each(msgData,function(){
						msg.push(bulidMsg(this));
					});
					$("#msgPanel").html(msg.join(""));
				}
			}
		};
		
		ws.onerror = function(evt){
			$.messager.alert("警告","连接异常！","error");
		};
		
		ws.onclose = function(){
			$.messager.alert("提示","服务已断开！","info");
		};
	}else{
		$.messager.alert("您的浏览器不支持！建议使用Chrome浏览器或者火狐浏览器!");
	}
}

function writeMsg(data){
	var $mp = $("#msgPanel");
	$mp.append(bulidMsg(data));
	saveMsg(data);
	$('#sendMsgBtn').linkbutton({text: '发送(ctrl+enter)',disabled:false});
	sending = false;
	//设置滚动条滚到底部
	var mp = $("#msgPanel")[0];
	mp.scrollTop = mp.scrollHeight;
}

function bulidTree(data){
	console.log(data);
	var $tree = $("#online");
	if(data.type == 'append'){
		var rootNode = $tree.tree("getRoot");
		$tree.tree("append",{parent:rootNode.target,data:data.data});
	}else{
		var node = $tree.tree('find', data.data);
		if(node)
			$tree.tree('remove', node.target);
	}
	
	
	$('#onlPanel').panel("setTitle","当前在线：<b>"+data.total+"<b/> 人");
}
/**
 * 发送信息
 */
function sendMsg() {
	if(!window.editor.isEmpty() && ws.readyState == 1 && !sending){
		$('#sendMsgBtn').linkbutton({text: '正在发送...',disabled:true});
		sending = true;
		ws.send(window.editor.html());
		window.editor.html('');
	}
}

function bulidMsg(data){
	var temp = '<p><b>$1</b><sub>(<span title="$2">$3</span>) $4<sub></p>';
	var city = "未知";
	var info = "IP:"+data.visitor.ip;
	if(data.visitor.city){
		city = data.visitor.city;
		info += " "+data.visitor.country + " " +data.visitor.province+ " " +data.visitor.isp;
	}
	return formatString(temp, data.visitor.nickname,info,city,new Date(data.date).format("hh:mm:ss"))+"<p>" + data.content + "</p>";
}

//时间格式化
Date.prototype.format = function (format) {
	/*
	 * eg:format="yyyy-MM-dd hh:mm:ss";
	 */
	if (!format) {
		format = "yyyy-MM-dd hh:mm:ss";
	}

	var o = {
			"M+" : this.getMonth() + 1, // month
			"d+" : this.getDate(), // day
			"h+" : this.getHours(), // hour
			"m+" : this.getMinutes(), // minute
			"s+" : this.getSeconds(), // second
			"q+" : Math.floor((this.getMonth() + 3) / 3), // quarter
			"S" : this.getMilliseconds()
			// millisecond
	};

	if (/(y+)/.test(format)) {
		format = format.replace(RegExp.$1, (this.getFullYear() + "").substr(4 - RegExp.$1.length));
	}

	for (var k in o) {
		if (new RegExp("(" + k + ")").test(format)) {
			format = format.replace(RegExp.$1, RegExp.$1.length == 1 ? o[k] : ("00" + o[k]).substr(("" + o[k]).length));
		}
	}
	return format;
};

var formatString = function(str) {
	for ( var i = 1; i < arguments.length; i++) {
		str = str.replace("$" + i, arguments[i]);
	}
	return str;
};

function obj2str(o) {
    var r = [];
    if (typeof o == "string") return "\"" + o.replace(/([\'\"\\])/g, "\\$1").replace(/(\n)/g, "\\n").replace(/(\r)/g, "\\r").replace(/(\t)/g, "\\t") + "\"";
    if (typeof o == "object") {
        if (!o.sort) {
            for (var i in o) r.push(i + ":" + obj2str(o[i]));
            if ( !! document.all && !/^\n?function\s*toString\(\)\s*\{\n?\s*\[native code\]\n?\s*\}\n?\s*$/.test(o.toString)) {
                r.push("toString:" + o.toString.toString());
            }
            r = "{" + r.join() + "}";
        } else {
            for (var i = 0; i < o.length; i++) r.push(obj2str(o[i]));
            r = "[" + r.join() + "]";
        }
        return r;
    }
    return o.toString();
}

function saveMsg(msg){
	if(window.localStorage && msg.visitor.nickname != "小K机器人"){
		var msgData = localStorage.getItem("sk_loc_msg_data") ? eval(localStorage.getItem("sk_loc_msg_data")) : [];
		msgData.push(msg);
		localStorage.setItem("sk_loc_msg_data",obj2str(msgData));
	}
}

//在窗口渲染完毕之后，初始化编辑器
$.parser.onComplete = function(){
	KindEditor.each({ 
		'plug-align' : {
			name : '对齐方式',
			method : {
				'justifyleft' : '左对齐',
				'justifycenter' : '居中对齐',
				'justifyright' : '右对齐'
			}
		},
		'plug-order' : {
			name : '编号',
			method : {
				'insertorderedlist' : '数字编号',
				'insertunorderedlist' : '项目编号'
			}
		},
		'plug-indent' : {
			name : '缩进',
			method : {
				'indent' : '向右缩进',
				'outdent' : '向左缩进'
			}
		}
	},function( pluginName, pluginData ){
		var lang = {};
		lang[pluginName] = pluginData.name;
		KindEditor.lang( lang );
		KindEditor.plugin( pluginName, function(K) {
			var self = this;
			self.clickToolbar( pluginName, function() {
				var menu = self.createMenu({
					name : pluginName,
					width : pluginData.width || 100
				});
				KindEditor.each( pluginData.method, function( i, v ){
					menu.addItem({
						title : v,
						checked : false,
						iconClass : pluginName+'-'+i,
						click : function() {
							self.exec(i).hideMenu();
						}
					});
				});
			});
		});
	});
	
	window.editor  = KindEditor.create('#content', {
		themeType : 'qq',
		pasteType:1,
		useContextmenu:false,
		newlineTag:"br",
		items : [
		         'bold','italic','underline','fontname','fontsize','forecolor','hilitecolor','plug-align','plug-order','plug-indent'
		         ]
	});
	if(window.editor){
		KindEditor.ctrl(window.editor.cmd.doc.body, 13, sendMsg);
		KindEditor.ctrl(window.editor.cmd.doc.body, 10, sendMsg);
	}
};