package com.godson.websocket.inbound;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;

import org.apache.catalina.websocket.MessageInbound;
import org.apache.catalina.websocket.WsOutbound;

import com.alibaba.fastjson.JSONArray;
import com.godson.websocket.Msg;
import com.godson.websocket.SocketEntity;
import com.godson.websocket.Visitor;
import com.godson.websocket.servlet.InitServlet;

public class ServerMessageInbound extends MessageInbound {

	private Visitor vt;
	private String key;
	private int count = 0;
	private Date lastSendDate;
	private Boolean dispeak = false;
	
	public ServerMessageInbound(Visitor vt,String key) {
		this.vt = vt;
		this.key = key;
	}

	@Override
	protected void onBinaryMessage(ByteBuffer arg0) throws IOException {

	}

	@Override
	protected void onTextMessage(CharBuffer msg) throws IOException {
		if(msg != null && !msg.equals("")){
			Msg mg = new Msg(msg, vt);
			if(lastSendDate != null && (new Date().getTime() - lastSendDate.getTime()) < 1000*10 && count ==10){
				mg.setVisitor(new Visitor("小K机器人", "8.8.8.8"));
				mg.setContent(CharBuffer.wrap("对不起，你发送信息的速度太快了哦。"));
				WsOutbound ob = this.getWsOutbound();
				ob.writeTextMessage(CharBuffer.wrap(JSONArray.toJSONString(mg)));
				ob.flush();
				dispeak = true;
			}else{
				if(dispeak){
					count = 0;
					dispeak=false;
				}
				
				HashMap<String, SocketEntity> onl = InitServlet.getSocketList();
				Collection<SocketEntity> se = onl.values();
				for (SocketEntity socketEntity : se) {
					CharBuffer buffer = CharBuffer.wrap(JSONArray.toJSONString(mg));
					WsOutbound ob = socketEntity.getServerbd().getWsOutbound();
					ob.writeTextMessage(buffer);
					ob.flush();
				}
				count++;
			}
			lastSendDate = new Date();
		}
	}

	@Override
	protected void onClose(int status) {
		InitServlet.getSocketList().remove(key);
		InitServlet.getNicknames().remove(key);
		try {
			OnlineMessageInbound.synchronizeTree(false,key);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@Override
	protected void onOpen(WsOutbound outbound) {
		SocketEntity se = new SocketEntity();
		se.setVisitor(vt);
		se.setServerbd(this);
		InitServlet.getSocketList().put(key, se);
		
		Msg mg = new Msg(CharBuffer.wrap(vt.getNickname()+"欢迎登录SK群聊系统!"), new Visitor("小K机器人", "8.8.8.8"));
		CharBuffer buffer = CharBuffer.wrap(JSONArray.toJSONString(mg));
		try {
			outbound.writeTextMessage(buffer);
			outbound.flush();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
