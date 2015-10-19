package com.jaminv.advancedmachines.block.mobfarm;

import com.jaminv.advancedmachines.ModObject;

import cpw.mods.fml.common.registry.GameRegistry;

public class MobFarmSetup {
	public static MobFarmBlock mobfarmBlock;

	public static void setupBlocks() {
		GameRegistry.registerBlock( mobfarmBlock = new MobFarmBlock(), ModObject.blockMobFarm.unlocalizedName );
		GameRegistry.registerTileEntity( MobFarmTileEntity.class, ModObject.blockMobFarm.unlocalizedName + "TileEntity" );
	}
	
	public static void setupCrafting() {
		
	}
}
