package com.godson.websocket.servlet;

import javax.servlet.http.HttpServletRequest;

import org.eclipse.jetty.websocket.WebSocket;
import org.eclipse.jetty.websocket.WebSocketServlet;

import com.godson.util.Utils;
import com.godson.websocket.Visitor;
import com.godson.websocket.inbound.ServerMessageWebSocket;

/**
 * 消息服务
 * @author east.com
 *
 */
public class ServicesServlet extends WebSocketServlet {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public WebSocket doWebSocketConnect(HttpServletRequest req, String arg1) {
		Visitor vt = Utils.getVistorInfo(req);
		return new ServerMessageWebSocket(vt,req.getParameter("key"));
	}
}
