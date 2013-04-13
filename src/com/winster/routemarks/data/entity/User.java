package com.winster.routemarks.data.entity;

import java.util.Date;

import lombok.Data;

import com.googlecode.objectify.annotation.Entity;
import com.googlecode.objectify.annotation.Id;
import com.googlecode.objectify.annotation.Index;

@Entity
public @Data class User {
	
	private @Id String id; //unique Id generated from TSR
	
	private @Index String userId;	//userId from oauth 
	private @Index String name;
	
	private String country;
	private String level1;
	private String level2;
	private String locality;
	
	private String account;//can be facebook/twitter/google
	private Date lastUpdatedTime;
	
}
