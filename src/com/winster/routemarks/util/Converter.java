package com.winster.routemarks.util;

import com.winster.routemarks.client.vo.TSRMessage;
import com.winster.routemarks.data.entity.Mark;
/**
 * 
 * @author root
 *
 */
public class Converter {

	/**
	 *  Convert a mark object to TSR message 
	 * @param mark
	 * @return
	 */
	public static TSRMessage convertMarkToMessage(Mark mark) {
		TSRMessage message = new TSRMessage();
		message.setMarkId(mark.getMarkId());
		message.setCategory(mark.getCategory());
		message.setCountrycode(mark.getCountryCode());
		message.setDescription(mark.getDescription());
		message.setVideoUrl(mark.getVideoUrl());
		message.setImageUrl(mark.getImageUrl());
		message.setLocality(mark.getLocality());
		message.setNature(mark.getReason());
		message.setSeverity(mark.getSeverity());
		message.setTransportation(mark.getTransportation());
		message.setUsername(mark.getUserName());
		message.setDate(mark.getLastUpdatedTime());
		message.setLocation(mark.getLocation());
		message.setLikeCount(mark.getLikeCount());
		message.setStarCount(mark.getStarCount());
		message.setDislikeCount(mark.getDislikeCount());
		
		return message;
	}

}
