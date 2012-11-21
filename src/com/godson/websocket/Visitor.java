package com.godson.websocket;

/**
 * 访客信息
 * @author east.com
 *
 */
public class Visitor {

	private String nickname;
	private String ip;
	private String city;
	private String province;
	private String country;
	private String isp;
	
	
	public Visitor(String nickname, String ip, String city, String province, String country,String isp) {
		this.nickname = nickname;
		this.ip = ip;
		this.city = city;
		this.province = province;
		this.country = country;
		this.isp = isp;
	}
	
	public String getNickname() {
		return nickname;
	}
	public String getIp() {
		return ip;
	}
	public String getCity() {
		return city;
	}
	public String getProvince() {
		return province;
	}
	public String getCountry() {
		return country;
	}
	public String getIsp() {
		return isp;
	}
}
