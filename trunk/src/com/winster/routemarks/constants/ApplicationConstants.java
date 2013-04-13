package com.winster.routemarks.constants;

public enum ApplicationConstants {
	
	TOKEN_TSR("tsrtoken"),
	TOKEN_FIRST_VISIT("firstvisit"),
	//TOKEN_CHANNEL("clientId"),//Not sure why this token is kept in session. I dont think its required
	
	TWITTER_CONSUMER_KEY("hFSSBs81vHZXaJ9LJNgQ"),
	TWITTER_CONSUMER_SECRET("qwblld4TVRvXc0akOEEC1XlXeuy3XoTzoRMimYTpsY"),
	
	TWITTER_URI_REQUEST_TOKEN("https://api.twitter.com/oauth/request_token"),
	TWITTER_URI_ACCESS_TOKEN("https://api.twitter.com/oauth/access_token"),
	TWITTER_URI_AUTHORIZE("https://api.twitter.com/oauth/authorize"),
	TWITTER_CALLBACK_URL("http://routemarks.com/socialAuth/callback?app=twitter"),
	
	
	FACEBOOK_APP_KEY("485688004787851"),
	FACEBOOK_APP_SECRET("1450c00e446cb4736a80021fed38147d"),
	FACEBOOK_CALLBACK_URL("http://routemarks.com/socialAuth/callback?app=facebook"),
	FACEBOOK_TSR_PAGE_ID("495931120457196"),
	
	GOOGLE_CLIENT_ID("284218108178.apps.googleusercontent.com"),
	GOOGLE_CLIENT_SECRET("zWxE-fJxK0UKEOe-rs_COw5F"),
	GOOGLE_CALLBACK_URL("http://routemarks.com/socialAuth/callback?app=google"),
	
	TSR_HOME_URL("/home"),
	TSR_ACCOUNT_URL("/account"),
	TSR_COMMUNITY_URL("/community"),
	TSR_MARK_URL("/mark"),
	
	ACCOUNT_TYPE_TWITTER("twitter"),
	ACCOUNT_TYPE_FACEBOOK("facebook"),	
	ACCOUNT_TYPE_GOOGLE("google"),
	
	COMMUNITY_MESSAGE_LIMIT("20"),

	CHILD_ENTITY_TYPE_MARK("mark"),
	CHILD_ENTITY_TYPE_ACTIVITY("activity"),
	CHILD_ENTITY_TYPE_PREFERENCE("preference"),
	
	ACTIVITY_TYPE_MARK("mark"),
	ACTIVITY_TYPE_LIKE("like"),
	ACTIVITY_TYPE_DISLIKE("dislike"),
	ACTIVITY_TYPE_STAR("star"),
	
	COMMUNITY_LOAD_TYPE_PREPEND("prepend"),
	COMMUNITY_LOAD_TYPE_RELOAD("reload"),
	COMMUNITY_LOAD_TYPE_APPEND("append"),
	
	COUNT_ACTIVE_SESSIONS("totalactivesession");
	
	private final String value;
	
	private ApplicationConstants(String value) {
		this.value = value;
	}
	
	public String getValue(){
		return this.value;
	}
	
}
