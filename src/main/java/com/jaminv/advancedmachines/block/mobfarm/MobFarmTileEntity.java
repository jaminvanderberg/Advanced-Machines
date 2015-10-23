package com.jaminv.advancedmachines.block.mobfarm;

import java.util.ArrayList;
import java.util.Arrays;

import com.jaminv.advancedmachines.block.BaseMachineTileEntity;
import com.jaminv.advancedmachines.item.ItemSoulCage;
import com.jaminv.advancedmachines.mobregistry.MobEntry;
import com.jaminv.advancedmachines.mobregistry.MobRegistry;

import cofh.api.energy.IEnergyReceiver;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.FurnaceRecipes;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagIntArray;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.Packet;
import net.minecraft.network.play.server.S35PacketUpdateTileEntity;
import net.minecraft.server.gui.IUpdatePlayerListBox;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityFurnace;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.ChatComponentTranslation;
import net.minecraft.util.IChatComponent;
import net.minecraft.util.MathHelper;
import net.minecraftforge.common.util.ForgeDirection;

public class MobFarmTileEntity extends BaseMachineTileEntity implements IUpdatePlayerListBox, IEnergyReceiver {
	
	public static final int RF_CAPACITY = 60000;
	public static final int RF_MAX_RECEIVE = 1000;
	
	public static final int SOULCAGE_SLOTS_COUNT = 1;
	public static final int OUTPUT_SLOTS_COUNT = 20;
	public static final int VOID_SLOTS_COUNT = 8;
	public static final int TOTAL_SLOTS_COUNT = SOULCAGE_SLOTS_COUNT + OUTPUT_SLOTS_COUNT + VOID_SLOTS_COUNT;
	
	public static final int FIRST_SOULCAGE_SLOT = 0;
	public static final int FIRST_OUTPUT_SLOT = FIRST_SOULCAGE_SLOT + SOULCAGE_SLOTS_COUNT;
	public static final int FIRST_VOID_SLOT = FIRST_OUTPUT_SLOT + OUTPUT_SLOTS_COUNT;

	private ItemStack[] itemStacks = new ItemStack[TOTAL_SLOTS_COUNT];
	private String customName;
	
	private int[] hpRemaining = new int[SOULCAGE_SLOTS_COUNT];
	private int[] maxHp = new int[SOULCAGE_SLOTS_COUNT];
	private int[] mobCount = new int[SOULCAGE_SLOTS_COUNT];
	private String[] entityId = new String[SOULCAGE_SLOTS_COUNT];
	private String[] mobName = new String[SOULCAGE_SLOTS_COUNT];
	
	private boolean hasSoul = false;
	private int wait = 20;
	private int energy = 0;	
		
	/* Upgrade Settings */
	private int speed = 4;
	private int count = 1;
	private int rfconsume = 1000;
	private int loot = 0;
	
	@Override
	public boolean isActive() { return hasSoul; }

	private int iteration = 0;
	
	@Override
	public void updateEntity() {
		super.updateEntity();
		
		if ( ! hasSoul ) { return; }
		wait--;
		if ( wait > 0 ) { return; }
		wait = 20;
		iteration++;
		System.out.println( (this.worldObj.isRemote ? "server" : "client" ) + " iteration: " + iteration );

		for( int i = 0; i < SOULCAGE_SLOTS_COUNT; i++ ) {
			if ( maxHp[i] < 0 ) { continue; }
			
			System.out.println( "hpRemaining[" + i + "] = " + hpRemaining[i] );

			if ( hpRemaining[i] <= 0 ) {
				System.out.println( "spawnMob(" + i + ")" );					
				spawnMob( i );
			} else {
				if ( energy >= mobCount[i] * rfconsume * count ) {
					hpRemaining[i] -= speed;
					energy -= mobCount[i] * rfconsume * count;
					
					// Run the kill process on the server only, then sync to the client
					// Prevents mis-sync from item drop randomization
					if ( hpRemaining[i] <= 0 && ! this.worldObj.isRemote ) {
						System.out.println( "killMob(" + i + ")" );
						killMob( i );
					}
				}
			}
		}
	}
	
