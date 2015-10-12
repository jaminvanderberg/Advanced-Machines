package com.jaminv.advancedmachines.block.machine;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.FurnaceRecipes;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.server.gui.IUpdatePlayerListBox;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityFurnace;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.ChatComponentTranslation;
import net.minecraft.util.IChatComponent;
import net.minecraft.util.MathHelper;

public class MachineTileEntity extends TileEntity implements IInventory, IUpdatePlayerListBox {
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
				burningCount++;
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
	
	public String getCustomName() {
		return this.customName;
	}

	public void setCustomName( String customName ) {
		this.customName = customName;
	}

	@Override
	public String getInventoryName() {
		return this.hasCustomInventoryName() ? this.customName : "contained.tutorial_tile_entity";
	}

	@Override
	public boolean hasCustomInventoryName() {
		return this.customName != null && !this.customName.equals( "" );
	}
	
//	@Override
//	public IChatComponent getDisplayName() {
//	    return this.hasCustomName() ? new ChatComponentText(this.getName()) : new ChatComponentTranslation(this.getName());
//	}

	@Override
	public int getSizeInventory() {
		return 9;
	}

	@Override
	public ItemStack getStackInSlot( int index ) {
		if ( index < 0 || index >= this.getSizeInventory() ) {
			return null;
		}
		return this.inventory[index];
	}

	@Override
	public ItemStack decrStackSize( int index, int count ) {
		if ( this.getStackInSlot( index ) != null ) {
			ItemStack itemstack;
			
			if ( this.getStackInSlot( index ).stackSize <= count ) {
				itemstack = this.getStackInSlot( index );
				this.setInventorySlotContents( index, null );
				this.markDirty();
				return itemstack;
			} else {
				itemstack = this.getStackInSlot( index ).splitStack( count );
				
				if ( this.getStackInSlot( index ).stackSize <= 0 ) {
					this.setInventorySlotContents( index, null );
				} else {
					this.setInventorySlotContents( index, this.getStackInSlot( index ) );
				}
				
				this.markDirty();
				return itemstack;
			}
		} else {
			return null;
		}
	}

	@Override
	public ItemStack getStackInSlotOnClosing( int index ) {
		ItemStack stack = this.getStackInSlot( index );
		this.setInventorySlotContents( index, null );
		return stack;
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
		
		this.inventory[index] = stack;
		this.markDirty();
	}

	@Override
	public int getInventoryStackLimit() {
		return 64;
	}

	@Override
	public boolean isUseableByPlayer( EntityPlayer player ) {
		return this.worldObj.getTileEntity( this.xCoord, this.yCoord, this.zCoord ) == this &&
			player.getDistanceSq( this.xCoord + 0.5, this.yCoord + 0.5, this.zCoord + 0.5 ) <= 64;
	}

	@Override
	public void openInventory() {
	}

	@Override
	public void closeInventory() {
	}

	@Override
	public boolean isItemValidForSlot( int index, ItemStack stack ) {
		return true;
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
		
		if ( this.hasCustomInventoryName() ) {
			nbt.setString( "CustomName", this.getCustomName() );
		}
	}
	
	@Override
	public void readFromNBT( NBTTagCompound nbt ) {
		super.readFromNBT( nbt );
		
		NBTTagList list = nbt.getTagList( "Items", 10 );
		for ( int i = 0; i < list.tagCount(); i++ ) {
			NBTTagCompound stackTag = list.getCompoundTagAt( i );
			int slot = stackTag.getByte( "Slot" ) & 255;
			this.setInventorySlotContents( slot, ItemStack.loadItemStackFromNBT( stackTag ) );
		}
		
		if ( nbt.hasKey( "CustomName", 8 ) ) {
			this.setCustomName( nbt.getString( "CustomName" ) );
		}
	}

}
