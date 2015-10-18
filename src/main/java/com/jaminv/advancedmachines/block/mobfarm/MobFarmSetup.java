package com.jaminv.advancedmachines.block.mobfarm;

import com.jaminv.advancedmachines.ModObject;

import cpw.mods.fml.common.registry.GameRegistry;

public class MobFarmSetup {
	public static MobFarmBlock machineBlock;

	public static void setupBlocks() {
		GameRegistry.registerBlock( machineBlock = new MobFarmBlock( ModObject.blockMobFarm.unlocalizedName ),
				ModObject.blockMobFarm.unlocalizedName );
		GameRegistry.registerTileEntity( MobFarmTileEntity.class, ModObject.blockMobFarm.unlocalizedName + "TileEntity" );
	}
	
	public static void setupCrafting() {
		
	}
}
