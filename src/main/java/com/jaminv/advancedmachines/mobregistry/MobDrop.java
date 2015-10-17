package com.jaminv.advancedmachines.mobregistry;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

public class MobDrop {

	public int minDrop, maxDrop;
	public ItemStack item;
	public float chance;
	public boolean damage;
	
	public MobDrop( ItemStack i, int min, int max, float chance, boolean dam ) {
		this.item = i;
		this.minDrop = min;
		this.maxDrop = max;
		this.chance = chance;
		this.damage = dam;
	}
	
	public MobDrop( ItemStack i, int min, int max ) {
		this( i, min, max, 1F, false );
	}
	
	public MobDrop( ItemStack i, float chance, boolean dam ) {
		this( i, 1, 1, chance, dam );
	}
	public MobDrop( ItemStack i, float chance ) {
		this( i, 1, 1, chance, false );
	}
	
	public MobDrop( ItemStack i, boolean dam ) {
		this( i, 1, 1, 1F, dam );
	}
	public MobDrop( ItemStack i ) {
		this( i, 1, 1, 1F, false );
	}
	
	public MobDrop( Item i, int min, int max, float chance, boolean dam ) {
		this( new ItemStack( i ), min, max, chance, dam );
	}
	
	public MobDrop( Item i, int min, int max ) {
		this( new ItemStack( i ), min, max, 1F, false );
	}
	
	public MobDrop( Item i, float chance, boolean dam ) {
		this( new ItemStack( i ), 1, 1, chance, dam );
	}
	public MobDrop( Item i, float chance ) {
		this( new ItemStack( i ), 1, 1, chance, false );
	}
	
	public MobDrop( Item i, boolean dam ) {
		this( new ItemStack( i ), 1, 1, 1F, dam );
	}
	public MobDrop( Item i ) {
		this( new ItemStack( i ), 1, 1, 1F, false );
	}
	
}
