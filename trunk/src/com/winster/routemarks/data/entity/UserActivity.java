package com.winster.routemarks.data.entity;

import lombok.Data;

import com.googlecode.objectify.annotation.Cache;
import com.googlecode.objectify.annotation.Entity;
import com.googlecode.objectify.annotation.Id;
import com.googlecode.objectify.annotation.Index;

@Entity
@Cache
public @Data class UserActivity {
	
	private @Id String activityId;   //id-activitycount
	private @Index String id;
	private String type;
	private String desc;
	private double points;
	
}
