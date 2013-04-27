package com.winster.routemarks.constants;

public enum DataConstants {
	
	MARKER_TABLE_NAME("19ZyjTyDCYccePHh_q-gYxP4oLfgS41lC_4oF1eA"),
	MARKER_COLUMN_MARKID("MarkId"),
	MARKER_COLUMN_TSRID("TSRId"),
	MARKER_COLUMN_USERNAME("UserName"),	
	MARKER_COLUMN_GROUPID("GroupId"),
	MARKER_COLUMN_LOCATION("Location"),
	MARKER_COLUMN_DATE("Date"),
	MARKER_COLUMN_VIDEO("Video"),
	MARKER_COLUMN_IMAGE("Image"),
	MARKER_COLUMN_CATEGORY("Category"),
	MARKER_COLUMN_TRANSPORTATION("Transportation"),
	MARKER_COLUMN_REASON("Reason"),
	MARKER_COLUMN_SEVERITY("Severity");
	
	private final String value;
	
	private DataConstants(String value) {
		this.value = value;
	}
	
	public String getValue(){
		return this.value;
	}
	
}
