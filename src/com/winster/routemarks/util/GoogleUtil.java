package com.winster.routemarks.util;

import com.winster.routemarks.client.vo.GeocodeAddressComponents;
import com.winster.routemarks.constants.GeoAddressConstants;

public enum GoogleUtil {
	INSTANCE;

	public String getAddress(GeocodeAddressComponents[] address, GeoAddressConstants level) {
		if(address!=null) {
			for(GeocodeAddressComponents component: address){
				if(component.getTypes()!=null) {
					for(String type : component.getTypes()){
						if(level.type.equals(type)) {
							return (level.category.equals("short")) ?component.getShort_name() :component.getLong_name();
						}
					}
				}
			}
		}
		return null;
	}

}
