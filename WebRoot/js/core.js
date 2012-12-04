var ws = null,sending = false;
/**
 * 建立信息服务通道
 */
function startWebSocket() {
	if ('WebSocket' in window)
		ws = new WebSocket(window.wsSer+"wsServices?nk="+window.nickName+"&key="+window.key);
	else if ('MozWebSocket' in window)
		ws = new MozWebSocket(window.wsSer+"wsServices?nk="+window.nickName+"&key="+window.key);
	else
		alert("您的浏览器不支持！建议使用Chrome浏览器或者火狐浏览器!");

	if(ws){
		var temp = '<p><b>$1</b><sub>(<span title="$2">$3</span>) $4<sub></p>';
		ws.onmessage = function(evt) {
			var data = eval("("+evt.data+")");
			var city = "未知";
			var info = "IP:"+data.visitor.ip;
			if(data.visitor.city){
				city = data.visitor.cit;
				info += " "+data.visitor.country + " " +data.visitor.province+ " " +data.visitor.isp;
			}
			var $mp = $("#msgPanel");
			$mp.append(formatString(temp, data.visitor.nickname,info,city,new Date(data.date).format("hh:mm:ss")))
			   .append("<p>" + data.content + "</p>");
			
			$('#sendMsgBtn').linkbutton({text: '发送(ctrl+enter)',disabled:false});
			sending = false;
			//设置滚动条滚到底部
			var mp = $("#msgPanel")[0];
			mp.scrollTop = mp.scrollHeight;
			saveMsg(data);
		};

		ws.onopen = function(evt) {
			startOnlineSocket();
			$('#sendMsgBtn').linkbutton({ text: '发送(ctrl+enter)'}).click(sendMsg);
		};
		
		ws.onerror = function(evt){
			$.messager.alert("警告","连接异常！","error");
		};
		
		ws.onclose = function(){
			alert("x");
		};
	}
}

/**
 * 建立在线访客服务通道
 */
function startOnlineSocket() {
	var olWs = null;
	if ('WebSocket' in window)
		olWs = new WebSocket(window.wsSer+"wsOnline?key="+window.key);
	else if ('MozWebSocket' in window)
		olWs = new MozWebSocket(window.wsSer+"wsOnline?key="+window.key);

	if(olWs){
		olWs.onmessage = function(evt) {
			var rs = eval("("+evt.data+")");
			var $tree = $("#online");
			if(rs.status){
				var rootNode = $tree.tree("getRoot");
				$tree.tree("append",{parent:rootNode.target,data:rs.data});
			}else{
				var node = $tree.tree('find', rs.data);
				if(node)
					$tree.tree('remove', node.target);
			}
			//保证不掉线
			setInterval(function(){
				olWs.send("");
			},30*1000);
		};
		
		olWs.onerror = function(evt){
			$.messager.alert("警告","程序异常！","error");
		};
	
	}
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
	if(window.localStorage){
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
	
	KindEditor.ctrl(window.editor.cmd.doc.body, 13, sendMsg);
	KindEditor.ctrl(window.editor.cmd.doc.body, 10, sendMsg);
	
};