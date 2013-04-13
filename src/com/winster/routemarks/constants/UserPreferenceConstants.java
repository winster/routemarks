package com.winster.routemarks.constants;

public enum UserPreferenceConstants {
	
	PREFERENCE_TYPE_SOCIAL_CONNECTED("socialconnected");
	
	private final String value;
	
	private UserPreferenceConstants(String value) {
		this.value = value;
	}
	
	public String getValue(){
		return this.value;
	}
	
}
