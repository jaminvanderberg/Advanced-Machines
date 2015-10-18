package com.jaminv.advancedmachines;

import com.jaminv.advancedmachines.config.Config;

import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.Mod.EventHandler;
import cpw.mods.fml.common.Mod.Instance;
import cpw.mods.fml.common.SidedProxy;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.event.FMLServerStartingEvent;

@Mod( modid = AdvancedMachines.MODID, name = AdvancedMachines.MODNAME, version = AdvancedMachines.VERSION )
public class AdvancedMachines {

	public static final String MODID = "advancedmachines";
	public static final String MODNAME = "Advanded Machines";
	public static final String VERSION = "0.0.1";
	
	@Instance
	public static AdvancedMachines instance = new AdvancedMachines();
	
	@SidedProxy( clientSide = "com.jaminv.advancedmachines.ClientProxy", serverSide = "com.jaminv.advancedmachines.ServerProxy" )
	public static CommonProxy proxy;
	
	private static int modGuiIndex = 0;
	
	public static final int GUI_MACHINE = modGuiIndex++;
	
	@EventHandler
	public void preInit( FMLPreInitializationEvent e ) {
		System.out.println( "[" + MODNAME + "] PreInitialization Start." );
		
		Config.load( e );
		proxy.preInit( e );
		
		System.out.println( "[" + MODNAME + "] PreInitialization End." );
	}
	
	@EventHandler
	public void init( FMLInitializationEvent e ) {
		System.out.println( "[" + MODNAME + "] Initialization Start." );
		
		proxy.init( e );
		
		System.out.println( "[" + MODNAME + "] Initialization End." );
	}
	
	@EventHandler
	public void postInit( FMLPostInitializationEvent e ) {
		System.out.println( "[" + MODNAME + "] PostInitialization Start." );
		
		proxy.postInit( e );
		
		System.out.println( "[" + MODNAME + "] PostInitialization End." );
	}
	
	@EventHandler
	public void serverStart( FMLServerStartingEvent e ) {
		proxy.serverStart( e );
	}
}
