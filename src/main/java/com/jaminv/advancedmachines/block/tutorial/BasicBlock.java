package com.jaminv.advancedmachines.block.tutorial;

import com.jaminv.advancedmachines.AdvancedMachines;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.creativetab.CreativeTabs;

public class BasicBlock extends Block {

	public BasicBlock( String unlocalizedName, Material mat ) {
		super( mat );
		this.setBlockName( unlocalizedName );
		this.setBlockTextureName( AdvancedMachines.MODID + ":" + unlocalizedName );
		this.setCreativeTab( CreativeTabs.tabBlock );
		this.setHardness( 2.0F );
		this.setResistance( 6.0F );
		this.setLightLevel( 1.0F );
		this.setHarvestLevel( "pickaxe", 3 );
		this.setStepSound( soundTypeMetal );
	}

}
