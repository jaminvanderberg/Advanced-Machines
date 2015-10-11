package com.jaminv.advancedmachines.block.machine;

import cpw.mods.fml.common.registry.GameRegistry;

public class MachineSetup {
	public static MachineBlock machineBlock;

	public static void setupBlocks() {
		GameRegistry.registerBlock( machineBlock = new MachineBlock( "machine" ), "machine" );
		GameRegistry.registerTileEntity( MachineTileEntity.class, "advancedmachines_machine_tile_entity" );
	}
	
	public static void setupCrafting() {
		
	}
}
