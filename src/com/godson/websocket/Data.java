package com.godson.websocket;

public class Data {

	private Boolean status = true;
	private String type = "msg";
	private Object msg;
	
	public Data() {}
	
	public Data(Object msg) {
		this.msg = msg;
	}

	public Boolean getStatus() {
		return status;
	}
	public void setStatus(Boolean status) {
		this.status = status;
	}
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	public Object getMsg() {
		return msg;
	}
	public void setMsg(Object msg) {
		this.msg = msg;
	}
}
