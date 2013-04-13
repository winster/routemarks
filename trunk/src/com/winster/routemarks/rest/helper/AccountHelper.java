package com.winster.routemarks.rest.helper;

import com.winster.routemarks.client.vo.AccountDetails;
import com.winster.routemarks.client.vo.GeocodeAddressComponents;
import com.winster.routemarks.client.vo.PreferenceData;
import com.winster.routemarks.constants.ApplicationConstants;
import com.winster.routemarks.constants.UserConstants;
import com.winster.routemarks.data.entity.UserCriteria;
import com.winster.routemarks.data.entity.UserPreference;
import com.winster.routemarks.data.helper.UserHelper;
import com.winster.routemarks.util.CookieUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import oauth.signpost.http.HttpParameters;

import com.google.api.services.plus.model.Person;

/**
 * Helps all account related actions
 * @author root
 *
 */
public class AccountHelper {

	/**
	 * Helper method for creating/updating a user. It also removes the guestUser record 
	 * @param req
	 * @param userDetails
	 * @throws ServletException 
	 */
	public static void updateUser(HttpServletRequest req, HttpServletResponse resp, AccountDetails userDetails) throws ServletException {
		try{
			//userId can be null in cases like user directly opens Account page
			String visitorId = RequestHelper.getSessionAttributeString(req, UserConstants.ATTR_ID.getValue());
			String id = UserHelper.updateUser(visitorId, userDetails);		
			RequestHelper.setSessionAttribute(req, UserConstants.ATTR_ID.getValue(), id);		
			CookieUtil.setCookie(resp, ApplicationConstants.TOKEN_FIRST_VISIT.getValue(), "false", 3600);			
		} catch(Exception e) {
			throw new ServletException(e);
		}
	}
	
	/**
	 * Helper method for creating an anonymous user 
	 * @param req
	 * @param resp
	 * @param addressComponents
	 * @throws ServletException 
	 */
	public static void createAnonymousUser(HttpServletRequest req, HttpServletResponse resp,
										GeocodeAddressComponents[] addressComponents) throws ServletException {
		try{
			String userId = UUID.randomUUID().toString();
			CookieUtil.setCookie(resp, ApplicationConstants.TOKEN_FIRST_VISIT.getValue(), "false", 3600);
			UserHelper.createAnonymousUser(userId, addressComponents);
			RequestHelper.setSessionAttribute(req, UserConstants.ATTR_ID.getValue(), userId);			
		}catch(Exception e) {
			throw new ServletException(e);
		}
	}

	/**
	 * Helper method for creating a user criteria
	 * 
	 * @param req
	 * @param resp
	 * @param channelCookie
	 * @param addressComponents
	 * @param firstVisit
	 */
	public static void createUserCriteria(HttpServletRequest req,  HttpServletResponse resp, 
			String id, String clientId, GeocodeAddressComponents[] addressComponents, boolean firstVisit) {
		UserHelper.createCriteria(id, clientId, addressComponents, firstVisit);
	}
	
	/**
	 * Helper method for converting a twitter response after authentication to TSR AccountDetails object
	 * @param httpParameters
	 * @return
	 */
	public static AccountDetails convertTwitterResponseToUserDetails(HttpParameters httpParameters) {
		AccountDetails userDetails = new AccountDetails();
		userDetails.setUserId(httpParameters.getFirst("user_id"));
		userDetails.setUserName(httpParameters.getFirst("screen_name"));
		userDetails.setAccountType(ApplicationConstants.ACCOUNT_TYPE_TWITTER.getValue());
		return userDetails;
	}
	
	/**
	 * Helper method for converting a Facebook response after "me" API to TSR AccountDetails object
	 * 
	 * @param user
	 * @return
	 */
	public static AccountDetails convertFacebookResponseToUserDetails(com.restfb.types.User user) {
		AccountDetails userDetails = new AccountDetails();
		
		userDetails.setUserId(user.getId());
		userDetails.setUserName(user.getName());
		userDetails.setAccountType(ApplicationConstants.ACCOUNT_TYPE_FACEBOOK.getValue());
		
		userDetails.setFirstName(user.getFirstName());
		userDetails.setGender(user.getGender());
		userDetails.setLastName(user.getLastName());
		userDetails.setLink(user.getLink());
		userDetails.setLocale(user.getLocale());
		userDetails.setTimezone(user.getTimezone());
		userDetails.setUpdatedTime(user.getUpdatedTime());
		
		return userDetails;
	}
	
