package com.winster.routemarks.rest.helper;

import com.winster.routemarks.client.vo.AccountDetails;
import com.winster.routemarks.client.vo.MarkData;
import com.winster.routemarks.constants.UserConstants;

import java.util.Date;

import javax.servlet.http.HttpServletRequest;

public class MarkClientHelper {

	/**
	 * 
	 * @param request
	 * @param mark
	 */
	public static void touchMarkData(HttpServletRequest request, MarkData mark){
		AccountDetails userDetails = (AccountDetails) RequestHelper.getSessionAttribute(request, UserConstants.ATTR_ACCOUNT_DETAILS.getValue());
		String userId = (String)RequestHelper.getSessionAttribute(request, UserConstants.ATTR_ID.getValue());
		if(userDetails!=null) {
			mark.setUserId(userDetails.getUserId());
			mark.setUserName(userDetails.getUserName());
			mark.setMarkId(userDetails.getUserId()+"-"+userDetails.getTotalMarkCount());
			mark.setId(userDetails.getUserId()+"-"+userDetails.getTotalMarkCount());
		} else {
			mark.setUserId(UserConstants.NAME_USER_ANONYMOUS.getValue());
			mark.setUserName(UserConstants.NAME_USER_ANONYMOUS.getValue());
			long currentMarkCount = Long.valueOf(RequestHelper.getSessionAttributeString(request, 
														UserConstants.ATTR_VISITOR_MARK_COUNT.getValue()));
			mark.setMarkId(userId+"-"+currentMarkCount); //markid is id-count
			mark.setId(userId+"-"+currentMarkCount);
		}
		mark.setLastUpdatedDate(new Date());
		if(mark.getEmbed() !=null) {
			if(mark.getEmbed().indexOf("iframe")>-1) {
				mark.setVideoUrl(mark.getEmbed());
			}else if(mark.getImageUrl()==null){
				mark.setImageUrl(mark.getEmbed());
			}
		}
	}
}
