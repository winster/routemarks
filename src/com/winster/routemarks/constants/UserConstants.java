package com.winster.routemarks.constants;

public enum UserConstants {
	STATUS_USER_CREATED("created"),
	STATUS_USER_CRITERIA_CREATED("criteria_created"),
	LOGIN_STATUS_TWITTER_LOGGED_IN("twitterloggedin"),
	LOGIN_STATUS_FACEBOOK_LOGGED_IN("facebookloggedin"),
	LOGIN_STATUS_GOOGLE_LOGGED_IN("googleloggedin"),
	
	ATTR_USER_STATUS("userstatus"),
	ATTR_USER_LOGIN_STATUS("userloginstatus"),
	ATTR_USER_CRITERIA_STATUS("usercriteriastatus"),
	ATTR_ID("userId"),
	ATTR_CLIENT_ID("clientId"),
	ATTR_TOKEN_ID("channeltoken"),
	
	ATTR_ACCOUNT_DETAILS("account"),
	ATTR_CRITERIA_PARAM("inputparam"),
	ATTR_CRITERIA_OFFSET("criteriaoffset"),
	
	ATTR_VISITOR_MARK_COUNT("visitorMarkCount"), //For visitor, no ActivityMaster available. So keep it in session
	ATTR_VISITOR_ACTIVITY_COUNT("visitorActivityCount"), //For visitor, no ActivityMaster available. So keep it in session	
	
	ATTR_TWITTER_OAUTH_TOKEN("token"),
	ATTR_TWITTER_OAUTH_TOKEN_SECRET("tokenSecret"),
	ATTR_TWITTER_OAUTH_VERIFIER("oauth_verifier"),
	
	ATTR_FACEBOOK_STATE("fbstate"),
	ATTR_FACEBOOK_ACCESSTOKEN("fbaccesstoken"),
	
	ATTR_GOOGLE_STATE("googlestate"),
	ATTR_GOOGLE_ACCESSTOKEN("googleaccesstoken"),
	ATTR_GOOGLE_USER_ID("googleuserId"),//Dont use this attribute blindly. This is for google library to work. check GoogleAuthServlet.getUserId(reg)
	
	NAME_USER_ANONYMOUS("Anonymous");
	
	private final String value;
	
	private UserConstants(String value) {
		this.value= value;
	}
	
	public String getValue(){
		return this.value;
	}
}