	/**
	 * Add Mob Drops to the Output Slots
	 * @param id Soulcage slot number
	 */
	private void killMob( int id ) {
		ArrayList<ItemStack> drop = new ArrayList<ItemStack>();
		ArrayList<MobEntry> mobentry = MobRegistry.getMobEntryList( this.entityId[id] );
		System.out.println( mobentry );
		if ( mobentry != null ) {
			for( int i = 0; i < mobentry.size(); i++ ) {
				drop.addAll( mobentry.get(i).getDrops( this.mobCount[id], this.loot, false ) );
				System.out.println( drop );
			}
			
			for( ItemStack item : drop ) {
				System.out.println( item );
				for ( int outputSlot = FIRST_OUTPUT_SLOT; outputSlot < FIRST_OUTPUT_SLOT + OUTPUT_SLOTS_COUNT; outputSlot++ ) {
					ItemStack outputStack = itemStacks[outputSlot];
					if ( outputStack == null ) {
						int put = Math.min( item.stackSize, item.getMaxStackSize() );
						System.out.println( "Empty slot: " + outputSlot + ", put = " + put );
						itemStacks[outputSlot] = new ItemStack( item.getItem(), put );
						item.stackSize -= put;
						if ( item.stackSize <= 0 ) { break; }
						continue;
					} else if ( outputStack.getItem() == item.getItem() 
						&& ( ! outputStack.getHasSubtypes() || outputStack.getItemDamage() == item.getItemDamage() )
						&& ItemStack.areItemStackTagsEqual( outputStack, item )
					) {
						int put = Math.min( item.stackSize, outputStack.getMaxStackSize() );
						itemStacks[outputSlot].stackSize += put;
						item.stackSize -= put;
						if ( item.stackSize <= 0 ) { break; }
						continue;					
					}
				}
			}
			
			markDirty();
		}
	}
	
	private void spawnMob( int id ) {		
		if ( energy >= maxHp[id] * count * rfconsume ) {
			hpRemaining[id] = maxHp[id];
			mobCount[id] = count;
			energy -= maxHp[id] * count * rfconsume;
		}
	}
	
	private void updateSoul( int id ) {
		if ( id < FIRST_SOULCAGE_SLOT || id >= FIRST_SOULCAGE_SLOT + SOULCAGE_SLOTS_COUNT ) {
			return;
		}
		id -= FIRST_SOULCAGE_SLOT;
		
		ItemStack itemstack = this.itemStacks[id];
		this.maxHp[id] = 0;
		if ( itemstack != null ) {
			if ( itemstack.getItem() instanceof ItemSoulCage ) {
				ItemSoulCage soulcage = (ItemSoulCage)itemstack.getItem();
				Entity mob = soulcage.getSoulEntity( itemstack, this.getWorldObj() );
				
				if ( mob instanceof EntityLivingBase ) {
					this.maxHp[id] = (int)((EntityLivingBase)mob).getMaxHealth();
					this.mobName[id] = soulcage.getSoulEntityName( itemstack );
				}
			}
		}
		if ( this.maxHp[id] <= 0 ) { this.hpRemaining[id] = 0; }
		
		this.hasSoul = false;
		for( int i = 0; i < SOULCAGE_SLOTS_COUNT; i++ ) {
			if ( maxHp[i] > 0 ) { this.hasSoul = true; return; }
		}
	}
	
	public int getHp( int id ) { return this.hpRemaining[id]; }
	public int getMaxHp( int id ) { return this.maxHp[id]; }
	public double getHpPercent( int id ) {
		double fraction = hpRemaining[id] / (double)maxHp[id];
		return MathHelper.clamp_double( fraction, 0.0, 1.0 );
	}
	
// =========== //
//  Inventory  //
// =========== //
	
	/**
	 * Gets the number of slots in the inventory
	 */
	@Override
	public int getSizeInventory() {
		return itemStacks.length;
	}


	/**
	 * Gets the stack in the given slot
	 */
	@Override
	public ItemStack getStackInSlot( int index ) {
		if ( index < 0 || index >= this.getSizeInventory() ) {
			return null;
		}
		return this.itemStacks[index];
	}

	
	@Override
	public ItemStack decrStackSize( int index, int count ) {
		ItemStack itemstack = getStackInSlot( index );
		if ( itemstack == null ) { return null; }

		ItemStack itemstackRemoved;
		if ( itemstack.stackSize <= count ) {
			itemstackRemoved = itemstack;
			this.setInventorySlotContents( index, null );
		} else {
			itemstackRemoved = itemstack.splitStack( count );
			if ( itemstack.stackSize == 0 ) {
				this.setInventorySlotContents( index, null );
			}
		}
		
		markDirty();
		return itemstackRemoved;
	}

