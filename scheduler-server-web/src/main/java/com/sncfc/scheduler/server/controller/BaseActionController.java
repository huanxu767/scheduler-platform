package com.sncfc.scheduler.server.controller;

import com.sncfc.scheduler.server.pojo.BaseResultBean;
import com.sncfc.scheduler.server.util.JSONUtil;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;


/**
 * 
 * 项目通用的Controller基类，留作扩展
 * 
 */
public class BaseActionController{

	protected void doResponse(HttpServletResponse response, String jsonStr) {
		// CLogger.debugLog(JsonResponseUtil.class.getName(), "start write",
		// jsonStr);
		try {
			response.getWriter().print(jsonStr);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void outResult(HttpServletResponse response, JSONObject jsonObj) {
		// response.setCharacterEncoding("UTF-8");
		response.setContentType("text/plain;charset=UTF-8");
		PrintWriter out = null;
		try {
			out = response.getWriter();
		} catch (Exception e) {
			e.printStackTrace();
		}
		out.print(jsonObj.toString());
		out.flush();
		out.close();
	}

	public void outResult(HttpServletResponse response, JSONArray jsonObj) {
		// response.setCharacterEncoding("UTF-8");
		response.setContentType("text/plain;charset=UTF-8");
		PrintWriter out = null;
		try {
			out = response.getWriter();
		} catch (Exception e) {
			e.printStackTrace();
		}
		out.print(jsonObj.toString());
		out.flush();
		out.close();
	}

	public void outResult(HttpServletResponse response, BaseResultBean bean) {
		// response.setCharacterEncoding("UTF-8");
		response.setContentType("text/plain;charset=UTF-8");
		PrintWriter out = null;
		try {
			out = response.getWriter();
		} catch (Exception e) {
			e.printStackTrace();
		}
		String str = JSONUtil.toJSONString(bean);
		out.print(str);
		out.flush();
		out.close();
	}
	public void outResult(HttpServletResponse response, String jsonString) {
		// response.setCharacterEncoding("UTF-8");
		response.setContentType("text/plain;charset=UTF-8");
		PrintWriter out = null;
		try {
			out = response.getWriter();
		} catch (Exception e) {
			e.printStackTrace();
		}
		out.print(jsonString);
		out.flush();
		out.close();
	}
	public void outResult(HttpServletResponse response, Map map) {
		response.setContentType("text/plain;charset=UTF-8");
//		response.setCharacterEncoding("UTF-8");
		PrintWriter out = null;
		try {
			out = response.getWriter();
		} catch (Exception e) {
			e.printStackTrace();
		}
		out.print(JSONUtil.toJSONString(map));
		out.flush();
		out.close();
	}
	public void outResult(HttpServletResponse response, List list) {
		response.setContentType("text/plain;charset=UTF-8");
//		response.setCharacterEncoding("UTF-8");
		PrintWriter out = null;
		try {
			out = response.getWriter();
		} catch (Exception e) {
			e.printStackTrace();
		}
		out.print(JSONUtil.toJSONString(list));
		out.flush();
		out.close();
	}
	/** 
	 * 以JSON格式输出 
	 * @param response 
	 */  
	protected void responseWithJson(HttpServletResponse response,
	        Object responseObject) {  
	    response.setCharacterEncoding("UTF-8");  
	    response.setContentType("application/json; charset=utf-8");  
	    PrintWriter out = null;  
	    try {  
	        out = response.getWriter();  
	        out.append(JSONUtil.toJSONString(responseObject));  
	    } catch (IOException e) {  
	        e.printStackTrace();  
	    } finally {  
	        if (out != null) {  
	            out.close();  
	        }  
	    }  
	}  
	  /** 
	 * 从request中获得参数Map，并返回可读的Map 
	 * 重复的不可读
	 * @param request 
	 * @return 
	 */  
	@SuppressWarnings("unchecked")  
	public  Map<String,String> getParameterMap(HttpServletRequest request) {
	    // 参数Map  
	    Map properties = request.getParameterMap();  
	    // 返回值Map  
	    Map returnMap = new HashMap();  
	    Iterator entries = properties.entrySet().iterator();  
	    Map.Entry entry;  
	    String name = "";  
	    String value = "";  
	    while (entries.hasNext()) {  
	        entry = (Map.Entry) entries.next();  
	        name = (String) entry.getKey();  
	        Object valueObj = entry.getValue();  
	        if(null == valueObj){  
	            value = "";  
	        }else if(valueObj instanceof String[]){  
	            String[] values = (String[])valueObj;  
	            for(int i=0;i<values.length;i++){  
	                value = values[i] + ",";  
	            }  
	            value = value.substring(0, value.length()-1);  
	        }else{  
	            value = valueObj.toString();  
	        }  
	        returnMap.put(name, value);  
	    }  
	    return returnMap;  
	}  
}
