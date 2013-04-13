package com.winster.routemarks.client.vo;

import lombok.Data;

public @Data class ActivityData {

	private String markId; //From client side all activities are made against a mark
	//private String activityId; //Not required for client
	private String type;
	private String desc;
}
