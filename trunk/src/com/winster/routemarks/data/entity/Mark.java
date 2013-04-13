package com.winster.routemarks.data.entity;

import java.util.Date;

import lombok.Data;

import com.googlecode.objectify.annotation.Cache;
import com.googlecode.objectify.annotation.Entity;
import com.googlecode.objectify.annotation.Id;
import com.googlecode.objectify.annotation.Index;

@Entity
@Cache
public @Data class Mark {
	
	private @Id String markId;
	private @Index String id;
	private String userId;
	private String userName;
	private String groupId;
	private String location;
	private Date lastUpdatedTime;
	private @Index long lastUpdatedTimeInMillis;
	private String category;
	private String transportation;
	private String reason;
	private String severity;
	private String description;
	private @Index String countryCode;
	private String country;
	private @Index String addressLevel1Code;
	private String addressLevel1;
	private @Index String addressLevel2Code;
	private String addressLevel2;
	private @Index String localityCode;
	private String locality;
	private String route;
	private String streetNumber;

	private long likeCount;
	private long starCount;
	private long dislikeCount;
	
	@Override
	public String toString(){
		return lastUpdatedTime+" - "+countryCode+" - "+category+" - "+transportation+" - "+reason+" - "+severity+" - "+description;		
	}
	
}
