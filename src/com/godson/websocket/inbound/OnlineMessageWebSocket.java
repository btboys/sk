package com.godson.websocket.inbound;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

import org.eclipse.jetty.websocket.WebSocket.OnTextMessage;

import com.alibaba.fastjson.JSONArray;
import com.godson.websocket.SocketEntity;
import com.godson.websocket.servlet.InitServlet;

/**
 * 
 * @author east.com
 * 
 */
public class OnlineMessageWebSocket implements OnTextMessage {

	private String key;
	private Connection connection;

	public OnlineMessageWebSocket(String key) {
		this.key = key;
	}
	
	public static void synchronizeTree(Boolean isOnline,String key) throws IOException {
		HashMap<String, SocketEntity> onl = InitServlet.getSocketList();
		Collection<SocketEntity> se = onl.values();
		Object data = "\""+key+"\"";
		if(isOnline){
			HashMap<String, Object> node = new HashMap<String, Object>();
			node.put("id", key);
			node.put("iconCls", "icon-user");
			node.put("text", InitServlet.getSocketList().get(key).getVisitor().getNickname());
			data = JSONArray.toJSONString(node);
		}
		
		for (SocketEntity socketEntity : se) {
			OnlineMessageWebSocket  omi = socketEntity.getOnlinebd();
			if(omi != null){
				Connection con = omi.getConnection();
				if(con!=null && con.isOpen()){
					con.sendMessage("{\"status\":"+(isOnline?"true":"false")+",\"data\":["+data+"]}");
				}
			}
		}
	}

	private void initTree(Connection con) throws IOException{
		List<HashMap<String, Object>> olt = onlineTree();
		con.sendMessage("{\"status\":true,\"data\":"+JSONArray.toJSONString(olt)+"}");
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

	public void onClose(int arg0, String arg1) {
		
	}

	public void onOpen(Connection con) {
		try {
			this.connection = con;
			synchronizeTree(true,key);
			InitServlet.getSocketList().get(key).setOnlinebd(this);
			initTree(con);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void onMessage(String msg) {
		
	}
	
	public Connection getConnection(){
		return connection;
	}
}
