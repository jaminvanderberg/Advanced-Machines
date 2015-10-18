package com.jaminv.advancedmachines.block.machine;

import com.jaminv.advancedmachines.ModObject;

import cpw.mods.fml.common.registry.GameRegistry;

public class MachineSetup {
	public static MachineBlock machineBlock;

	public static void setupBlocks() {
		GameRegistry.registerBlock( machineBlock = new MachineBlock( ModObject.blockMachine.unlocalizedName ),
				ModObject.blockMachine.unlocalizedName );
		GameRegistry.registerTileEntity( MachineTileEntity.class, ModObject.blockMachine.unlocalizedName + "TileEntity" );
	}
	
	public static void setupCrafting() {
		
	}
}
