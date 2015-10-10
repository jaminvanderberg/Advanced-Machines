package com.jaminv.advancedmachines.block;

import cpw.mods.fml.common.registry.GameRegistry;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;

public class ModBlocks {

	public static Block testBlock;
	public static Block multitextureBlock;
	public static Block metaBlock;
	public static Block tileEntityBlock;
	
	public static final void init() {
		GameRegistry.registerBlock( testBlock = new BasicBlock( "testBlock", Material.iron ), "testBlock" );
		GameRegistry.registerBlock( multitextureBlock = new MultitextureBlock( "multitextureBlock", Material.cloth ), "multitextureBlock" );
		GameRegistry.registerBlock( metaBlock = new MetaBlock( "metablock", Material.cloth ), ItemBlockMetaBlock.class, "metablock" );
		
		GameRegistry.registerTileEntity( ModTileEntity.class, "tutorial_tile_entity" );
		GameRegistry.registerBlock( tileEntityBlock = new ModBlockTileEntity( "tile_entity" ), "tile_entity" );
	}
	
}
