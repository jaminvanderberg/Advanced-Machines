package com.jaminv.advancedmachines.block.machine;

import java.util.Arrays;

import cofh.api.energy.IEnergyReceiver;
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

public class MachineTileEntity extends TileEntity implements IInventory, IUpdatePlayerListBox, IEnergyReceiver {
	
	public static final int RF_CAPACITY = 60000;
	public static final int RF_MAX_RECEIVE = 200;
	
	public static final int FUEL_SLOTS_COUNT = 4;
	public static final int INPUT_SLOTS_COUNT = 5;
	public static final int OUTPUT_SLOTS_COUNT = 5;
	public static final int TOTAL_SLOTS_COUNT = FUEL_SLOTS_COUNT + INPUT_SLOTS_COUNT + OUTPUT_SLOTS_COUNT;
	
	public static final int FIRST_FUEL_SLOT = 0;
	public static final int FIRST_INPUT_SLOT = FIRST_FUEL_SLOT + FUEL_SLOTS_COUNT;
	public static final int FIRST_OUTPUT_SLOT = FIRST_INPUT_SLOT + INPUT_SLOTS_COUNT;

	private ItemStack[] itemStacks = new ItemStack[TOTAL_SLOTS_COUNT];
	private String customName;
	
	private int[] burnTimeRemaining = new int[FUEL_SLOTS_COUNT];
	private int[] burnTimeInitialValue = new int[FUEL_SLOTS_COUNT];
	
	private short cookTime;
	private static final short COOK_TIME_FOR_COMPLETION = 200;
	
	private int cachedNumberOfBurningSlots = -1;
	
	private int energy = 0;
	
	public double fractionOfFuelRemaining( int fuelSlot ) {
		if ( burnTimeInitialValue[fuelSlot] <= 0 ) { return 0; }
		double fraction = burnTimeRemaining[fuelSlot] / (double)burnTimeInitialValue[fuelSlot];
		return MathHelper.clamp_double( fraction, 0.0, 1.0 );
	}
	
	public int secondsOfFuelRemaining( int fuelSlot ) {
		if ( burnTimeRemaining[fuelSlot] <= 0 ) { return 0; }
		return burnTimeRemaining[fuelSlot] / 20;
	}
	
	/**
	 * Number of slots which have fuel burning in them.
	 * @return int
	 */
	public int numberOfBurningFuelSlots() {
		int burningCount = 0;
		for ( int burnTime : burnTimeRemaining ) {
			if ( burnTime > 0 ) { burningCount++; }
		}
		return burningCount;
	}
	
	/**
	 * Returns the amount of cook time completed on the currently cooking item
	 * @return double fraction remaining, 0.0-1.0
	 */
	public double fractionOfCookTimeComplete() {
		double fraction = cookTime / (double)COOK_TIME_FOR_COMPLETION;
		return MathHelper.clamp_double( fraction, 0.0, 1.0 );
	}


	
	@Override
	public void updateEntity() {
		this.update();
		super.updateEntity();
	}

	@Override
	public void update() {
		if ( canSmelt() ) {
			int numberOfFuelBurning = burnFuel();
			
			if ( numberOfFuelBurning > 0 ) {
				cookTime += numberOfFuelBurning;
			} else {
				cookTime -= 2;
			}
			
			if ( cookTime < 0 ) cookTime = 0;
			
			if ( cookTime >= COOK_TIME_FOR_COMPLETION ) {
				smeltItem();
				cookTime = 0;
			}
		} else {
			cookTime = 0;
		}
	}
	
