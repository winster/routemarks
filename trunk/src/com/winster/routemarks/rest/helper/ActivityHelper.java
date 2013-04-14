package com.winster.routemarks.rest.helper;

import com.winster.routemarks.client.vo.AccountDetails;
import com.winster.routemarks.client.vo.ActivityData;
import com.winster.routemarks.constants.ApplicationConstants;
import com.winster.routemarks.constants.NumeralConstants;
import com.winster.routemarks.data.entity.UserActivity;
import com.winster.routemarks.data.entity.UserActivityMaster;
import com.winster.routemarks.data.helper.UserHelper;
import com.winster.routemarks.rest.endpoint.Community;

import java.io.BufferedReader;
import java.util.logging.Logger;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;

import com.google.gson.Gson;

/**
 * Handles all activity related actions
 * @author root
 *
 */
public class ActivityHelper {
	
	private static final Logger log = Logger.getLogger(Community.class.getName());

	/**
	 * Create and returns activityId  
	 * @param req
	 * @param isVisitor
	 * @return
	 */
	public static String getActivityId(HttpServletRequest req, String id, AccountDetails userDetails) {
		long count = 0;
		if(userDetails==null){
			count = AccountHelper.updateActivityCountForVisitor(req);			
		} else {
			count = userDetails.getTotalActivityCount()+1;
			userDetails.setTotalActivityCount(count);
		}
		return id+"-"+count;
	}

	/**
	 * Returns ponits for given activity type
	 * @param type
	 * @return
	 */
	public static double getPointsForActivity(String type) {
		double points = 0;
		if(ApplicationConstants.ACTIVITY_TYPE_MARK.getValue().equals(type)) {
			points = NumeralConstants.POINTS_MARK.getValue();
		} else if(ApplicationConstants.ACTIVITY_TYPE_DISLIKE.getValue().equals(type)) {
			points = NumeralConstants.POINTS_DISLIKE.getValue();
		} else if(ApplicationConstants.ACTIVITY_TYPE_LIKE.getValue().equals(type)) {
			points = NumeralConstants.POINTS_LIKE.getValue();
		} else if(ApplicationConstants.ACTIVITY_TYPE_STAR.getValue().equals(type)) {
			points = NumeralConstants.POINTS_STAR.getValue();
		}    
		return points;
	}

	/**
	 * Gets Activity Data from request
	 * @param req
	 * @return
	 * @throws ServletException
	 */
	public static ActivityData retriveActivityDataRequest(HttpServletRequest req) throws ServletException {
		StringBuffer jb = new StringBuffer();
		String line = null;
		try {
			BufferedReader reader = req.getReader();
		    while ((line = reader.readLine()) != null)
		    	jb.append(line);
		} catch (Exception e) { 
			log.severe(e.getMessage());
			throw new ServletException(e);
		}	  
		ActivityData activityData = new Gson().fromJson(jb.toString(), ActivityData.class);		
		return activityData;
	}

	/**
	 * Get Recent Activity Data for the user
	 * @param accountDetails
	 * @return
	 */
	public static void setRecentActivity(String id, AccountDetails accountDetails) {
		UserActivityMaster activityMaster = UserHelper.fetchUserActivityMasterEntityById(id);
		if(activityMaster.getRecentActivityId()!=null) {
			UserActivity activity = UserHelper.fetchUserActivityEntity(activityMaster.getRecentActivityId());
			ActivityData data = null;
			if(activity!=null) {
				data = new ActivityData();
				data.setType(activity.getType());
				if(activity.getDesc()!=null && activity.getDesc().length()>70) {
					data.setDesc(activity.getDesc().substring(0,70));
				}else {
					data.setDesc(activity.getDesc());
				}
				accountDetails.setRecentActivity(data);
			}
		}
		accountDetails.setTotalPoints(activityMaster.getTotalPoints());
	}

}

