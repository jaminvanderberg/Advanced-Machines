package com.jaminv.advancedmachines.block;

import net.minecraft.block.BlockContainer;
import net.minecraft.block.material.Material;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;

public class ModBlockTileEntity extends BlockContainer {

	protected ModBlockTileEntity( String unlocalizedName ) {
		super( Material.iron );
		this.setBlockName( unlocalizedName );
		this.setCreativeTab( CreativeTabs.tabBlock );		
		this.setHardness( 2.0f );
		this.setResistance( 6.0f );
		this.setHarvestLevel( "pickaxe", 2 );
	}

	@Override
	public TileEntity createNewTileEntity( World worldIn, int meta ) {
		return new ModTileEntity();
	}



//	@Override
//	public void breakBlock( World world, BlockPos pos, IBlockState state ) {
//		super.breakBlock( world, pos, state );
//		world.removeTileEntity( pos );
//	}
//
//	@Override
//	public boolean onBlockEventReceived(World worldIn, BlockPos pos, IBlockState state, int eventID, int eventParam ) {
//		return super.onBlockEventReceived( worldIn, pos, state, eventId, eventParam );
//		TileEntity tileentity = worldIn.getTileEntity( pos );
//		return tileentity == null ? false: tileentity.receiveClientEvent( eventId, eventParam );
//	}
//	
	
}