	private int burnFuel() {
		int burningCount = 0;
		boolean inventoryChanged = false;
		
		for ( int i = 0; i < FUEL_SLOTS_COUNT; i++ ) {
			int fuelSlotNumber = i + FIRST_FUEL_SLOT;
			if ( burnTimeRemaining[i] > 0 ) {
				burnTimeRemaining[i]--;
				burningCount+=10;
			}
			if ( burnTimeRemaining[i] == 0 ) {
				if ( itemStacks[fuelSlotNumber] != null && getItemBurnTime( itemStacks[fuelSlotNumber] ) > 0) {
					burnTimeRemaining[i] = burnTimeInitialValue[i] = getItemBurnTime( itemStacks[fuelSlotNumber] );
					itemStacks[fuelSlotNumber].stackSize--;
					burningCount++;
					inventoryChanged = true;
					
					if ( itemStacks[fuelSlotNumber].stackSize == 0 ) {
						itemStacks[fuelSlotNumber] = itemStacks[fuelSlotNumber].getItem().getContainerItem( itemStacks[fuelSlotNumber] );
					}
				}
			}
		}
		
		if ( inventoryChanged ) { markDirty(); }
		return burningCount;		
	}
	
	/**
	 * Check if any of the input items are smeltable and there is sufficient space in the output slots
	 * @return true if smelting is possible
	 */
	private boolean canSmelt() { return smeltItem( false ); }
	
	/**
	 * Smelt an input item into an output slot, if possible
	 */
	private void smeltItem() { smeltItem( true ); }
	
	/**
	 * 
	 * @return
	 */
	private boolean smeltItem( boolean performSmelt ) {
		Integer firstSuitableInputSlot = null;
		Integer firstSuitableOutputSlot = null;
		ItemStack result = null;
		
		for ( int inputSlot = FIRST_INPUT_SLOT; inputSlot < FIRST_INPUT_SLOT + INPUT_SLOTS_COUNT; inputSlot++ ) {
			if ( itemStacks[inputSlot] != null ) {
				result = getSmeltingResultForItem( itemStacks[inputSlot] );
				if ( result != null ) {
					for ( int outputSlot = FIRST_OUTPUT_SLOT; outputSlot < FIRST_OUTPUT_SLOT + OUTPUT_SLOTS_COUNT; outputSlot++ ) {
						ItemStack outputStack = itemStacks[outputSlot];
						if ( outputStack == null ) {
							firstSuitableInputSlot = inputSlot;
							firstSuitableOutputSlot = outputSlot;
							break;
						}
						
						if ( outputStack.getItem() == result.getItem() 
							&& ( ! outputStack.getHasSubtypes() || outputStack.getItemDamage() == result.getItemDamage() )
							&& ItemStack.areItemStackTagsEqual( outputStack, result )
						) {
							int combinedSize = itemStacks[outputSlot].stackSize + result.stackSize;
							if ( combinedSize <= getInventoryStackLimit() && combinedSize <= itemStacks[outputSlot].getMaxStackSize() ) {
								firstSuitableInputSlot = inputSlot;
								firstSuitableOutputSlot = outputSlot;
								break;
							}
						}
					}
					if ( firstSuitableInputSlot != null ) { break; }
				}
			}
		}
		
		if ( firstSuitableInputSlot == null ) { return false; }
		if ( ! performSmelt ) { return true; }
		
		itemStacks[firstSuitableInputSlot].stackSize--;
		if ( itemStacks[firstSuitableInputSlot].stackSize <= 0 ) { itemStacks[firstSuitableInputSlot] = null; }
		if ( itemStacks[firstSuitableOutputSlot] == null ) {
			itemStacks[firstSuitableOutputSlot] = result.copy();
		} else {
			itemStacks[firstSuitableOutputSlot].stackSize += result.stackSize;
		}
		markDirty();
		return true;
	}
	
	/**
	 * Returns the smelting result for the given stack.  Returns null if the given stack can not be smelted..
	 * @return ItemStack
	 */
	public static ItemStack getSmeltingResultForItem( ItemStack stack ) {
		return FurnaceRecipes.smelting().getSmeltingResult( stack );
	}

	public static short getItemBurnTime( ItemStack stack ) {
		int burntime = TileEntityFurnace.getItemBurnTime( stack );
		return (short)MathHelper.clamp_int( burntime, 0, Short.MAX_VALUE );
	}
	
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
	
