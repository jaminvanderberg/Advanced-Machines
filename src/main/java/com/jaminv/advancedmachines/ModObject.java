package com.jaminv.advancedmachines;

public enum ModObject {
	itemSoulCage;
	
	public final String unlocalizedName;
	
	private ModObject() {
		unlocalizedName = name();
	}
}