	@Override
	public void setInventorySlotContents( int index, ItemStack stack ) {
		if ( index < 0 || index >= this.getSizeInventory() ) {
			return;
		}
		
		if ( stack != null && stack.stackSize > this.getInventoryStackLimit() ) {
			stack.stackSize = this.getInventoryStackLimit();
		}
		
		if ( stack != null && stack.stackSize == 0 ) {
			stack = null;
		}

		this.itemStacks[index] = stack;
		
		this.updateSoul( index );
		this.markDirty();
	}
	
	@Override
	public int getInventoryStackLimit() {
		return 64;
	}

	@Override
	public boolean isUseableByPlayer( EntityPlayer player ) {
		if ( this.worldObj.getTileEntity( this.xCoord, this.yCoord, this.zCoord ) != this ) return false;
		final double X_CENTER_OFFSET = 0.5;
		final double Y_CENTER_OFFSET = 0.5;
		final double Z_CENTER_OFFSET = 0.5;
		final double MAXIMUM_DISTANCE_SQ = 8.0 * 8.0;
		return player.getDistanceSq( 
				this.xCoord + X_CENTER_OFFSET
				, this.yCoord + Y_CENTER_OFFSET
				, this.zCoord + Z_CENTER_OFFSET 
			) <= MAXIMUM_DISTANCE_SQ;
	}
	
	static public boolean isItemValidForSoulcage( ItemStack itemstack ) {
		if ( ! ( itemstack.getItem() instanceof ItemSoulCage ) ) { return false; }
		ItemSoulCage soulcage = (ItemSoulCage)itemstack.getItem();
		return soulcage.hasSoul( itemstack );
	}
	
	//static public boolean isItemValidForInputSlot( ItemStack itemstack ) {
	//	return getSmeltingResultForItem( itemstack ) != null;
	//}
	
	static public boolean isItemValidForOutputSlot( ItemStack itemstack ) {
		return false;
	}

	@Override
	public void writeToNBT( NBTTagCompound nbt ) {
		super.writeToNBT( nbt );
		
		NBTTagList list = new NBTTagList();
		for ( int i = 0; i < this.getSizeInventory(); i++ ) {
			if ( this.getStackInSlot( i ) != null ) {
				NBTTagCompound stackTag = new NBTTagCompound();
				stackTag.setByte( "Slot", (byte) i );
				this.getStackInSlot( i ).writeToNBT( stackTag );
				list.appendTag( stackTag );
			}
		}
		nbt.setTag( "Items", list );
		
		nbt.setTag( "hpRemaining", new NBTTagIntArray( this.hpRemaining ) );
		nbt.setTag( "maxHp", new NBTTagIntArray( this.maxHp ) );
		nbt.setTag( "mobCount", new NBTTagIntArray( this.mobCount ) );

		for ( int i = 0; i < SOULCAGE_SLOTS_COUNT; i++ ) {
			if ( this.mobName[i] != null && ! this.mobName[i].equals( "" ) ) { nbt.setString( "mobName" + i, this.mobName[i] ); }
			if ( this.entityId[i] != null && ! this.entityId[i].equals( "" ) ) { nbt.setString( "entityId" + i, this.entityId[i] ); }
		}
		
		nbt.setBoolean( "hasSoul", this.hasSoul );
		nbt.setInteger( "wait", this.wait );
		nbt.setInteger( "energy", this.energy );
		
		nbt.setInteger( "speed", this.speed );
		nbt.setInteger( "count", this.count );
		nbt.setInteger( "rfconsume", this.rfconsume );
		nbt.setInteger( "loot", this.loot );
	}
	
	@Override
	public void readFromNBT( NBTTagCompound nbt ) {
		super.readFromNBT( nbt );
		
		NBTTagList list = nbt.getTagList( "Items", 10 );
		for ( int i = 0; i < list.tagCount(); i++ ) {
			NBTTagCompound stackTag = list.getCompoundTagAt( i );
			byte slot = stackTag.getByte( "Slot" );
			this.setInventorySlotContents( slot, ItemStack.loadItemStackFromNBT( stackTag ) );
		}
		
		this.hpRemaining = Arrays.copyOf( nbt.getIntArray( "hpRemaining" ), SOULCAGE_SLOTS_COUNT );
		this.maxHp = Arrays.copyOf( nbt.getIntArray( "maxHp" ), SOULCAGE_SLOTS_COUNT );
		this.mobCount = Arrays.copyOf( nbt.getIntArray( "mobCount" ), SOULCAGE_SLOTS_COUNT );

		for ( int i = 0 ; i < SOULCAGE_SLOTS_COUNT; i++ ) {
			if ( nbt.hasKey( "mobName" + i ) ) { this.mobName[i] = nbt.getString( "mobName" + i ); }
			if ( nbt.hasKey( "entityId" + i ) ) { this.entityId[i] = nbt.getString( "entityId" + i ); }
		}
		
		this.hasSoul = nbt.getBoolean( "hasSoul" );
		this.wait = nbt.getInteger( "wait" );
		this.energy = nbt.getInteger( "energy" );
		
		this.speed = nbt.getInteger( "speed" );
		this.count = nbt.getInteger( "count" );
		this.rfconsume = nbt.getInteger( "rfconsume" );
		this.loot = nbt.getInteger( "loot" );
	}
	
