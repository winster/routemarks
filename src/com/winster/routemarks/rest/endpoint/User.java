package com.winster.routemarks.rest.endpoint;

import com.winster.routemarks.client.vo.AccountDetails;
import com.winster.routemarks.client.vo.GeocodeAddressComponents;
import com.winster.routemarks.client.vo.PreferenceData;
import com.winster.routemarks.constants.UserConstants;
import com.winster.routemarks.rest.helper.AccountHelper;
import com.winster.routemarks.rest.helper.ActivityHelper;
import com.winster.routemarks.rest.helper.LocationHelper;
import com.winster.routemarks.rest.helper.RequestHelper;

import java.io.IOException;
import java.util.List;
import java.util.logging.Logger;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.gson.Gson;

public class User extends HttpServlet{
	
	private static final long serialVersionUID = 1L;
	private static final Logger log = Logger.getLogger(User.class.getName());


	/**
	 * All GET requests are served here
	 */
	public void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		try{
			String path = req.getPathInfo();
			if(path.endsWith("card")) {
				String loginStatus = (String) req.getSession().getAttribute(UserConstants.ATTR_USER_LOGIN_STATUS.getValue());
				if(loginStatus!=null) {
					AccountDetails accountDetails = (AccountDetails)RequestHelper.getSessionAttribute(req, 
															UserConstants.ATTR_ACCOUNT_DETAILS.getValue());
					String id = (String)RequestHelper.getSessionAttribute(req, UserConstants.ATTR_ID.getValue());
					ActivityHelper.setRecentActivity(id, accountDetails);
					AccountHelper.setRecentCriteria(id, accountDetails);
					returnUserDetails(accountDetails, resp);
				}
			} else if(path.endsWith("preferences")) {
				List<PreferenceData> preferences = AccountHelper.findUserPreferences(req);
				if(preferences!=null) {
					returnUserPreferenceDetails(preferences, resp);
				}
			} else if(path.endsWith("invalidate")) {
				req.getSession().setMaxInactiveInterval(1);
			} else {
				/*UserService userService = UserServiceFactory.getUserService();
		        com.google.appengine.api.users.User user = userService.getCurrentUser();
		
		        if (user != null) {
		            resp.setContentType("text/plain");
		            resp.getWriter().println("Hello, " + user.getNickname());
		        } else {
		            resp.sendRedirect(userService.createLoginURL(req.getRequestURI()));
		        }*/
			}		
		} catch(Exception e) {
			log.severe(e.getMessage());
			throw new ServletException(e);
		}

	}

	/**
	 * All POST requests are served here
	 */
	public void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		String path = req.getPathInfo();
		if(path.endsWith("anonymous")) {
			GeocodeAddressComponents[] addressComponents = LocationHelper.retriveGeoAddressFromRequest(req);
			AccountHelper.createAnonymousUser(req, resp, addressComponents);
		}
	}
	
	/**
	 * Writes AccountDetails to response object
	 * @param req
	 * @param resp
	 * @throws IOException
	 */
	private void returnUserDetails(AccountDetails accountDetails, HttpServletResponse resp) throws IOException {
		String json = new Gson().toJson(accountDetails);
		resp.setContentType("application/json");  // Set content type of the response so that jQuery knows what it can expect.
		resp.setCharacterEncoding("UTF-8"); // You want world domination, huh?
	    resp.getWriter().write(json);
	}
	
	/**
	 * Writes Preference to response object
	 * @param req
	 * @param resp
	 * @throws IOException
	 */
	private void returnUserPreferenceDetails(List<PreferenceData> preferences, HttpServletResponse resp) throws IOException {
		String json = new Gson().toJson(preferences);
		resp.setContentType("application/json");  // Set content type of the response so that jQuery knows what it can expect.
		resp.setCharacterEncoding("UTF-8"); // You want world domination, huh?
	    resp.getWriter().write(json);
	}

}