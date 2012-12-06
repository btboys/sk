package com.godson.websocket.inbound;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

import org.eclipse.jetty.websocket.WebSocket.OnTextMessage;

import com.alibaba.fastjson.JSONArray;
import com.godson.websocket.Data;
import com.godson.websocket.Msg;
import com.godson.websocket.SocketEntity;
import com.godson.websocket.TreeData;
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
		InitServlet.getNicknames().remove(key);
		try {
			synchronizeTree(false,key);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void onOpen(Connection con) {
		this.connection = con;
		SocketEntity se = new SocketEntity();
		se.setVisitor(vt);
		se.setServerbd(this);
		se.setKey(key);
		
		InitServlet.getSocketList().put(key, se);
		InitServlet.getNicknames().put(key, vt.getNickname());
		
		try {
			Msg mg = new Msg(vt.getNickname()+"欢迎登录SK群聊系统!", new Visitor("小K机器人", "8.8.8.8"));
			Data data = new Data(mg);
			con.sendMessage(JSONArray.toJSONString(data));
			
			List<HashMap<String, Object>> olt = onlineTree();
			data.setType("vt");

			TreeData td = new TreeData();
			td.setType("append");
			td.setTotal(olt.size());
			td.setData(olt);
			data.setMsg(td);
			con.sendMessage(JSONArray.toJSONString(data));
			
			synchronizeTree(true,key);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void onMessage(String msg) {
		if(msg != null && !msg.equals("")){
			try {
				Msg mg = new Msg(msg, vt);
				if(lastSendDate != null && (new Date().getTime() - lastSendDate.getTime()) < 1000*10 && count ==10){
					mg.setVisitor(new Visitor("小K机器人", "8.8.8.8"));
					mg.setContent("对不起，你发送信息的速度太快了哦。");
					Data data = new Data(mg);
					this.connection.sendMessage(JSONArray.toJSONString(data));
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
						Data data = new Data(mg);
						con.sendMessage(JSONArray.toJSONString(data));
					}
					count++;
				}
				lastSendDate = new Date();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	
	private static List<HashMap<String, Object>> onlineTree() {
		List<HashMap<String, Object>> childNode = new ArrayList<HashMap<String, Object>>();
		HashMap<String, SocketEntity> onl = InitServlet.getSocketList();
		Set<String> keys = onl.keySet();
		for (String key : keys) {
			SocketEntity socketEntity = onl.get(key);
			HashMap<String, Object> node = new HashMap<String, Object>();
			node.put("id", key);
			node.put("iconCls", "icon-user");
			node.put("text", socketEntity.getVisitor().getNickname());
			childNode.add(node);
		}

		return childNode;
	}
	
	private void synchronizeTree(Boolean isOnline,String key) throws IOException {
		TreeData td = new TreeData();
		HashMap<String, SocketEntity> onl = InitServlet.getSocketList();
		Collection<SocketEntity> se = onl.values();
		td.setData(key);
		td.setType("del");
		td.setTotal(InitServlet.getNicknames().size());
		
		if(isOnline){
			List<HashMap<String, Object>> t = new ArrayList<HashMap<String,Object>>();
			HashMap<String, Object> node = new HashMap<String, Object>();
			node.put("id", key);
			node.put("iconCls", "icon-user");
			node.put("text", InitServlet.getSocketList().get(key).getVisitor().getNickname());
			t.add(node);
			td.setData(t);
			td.setType("append");
		}
		
		Data data = new Data(td);
		data.setType("vt");
		for (SocketEntity socketEntity : se) {
			ServerMessageWebSocket  omi = socketEntity.getServerbd();
			if(omi != null && !socketEntity.getKey().equals(key)){
				Connection con = omi.getConnection();
				if(con!=null && con.isOpen()){
					con.sendMessage(JSONArray.toJSONString(data));
				}
			}
		}
	}
	
	public Connection getConnection(){
		return connection;
	}
	
	public String getKey() {
		return key;
	}
}
