package com.godson.websocket.inbound;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

import org.apache.catalina.websocket.MessageInbound;
import org.apache.catalina.websocket.WsOutbound;

import com.alibaba.fastjson.JSONArray;
import com.godson.websocket.SocketEntity;
import com.godson.websocket.servlet.InitServlet;

/**
 * 
 * @author east.com
 * 
 */
public class OnlineMessageInbound extends MessageInbound {

	private String key;

	public OnlineMessageInbound(String key) {
		this.key = key;
	}

	@Override
	protected void onBinaryMessage(ByteBuffer arg0) throws IOException {

	}

	@Override
	protected void onTextMessage(CharBuffer msg) throws IOException {

	}

	@Override
	protected void onOpen(WsOutbound outbound) {
		try {
			synchronizeTree(true,key);
			InitServlet.getSocketList().get(key).setOnlinebd(this);
			initTree(outbound);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public static void synchronizeTree(Boolean isOnline,String key) throws IOException {
		HashMap<String, SocketEntity> onl = InitServlet.getSocketList();
		Collection<SocketEntity> se = onl.values();
		Object data = "\""+key+"\"";
		if(isOnline){
			HashMap<String, Object> node = new HashMap<String, Object>();
			node.put("id", key);
			node.put("text", InitServlet.getSocketList().get(key).getVisitor().getNickname());
			data = JSONArray.toJSONString(node);
		}
		
		for (SocketEntity socketEntity : se) {
			OnlineMessageInbound  omi = socketEntity.getOnlinebd();
			if(omi != null){
				CharBuffer buffer = CharBuffer.wrap("{\"status\":"+(isOnline?"true":"false")+",\"data\":["+data+"]}");
				WsOutbound ob = socketEntity.getOnlinebd().getWsOutbound();
				ob.writeTextMessage(buffer);
				ob.flush();
			}
		}
	}

	private void initTree(WsOutbound outbound) throws IOException {
		List<HashMap<String, Object>> olt = onlineTree();
		CharBuffer buffer = CharBuffer.wrap("{\"status\":true,\"data\":"+JSONArray.toJSONString(olt)+"}");
		outbound.writeTextMessage(buffer);
		outbound.flush();
	}

	private static List<HashMap<String, Object>> onlineTree() {
		List<HashMap<String, Object>> childNode = new ArrayList<HashMap<String, Object>>();
		HashMap<String, SocketEntity> onl = InitServlet.getSocketList();
		Set<String> keys = onl.keySet();
		for (String key : keys) {
			SocketEntity socketEntity = onl.get(key);
			HashMap<String, Object> node = new HashMap<String, Object>();
			node.put("id", key);
			node.put("text", socketEntity.getVisitor().getNickname());
			childNode.add(node);
		}

		return childNode;
	}
}
