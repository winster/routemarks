package com.winster.routemarks.rest.endpoint;

import com.winster.routemarks.client.vo.AccountDetails;
import com.winster.routemarks.client.vo.PreferenceData;
import com.winster.routemarks.constants.ApplicationConstants;
import com.winster.routemarks.constants.UserConstants;
import com.winster.routemarks.constants.UserPreferenceConstants;
import com.winster.routemarks.data.entity.UserActivityMaster;
import com.winster.routemarks.data.helper.UserHelper;
import com.winster.routemarks.rest.helper.AccountHelper;
import com.winster.routemarks.rest.helper.RequestHelper;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.UUID;
import java.util.logging.Logger;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import oauth.signpost.OAuthConsumer;
import oauth.signpost.OAuthProvider;
import oauth.signpost.basic.DefaultOAuthConsumer;
import oauth.signpost.basic.DefaultOAuthProvider;

import com.google.appengine.api.urlfetch.HTTPMethod;
import com.google.appengine.api.urlfetch.HTTPRequest;
import com.google.appengine.api.urlfetch.HTTPResponse;
import com.google.appengine.api.urlfetch.URLFetchServiceFactory;
import com.restfb.DefaultFacebookClient;
import com.restfb.FacebookClient;

/**
 * Rest interface for handling all authentication requests
 * @author root
 *
 */
public class SocialAuth extends HttpServlet {

	private static final long serialVersionUID = 1L;
	private static final Logger log = Logger.getLogger(SocialAuth.class.getName());

