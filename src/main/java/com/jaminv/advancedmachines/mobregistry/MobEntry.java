package com.jaminv.advancedmachines.mobregistry;

import java.util.ArrayList;
import java.util.Random;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

public abstract class MobEntry {

	protected String entityid;
	
	public String getId() { return entityid; }
	
	public abstract ArrayList<ItemStack> getDrops( int count, int loot, boolean player );
	
	public abstract int getXp();
	
	public ItemStack randomDrop( Item item, int min, int max, int count ) {
		Random rand = new Random();
		int drops = 0;
		for ( int i = 0; i < count; i++ ) {
			drops += rand.nextInt( max + 1 ) + min;
		}
		return new ItemStack( item, drops );
	}
}
