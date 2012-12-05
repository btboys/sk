package com.godson.websocket;

import java.nio.CharBuffer;
import java.util.Date;

import com.godson.util.Utils;

/**
 * 消息体
 * @author east.com
 *
 */
public class Msg {

	private CharBuffer content;
	private Date date = new Date();
	private Visitor visitor;
	
	public Msg(CharBuffer content, Visitor visitor) {
		this.content = content;
		this.visitor = visitor;
	}
	
	public String getContent() {
		String cleanContent = Utils.replseJs(content.toString());
		cleanContent=cleanContent.replaceAll("<a href[^>]*>", "ok").replaceAll("</a>", "ok");
		return cleanContent;
	}
	public Date getDate() {
		return date;
	}
	public Visitor getVisitor() {
		return visitor;
	}
	public void setContent(CharBuffer content) {
		this.content = content;
	}
	
	public void setVisitor(Visitor visitor) {
		this.visitor = visitor;
	}
}
