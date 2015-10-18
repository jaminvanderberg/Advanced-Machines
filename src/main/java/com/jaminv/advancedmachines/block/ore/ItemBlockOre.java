package com.jaminv.advancedmachines.block.ore;

import net.minecraft.block.Block;
import net.minecraft.item.ItemBlockWithMetadata;
import net.minecraft.item.ItemStack;

public class ItemBlockOre extends ItemBlockWithMetadata {

	public ItemBlockOre( Block block ) {
		super( block, block );
	}

	@Override
	public String getUnlocalizedName( ItemStack stack ) {
		return this.getUnlocalizedName() + BlockOre.ORETYPES[stack.getItemDamage()];
	}

}
