package com.winster.routemarks.util;

import com.winster.routemarks.constants.UserPreferenceConstants;
import com.winster.routemarks.data.entity.UserPreference;
import com.winster.routemarks.data.helper.UserHelper;

import java.util.List;


public class SocialUtil {

	/**
	 * 
	 * @param id
	 * @return
	 */
	public static boolean isSocialConnected(String id){
		List<UserPreference> preferences = UserHelper.fetchUserPreferenceEntities(id);
		boolean isSocialConnected = false;
		if(preferences!=null) {
			for(UserPreference preference : preferences) {
				if(UserPreferenceConstants.PREFERENCE_TYPE_SOCIAL_CONNECTED.getValue().
						equals(preference.getType())){
					String value = preference.getValue();
					if("1".equals(value)) {
						isSocialConnected = true;
						break;
					}
				}
			}
		}
		return isSocialConnected;
	}
}
