package com.godson.websocket.servlet;

import javax.servlet.http.HttpServletRequest;

import org.apache.catalina.websocket.StreamInbound;
import org.apache.catalina.websocket.WebSocketServlet;

import com.godson.websocket.inbound.OnlineMessageInbound;

/**
 * 刷新在线用户
 * @author east.com
 *
 */
public class OnlineServlet extends WebSocketServlet {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@Override
	protected StreamInbound createWebSocketInbound(String arg0, HttpServletRequest request) {
		return new OnlineMessageInbound(request.getParameter("key"));
	}

}
