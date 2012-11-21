package com.godson.websocket.servlet;

import java.util.HashMap;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;

import com.godson.websocket.SocketEntity;

/**
 * 初始化全局存储对象
 * @author east.com
 *
 */
public class InitServlet extends HttpServlet {

	private static final long serialVersionUID = -3163557381361759907L;  
    
    private static HashMap<String,SocketEntity> socketEntityList;  
      
    public void init(ServletConfig config) throws ServletException {    
        InitServlet.socketEntityList = new HashMap<String,SocketEntity>();   
        super.init(config);    
    }    
        
    public static HashMap<String,SocketEntity> getSocketList() {    
        return InitServlet.socketEntityList;    
    }   
}
