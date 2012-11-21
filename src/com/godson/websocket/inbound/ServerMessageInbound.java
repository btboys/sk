package com.godson.websocket.inbound;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.util.Collection;
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
	public ServerMessageInbound(Visitor vt,String key) {
		this.vt = vt;
		this.key = key;
	}

	@Override
	protected void onBinaryMessage(ByteBuffer arg0) throws IOException {

	}

	@Override
	protected void onTextMessage(CharBuffer msg) throws IOException {
		HashMap<String, SocketEntity> onl = InitServlet.getSocketList();
		Collection<SocketEntity> se = onl.values();
		for (SocketEntity socketEntity : se) {
			Msg mg = new Msg(msg, socketEntity.getVisitor());
			CharBuffer buffer = CharBuffer.wrap(JSONArray.toJSONString(mg));
			WsOutbound ob = socketEntity.getServerbd().getWsOutbound();
			ob.writeTextMessage(buffer);
			ob.flush();
		}
	}

	@Override
	protected void onClose(int status) {
		InitServlet.getSocketList().remove(key);
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
	}
}
