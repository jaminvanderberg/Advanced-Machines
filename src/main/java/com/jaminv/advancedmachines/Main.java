package com.jaminv.advancedmachines;

import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.Mod.EventHandler;
import cpw.mods.fml.common.Mod.Instance;
import cpw.mods.fml.common.SidedProxy;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;

@Mod( modid = Main.MODID, name = Main.MODNAME, version = Main.VERSION )
public class Main {

	public static final String MODID = "advancedmachines";
	public static final String MODNAME = "Advanded Machines";
	public static final String VERSION = "0.0.1";
	
	@Instance
	public static Main instance = new Main();
	
	@SidedProxy( clientSide = "com.jaminv.advancedmachines.ClientProxy", serverSide = "com.jaminv.advancedmachines.ServerProxy" )
	public static CommonProxy proxy;
	
	@EventHandler
	public void preInit( FMLPreInitializationEvent e ) {
		System.out.println( "[" + MODNAME + "] PreInitialization Start." );
		
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
}
