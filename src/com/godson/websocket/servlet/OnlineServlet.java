package com.godson.websocket.servlet;

import javax.servlet.http.HttpServletRequest;

import org.eclipse.jetty.websocket.WebSocket;
import org.eclipse.jetty.websocket.WebSocketServlet;

import com.godson.websocket.inbound.OnlineMessageWebSocket;

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

	public WebSocket doWebSocketConnect(HttpServletRequest request, String arg1) {
		return new OnlineMessageWebSocket(request.getParameter("key"));
	}

}
