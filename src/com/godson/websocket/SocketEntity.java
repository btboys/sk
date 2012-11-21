package com.godson.websocket;

import com.godson.websocket.inbound.OnlineMessageInbound;
import com.godson.websocket.inbound.ServerMessageInbound;

/**
 * 当前访客信息存储主体
 * @author east.com
 *
 */
public class SocketEntity {

	private ServerMessageInbound serverbd;
	private OnlineMessageInbound onlinebd;
	private Visitor visitor;
	
	public ServerMessageInbound getServerbd() {
		return serverbd;
	}
	public void setServerbd(ServerMessageInbound serverbd) {
		this.serverbd = serverbd;
	}
	public OnlineMessageInbound getOnlinebd() {
		return onlinebd;
	}
	public void setOnlinebd(OnlineMessageInbound onlinebd) {
		this.onlinebd = onlinebd;
	}
	public Visitor getVisitor() {
		return visitor;
	}
	public void setVisitor(Visitor visitor) {
		this.visitor = visitor;
	}
}
