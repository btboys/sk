package com.godson.websocket;

import com.godson.websocket.inbound.OnlineMessageWebSocket;
import com.godson.websocket.inbound.ServerMessageWebSocket;

/**
 * 当前访客信息存储主体
 * @author east.com
 *
 */
public class SocketEntity {

	private ServerMessageWebSocket serverbd;
	private OnlineMessageWebSocket onlinebd;
	private Visitor visitor;
	
	public ServerMessageWebSocket getServerbd() {
		return serverbd;
	}
	public void setServerbd(ServerMessageWebSocket serverbd) {
		this.serverbd = serverbd;
	}
	public OnlineMessageWebSocket getOnlinebd() {
		return onlinebd;
	}
	public void setOnlinebd(OnlineMessageWebSocket onlinebd) {
		this.onlinebd = onlinebd;
	}
	public Visitor getVisitor() {
		return visitor;
	}
	public void setVisitor(Visitor visitor) {
		this.visitor = visitor;
	}
}
