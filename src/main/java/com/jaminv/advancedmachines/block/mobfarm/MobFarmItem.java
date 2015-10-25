package com.jaminv.advancedmachines.block.mobfarm;

import net.minecraft.block.Block;
import net.minecraft.item.ItemBlockWithMetadata;
import net.minecraft.item.ItemStack;

public class MobFarmItem extends ItemBlockWithMetadata {

	public MobFarmItem( Block block ) {
		super( block, block );
	}

	@Override
	public String getUnlocalizedName( ItemStack stack ) {
		return this.getUnlocalizedName() + MobFarmBlock.MACHINETYPES[stack.getItemDamage()];
	}

}
