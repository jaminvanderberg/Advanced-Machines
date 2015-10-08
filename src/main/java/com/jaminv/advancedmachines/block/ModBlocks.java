package com.jaminv.advancedmachines.block;

import cpw.mods.fml.common.registry.GameRegistry;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;

public class ModBlocks {

	public static Block testBlock;
	
	public static final void init() {
		GameRegistry.registerBlock( testBlock = new BasicBlock( "testBlock", Material.iron ), "testBlock" );
	}
	
}