	/**
	 * Helper method for converting a Google response after authentication
	 * 
	 * @param user
	 * @return
	 */
	public static AccountDetails convertGoogleResponseToUserDetails(Person profile) {
		AccountDetails userDetails = new AccountDetails();
		
		userDetails.setUserId(profile.getId());
		userDetails.setUserName(profile.getDisplayName());
		userDetails.setAccountType(ApplicationConstants.ACCOUNT_TYPE_GOOGLE.getValue());
		
		return userDetails;
	}

	/**
	 * A Dummy user
	 * @return
	 */
	public static AccountDetails createDummyAccountDetails(String accountType) {
		AccountDetails ad = new AccountDetails();
		ad.setUserId("1234567890");
		ad.setUserName("vinu tsr");
		ad.setAccountType(accountType);
		return ad;
	}
	
	
	/**
	 * 
	 * @param id
	 * @param count
	 * @param preferenceData
	 */
	public static void createUserPreference(String id, long count, PreferenceData preferenceData) {
		String preferenceId = id+"-"+count;
		UserHelper.createUserPreference(id, preferenceId, preferenceData);
	}
	

	/**
	 * Helper method 
	 * @param httpParameters
	 * @return
	 */
	public static List<PreferenceData> findUserPreferences(HttpServletRequest req) {
		String id = (String) req.getSession().getAttribute(UserConstants.ATTR_ID.getValue());
		List<UserPreference> preferences =  UserHelper.fetchUserPreferenceEntities(id);
		List<PreferenceData> preferenceDataList = null;
		if(preferences!=null) {
			preferenceDataList = new ArrayList<PreferenceData>();
			for(UserPreference preference : preferences) {
				PreferenceData preferenceData = new PreferenceData();
				preferenceData.setType(preference.getType());
				preferenceData.setValue(preference.getValue());
				preferenceDataList.add(preferenceData);
			}
		}		
		return preferenceDataList;
	}

	/**
	 * 
	 * @param req
	 */
	public static void updateMarkCountForVisitor(HttpServletRequest req) {
		String countString = RequestHelper.getSessionAttributeString(req,UserConstants.ATTR_VISITOR_MARK_COUNT.getValue());
		if(countString==null) {
			countString = "0";
		}
		RequestHelper.setSessionAttribute(req, UserConstants.ATTR_VISITOR_MARK_COUNT.getValue(), Long.valueOf(countString)+1);		
	}
	
	/**
	 * 
	 * @param req
	 */
	public static long updateActivityCountForVisitor(HttpServletRequest req) {
		String countString = RequestHelper.getSessionAttributeString(req,UserConstants.ATTR_VISITOR_ACTIVITY_COUNT.getValue());
		if(countString==null) {
			countString = "0";
		}
		long count = Long.valueOf(countString)+1;
		RequestHelper.setSessionAttribute(req, UserConstants.ATTR_VISITOR_ACTIVITY_COUNT.getValue(), count);
		return count;
	}

	/**
	 * Store last criteria selected
	 * @param accountDetails
	 * @return
	 */
	public static void setRecentCriteria(String id, AccountDetails accountDetails) {
		UserCriteria criteria = UserHelper.fetchUserCriteriaEntityById(id);
		StringBuffer criteriaData = new StringBuffer();
		if(criteria!=null) {
			criteriaData.append(criteria.getCountry()).append("-").append(criteria.getLevel1()).
						append("-").append(criteria.getLevel2()).append("-").append(criteria.getLocality());
		}
		accountDetails.setRecentCriteria(criteriaData.toString());
	}

	/**
	 * 
	 * @param id
	 * @param preferenceData
	 */
	public static void deleteUserPreference(String id, PreferenceData preferenceData) {
		UserHelper.deleteUserPreference(id, preferenceData.getType());		
	}
	
	
}
