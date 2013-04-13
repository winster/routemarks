package com.winster.routemarks.client.vo;

import java.io.Serializable;
import java.util.Date;

import lombok.Data;

public @Data class AccountDetails implements Serializable {

	private static final long serialVersionUID = 1L;
	
	private String id;//this is unique
	private String userName;
	private String userId;//this is obtained from facebook/twitter/google ; may not be unique
	private String accountType;

	private String firstName;
	private String lastName;
	private String link;
	private String gender;
	private Double timezone;
	private String locale;
	private Date updatedTime;
	
	private long totalMarkCount;
	private long totalActivityCount;
	private long totalPrefernceCount;	
	private double totalPoints;
	
	private String recentCriteria;
	private ActivityData recentActivity;
	
}
