package com.winster.routemarks.listener;

import com.winster.routemarks.constants.ApplicationConstants;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpSessionEvent;
import javax.servlet.http.HttpSessionListener;

public class TSRSessionListener implements HttpSessionListener {

	@Override
	public void sessionCreated(HttpSessionEvent arg0) {
		ServletContext context = arg0.getSession().getServletContext();
		if(context.getAttribute(ApplicationConstants.COUNT_ACTIVE_SESSIONS.getValue())==null) {
			context.setAttribute(ApplicationConstants.COUNT_ACTIVE_SESSIONS.getValue(), 1);
		} else {
			long count = Long.parseLong(context.getAttribute(ApplicationConstants.COUNT_ACTIVE_SESSIONS.getValue()).toString());
			count++;
			context.setAttribute(ApplicationConstants.COUNT_ACTIVE_SESSIONS.getValue(), count);
		}
		

	}

	@Override
	public void sessionDestroyed(HttpSessionEvent arg0) {
		ServletContext context = arg0.getSession().getServletContext();
		long count = Long.parseLong(context.getAttribute(ApplicationConstants.COUNT_ACTIVE_SESSIONS.getValue()).toString());
		count--;
		context.setAttribute(ApplicationConstants.COUNT_ACTIVE_SESSIONS.getValue(), count);		
	}

}
