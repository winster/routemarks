package com.winster.routemarks.rest.endpoint;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.appengine.api.channel.ChannelService;
import com.google.appengine.api.channel.ChannelServiceFactory;
import com.winster.routemarks.client.vo.AccountDetails;
import com.winster.routemarks.client.vo.ActivityData;
import com.winster.routemarks.client.vo.GeocodeAddressComponents;
import com.winster.routemarks.constants.ApplicationConstants;
import com.winster.routemarks.constants.GeoAddressConstants;
import com.winster.routemarks.constants.NumeralConstants;
import com.winster.routemarks.constants.UserConstants;
import com.winster.routemarks.data.fusion.FusionTableFactory;
import com.winster.routemarks.data.helper.CommunityHelper;
import com.winster.routemarks.data.helper.MarkHelper;
import com.winster.routemarks.data.helper.UserHelper;
import com.winster.routemarks.rest.helper.AccountHelper;
import com.winster.routemarks.rest.helper.ActivityHelper;
import com.winster.routemarks.rest.helper.LocationHelper;
import com.winster.routemarks.rest.helper.RequestHelper;
import com.winster.routemarks.util.GoogleUtil;

/**
 * Interface for handling all community related activities
 * @author root
 *
 */
public class Community extends HttpServlet {

	private static final long serialVersionUID = 1L;
	private static final Logger log = Logger.getLogger(Community.class.getName());

