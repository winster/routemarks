package com.winster.routemarks.data.entity;

import lombok.Data;

import com.googlecode.objectify.annotation.Cache;
import com.googlecode.objectify.annotation.Entity;
import com.googlecode.objectify.annotation.Id;

@Entity
@Cache
public @Data class UserActivityMaster {
	
	private @Id String id;
	private long totalMarkCount;  		//used for creating Mark Id
	private long totalActivityCount;	//used for creating Activity Id
	private long totalPreferenceCount;	//used for creating Preference Id
	
	private double totalPoints;
	private String recentActivityId;
}
