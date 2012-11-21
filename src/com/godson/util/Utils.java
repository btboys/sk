package com.godson.util;

import java.io.IOException;

import javax.servlet.http.HttpServletRequest;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;

import com.alibaba.fastjson.JSONObject;
import com.godson.websocket.Visitor;

/**
 * 工具类
 * 
 * @author east.com
 * 
 */
public final class Utils {
	/**
	 * 获取客户端IP
	 * 
	 * @param request
	 * @return
	 */
	public final static String getIpAddr(HttpServletRequest request) {
		String ip = request.getHeader("x-forwarded-for");
		if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
			ip = request.getHeader("Proxy-Client-IP");
		}
		if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
			ip = request.getHeader("WL-Proxy-Client-IP");
		}
		if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
			ip = request.getRemoteAddr();
		}

		if (ip.split(",").length > 1) {
			ip = ip.split(",")[0];
		}

		return ip;
	}

	/**
	 * 获取访客信息
	 * 
	 * @param request
	 * @return
	 */
	public final static Visitor getVistorInfo(HttpServletRequest request) {
		String ip = getIpAddr(request);
		Visitor vt = null;
		HttpGet httpGet = new HttpGet("http://ip.taobao.com/service/getIpInfo.php?ip=" + ip);
		try {
			HttpResponse response = new DefaultHttpClient().execute(httpGet);
			if (response.getStatusLine().getStatusCode() == 200) {
				String result = EntityUtils.toString(response.getEntity());
				JSONObject obj = JSONObject.parseObject(result);
				if (obj.getIntValue("code") == 0) {
					String nickname = request.getParameter("nk");
					nickname = new String(nickname.getBytes("8859_1"),"utf-8");
					String city = request.getParameter("city");
					String province = request.getParameter("region");
					String country = request.getParameter("country");
					String isp = request.getParameter("isp");
					vt = new Visitor(nickname, ip, city, province, country, isp);
				}
			}
		} catch (ClientProtocolException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return vt;
	}

	private Utils() {
	}
}
