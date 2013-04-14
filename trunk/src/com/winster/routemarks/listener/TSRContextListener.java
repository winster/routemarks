package com.winster.routemarks.listener;

import com.winster.routemarks.data.entity.Mark;
import com.winster.routemarks.data.entity.User;
import com.winster.routemarks.data.entity.UserActivity;
import com.winster.routemarks.data.entity.UserActivityMaster;
import com.winster.routemarks.data.entity.UserCriteria;
import com.winster.routemarks.data.entity.UserPreference;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import com.googlecode.objectify.ObjectifyService;

public class TSRContextListener implements ServletContextListener {
	
	public void contextInitialized(ServletContextEvent event) {
		// This will be invoked as part of a warmup request, or the first user
		// request if no warmup request was invoked.
		ObjectifyService.register(User.class);
		ObjectifyService.register(UserActivityMaster.class);
		ObjectifyService.register(UserCriteria.class);
		ObjectifyService.register(UserPreference.class);
		ObjectifyService.register(UserActivity.class);
		ObjectifyService.register(Mark.class);
	}
	
	public void contextDestroyed(ServletContextEvent event) {
		// App Engine does not currently invoke this method.
	}
}