<%@ page language="java" import="java.util.*,java.net.URLEncoder,java.net.URLDecoder,com.godson.websocket.servlet.InitServlet" pageEncoding="UTF-8"%>
<%
	String path = request.getContextPath();
	String basePath = request.getScheme() + "://"+ request.getServerName() + ":" + request.getServerPort()+ path + "/";
	String wsPath = "ws://"+ request.getServerName() + ":" + request.getServerPort()+ path + "/";
	request.setCharacterEncoding("UTF-8");
	String nickName = request.getParameter("nickName");
	
	if(nickName != null){
		if(InitServlet.getNicknames().containsValue(nickName)){
			response.sendRedirect(basePath+"?error=repeat");
		}
		Cookie ck = new Cookie("nickName",URLEncoder.encode(nickName,"utf-8"));
		ck.setMaxAge(3600*1000);
		response.addCookie(ck);
		response.sendRedirect(basePath);
	}
	
	Cookie cookies[]=request.getCookies(); 
	if(cookies!=null){
		for(int i = 0;i < cookies.length; i++){
			Cookie ck = cookies[i];
			if(ck.getName().equals("nickName")){
				nickName = URLDecoder.decode(ck.getValue(),"utf-8");
			}
		}
	}
	
	Boolean isIE = request.getHeader("USER-AGENT").toLowerCase().indexOf("msie") > 0 ? true : false; 
	
	String key = UUID.randomUUID().toString().replaceAll("-","");
%>
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<html>
<head>
<base href="<%=basePath%>">
<title>Socket 群聊系统</title>
<meta http-equiv="pragma" content="no-cache">
<meta http-equiv="cache-control" content="no-cache">
<meta http-equiv="expires" content="0">
<meta http-equiv="keywords" content="群聊系统">
<meta http-equiv="description" content="基于HTML5 WebSocket的群聊系统">
<link rel="stylesheet" type="text/css" href="themes/metro/easyui.css">
<link rel="stylesheet" type="text/css" href="themes/icon.css">
<style type="text/css">
html,body{
	overflow: hidden;
	width: 100%;
	height: 100%;
}
#deviceInfo {
line-height: 100px;
font-size: 14px;
text-align: center;
}
#deviceInfo {
width: 640px;
height: 100px;
line-height: 100px;
font-size: 14px;
text-align: center;
border: 1px solid #99bbe8;
margin: -50px auto auto -320px;
background: #e4f0f8;
position: absolute;
top: 25%;
left: 50%;
border-radius: 10px;
}
</style>
<script src="js/jquery-1.8.0.min.js"></script>
<script src="js/jquery.easyui.min.js"></script>
<script charset="utf-8" src="js/kindeditor-4.1.4//kindeditor-min.js"></script>
<script charset="utf-8" src="js/kindeditor-4.1.4/lang/zh_CN.js"></script>
<script type="text/javascript">
window.wsSer = "<%=wsPath%>";
window.nickName = "<%=nickName%>";
window.key = "<%=key%>";
<%
if(request.getParameter("error")!=null&&request.getParameter("error").equals("repeat")){
%>
$(function(){
	$.messager.alert("提示","您输入的昵称已经存在哦....","info");
});
<%} %>
</script>
<script charset="utf-8" src="js/core.js?ver=1.2"></script>
</head>

<body <%= (isIE || nickName==null ? "" : "onload='startWebSocket()'") %>>
	<%
	if(isIE){
	%>
	<div id="deviceInfo">
		<div id="deviceInfoBox">
		当前使用的浏览器不支持该程序运行！
		<div>推荐使用<span>谷歌浏览器</span>或者<span>火狐浏览器</span>来获得程序支持。<a href="https://www.google.com/intl/zh-CN/chrome/browser/" target="_blank">点击这里下载</a></div>
		</div>
	</div>
	<%
	}else if(nickName!=null){
	%>
	<div class="easyui-dialog" title="SK群聊--<%=nickName%>" data-options="width:700,height:500,iconCls:'icon-sum',closable: false,   
    cache: false,maximizable:true">
		<div class="easyui-layout" fit="true">
			<div region="center" border="false">
				<div class="easyui-layout" fit="true" border="false">
					<div region="center" border="false" id="msgPanel" style="padding: 0 10px;">	
						
					</div>
					<div region="south" style="height: 160px" border="false">
						<div class="easyui-layout" fit="true">
							<div region="center" border="false" style="overflow: hidden;">
								<textarea id="content" style="width:100%;height:100px;visibility:hidden;"></textarea>
							</div>
							<div region="south" style="height: 30px;text-align:right;padding:2px 2px;overflow: hidden;border-bottom: none;border-right: none;border-left: none;">
								<a href="javascript:void(0)" id="sendMsgBtn" iconCls="icon-redo" class="easyui-linkbutton">正在建立链接...</a>
							</div>
						</div>
					</div>
				</div>
			</div>
			<div region="east" style="width: 200px;border-bottom: none;border-right: none;border-top: none;" >
				<div class="easyui-layout" fit="true" border="false">
					<div region="center" border="false" id="onlPanel" style="padding: 0 10px;" title="&nbsp;">	
						<ul id="online" class="easyui-tree" style="padding: 5px 0;" data-options="animate:true">
							<li iconCls="icon-users">SK群聊</li>
						</ul>
					</div>
					<div region="south" style="height: 160px;border-bottom: none;border-right: none;border-left: none;padding: 3px;">
						<p>后台代码：<br/><a href="https://github.com/btboys/sk" target="_blank">https://github.com/btboys/sk</a></p>
						<p>作者：____′↘夏悸	<br/><a href="http://bbs.btboys.com" target="_blank">easyui社区出品</a></p>
					</div>
				</div>
			</div>
		</div>
	</div>
	<%
	}else{
	%>
	<div id="deviceInfo">
		<div id="deviceInfoBox">
		<form action="" method="post">
			输入昵称：<input type="text" maxlength="8" name="nickName" required="true"/> <button>开始聊天...</button>
		</form>
		</div>
	</div>
	<%} %>
</body>
</html>
