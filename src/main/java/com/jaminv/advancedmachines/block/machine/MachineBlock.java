package com.jaminv.advancedmachines.block.machine;

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
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.IIcon;
import net.minecraft.world.World;

public class MachineBlock extends BaseMachineBlock {
	
	public static final String[] FACE = { "MachineFurnace" };
	protected IIcon[] face;

	protected MachineBlock( String unlocalizedName ) {
		super( ModObject.blockMachine );
	}
	
	@Override
	public void registerBlockIcons( IIconRegister reg ) {
		super.registerBlockIcons( reg );
		
		face = new IIcon[FACE.length];
		for ( int i = 0; i < FACE.length; i++ ) {
			this.face[i] = reg.registerIcon( AdvancedMachines.MODID + ":" + FACE[i] );
		}
	}

	@Override
	protected IIcon getFace(int meta) {
		return face[meta];
	}
	
	@Override
	public TileEntity createNewTileEntity( World worldIn, int meta ) {
		return new MachineTileEntity();
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
			player.openGui( AdvancedMachines.instance, AdvancedMachines.GUI_MACHINE, world, posX, posY, posZ );
		}
		return true;
	}

}
