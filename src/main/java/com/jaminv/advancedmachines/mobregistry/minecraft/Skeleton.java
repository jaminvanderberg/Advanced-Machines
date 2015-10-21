package com.jaminv.advancedmachines.mobregistry.minecraft;

import java.util.ArrayList;

import com.jaminv.advancedmachines.mobregistry.MobEntry;

import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

public class Skeleton extends MobEntry {

	@Override
	public ArrayList<ItemStack> getDrops(int count, int loot, boolean player) {
		ArrayList<ItemStack> drops = new ArrayList<ItemStack>();
		
		drops.add( randomDrop( Items.bone, 0, 2, count ) );
		drops.add( randomDrop( Items.arrow, 0, 2, count ) );
		
		return drops;
	}

	@Override
	public int getXp() {
		// TODO Auto-generated method stub
		return 0;
	}
	
}
