package com.jaminv.advancedmachines.block.ore;

import java.util.List;

import com.jaminv.advancedmachines.AdvancedMachines;
import com.jaminv.advancedmachines.ModObject;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IIcon;

public class BlockOre extends Block {
	
	public static final String[] ORETYPES = { "Titanium" };
	public static final IIcon[] TEXTURES = new IIcon[ORETYPES.length];
	public static int[] HARVESTLEVEL = { 3 };
	
	public BlockOre() {
		super( Material.rock );
		this.setBlockName( ModObject.blockOre.unlocalizedName );
		this.setCreativeTab( CreativeTabs.tabBlock );
		this.setHardness( 3.0F );
		this.setResistance( 5.0F );
		
		setHarvestLevel( "pickaxe", 2 );
		for ( int i = 0; i < HARVESTLEVEL.length; i++ ) {
			setHarvestLevel( "pickaxe", HARVESTLEVEL[i], i );
		}
	}

	@Override
	public void getSubBlocks( Item item, CreativeTabs tab, List list ) {
		for ( int i = 0; i < ORETYPES.length; i++ ) {
			list.add( new ItemStack( item, 1, i ) );
		}
	}
	
	@Override
	public void registerBlockIcons( IIconRegister reg ) {
		for ( int i = 0; i < ORETYPES.length; i++ ) {
			TEXTURES[i] = reg.registerIcon( AdvancedMachines.MODID + ":" + ModObject.blockOre.unlocalizedName + ORETYPES[i] );
		}
	}
	
	@Override
	public IIcon getIcon( int side, int meta ) {
		return TEXTURES[meta];
	}

	@Override
	public int damageDropped( int meta ) {
		return meta;
	}
	
}
