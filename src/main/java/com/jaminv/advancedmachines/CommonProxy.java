package com.jaminv.advancedmachines;

import com.jaminv.advancedmachines.block.ModBlocks;
import com.jaminv.advancedmachines.gui.ModGuiHandler;
import com.jaminv.advancedmachines.item.ModItems;
import com.jaminv.advancedmachines.mobregistry.MobRegistry;

import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.event.FMLServerStartingEvent;
import cpw.mods.fml.common.network.NetworkRegistry;

public class CommonProxy {
	
	public void preInit( FMLPreInitializationEvent e ) {
		ModItems.preInit();
		ModBlocks.preInit();
	}
	
	public void init( FMLInitializationEvent e ) {
		NetworkRegistry.INSTANCE.registerGuiHandler( AdvancedMachines.instance, new ModGuiHandler() );
		
		ModItems.init();
		ModBlocks.init();
	}
	
	public void postInit( FMLPostInitializationEvent e ) {
		ModItems.postInit();
		ModBlocks.postInit();
	}
	
	public void serverStart( FMLServerStartingEvent e ) {
		MobRegistry.init();
	}
}
