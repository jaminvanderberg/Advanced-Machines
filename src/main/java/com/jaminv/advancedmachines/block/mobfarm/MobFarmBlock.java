package com.jaminv.advancedmachines.block.mobfarm;

import java.util.List;

import com.jaminv.advancedmachines.AdvancedMachines;
import com.jaminv.advancedmachines.ModObject;
import com.jaminv.advancedmachines.block.BaseMachineBlock;

import cofh.api.energy.IEnergyStorage;
import net.minecraft.block.Block;
import net.minecraft.block.BlockContainer;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.IIcon;
import net.minecraft.world.World;

public class MobFarmBlock extends BaseMachineBlock {

	public static final String[] MACHINETYPES = { "Basic", "Advanced", "Ultimate" };
	public static IIcon[][] face;
	
	protected MobFarmBlock() {
		super( ModObject.blockMobFarm );
	}
	
	@Override
	public void registerBlockIcons( IIconRegister reg ) {
		super.registerBlockIcons( reg );
		
		this.face = new IIcon[1][2];
		this.face[0][0] = reg.registerIcon( AdvancedMachines.MODID + ":MachineMobFarm" );
		this.face[0][1] = reg.registerIcon( AdvancedMachines.MODID + ":MachineMobFarmActive" );
	}
	
	@Override
	public void getSubBlocks( Item item, CreativeTabs tab, List list ) {
		for ( int i = 0; i < MACHINETYPES.length; i++ ) {
			list.add( new ItemStack( item, 1, i ) );
		}
	}

	@Override
	public int damageDropped( int i ) {
		return i;
	}

	@Override
	protected IIcon getFace( int meta, boolean active ) {
		return face[0][active ? 1 : 0];
	}	

	@Override
	public TileEntity createNewTileEntity( World worldIn, int meta ) {
		return new MobFarmTileEntity();
	}

	@Override
	public void breakBlock( World worldIn, int posX, int posY, int posZ, Block block, int state ) {
		IInventory inventory = worldIn.getTileEntity( posX, posY, posZ ) instanceof IInventory ? (IInventory)worldIn.getTileEntity( posX, posY, posZ ) : null;
		
		if (inventory != null){
			// For each slot in the inventory
			for (int i = 0; i < inventory.getSizeInventory(); i++){
				// If the slot is not empty
				if (inventory.getStackInSlot( i ) != null)
				{
					// Create a new entity item with the item stack in the slot
					EntityItem item = new EntityItem(worldIn, posX + 0.5, posY + 0.5, posZ + 0.5, inventory.getStackInSlot( i ) );

					// Apply some random motion to the item
					float multiplier = 0.1f;
					float motionX = worldIn.rand.nextFloat() - 0.5f;
					float motionY = worldIn.rand.nextFloat() - 0.5f;
					float motionZ = worldIn.rand.nextFloat() - 0.5f;

					item.motionX = motionX * multiplier;
					item.motionY = motionY * multiplier;
					item.motionZ = motionZ * multiplier;

					// Spawn the item in the world
					worldIn.spawnEntityInWorld(item);
				}
			}

			// Clear the inventory so nothing else (such as another mod) can do anything with the items
		    for (int i = 0; i < inventory.getSizeInventory(); i++) {
		        inventory.setInventorySlotContents(i, null);
			}
		}

		// Super MUST be called last because it removes the tile entity
		super.breakBlock(worldIn, posX, posY, posZ, block, state);
	}	

	@Override
	public boolean onBlockActivated( World world, int posX, int posY, int posZ, EntityPlayer player, int side, float hitX, float hitY, float hitZ ) {
		if ( ! world.isRemote ) {
			player.openGui( AdvancedMachines.instance, AdvancedMachines.GUI_MOBFARM, world, posX, posY, posZ );
		}
		return true;
	}

	
	
}
