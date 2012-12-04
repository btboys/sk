package com.godson.websocket.inbound;

import java.io.IOException;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;

import org.eclipse.jetty.websocket.WebSocket.OnTextMessage;

import com.alibaba.fastjson.JSONArray;
import com.godson.websocket.Msg;
import com.godson.websocket.SocketEntity;
import com.godson.websocket.Visitor;
import com.godson.websocket.servlet.InitServlet;

public class ServerMessageWebSocket implements OnTextMessage {

	private Visitor vt;
	private String key;
	private int count = 0;
	private Date lastSendDate;
	private Boolean dispeak = false;
	private Connection connection;
	
	public ServerMessageWebSocket(Visitor vt,String key) {
		this.vt = vt;
		this.key = key;
	}

	public void onClose(int arg0, String arg1) {
		InitServlet.getSocketList().remove(key);
		try {
			OnlineMessageWebSocket.synchronizeTree(false,key);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void onOpen(Connection con) {
		this.connection = con;
		SocketEntity se = new SocketEntity();
		se.setVisitor(vt);
		se.setServerbd(this);
		InitServlet.getSocketList().put(key, se);

		try {
			Msg mg = new Msg(vt.getNickname()+"欢迎登录SK群聊系统!", new Visitor("小K机器人"+con.getMaxIdleTime(), "8.8.8.8"));
			con.sendMessage(JSONArray.toJSONString(mg));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void onMessage(String msg) {
		try {
			Msg mg = new Msg(msg, vt);
			if(lastSendDate != null && (new Date().getTime() - lastSendDate.getTime()) < 1000*10 && count ==10){
				mg.setVisitor(new Visitor("小K机器人", "8.8.8.8"));
				mg.setContent("对不起，你发送信息的速度太快了哦。");
				this.connection.sendMessage(JSONArray.toJSONString(mg));
				dispeak = true;
			}else{
				if(dispeak){
					count = 0;
					dispeak=false;
				}
				
				HashMap<String, SocketEntity> onl = InitServlet.getSocketList();
				Collection<SocketEntity> se = onl.values();
				for (SocketEntity socketEntity : se) {
					Connection con = socketEntity.getServerbd().getConnection();
					con.sendMessage(JSONArray.toJSONString(mg));
				}
				count++;
			}
			lastSendDate = new Date();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public Connection getConnection(){
		System.out.println("MaxIdleTime:"+connection.getMaxIdleTime());
		System.out.println("MaxTextMessageSize:"+connection.getMaxTextMessageSize());
		return connection;
	}
}