	static public boolean isItemValidForFuelSlot( ItemStack itemstack ) {
		return getItemBurnTime( itemstack ) > 0;
	}
	
	static public boolean isItemValidForInputSlot( ItemStack itemstack ) {
		return getSmeltingResultForItem( itemstack ) != null;
	}
	
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
		
		nbt.setShort( "cookTime", cookTime );
		nbt.setTag( "burnTimeRemaining", new NBTTagIntArray( burnTimeRemaining ) );
		nbt.setTag( "burnTimeInitial", new NBTTagIntArray( burnTimeInitialValue ) );
		
		nbt.setInteger( "energy", this.energy );
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
		
		cookTime = nbt.getShort( "cookTime" );
		burnTimeRemaining = Arrays.copyOf( nbt.getIntArray( "burnTimeRemaining" ), FUEL_SLOTS_COUNT );
		burnTimeInitialValue = Arrays.copyOf( nbt.getIntArray( "burnTimeInitial" ), FUEL_SLOTS_COUNT );
		cachedNumberOfBurningSlots = -1;
		
		energy = nbt.getInteger( "energy" );
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
	
	private static final byte COOK_FIELD_ID = 0;
	private static final byte FIRST_BURN_TIME_REMAINING_FIELD_ID = 1;
	private static final byte FIRST_BURN_TIME_INITIAL_FIELD_ID = FIRST_BURN_TIME_REMAINING_FIELD_ID + (byte)FUEL_SLOTS_COUNT;
	private static final byte ENERGY_FIELD_ID = FIRST_BURN_TIME_INITIAL_FIELD_ID + (byte)FUEL_SLOTS_COUNT;
	private static final byte NUMBER_OF_FIELDS = ENERGY_FIELD_ID;
	
	public int getField( int id ) {
		if ( id == COOK_FIELD_ID ) { return cookTime; }
		if ( id >= FIRST_BURN_TIME_REMAINING_FIELD_ID && id < FIRST_BURN_TIME_REMAINING_FIELD_ID + FUEL_SLOTS_COUNT ) {
			return burnTimeRemaining[id - FIRST_BURN_TIME_REMAINING_FIELD_ID];
		}
		if ( id >= FIRST_BURN_TIME_INITIAL_FIELD_ID && id < FIRST_BURN_TIME_INITIAL_FIELD_ID + FUEL_SLOTS_COUNT ) {
			return burnTimeInitialValue[id - FIRST_BURN_TIME_INITIAL_FIELD_ID];
		}
		if ( id == ENERGY_FIELD_ID ) {
			return energy;
		}
		System.err.println( "Invalid field ID in MachineTileEntity.getField: " + id );
		return 0;
	}
	
	public void setField( int id, int value ) {
		if ( id == COOK_FIELD_ID ) { 
			cookTime = (short)value; 
		} else if ( id >= FIRST_BURN_TIME_REMAINING_FIELD_ID && id < FIRST_BURN_TIME_REMAINING_FIELD_ID + FUEL_SLOTS_COUNT ) {
			burnTimeRemaining[id - FIRST_BURN_TIME_REMAINING_FIELD_ID] = value;
		} else if ( id >= FIRST_BURN_TIME_INITIAL_FIELD_ID && id < FIRST_BURN_TIME_INITIAL_FIELD_ID + FUEL_SLOTS_COUNT ) {
			burnTimeInitialValue[id - FIRST_BURN_TIME_INITIAL_FIELD_ID] = value;
		} else if ( id == ENERGY_FIELD_ID ) {
			System.out.println( value );
			energy = value;
		} else {
			System.err.println( "Invalid field ID in MachineTileEntity.setField: " + id );
		}
	}
	
	public int getFieldCount() {
		return NUMBER_OF_FIELDS;
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
		System.out.println( this.energy );
		return this.energy / (double)RF_CAPACITY;
	}


}
