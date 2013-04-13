package com.winster.routemarks.client.vo;

import lombok.Data;

public @Data class CommunityMessage {
	
	private String token;
	private TSRMessage[] updates;
	private String loadType;
}
