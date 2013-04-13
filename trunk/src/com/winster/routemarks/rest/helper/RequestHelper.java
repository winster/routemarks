package com.winster.routemarks.rest.helper;

import javax.servlet.http.HttpServletRequest;

public class RequestHelper {
	
	public static String getRequestAttribute(HttpServletRequest req, String name) {
		String value = null;
		if(req.getAttribute(name)!=null) {
			value = req.getAttribute(name).toString();
		}
		return value;
	}
	
	public static String getRequestQueryParam(HttpServletRequest req, String name) {
		String value = null;
		if(req.getParameter(name)!=null) {
			value = req.getParameter(name).toString();
		}
		return value;
	}
	
	public static String getSessionAttributeString(HttpServletRequest req, String name) {
		String value = null;
		if(req.getSession().getAttribute(name)!=null) {
			value = req.getSession().getAttribute(name).toString();
		}
		return value;
	}
	
	public static Object getSessionAttribute(HttpServletRequest req, String name) {
		Object value = null;
		if(req.getSession().getAttribute(name)!=null) {
			value = req.getSession().getAttribute(name);
		}
		return value;
	}
	
	public static void setSessionAttribute(HttpServletRequest req, String name, Object value) {
		req.getSession().setAttribute(name, value);
	}

}
