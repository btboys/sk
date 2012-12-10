package com.godson.websocket.inbound;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

import org.apache.catalina.websocket.MessageInbound;
import org.apache.catalina.websocket.WsOutbound;

import com.alibaba.fastjson.JSONArray;
import com.godson.websocket.Data;
import com.godson.websocket.Msg;
import com.godson.websocket.SocketEntity;
import com.godson.websocket.TreeData;
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
				Data data = new Data(mg);
				ob.writeTextMessage(CharBuffer.wrap(JSONArray.toJSONString(data)));
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
					Data data = new Data(mg);
					CharBuffer buffer = CharBuffer.wrap(JSONArray.toJSONString(data));
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
		this.synchronizeTree(false,key);
	}

	@Override
	protected void onOpen(WsOutbound outbound) {
		SocketEntity se = new SocketEntity();
		se.setVisitor(vt);
		se.setServerbd(this);
		InitServlet.getSocketList().put(key, se);
		InitServlet.getNicknames().put(key, vt.getNickname());
		
		Msg mg = new Msg(CharBuffer.wrap(vt.getNickname()+"欢迎登录SK群聊系统!"), new Visitor("小K机器人", "8.8.8.8"));
		Data data = new Data(mg);
		this.sendMsg(outbound,JSONArray.toJSONString(data));
		
		List<HashMap<String, Object>> olt = onlineTree();
		data.setType("vt");

		TreeData td = new TreeData();
		td.setType("append");
		td.setTotal(olt.size());
		td.setData(olt);
		data.setMsg(td);
		this.sendMsg(outbound,JSONArray.toJSONString(data));
		
		synchronizeTree(true,key);
	}
	
	private List<HashMap<String, Object>> onlineTree() {
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
	
	private void synchronizeTree(Boolean isOnline,String key){
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
			ServerMessageInbound  omi = socketEntity.getServerbd();
			if(omi != null && !omi.getKey().equals(key)){
				this.sendMsg(omi.getWsOutbound(), JSONArray.toJSONString(data));
			}
		}
	}
	
	private void sendMsg(WsOutbound outbound,String data){
		try {
			outbound.writeTextMessage(CharBuffer.wrap(data));
			outbound.flush();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public String getKey() {
		return key;
	}
}
