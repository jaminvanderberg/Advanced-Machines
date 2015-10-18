package com.jaminv.advancedmachines.config;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import com.jaminv.advancedmachines.AdvancedMachines;

import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.common.config.Configuration;

public class Config {
	
	/**
	 * Config Section
	 */
	public static class Section {
		public final String name;
		public final String lang;
		
		public Section( String name, String lang ) {
			this.name = name;
			this.lang = lang;
			register();
		}
		
		private void register() {
			sections.add( this );
		}
		
		public String lc() {
			return name.toLowerCase( Locale.US );
		}
	}
	
	public static final List<Section> sections;
	
	static {
		sections = new ArrayList<Section>();
	}
	
	public static Configuration config;
	
// ========== //
//  Sections  //
// ========== //
	
	public static final Section sectionMobFarm = new Section( "Mob Farm", "mobfarm" );
	
// =================== //
//  Config Properties  //
// =================== //
	
	public static String[] mobfarmBlacklist = new String[0];
	public static boolean mobfarmAllowBosses = true;
	
// ============= //
//  Load Config  //
// ============= //
	
	public static void load( FMLPreInitializationEvent event ) {
		FMLCommonHandler.instance().bus().register( AdvancedMachines.instance );
		
		config = new Configuration( event.getSuggestedConfigurationFile() );
		syncConfig( false );
	}	

	public static void syncConfig( boolean load ) {
		try {
			if ( load ) {
				config.load();
			}
			Config.processConfig( config );
		} catch( Exception e ) {
			System.err.println( "AdvancedMachines configuration load error" );
			e.printStackTrace();
		} finally {
			if ( config.hasChanged() ) {
				config.save();
			}
		}
	}
	
	public static void processConfig( Configuration config ) {
		mobfarmBlacklist = config.getStringList( "mobfarmBlacklist", sectionMobFarm.name, mobfarmBlacklist,
			"Entities listed here can not be captured with the Soul Cage or used in the Mob Farm" );
		mobfarmAllowBosses = config.getBoolean( "mobfarmAllowBosses", sectionMobFarm.name, mobfarmAllowBosses,
			"Prevent mobs with a boss bar from being captured in a Soul Cage or used in the Mob Farm" );
	}
	
	private Config() {}
}
