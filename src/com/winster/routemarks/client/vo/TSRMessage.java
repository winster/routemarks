package com.winster.routemarks.client.vo;

import java.util.Date;

import lombok.Data;

public @Data class TSRMessage {
	
	private String markId;
	private String username;
	private String countrycode;
	private String locality;
	private String category;
	private String transportation;
	private String nature;
	private String severity;
	private String description;
	private String videoUrl;
	private String imageUrl;
	private Date date;
	private String location;
	private long likeCount;
	private long starCount;
	private long dislikeCount;
}
