package com.winster.routemarks.rest.helper;

import com.winster.routemarks.client.vo.TSRMessage;
import com.winster.routemarks.constants.ApplicationConstants;
import com.winster.routemarks.constants.UserConstants;
import com.winster.routemarks.data.entity.Mark;
import com.winster.routemarks.rest.google.ServiceUtils;
import com.winster.routemarks.rest.signpost.GoogleAppEngineOAuthConsumer;
import com.winster.routemarks.util.Converter;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.logging.Logger;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;

import oauth.signpost.OAuthConsumer;

import com.google.api.services.plus.Plus;
import com.google.api.services.plus.model.Person;
import com.google.appengine.api.urlfetch.HTTPHeader;
import com.google.appengine.api.urlfetch.HTTPMethod;
import com.google.appengine.api.urlfetch.HTTPRequest;
import com.google.appengine.api.urlfetch.HTTPResponse;
import com.google.appengine.api.urlfetch.URLFetchServiceFactory;
import com.restfb.DefaultFacebookClient;
import com.restfb.FacebookClient;
import com.restfb.Parameter;
import com.restfb.types.FacebookType;

/**
 * Helper class for all integration with social n/w sites
 * @author root
 *
 */
public class SocialAuthHelper {

	private static final Logger log = Logger.getLogger(SocialAuthHelper.class.getName());

	/**
	 * public interface for updating social status to outer world
	 * @param req
	 * @param mark
	 * @return
	 * @throws ServletException
	 * @throws IOException 
	 */
	public static boolean updateSocialStatus(HttpServletRequest req, Mark mark) throws ServletException, IOException {
		boolean flag = true;
		String loginStatus = null;
		if((loginStatus = getUserLogInStatus(req))==null) {
			flag = false;
		} else {
			TSRMessage tsrMessage = Converter.convertMarkToMessage(mark);
			if(loginStatus.equals(UserConstants.LOGIN_STATUS_TWITTER_LOGGED_IN.getValue())) {
				String token = RequestHelper.getSessionAttributeString(req, UserConstants.ATTR_TWITTER_OAUTH_TOKEN.getValue());
				String tokenSecret = (String) req.getSession().getAttribute(UserConstants.ATTR_TWITTER_OAUTH_TOKEN_SECRET.getValue());
				String message = createTweetMessage(tsrMessage);							
				flag = postTweet(token, tokenSecret, message);	
			} else if(loginStatus.equals(UserConstants.LOGIN_STATUS_FACEBOOK_LOGGED_IN.getValue())) {
				String token = RequestHelper.getSessionAttributeString(req, UserConstants.ATTR_FACEBOOK_ACCESSTOKEN.getValue());
				String message = createFacebookPostMessage(tsrMessage);							
				flag = postStatusOnPageWall(token, message);
			} else if(loginStatus.equals(UserConstants.LOGIN_STATUS_GOOGLE_LOGGED_IN.getValue())) {
				String message = createGooglePostMessage(tsrMessage);
				String userId = RequestHelper.getSessionAttributeString(req, UserConstants.ATTR_ID.getValue());
				flag = postStatusOnGooglePage(userId, message);
			}
		}
		return flag;
	}

	/**
	 * Method to retrieve user login status
	 * @param req
	 * @return
	 */
	private static String getUserLogInStatus(HttpServletRequest req) {
		String logInStatus = RequestHelper.getSessionAttributeString(req, UserConstants.ATTR_USER_LOGIN_STATUS.getValue());
		return logInStatus;		
	}

	/**
	 * Helper method to send a tweet
	 * 
	 * @param token
	 * @param secret
	 * @return
	 * @throws ServletException
	 */
    private static boolean postTweet(String token, String secret, String message) throws ServletException {
    	try{
    		OAuthConsumer consumer = new GoogleAppEngineOAuthConsumer(
    				ApplicationConstants.TWITTER_CONSUMER_KEY.getValue(),
					ApplicationConstants.TWITTER_CONSUMER_SECRET.getValue());
            consumer.setTokenWithSecret(token, secret); 
            
            final HTTPRequest postTweet = new HTTPRequest(new URL("http://api.twitter.com/1/statuses/update.json"), HTTPMethod.POST);
            postTweet.setPayload(("status=" + URLEncoder.encode(message, "UTF-8")).getBytes());
            postTweet.addHeader(new HTTPHeader("Content-Type", "application/x-www-form-urlencoded"));
            consumer.sign(postTweet);
            
            HTTPResponse response = URLFetchServiceFactory.getURLFetchService().fetch(postTweet);
            int respCode = response.getResponseCode();
            if (respCode == HttpURLConnection.HTTP_OK) {
                return true;
            } else {
            	return false;
            }
        } catch (Exception e) {
        	log.severe(e.getMessage());
        	throw new ServletException(e);
		}
    }
    
    /**
     * Helper method to post a message on TSR page wall using token 
     * @param token
     * @param message
     * @return
     * @throws ServletException
     */
    private static boolean postStatusOnPageWall(String token, String message) throws ServletException {
		try{
			FacebookClient facebookClient = new DefaultFacebookClient(token);
			// Publishing a simple message.
			// FacebookType represents any Facebook Graph Object that has an ID property.
			/*FacebookType publishMessageResponse = facebookClient.publish(ApplicationConstants.FACEBOOK_TSR_PAGE_ID.getValue()+"/feed", 
															FacebookType.class,Parameter.with("message", message));*/
			
			FacebookType publishMessageResponse = facebookClient.publish("me/routemarks:mark", 
					FacebookType.class,Parameter.with("spot", "http://www.routemarks.com/home/loc/"+message));
			log.info("Published message ID: " + publishMessageResponse.getId());
			return true;
        } catch (Exception e) {
        	log.severe(e.getMessage());
        	throw new ServletException(e);
		}
	}
 
    /**
     * Helper method to post a message on Google TSR page wall using google plus api 
     * @param userId
     * @param message
     * @return
     * @throws IOException 
     */
	private static boolean postStatusOnGooglePage(String userId, String message) throws IOException {
		Plus plus = ServiceUtils.loadPlusClient(userId);
		Person profile = plus.people().get("me").execute();
		return false;
	}
	
    /**
     * Method which constructs tweet message
     * @param tsrMessage
     * @return
     */
    private static String createTweetMessage(TSRMessage tsrMessage){
    	return "#RouteMarks ::"+tsrMessage.getCountrycode()+"::"+tsrMessage.getLocality()+"::"+tsrMessage.getCategory()+" - "+
				tsrMessage.getTransportation()+" - "+tsrMessage.getNature()+" - "+tsrMessage.getDescription();
    }

    /**
     * Method which constructs facebook wall post
     * @param tsrMessage
     * @return
     */
    private static String createFacebookPostMessage(TSRMessage tsrMessage){
    	return tsrMessage.getLocation();
    }
    

    /**
     * Method which constructs google page post
     * @param tsrMessage
     * @return
     */
    private static String createGooglePostMessage(TSRMessage tsrMessage){
    	return tsrMessage.getCountrycode()+"::"+tsrMessage.getLocality()+"::"+tsrMessage.getCategory()+" - "+
				tsrMessage.getTransportation()+" - "+tsrMessage.getNature()+" - "+tsrMessage.getDescription();
    }
}