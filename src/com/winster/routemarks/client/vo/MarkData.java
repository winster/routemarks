package com.winster.routemarks.client.vo;

import java.util.Date;
import lombok.Data;

public @Data class MarkData {
	
	private String markId;
	private String id;
	private String userId;
	private String userName;
	private String emailId;
	private String location;
	private Date lastUpdatedDate;
	private String category;
	private String transportation;
	private String reason;
	private String severity;
	private String description;
	private GeocodeAddressComponents[] address;
	private long likeCount;
	private long starCount;
	private long dislikeCount;
	private String groupId;
	
	@Override
	public String toString(){
		return lastUpdatedDate+" - "+category+" - "+
					transportation+" - "+reason+" - "+severity+" - "+description;		
	}
	
}