	/**
	 * All GET requests are served here.
	 */
	public void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		String path = req.getPathInfo();
		if(path.endsWith("auth")) {
			String id = RequestHelper.getRequestQueryParam(req, "id");
			if(ApplicationConstants.ACCOUNT_TYPE_TWITTER.getValue().equals(id)) {
				requestTwitterAuth(req, resp);
			} else if(ApplicationConstants.ACCOUNT_TYPE_FACEBOOK.getValue().equals(id)) {
				requestFacebookAuth(req, resp);
			} /*Not applicable. Check GoogleAuthServlet 
			  else if(ApplicationConstants.ACCOUNT_TYPE_GOOGLE.getValue().equals(id)) {
				requestGoogleAuth(req, resp);
			}*/
		} else if(path.endsWith("callback")) {
			String app = RequestHelper.getRequestQueryParam(req, "app");
			if(ApplicationConstants.ACCOUNT_TYPE_TWITTER.getValue().equals(app)) {
				callbackTwitterAuth(req, resp);
			} else if(ApplicationConstants.ACCOUNT_TYPE_FACEBOOK.getValue().equals(app)) {
				callbackFacebookAuth(req, resp);
			} /*Not applicable. Check GoogleAuthServlet
			  else if(ApplicationConstants.ACCOUNT_TYPE_GOOGLE.getValue().equals(app)) {
				callbackGoogleAuth(req, resp);
			} */
		} 
	}
	
	/**
	 * All POST requests are served here.
	 */
	public void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		String path = req.getPathInfo();
		AccountDetails userDetails  = (AccountDetails) req.getSession().getAttribute(UserConstants.ATTR_ACCOUNT_DETAILS.getValue());
		if(path.endsWith("update") && userDetails!=null) {
			String id = (String)req.getSession().getAttribute(UserConstants.ATTR_ID.getValue());
			String toggleCheckbox = RequestHelper.getRequestQueryParam(req, "toggleCheckbox");
			//String messageBox = RequestHelper.getRequestQueryParam(req, "messageBox");
			PreferenceData preferenceData = new PreferenceData();
			if("on".equals(toggleCheckbox)) {
				UserActivityMaster userActivityMaster = UserHelper.updateUserActivityMaster(id, 
						ApplicationConstants.CHILD_ENTITY_TYPE_PREFERENCE.getValue(), 
						0, null);
				long count  = userActivityMaster.getTotalPreferenceCount();

				preferenceData.setType(UserPreferenceConstants.PREFERENCE_TYPE_SOCIAL_CONNECTED.getValue());
				preferenceData.setValue("1");
				AccountHelper.createUserPreference(id, count, preferenceData);
			} else {
				preferenceData.setType(UserPreferenceConstants.PREFERENCE_TYPE_SOCIAL_CONNECTED.getValue());
				preferenceData.setValue("0");
				AccountHelper.deleteUserPreference(id, preferenceData);
			}
			resp.sendRedirect(ApplicationConstants.TSR_ACCOUNT_URL.getValue());
		}
	}
	/**
	 * Handle request for Twitter authentication
	 * @param req
	 * @param resp
	 * @throws ServletException
	 */
	private void requestTwitterAuth(HttpServletRequest req, HttpServletResponse resp) throws ServletException{
		try {
			//For production, make test = false
			boolean test = false;
			if(test){
				RequestHelper.setSessionAttribute(req, UserConstants.ATTR_USER_LOGIN_STATUS.getValue(), UserConstants.LOGIN_STATUS_TWITTER_LOGGED_IN.getValue());
	        	AccountDetails userDetails = AccountHelper.createDummyAccountDetails(ApplicationConstants.ACCOUNT_TYPE_TWITTER.getValue());
	        	RequestHelper.setSessionAttribute(req, UserConstants.ATTR_ACCOUNT_DETAILS.getValue(), userDetails);
	        	AccountHelper.updateUser(req, resp, userDetails);
	        	resp.sendRedirect(ApplicationConstants.TSR_ACCOUNT_URL.getValue());
	        	return;
			}
			OAuthConsumer consumer = new DefaultOAuthConsumer(
					ApplicationConstants.TWITTER_CONSUMER_KEY.getValue(),
					ApplicationConstants.TWITTER_CONSUMER_SECRET.getValue());
			OAuthProvider provider = new DefaultOAuthProvider(
					ApplicationConstants.TWITTER_URI_REQUEST_TOKEN.getValue(),
					ApplicationConstants.TWITTER_URI_ACCESS_TOKEN.getValue(),
					ApplicationConstants.TWITTER_URI_AUTHORIZE.getValue());
				
			String authUrl = provider.retrieveRequestToken(consumer, ApplicationConstants.TWITTER_CALLBACK_URL.getValue());
			RequestHelper.setSessionAttribute(req, UserConstants.ATTR_TWITTER_OAUTH_TOKEN.getValue(), consumer.getToken());
			RequestHelper.setSessionAttribute(req, UserConstants.ATTR_TWITTER_OAUTH_TOKEN_SECRET.getValue(), consumer.getTokenSecret());
			resp.sendRedirect(authUrl);				
		} catch (Exception e) {
			log.severe(e.getMessage());
			throw new ServletException(e);
		}
	}
	
	/**
	 * Handle callback from Twitter redirect after authentication
	 * @param req
	 * @param resp
	 * @throws ServletException
	 */
	private void callbackTwitterAuth(HttpServletRequest req, HttpServletResponse resp) throws ServletException{
		String oauth_verifier = req.getParameter(UserConstants.ATTR_TWITTER_OAUTH_VERIFIER.getValue());
		try {
			if(oauth_verifier!=null) {
				String token = (String) req.getSession().getAttribute(UserConstants.ATTR_TWITTER_OAUTH_TOKEN.getValue());
				String tokenSecret = (String) req.getSession().getAttribute(UserConstants.ATTR_TWITTER_OAUTH_TOKEN_SECRET.getValue());
				OAuthConsumer consumer = new DefaultOAuthConsumer(
						ApplicationConstants.TWITTER_CONSUMER_KEY.getValue(),
						ApplicationConstants.TWITTER_CONSUMER_SECRET.getValue());
				OAuthProvider provider = new DefaultOAuthProvider(
						ApplicationConstants.TWITTER_URI_REQUEST_TOKEN.getValue(),
						ApplicationConstants.TWITTER_URI_ACCESS_TOKEN.getValue(),
						ApplicationConstants.TWITTER_URI_AUTHORIZE.getValue());
				consumer.setTokenWithSecret(token, tokenSecret);
			
				provider.setOAuth10a(true);
				provider.retrieveAccessToken(consumer, oauth_verifier);
				RequestHelper.setSessionAttribute(req, UserConstants.ATTR_USER_LOGIN_STATUS.getValue(), UserConstants.LOGIN_STATUS_TWITTER_LOGGED_IN.getValue());
				RequestHelper.setSessionAttribute(req, UserConstants.ATTR_TWITTER_OAUTH_TOKEN.getValue(), consumer.getToken());
				RequestHelper.setSessionAttribute(req, UserConstants.ATTR_TWITTER_OAUTH_TOKEN_SECRET.getValue(), consumer.getTokenSecret());
				
				AccountDetails userDetails = AccountHelper.convertTwitterResponseToUserDetails(provider.getResponseParameters());
				RequestHelper.setSessionAttribute(req, UserConstants.ATTR_ACCOUNT_DETAILS.getValue(), userDetails);
				AccountHelper.updateUser(req, resp, userDetails);
			}			
			resp.sendRedirect(ApplicationConstants.TSR_ACCOUNT_URL.getValue());			
		} catch(Exception e){
			log.severe(e.getMessage());
			throw new ServletException(e);
		}
	}
	
	/**
	 * Handle request for Facebook authentication
	 * @param req
	 * @param resp
	 * @throws ServletException
	 */
	private void requestFacebookAuth(HttpServletRequest req, HttpServletResponse resp) throws ServletException{
		try {
			//For production, make test = false
			boolean test = false;
			if(test){
				RequestHelper.setSessionAttribute(req, UserConstants.ATTR_USER_LOGIN_STATUS.getValue(), UserConstants.LOGIN_STATUS_FACEBOOK_LOGGED_IN.getValue());
	        	AccountDetails userDetails = AccountHelper.createDummyAccountDetails(ApplicationConstants.ACCOUNT_TYPE_FACEBOOK.getValue());
	        	RequestHelper.setSessionAttribute(req, UserConstants.ATTR_ACCOUNT_DETAILS.getValue(), userDetails);
	        	AccountHelper.updateUser(req, resp, userDetails);
	        	System.out.println("");
	        	resp.sendRedirect(ApplicationConstants.TSR_ACCOUNT_URL.getValue());
	        	return;
			}
			String state = UUID.randomUUID().toString();
			RequestHelper.setSessionAttribute(req, UserConstants.ATTR_FACEBOOK_STATE.getValue(), state);
			String dialogUrl = "https://www.facebook.com/dialog/oauth?client_id="+ApplicationConstants.FACEBOOK_APP_KEY.getValue() +
					"&redirect_uri="+URLEncoder.encode(ApplicationConstants.FACEBOOK_CALLBACK_URL.getValue(), "UTF-8")+
					"&state="+state+"&scope=publish_actions";
			resp.sendRedirect(dialogUrl);			
		} catch (Exception e) {
			log.severe(e.getMessage());
			throw new ServletException(e);
		}	
	}
	
	/**
	 * Handle callback from Facebook after authentication
	 * @param req
	 * @param resp
	 * @throws ServletException
	 */
	private void callbackFacebookAuth(HttpServletRequest req, HttpServletResponse resp) throws ServletException{
		try{
			String code = RequestHelper.getRequestQueryParam(req, "code");
			boolean noLuck  = false;
			if(code!=null) {
				String sessionState = RequestHelper.getSessionAttributeString(req, UserConstants.ATTR_FACEBOOK_STATE.getValue());
				String requestState = RequestHelper.getRequestQueryParam(req, "state");
				if(sessionState!=null && sessionState.equals(requestState)) {
					final HTTPRequest accessTokenRequest = new HTTPRequest(new URL("https://graph.facebook.com/oauth/access_token?" +
							"client_id="+ApplicationConstants.FACEBOOK_APP_KEY.getValue()+
							"&redirect_uri="+URLEncoder.encode(ApplicationConstants.FACEBOOK_CALLBACK_URL.getValue(), "UTF-8")+
							"&client_secret="+ApplicationConstants.FACEBOOK_APP_SECRET.getValue()+
							"&code="+code), HTTPMethod.GET);
					HTTPResponse response = URLFetchServiceFactory.getURLFetchService().fetch(accessTokenRequest);
					int respCode = response.getResponseCode();
		            if (respCode == HttpURLConnection.HTTP_OK) {
		            	String content = new String(response.getContent());
		            	String accessToken = content.substring(13,content.indexOf("&"));
		            	RequestHelper.setSessionAttribute(req, UserConstants.ATTR_FACEBOOK_ACCESSTOKEN.getValue(), accessToken);
		            	RequestHelper.setSessionAttribute(req, UserConstants.ATTR_USER_LOGIN_STATUS.getValue(), UserConstants.LOGIN_STATUS_FACEBOOK_LOGGED_IN.getValue());
		            	
		            	FacebookClient facebookClient = new DefaultFacebookClient(accessToken);
		            	com.restfb.types.User user = facebookClient.fetchObject("me", com.restfb.types.User.class);
		            	AccountDetails userDetails = AccountHelper.convertFacebookResponseToUserDetails(user);
		            	RequestHelper.setSessionAttribute(req, UserConstants.ATTR_ACCOUNT_DETAILS.getValue(), userDetails);
		            	AccountHelper.updateUser(req, resp, userDetails);
					} else {
		            	noLuck = true;	
		            }						
				} else {
					noLuck = true;	
				}
			} else {
				noLuck = true;
			}
			if(noLuck) {
				log.warning("User did not approve");
			}
			resp.sendRedirect(ApplicationConstants.TSR_ACCOUNT_URL.getValue());
		}catch(Exception e){
			log.severe(e.getMessage());
			throw new ServletException(e);
		}
	}
	
	/**
	 * Not used. Check GoogleAuthServlet
	 * Handle request for Google authentication
	 * @param req
	 * @param resp
	 * @throws IOException 
	 */
	/*@Deprecated
	private void requestGoogleAuth(HttpServletRequest req, HttpServletResponse resp) throws ServletException {
		try{
			String state = UUID.randomUUID().toString();
			RequestHelper.setSessionAttribute(req, UserConstants.ATTR_GOOGLE_STATE.getValue(), state);
			String authUrl = "https://accounts.google.com/o/oauth2/auth?" +
					"response_type=code" +
					"&client_id=284218108178.apps.googleusercontent.com" +
					"&redirect_uri="+URLEncoder.encode(ApplicationConstants.GOOGLE_CALLBACK_URL.getValue(), "UTF-8")+
					"&scope="+URLEncoder.encode("https://www.googleapis.com/auth/userinfo.profile"+" "+
								"https://www.googleapis.com/auth/plus.me", "UTF-8")+"&"+
					"&state="+state+
					"&access_type=offline"+
					"&approval_prompt=auto";
			resp.sendRedirect(authUrl);
		} catch(Exception e) {
			log.severe(e.getMessage());
			throw new ServletException(e);
		}
	}*/

	/**
	 * Not used. Check GoogleOAuth2Callback
	 * Handle callback from Google after authentication
	 * @param req
	 * @param resp
	 * @throws ServletException
	 */
	/*@Deprecated
	private void callbackGoogleAuth(HttpServletRequest req, HttpServletResponse resp) throws ServletException{
		try{
			String code = RequestHelper.getRequestQueryParam(req, "code");
			boolean noLuck  = false;
			if(code!=null) {
				String sessionState = RequestHelper.getSessionAttributeString(req, UserConstants.ATTR_GOOGLE_STATE.getValue());
				String requestState = RequestHelper.getRequestQueryParam(req, "state");
				if(sessionState!=null && sessionState.equals(requestState)) {
					final HTTPRequest accessTokenRequest = new HTTPRequest(new URL("https://www.accounts.google.com/o/oauth2/token?" +
							"code="+code+
							"&client_id="+ApplicationConstants.GOOGLE_CLIENT_ID.getValue()+
							"&client_secret="+ApplicationConstants.GOOGLE_CLIENT_SECRET.getValue()+
							"&redirect_uri="+ApplicationConstants.GOOGLE_CALLBACK_URL.getValue()+
							"&grant_type=authorization_code"), HTTPMethod.POST);
					HTTPResponse response = URLFetchServiceFactory.getURLFetchService().fetch(accessTokenRequest);
					int respCode = response.getResponseCode();
					if (respCode == HttpURLConnection.HTTP_OK) {
		            	String content = new String(response.getContent());
		            	GoogleAccessResponse googleResp = new Gson().fromJson(content, GoogleAccessResponse.class);
						String accessToken = googleResp.getAccess_token();
		            	RequestHelper.setSessionAttribute(req, UserConstants.ATTR_GOOGLE_ACCESSTOKEN.getValue(), accessToken);
		            	RequestHelper.setSessionAttribute(req, UserConstants.ATTR_USER_LOGIN_STATUS.getValue(), UserConstants.LOGIN_STATUS_GOOGLE_LOGGED_IN.getValue());
		            	
		            			            	
		            	Map<String,String> user =new HashMap<String, String>();
		            	user.put("id", "dummyids");
		            	user.put("name", "TEST GOOGLE ACCOUNT");
		            	
		            	AccountDetails userDetails = AccountHelper.convertGoogleResponseToUserDetails(user);
		            	RequestHelper.setSessionAttribute(req, UserConstants.ATTR_ACCOUNT_DETAILS.getValue(), userDetails);
		            	AccountHelper.updateUser(req, resp, userDetails);
					} else {
		            	noLuck = true;	
		            }
					if(noLuck) {
						log.warning("User did not approve");
					}
					resp.sendRedirect(ApplicationConstants.TSR_ACCOUNT_URL.getValue());
				}
			}
		} catch(Exception e) {
			log.severe(e.getMessage());
			throw new ServletException(e);
		}
	}
	*/
}
