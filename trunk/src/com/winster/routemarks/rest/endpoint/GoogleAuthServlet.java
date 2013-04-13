package com.winster.routemarks.rest.endpoint;

import com.winster.routemarks.constants.UserConstants;
import com.winster.routemarks.rest.google.ServiceUtils;
import com.winster.routemarks.rest.helper.RequestHelper;

import java.io.IOException;
import java.util.UUID;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;

import com.google.api.client.auth.oauth2.AuthorizationCodeFlow;
import com.google.api.client.extensions.appengine.auth.oauth2.AbstractAppEngineAuthorizationCodeServlet;

/**
 * @author Winster
 *
 */
public class GoogleAuthServlet extends AbstractAppEngineAuthorizationCodeServlet {

	private static final long serialVersionUID = 1L;
	
	/**
	 * A funky implementation by library. It somehome expects a userId before authentication. :P
	 */
	@Override
	protected String getUserId(HttpServletRequest req) throws ServletException, IOException {
		String userId =  RequestHelper.getSessionAttributeString(req, UserConstants.ATTR_GOOGLE_USER_ID.getValue());
		if(userId ==null) {
			userId =  UUID.randomUUID().toString();
			req.getSession().setAttribute(UserConstants.ATTR_GOOGLE_USER_ID.getValue(), userId);
		}
		return userId;
	}
	  
	@Override
	protected AuthorizationCodeFlow initializeFlow() throws IOException {
		return ServiceUtils.newFlow();
	}

	@Override
	protected String getRedirectUri(HttpServletRequest req) {
		return ServiceUtils.getRedirectUri(req);
	}
}