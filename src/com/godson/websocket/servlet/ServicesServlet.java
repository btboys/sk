package com.godson.websocket.servlet;

import javax.servlet.http.HttpServletRequest;

import org.apache.catalina.websocket.StreamInbound;
import org.apache.catalina.websocket.WebSocketServlet;

import com.godson.util.Utils;
import com.godson.websocket.Visitor;
import com.godson.websocket.inbound.ServerMessageInbound;

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

	@Override
	protected StreamInbound createWebSocketInbound(String arg0,HttpServletRequest req) {
        Visitor vt = Utils.getVistorInfo(req);
        InitServlet.getNicknames().put(req.getParameter("key"), vt.getNickname());
		return new ServerMessageInbound(vt,req.getParameter("key"));
	}
}
