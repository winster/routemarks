package com.winster.routemarks.data.entity;

import lombok.Data;

import com.googlecode.objectify.annotation.Cache;
import com.googlecode.objectify.annotation.Entity;
import com.googlecode.objectify.annotation.Id;
import com.googlecode.objectify.annotation.Index;

@Entity
@Cache
public @Data class UserPreference {
	
	private @Id String preferenceId; //id-preference count
	private @Index String id;
	private String type;
	private String value;
}
