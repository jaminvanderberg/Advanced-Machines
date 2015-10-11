package com.jaminv.advancedmachines;

import com.jaminv.advancedmachines.block.ModBlocks;
import com.jaminv.advancedmachines.crafting.ModCrafting;
import com.jaminv.advancedmachines.gui.ModGuiHandler;
import com.jaminv.advancedmachines.item.ModItems;

import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.network.NetworkRegistry;

public class CommonProxy {
	
	public void preInit( FMLPreInitializationEvent e ) {
		ModItems.init();
		ModBlocks.init();
		ModCrafting.init();
	}
	
	public void init( FMLInitializationEvent e ) {
		NetworkRegistry.INSTANCE.registerGuiHandler( AdvancedMachines.instance, new ModGuiHandler() );
	}
	
	public void postInit( FMLPostInitializationEvent e ) {
		
	}

}
