package com.winster.routemarks.rest.endpoint;

import com.winster.routemarks.client.vo.AccountDetails;
import com.winster.routemarks.constants.ApplicationConstants;
import com.winster.routemarks.constants.UserConstants;
import com.winster.routemarks.rest.google.DashboardException;
import com.winster.routemarks.rest.google.ServiceUtils;
import com.winster.routemarks.rest.helper.AccountHelper;
import com.winster.routemarks.rest.helper.RequestHelper;

import java.io.IOException;
import java.util.logging.Logger;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.api.client.auth.oauth2.AuthorizationCodeFlow;
import com.google.api.client.auth.oauth2.AuthorizationCodeResponseUrl;
import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.extensions.appengine.auth.oauth2.AbstractAppEngineAuthorizationCodeCallbackServlet;
import com.google.api.services.plus.Plus;
import com.google.api.services.plus.model.Person;

/**
 * Holds information used in the authorization flow, such as which URL to redirect
 * to on success/failure.
 *
 * @author Winster
 */
public class GoogleOAuth2Callback extends AbstractAppEngineAuthorizationCodeCallbackServlet {

	private static final long serialVersionUID = 1L;
	private static final Logger log = Logger.getLogger(GoogleAuthServlet.class.getName());
	
  	@Override
  	protected void onSuccess(HttpServletRequest req, HttpServletResponse resp, Credential credential)
		  throws ServletException, IOException {
  		try {
			fetchProfile(req,resp, getUserId(req));
		} catch (ServletException e) {
			log.severe(e.getMessage());
			throw new ServletException(e);
		}
		resp.sendRedirect(ApplicationConstants.TSR_ACCOUNT_URL.getValue());	  	
  	}

  	@Override
  	protected void onError(HttpServletRequest req, HttpServletResponse resp, AuthorizationCodeResponseUrl errorResponse)
    		  throws IOException {
  		resp.sendRedirect(ApplicationConstants.TSR_ACCOUNT_URL.getValue());
  	}

  	@Override
  	protected String getRedirectUri(HttpServletRequest req) {
	  	return ServiceUtils.getRedirectUri(req);
  	}
  	
	/**
	 * Check GoogleAuthServlet getUserId(req) method
	 */
	@Override
	protected String getUserId(HttpServletRequest req) throws ServletException, IOException {
		return RequestHelper.getSessionAttributeString(req, UserConstants.ATTR_GOOGLE_USER_ID.getValue());		
	}

  	@Override
  	protected AuthorizationCodeFlow initializeFlow() throws IOException {
  		return ServiceUtils.newFlow();
  	}
  
  
	/**
	 * Get profile info using client library and store info in session
	 * @param request
	 * @param response
	 * @param userId
	 * @throws IOException
	 * @throws ServletException
	 */
	private void fetchProfile(HttpServletRequest request, HttpServletResponse response, String userId) throws IOException, ServletException {
		String message = null;
		try {
			Plus plus = ServiceUtils.loadPlusClient(userId);
			Person profile = getProfile(plus);
	
			AccountDetails userDetails = AccountHelper.convertGoogleResponseToUserDetails(profile);
	    	RequestHelper.setSessionAttribute(request, UserConstants.ATTR_ACCOUNT_DETAILS.getValue(), userDetails);
	    	AccountHelper.updateUser(request, response, userDetails);
	    	RequestHelper.setSessionAttribute(request, UserConstants.ATTR_USER_LOGIN_STATUS.getValue(), UserConstants.LOGIN_STATUS_GOOGLE_LOGGED_IN.getValue());
			
		} catch (DashboardException ex) {
			if (ex.getStatusCode() == HttpServletResponse.SC_UNAUTHORIZED) {
				ServiceUtils.deleteCredentials(userId);
				message = "There was a problem running the query with your credentials. Refresh, please!";
			} else {
				message = "Encountered an exception (" + ex.getStatusCode() + "): " + ex.getMessage();
				log.severe(message);
			}
			throw new ServletException(ex.getMessage()+" - "+message);
		}
	}
	
	/** 
	 * Get the profile for the authenticated user. 
	 * */
	public static Person getProfile(Plus plus) throws IOException {
		log.info("Get my Google+ profile");
	    Person profile = plus.people().get("me").execute();
	    log.info(profile.getDisplayName());
	    return profile;
	}
}