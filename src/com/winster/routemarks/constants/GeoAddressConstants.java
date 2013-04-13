package com.winster.routemarks.constants;

public enum GeoAddressConstants {
	COUNTRYCODE("country","short"),
	COUNTRY("country","long"),
	LEVEL1CODE("administrative_area_level_1","short"), 
	LEVEL1("administrative_area_level_1","long"),
	LEVEL2CODE("administrative_area_level_2","short"), 
	LEVEL2("administrative_area_level_2","long"), 
	LOCALITYCODE("locality","short"), 
	LOCALITY("locality","long"), 
	ROUTE("route","short"), 
	STREET_NUMBER("street_number","short"),
	POSTAL_CODE("postal_code","short");
	
	public final String type;
	public final String category;
	
	private GeoAddressConstants(String type, String category) {
		this.type = type;
		this.category = category;
	}
}
