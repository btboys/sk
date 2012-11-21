var ws = null,sending = false;
function startWebSocket() {
	if ('WebSocket' in window)
		ws = new WebSocket(window.wsSer+"wsServices?nk="+window.nickName+"&key="+window.key);
	else if ('MozWebSocket' in window)
		ws = new MozWebSocket(window.wsSer+"wsServices?nk="+window.nickName+"&key="+window.key);
	else
		alert("您的浏览器不支持！建议使用Chrome浏览器或者火狐浏览器!");

	if(ws){
		ws.onmessage = function(evt) {
			var data = eval("("+evt.data+")");
			var $mp = $("#msgPanel");
			$mp.append("<p><b>" + data.visitor.nickname +"</b> "+ new Date(data.date).format() + "</p>")
			   .append("<p>" + data.content + "</p>");
			
			$('#sendMsgBtn').linkbutton({text: '发送(ctrl+enter)',disabled:false});
			sending = false;
		};

		ws.onopen = function(evt) {
			startOnlineSocket();
			$('#sendMsgBtn').linkbutton({ text: '发送(ctrl+enter)'}).click(sendMsg);
		};
	}
}

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
				console.log(rs.data);
				var rootNode = $tree.tree("getRoot");
				$tree.tree("append",{parent:rootNode.target,data:rs.data});
			}else{
				var node = $tree.tree('find', rs.data);
				if(node)
					$tree.tree('remove', node.target);
			}
		};
	}
}

function sendMsg() {
	if(!window.editor.isEmpty() && ws.readyState == 1 && !sending){
		$('#sendMsgBtn').linkbutton({text: '正在发送...',disabled:true});
		sending = true;
		ws.send(window.editor.html());
		window.editor.html('');

		var $mp = $("#msgPanel");
		var ih = $mp[0].scrollHeight;
		$mp.scrollTop(ih);
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
		items : [
		         'bold','italic','underline','fontname','fontsize','forecolor','hilitecolor','plug-align','plug-order','plug-indent'
		         ]
	});
	
	KindEditor.ctrl(window.editor.cmd.doc.body, 13, sendMsg);
	KindEditor.ctrl(window.editor.cmd.doc.body, 10, sendMsg);
	
};