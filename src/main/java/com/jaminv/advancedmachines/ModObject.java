package com.jaminv.advancedmachines;

public enum ModObject {
	blockOre,
	
	blockMachine,
	blockMobFarm,
	
	ingotTitanium,
	
	itemSoulCage;
	
	public final String unlocalizedName;
	
	private ModObject() {
		unlocalizedName = name();
	}
}
