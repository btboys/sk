package com.godson.websocket;

import java.nio.CharBuffer;
import java.util.Date;

import net.sf.xsshtmlfilter.HTMLFilter;

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
		String cleanContent = new HTMLFilter().filter(content.toString());
		return cleanContent;
	}
	public Date getDate() {
		return date;
	}
	public Visitor getVisitor() {
		return visitor;
	}
	
	
}
