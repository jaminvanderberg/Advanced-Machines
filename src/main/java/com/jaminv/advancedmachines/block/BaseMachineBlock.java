package com.jaminv.advancedmachines.block;

import com.jaminv.advancedmachines.AdvancedMachines;
import com.jaminv.advancedmachines.ModObject;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.block.BlockContainer;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.IIcon;
import net.minecraft.util.MathHelper;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

public abstract class BaseMachineBlock extends BlockContainer {
	
	protected String[] machineSideTexture = { "MachineBottom", "MachineTop", "MachineSide" };
	
	@SideOnly( Side.CLIENT )
	public IIcon[] machineSide;
	
	private final ModObject mo;
	
	protected BaseMachineBlock( ModObject mo ) {
		super( Material.iron );
		this.setBlockName( mo.unlocalizedName );
		this.setCreativeTab( CreativeTabs.tabBlock );
		this.mo = mo;
	}

	@Override
	public void onBlockPlacedBy( World world, int x, int y, int z, EntityLivingBase player, ItemStack stack ) {
		super.onBlockPlacedBy( world, x, y, z, player, stack );
		int heading = MathHelper.floor_double( player.rotationYaw * 4.0F / 360.0F + 0.5D) & 3;
		TileEntity te = world.getTileEntity( x, y, z );
		if ( te instanceof BaseMachineTileEntity ) {
			BaseMachineTileEntity bmte = (BaseMachineTileEntity)te;
			bmte.facing = getFacingFromHeading( heading );
			System.out.println( "bmte.facing = " +  bmte.facing );
		}
	}
	
	protected short getFacingFromHeading( int heading ) {
		switch ( heading ) {
		case 0:
			return 2;
		case 1:
			return 5;
		case 2:
			return 3;
		case 3:
		default:
			return 4;
		}
	}
	
	@Override
	@SideOnly( Side.CLIENT )
	public void registerBlockIcons( IIconRegister ir ) {
		machineSide = new IIcon[machineSideTexture.length];
		for ( int i = 0; i < machineSideTexture.length; i++ ) {
			machineSide[i] = ir.registerIcon( AdvancedMachines.MODID + ":" + machineSideTexture[i] );
		}
	}
	
	protected abstract IIcon getFace( int meta );
	
	@Override
	@SideOnly( Side.CLIENT )
	public IIcon getIcon( IBlockAccess world, int x, int y, int z, int side ) {
		TileEntity te = world.getTileEntity( x, y, z );
		short facing = 0;
		if ( te instanceof BaseMachineTileEntity ) {
			facing = ((BaseMachineTileEntity)te).getFacing();
		}
		System.out.println( "Facing: " + facing + ", Side: " + side );
		
		if ( side == facing ) {
			return this.getFace( world.getBlockMetadata( x, y,  z ) );
		}
		if ( side >= 2 ) { return this.machineSide[2]; }
		return this.machineSide[side];
	}
	
	@Override
	@SideOnly( Side.CLIENT )
	public IIcon getIcon( int side, int meta ) {
		if ( side == 3 ) {
			return this.getFace( meta );
		}
		if ( side > 2 ) {
			return this.machineSide[2];
		}
		return this.machineSide[side];
	}
}
