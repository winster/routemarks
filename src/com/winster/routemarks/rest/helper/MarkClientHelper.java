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
		String id = (String)RequestHelper.getSessionAttribute(request, UserConstants.ATTR_ID.getValue());
		mark.setId(id);
		if(userDetails!=null) {
			mark.setUserId(userDetails.getUserId());
			mark.setUserName(userDetails.getUserName());
			mark.setMarkId(userDetails.getUserId()+"-"+userDetails.getTotalMarkCount());
		} else {
			mark.setUserId(UserConstants.NAME_USER_ANONYMOUS.getValue());
			mark.setUserName(UserConstants.NAME_USER_ANONYMOUS.getValue());
			long currentMarkCount = Long.valueOf(RequestHelper.getSessionAttributeString(request, 
														UserConstants.ATTR_VISITOR_MARK_COUNT.getValue()));
			mark.setMarkId(id+"-"+currentMarkCount); //markid is id-count
		}
		mark.setLastUpdatedDate(new Date());
	}
}
