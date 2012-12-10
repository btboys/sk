package com.godson.websocket;

import com.godson.websocket.inbound.ServerMessageInbound;

/**
 * 当前访客信息存储主体
 * @author east.com
 *
 */
public class SocketEntity {

	private ServerMessageInbound serverbd;
	private Visitor visitor;
	
	public ServerMessageInbound getServerbd() {
		return serverbd;
	}
	public void setServerbd(ServerMessageInbound serverbd) {
		this.serverbd = serverbd;
	}
	public Visitor getVisitor() {
		return visitor;
	}
	public void setVisitor(Visitor visitor) {
		this.visitor = visitor;
	}
}
