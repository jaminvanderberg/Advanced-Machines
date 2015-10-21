package com.jaminv.advancedmachines.mobregistry;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.jaminv.advancedmachines.mobregistry.minecraft.Skeleton;

public class MobRegistry {
	
	private static Map<String, ArrayList<MobEntry>> registry = new HashMap<String, ArrayList<MobEntry>>();
	
	public static void registerMob( MobEntry entry ) {
		if ( ! registry.containsKey( entry.getId() ) ) {
			registry.put( entry.getId(), new ArrayList<MobEntry>() );
		}
		registry.get( entry.getId() ).add( entry );
	}
	
	public static ArrayList<MobEntry> getMobEntryList( String entityid ) {
		if ( ! registry.containsKey( entityid ) ) { return null; }
		return registry.get( entityid );
	}
	
	public static boolean hasEntry( String entityid ) {
		return registry.containsKey( entityid );
	}

	public static void serverStart() {
		registerMob( new Skeleton() );	
	}
	
}