	/**
	 * Handles all POST requests here
	 */
	public void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		try{
			FusionTableFactory.INSTANCE.initialize(req);
			
			String path = req.getPathInfo();
			int limit = (int)NumeralConstants.COMMUNITY_MESSAGE_LIMIT.getValue();			
			if(path.endsWith("list")) {
				GeocodeAddressComponents[] addressComponents = LocationHelper.retriveGeoAddressFromRequest(req);
				/*
				 * Not required as community is no more a separate screen
				 * if(CookieUtil.getCookieValue(req, ApplicationConstants.TOKEN_FIRST_VISIT.getValue()).equals("true")) {
					createAnonymousUser(req, resp, addressComponents);
				}*/
				String id = RequestHelper.getSessionAttributeString(req, UserConstants.ATTR_ID.getValue());
				String	clientId = RequestHelper.getSessionAttributeString(req, UserConstants.ATTR_CLIENT_ID.getValue());
				String token = RequestHelper.getSessionAttributeString(req, UserConstants.ATTR_TOKEN_ID.getValue());
				//String clientId = null;
				//String channelCookie = null;
				//UserId will be present in session, in the below condition
				//if(CookieUtil.getCookieValue(req, ApplicationConstants.TOKEN_CHANNEL.getValue())==null) {
				//	long count = Long.parseLong(req.getSession().getServletContext().getAttribute(
				//							ApplicationConstants.COUNT_ACTIVE_SESSIONS.getValue()).toString());
				if(clientId==null) {
					clientId = id;// CommunityHelper.createClientId(addressComponents, count);
					req.getSession().setAttribute(UserConstants.ATTR_CLIENT_ID.getValue(), clientId);
					ChannelService channelService = ChannelServiceFactory.getChannelService();
					token = channelService.createChannel(clientId);
					req.getSession().setAttribute(UserConstants.ATTR_TOKEN_ID.getValue(), token);					
				}
					//String id = (String) req.getSession().getAttribute(UserConstants.ATTR_ID.getValue());
					//channelCookie = id+"_"+clientId;
					//CookieUtil.setCookie(resp, ApplicationConstants.TOKEN_CHANNEL.getValue(), channelCookie);
				//}
				//if(req.getSession().getAttribute(UserConstants.ATTR_CHANNEL_TOKEN.getValue())==null) {
				//	if(channelCookie ==null || clientId==null){
				//		channelCookie = CookieUtil.getCookieValue(req, ApplicationConstants.TOKEN_CHANNEL.getValue());
				//		clientId = channelCookie.split("_")[1];
				//	}
				createUserCriteria(req, resp, id, clientId, addressComponents, true);
				
				Map<String,String> input = new HashMap<String,String>();
				input.put("1", GoogleUtil.INSTANCE.getAddress(addressComponents,GeoAddressConstants.COUNTRYCODE));
				RequestHelper.setSessionAttribute(req, UserConstants.ATTR_CRITERIA_PARAM.getValue(), input);
				
				String message = CommunityHelper.sendChannelMessageAfterLoad(token, id, limit, 0, input );
				resp.getWriter().write(message); 
					//req.getSession().setAttribute(UserConstants.ATTR_CHANNEL_TOKEN.getValue(), token);
				//} else {
					//clientId = channelCookie.split("_")[1];
				//	String token = (String) req.getSession().getAttribute(UserConstants.ATTR_CHANNEL_TOKEN.getValue());
				//	String message = CommunityHelper.sendChannelMessageForDefault(token, addressComponents);
				//	resp.getWriter().write(message);
				//}
				RequestHelper.setSessionAttribute(req, UserConstants.ATTR_CRITERIA_OFFSET.getValue(), null);				
			} else if(path.endsWith("criteria")) {
				String id = RequestHelper.getSessionAttributeString(req, UserConstants.ATTR_ID.getValue());
				String clientId = RequestHelper.getSessionAttributeString(req, UserConstants.ATTR_CLIENT_ID.getValue());
				GeocodeAddressComponents[] addressComponents = LocationHelper.retriveGeoAddressFromRequest(req);
				//String channelCookie = CookieUtil.getCookieValue(req, ApplicationConstants.TOKEN_CHANNEL.getValue());
				//String clientId = channelCookie.split("_")[1];
				createUserCriteria(req, resp, id, clientId, addressComponents, false);
				Map<String,String> input = new HashMap<String,String>();
				input.put("1", GoogleUtil.INSTANCE.getAddress(addressComponents,GeoAddressConstants.COUNTRYCODE));
				input.put("2", GoogleUtil.INSTANCE.getAddress(addressComponents,GeoAddressConstants.LEVEL1CODE));
				input.put("3", GoogleUtil.INSTANCE.getAddress(addressComponents,GeoAddressConstants.LEVEL2CODE));
				input.put("4", GoogleUtil.INSTANCE.getAddress(addressComponents,GeoAddressConstants.LOCALITYCODE));
				RequestHelper.setSessionAttribute(req, UserConstants.ATTR_CRITERIA_PARAM.getValue(), input);
				CommunityHelper.sendChannelMessageForNewCriteria(clientId, limit, 0, input, ApplicationConstants.COMMUNITY_LOAD_TYPE_RELOAD.getValue());
				RequestHelper.setSessionAttribute(req, UserConstants.ATTR_CRITERIA_OFFSET.getValue(), null);				
			} else if(path.endsWith("updatemark")) {
				ActivityData activityData = ActivityHelper.retriveActivityDataRequest(req);
				insertActivity(req, activityData);
				MarkHelper.updateMark(activityData);						
			} else if(path.endsWith("more")) {
				String	clientId = RequestHelper.getSessionAttributeString(req, UserConstants.ATTR_CLIENT_ID.getValue());
				@SuppressWarnings("unchecked")
				Map<String,String> input = (Map<String,String>)RequestHelper.getSessionAttribute(req, UserConstants.ATTR_CRITERIA_PARAM.getValue());
				int offset = limit;
				if(RequestHelper.getSessionAttributeString(req, UserConstants.ATTR_CRITERIA_OFFSET.getValue())!=null) {
					offset = limit+Integer.valueOf(RequestHelper.getSessionAttributeString(req, UserConstants.ATTR_CRITERIA_OFFSET.getValue()));
				}
				RequestHelper.setSessionAttribute(req, UserConstants.ATTR_CRITERIA_OFFSET.getValue(), offset);				
				CommunityHelper.sendChannelMessageForNewCriteria(clientId, limit, offset, input, ApplicationConstants.COMMUNITY_LOAD_TYPE_APPEND.getValue());
			} else if(path.endsWith("reopenchannel")) {
				
			}
		} catch(Exception e){
			log.severe(e.getMessage());
			throw new ServletException(e);
		}
	}

	/**
	 * Single interface which makes call to createUserCriteria of AccountHelper. 
	 * 
	 * @param req
	 * @param resp
	 * @param channelCookie
	 * @param addressComponents
	 * @param firstVisit
	 */
	private void createUserCriteria(HttpServletRequest req, HttpServletResponse resp, 
			String id, String clientId, GeocodeAddressComponents[] addressComponents, boolean firstVisit) {
		AccountHelper.createUserCriteria(req, resp, id, clientId, addressComponents, firstVisit);
	}

	/**
	 * Is userId is null in session, that also means that the cookie "firstVisit" is also true. 
	 * So here, just duplicate the logic given in anonymous user creation.
	 * 
	 * @param req
	 * @param resp
	 * @param addressComponents
	 * @throws ServletException
	 
	private void createAnonymousUser(HttpServletRequest req, HttpServletResponse resp, 
												GeocodeAddressComponents[] addressComponents) throws ServletException {		
		AccountHelper.createAnonymousUser(req, resp, addressComponents);
	}*/
	
	/**
	 * 
	 * @param req
	 * @param activityData
	 */
	private void insertActivity(HttpServletRequest req, ActivityData activityData) {
		AccountDetails userDetails  = (AccountDetails) req.getSession().getAttribute(UserConstants.ATTR_ACCOUNT_DETAILS.getValue());
		String id = (String)RequestHelper.getSessionAttribute(req, UserConstants.ATTR_ID.getValue());
		String activityId = ActivityHelper.getActivityId(req, id, userDetails);
		double points = ActivityHelper.getPointsForActivity(activityData.getType());
		
		UserHelper.createUserActivity(activityId, id, activityData.getType(), "Mark : "+activityData.getMarkId(), points);	
		if(userDetails !=null){
			UserHelper.updateUserActivityMaster(id, 
										ApplicationConstants.CHILD_ENTITY_TYPE_ACTIVITY.getValue(), 
										points, 
										activityId);
		}
	}

}
