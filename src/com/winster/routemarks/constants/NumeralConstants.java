package com.winster.routemarks.constants;

public enum NumeralConstants {
	
	COMMUNITY_MESSAGE_LIMIT(20),
	POINTS_MARK(10),
	POINTS_LIKE(1),
	POINTS_STAR(2),
	POINTS_DISLIKE(1);
	
	private final double value;
	
	private NumeralConstants(double value) {
		this.value = value;
	}
	
	public double getValue(){
		return this.value;
	}
	
}
