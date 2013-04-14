package com.winster.routemarks.util;

import com.winster.routemarks.client.vo.TSRMessage;
import com.winster.routemarks.data.entity.Mark;

public class Converter {

	/**
	 * 
	 * @param mark
	 * @return
	 */
	public static TSRMessage convertMarkToMessage(Mark mark) {
		TSRMessage message = new TSRMessage();
		message.setCategory(mark.getCategory());
		message.setCountrycode(mark.getCountryCode());
		message.setDescription(mark.getDescription());
		message.setLocality(mark.getLocality());
		message.setNature(mark.getReason());
		message.setSeverity(mark.getSeverity());
		message.setTransportation(mark.getTransportation());
		message.setUsername(mark.getUserName());
		message.setDate(mark.getLastUpdatedTime());
		message.setLocation(mark.getLocation());
		return message;
	}

}
