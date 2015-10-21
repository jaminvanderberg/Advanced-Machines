package com.jaminv.advancedmachines.block;

import java.util.Arrays;

import cofh.api.energy.IEnergyReceiver;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagIntArray;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.server.gui.IUpdatePlayerListBox;
import net.minecraft.tileentity.TileEntity;

public abstract class BaseMachineTileEntity extends TileEntity implements IInventory {
	
	public short facing;
	
	public abstract boolean isActive();
	
	public short getFacing() { return facing; }
	
	@Override
	public void writeToNBT( NBTTagCompound nbt ) {
		super.writeToNBT( nbt );
		
		nbt.setShort( "facing", facing );
	}	

	@Override
	public void readFromNBT( NBTTagCompound nbt ) {
		super.readFromNBT( nbt );
		
		facing = nbt.getShort( "facing" );
	}	
	
}
