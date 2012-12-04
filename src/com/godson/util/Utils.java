package com.godson.util;

import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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
		String nickname = request.getParameter("nk");
		
		vt = new Visitor(nickname, ip);
		HttpGet httpGet = new HttpGet("http://ip.taobao.com/service/getIpInfo.php?ip=" + ip);
		try {
			HttpResponse response = new DefaultHttpClient().execute(httpGet);
			if (response.getStatusLine().getStatusCode() == 200) {
				String result = EntityUtils.toString(response.getEntity());
				JSONObject obj = JSONObject.parseObject(result);
				if (obj.getIntValue("code") == 0) {
					JSONObject data = obj.getJSONObject("data");
					String city = data.getString("city");
					String province = data.getString("region");
					String country = data.getString("country");
					String isp = data.getString("isp");
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

	public static String replseJs(String html) {
		try {
			Pattern replse1 = Pattern.compile("<[\\s]*?script[^>]*?>[\\s\\S]*?<[\\s]*?\\/[\\s]*?script[\\s]*?>", Pattern.CASE_INSENSITIVE);
			Pattern replse2 = Pattern.compile("<[\\s]*?link[^>]*?>[\\s\\S]*?<[\\s]*?\\/[\\s]*?link[\\s]*?>", Pattern.CASE_INSENSITIVE);
			Pattern replse3 = Pattern.compile("<[\\s]*?style[^>]*?>[\\s\\S]*?<[\\s]*?\\/[\\s]*?style[\\s]*?>", Pattern.CASE_INSENSITIVE);
			Pattern replse4 = Pattern.compile("<[\\s]*?iframe[^>]*?>[\\s\\S]*?<[\\s]*?\\/[\\s]*?iframe[\\s]*?>", Pattern.CASE_INSENSITIVE);
			Pattern replse5 = Pattern.compile("<(script|link|style|iframe)(.|\\n)*<\\/\\>\\s*");
			Pattern replse6 = Pattern.compile("javascript");
			Pattern replse7 = Pattern.compile("expression\\([^)]+\\)");
			Pattern replse8 = Pattern.compile("<[\\s]*?link[^>]*?>", Pattern.CASE_INSENSITIVE);
			Pattern replse9 = Pattern.compile("<[\\s]*?iframe[^>]*?>", Pattern.CASE_INSENSITIVE);

			Matcher matcher1 = replse1.matcher(html);
			html = matcher1.replaceAll("");

			Matcher matcher2 = replse2.matcher(html);
			html = matcher2.replaceAll("");

			Matcher matcher3 = replse3.matcher(html);
			html = matcher3.replaceAll("");

			Matcher matcher4 = replse4.matcher(html);
			html = matcher4.replaceAll("");

			Matcher matcher5 = replse5.matcher(html);
			html = matcher5.replaceAll("");

			Matcher matcher6 = replse6.matcher(html);
			html = matcher6.replaceAll(" ");

			Matcher matcher7 = replse7.matcher(html);
			html = matcher7.replaceAll("");

			Matcher matcher8 = replse8.matcher(html);
			html = matcher8.replaceAll(" ");

			Matcher matcher9 = replse9.matcher(html);
			html = matcher9.replaceAll(" ");

		} catch (Exception e) {
			e.printStackTrace();
		}
		return html;
	}

	private Utils() {
	}
}
