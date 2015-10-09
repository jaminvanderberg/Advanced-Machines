package com.jaminv.advancedmachines.block;

import java.util.List;

import com.jaminv.advancedmachines.Main;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IIcon;

public class MetaBlock extends Block {

	public MetaBlock( String unlocalizedName, Material mat ) {
		super( mat );
		this.setBlockName( unlocalizedName );
		this.setBlockTextureName( Main.MODID + ":" + unlocalizedName );
		this.setCreativeTab( CreativeTabs.tabBlock );
		this.setHardness( 2.0F );
		this.setResistance( 6.0F );
		this.setStepSound( soundTypeGravel );
	}

	public IIcon[] icons = new IIcon[6];

	@Override
	public void registerBlockIcons( IIconRegister reg ) {
		for ( int i = 0; i < 6; i++ ) {
			this.icons[i] = reg.registerIcon( this.textureName + "_" + i );
		}
	}
	
	@Override
	public IIcon getIcon( int side, int meta ) {
		if ( meta > 5 ) {
			meta = 0;
		}
		return this.icons[meta];
	}

	@Override
	public int damageDropped( int meta ) {
		return meta;
	}

	@Override
	public void getSubBlocks( Item item, CreativeTabs tab, List list ) {
		for ( int i = 0; i < 6; i++ ) {
			list.add( new ItemStack( item, 1, i ) );
		}
	}
	
}
