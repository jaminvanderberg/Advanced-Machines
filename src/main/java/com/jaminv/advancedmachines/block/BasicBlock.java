package com.jaminv.advancedmachines.block;

import com.jaminv.advancedmachines.Main;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.creativetab.CreativeTabs;

public class BasicBlock extends Block {

	protected BasicBlock( String unlocalizedName, Material mat ) {
		super( mat );
		this.setBlockName( unlocalizedName );
		this.setBlockTextureName( Main.MODID + ":" + unlocalizedName );
		this.setCreativeTab( CreativeTabs.tabBlock );
		this.setHardness( 2.0F );
		this.setResistance( 6.0F );
		this.setLightLevel( 1.0F );
		this.setHarvestLevel( "pickaxe", 3 );
		this.setStepSound( soundTypeMetal );
	}

}
