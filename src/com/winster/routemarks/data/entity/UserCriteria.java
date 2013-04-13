package com.winster.routemarks.data.entity;

import java.util.Date;

import lombok.Data;

import com.googlecode.objectify.annotation.Entity;
import com.googlecode.objectify.annotation.Id;
import com.googlecode.objectify.annotation.Index;

@Entity
public @Data class UserCriteria {
	
	private @Id String id;
	private @Index String clientId;
	private @Index String country;
	private @Index String level1;
	private @Index String level2;
	private @Index String locality;
	private @Index String criteria;
	private Date lastUpdatedTime;
	private @Index long lastUpdatedTimeInMillis;
}
