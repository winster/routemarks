package com.winster.routemarks.client.vo;

import lombok.Data;

public @Data class GeocodeAddressComponents {
	private String long_name;
	private String short_name;
	private String[] types;
}
