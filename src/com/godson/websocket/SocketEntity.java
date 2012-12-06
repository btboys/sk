package com.godson.websocket;

import com.godson.websocket.inbound.ServerMessageWebSocket;

/**
 * 当前访客信息存储主体
 * @author east.com
 *
 */
public class SocketEntity {

	private ServerMessageWebSocket serverbd;
	private Visitor visitor;
	private String key;
	
	public ServerMessageWebSocket getServerbd() {
		return serverbd;
	}
	public void setServerbd(ServerMessageWebSocket serverbd) {
		this.serverbd = serverbd;
	}
	public Visitor getVisitor() {
		return visitor;
	}
	public void setVisitor(Visitor visitor) {
		this.visitor = visitor;
	}
	public String getKey() {
		return key;
	}
	public void setKey(String key) {
		this.key = key;
	}
}
