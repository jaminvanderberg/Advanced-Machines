package com.jaminv.advancedmachines.block;

import com.jaminv.advancedmachines.AdvancedMachines;
import com.jaminv.advancedmachines.block.machine.MachineBlock;
import com.jaminv.advancedmachines.block.machine.MachineSetup;
import com.jaminv.advancedmachines.block.machine.MachineTileEntity;
import com.jaminv.advancedmachines.block.tutorial.BasicBlock;
import com.jaminv.advancedmachines.block.tutorial.ItemBlockMetaBlock;
import com.jaminv.advancedmachines.block.tutorial.MetaBlock;
import com.jaminv.advancedmachines.block.tutorial.MultitextureBlock;
import com.jaminv.advancedmachines.gui.ModGuiHandler;

import cpw.mods.fml.common.network.NetworkRegistry;
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
		
		MachineSetup.setupBlocks();
	}
	
}