	@Override
	public Packet getDescriptionPacket() {
		NBTTagCompound nbt = new NBTTagCompound();
		writeToNBT( nbt );
		final int METADATA = 0;
		return new S35PacketUpdateTileEntity( this.xCoord, this.yCoord, this.zCoord, METADATA, nbt );
	}

	@Override
	public void onDataPacket(NetworkManager net, S35PacketUpdateTileEntity pkt) {
		readFromNBT( pkt.func_148857_g() );
	}
	
// ----------------------
	
	public void clear() {
		Arrays.fill( itemStacks, null );
	}
	
	@Override
	public String getInventoryName() {
		return "container:advancedmachines.furnace.name";
	}

	@Override
	public boolean hasCustomInventoryName() {
		return false;
	}
	
// ======== //
//  Fields  //
// ======== //
	
	private static final byte FIRST_HP_FIELD_ID = 0;
	private static final byte ENERGY_FIELD_ID = FIRST_HP_FIELD_ID + (byte)SOULCAGE_SLOTS_COUNT;
	private static final byte NUMBER_OF_FIELDS = ENERGY_FIELD_ID + 1;
	
	public int getField( int id ) {
		if ( id >= FIRST_HP_FIELD_ID && id < FIRST_HP_FIELD_ID + SOULCAGE_SLOTS_COUNT ) {
			return hpRemaining[id - FIRST_HP_FIELD_ID];
		}
		if ( id == ENERGY_FIELD_ID ) {
			return energy;
		}
		System.err.println( "Invalid field ID in MachineTileEntity.getField: " + id );
		return 0;
	}
	
	public void setField( int id, int value ) {
		if ( id >= FIRST_HP_FIELD_ID && id < FIRST_HP_FIELD_ID + SOULCAGE_SLOTS_COUNT ) {
			hpRemaining[id - FIRST_HP_FIELD_ID] = value;
		} else if ( id == ENERGY_FIELD_ID ) {
			energy = value;
		} else {
			System.err.println( "Invalid field ID in MachineTileEntity.setField: " + id );
		}
	}
	
	public int getFieldCount() {
		return NUMBER_OF_FIELDS;
	}
	
	private static final byte FIRST_MOB_NAME = 0;
	private static final byte NUMBER_OF_MOB_NAMES = FIRST_MOB_NAME + SOULCAGE_SLOTS_COUNT;
	
	public String getMobName( int id ) {
		if ( this.maxHp[id] > 0 ) { 
			return mobName[id];
		}
		return null;
	}
	
// ===================== //
//  Unused but required  //
// ===================== //
	
	@Override
	public boolean isItemValidForSlot( int index, ItemStack stack ) {
		return true;
	}
	
	@Override
	public ItemStack getStackInSlotOnClosing( int index ) {
		ItemStack stack = this.getStackInSlot( index );
		this.setInventorySlotContents( index, null );
		return stack;
	}	

	@Override
	public void openInventory() { }

	@Override
	public void closeInventory() { }

// ================= //
//  IEnergyReceiver  //
// ================= //

	@Override
	public boolean canConnectEnergy( ForgeDirection from ) {
				return true;
	}

	@Override
	public int receiveEnergy( ForgeDirection from, int maxReceive, boolean simulate ) {
		int energyReceived = Math.min( RF_CAPACITY - energy, Math.min( RF_MAX_RECEIVE, maxReceive ) );

		if ( ! simulate ) {
			this.energy += energyReceived;
		}
		return energyReceived;
	}

	@Override
	public int getEnergyStored( ForgeDirection from ) {
		return this.energy;
	}

	@Override
	public int getMaxEnergyStored( ForgeDirection from ) {
		return RF_CAPACITY;
	}
	
	public double getEnergyPercent() {
		return this.energy / (double)RF_CAPACITY;
	}

}
