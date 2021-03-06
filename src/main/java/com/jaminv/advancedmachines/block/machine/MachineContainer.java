package com.jaminv.advancedmachines.block.machine;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.ICrafting;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;


public class MachineContainer extends Container {
	
	private MachineTileEntity te;
	
	private int[] cachedFields;	
	
	/*
	 * SLOTS:
	 * 0-8 = hotbar slots (map to playerInv slot 0-8)
	 * 9-35 = player inventory slots (map to playerInv slot 9-35)
	 * 36-39 = fuel slots (tileEntity slot 0-3)
	 * 40-44 = input slots (tileEntity slot 4-8)
	 * 45-49 = output slots (tileEntity slot 9-13)
	 */
	private final int HOTBAR_SLOT_COUNT = 9;
	private final int PLAYER_INVENTORY_ROW_COUNT = 3;
	private final int PLAYER_INVENTORY_COLUMN_COUNT = 9;
	private final int PLAYER_INVENTORY_SLOT_COUNT = PLAYER_INVENTORY_COLUMN_COUNT * PLAYER_INVENTORY_ROW_COUNT;
	private final int VANILLA_SLOT_COUNT = HOTBAR_SLOT_COUNT + PLAYER_INVENTORY_SLOT_COUNT;
	
	public final int FUEL_SLOTS_COUNT = 4;
	public final int INPUT_SLOTS_COUNT = 5;
	public final int OUTPUT_SLOTS_COUNT = 5;
	public final int FURNACE_SLOTS_COUNT = FUEL_SLOTS_COUNT + INPUT_SLOTS_COUNT + OUTPUT_SLOTS_COUNT;
	
	private final int FIRST_VANILLA_SLOT_INDEX = 0;
	private final int FIRST_FUEL_SLOT_INDEX = FIRST_VANILLA_SLOT_INDEX + VANILLA_SLOT_COUNT;
	private final int FIRST_INPUT_SLOT_INDEX = FIRST_FUEL_SLOT_INDEX + FUEL_SLOTS_COUNT;
	private final int FIRST_OUTPUT_SLOT_INDEX =  FIRST_INPUT_SLOT_INDEX + INPUT_SLOTS_COUNT;
	
	private final int FIRST_FUEL_SLOT_NUMBER = 0;
	private final int FIRST_INPUT_SLOT_NUMBER = FIRST_FUEL_SLOT_NUMBER + FUEL_SLOTS_COUNT;
	private final int FIRST_OUTPUT_SLOT_NUMBER = FIRST_INPUT_SLOT_NUMBER + INPUT_SLOTS_COUNT;
	
	public MachineContainer(IInventory invPlayer, MachineTileEntity te) {
		this.te = te;
		
		final int SLOT_X_SPACING = 18;
		final int SLOT_Y_SPACING = 18;
		final int HOTBAR_XPOS = 8;
		final int HOTBAR_YPOS = 183;
		// Player hotbar
		for ( int x = 0; x < HOTBAR_SLOT_COUNT; x++ ) {
			int slotNumber = x;
			this.addSlotToContainer( new Slot( invPlayer, slotNumber, HOTBAR_XPOS + SLOT_X_SPACING * x, HOTBAR_YPOS ) );
		}
		
		final int PLAYER_INVENTORY_XPOS = 8;
		final int PLAYER_INVENTORY_YPOS = 125;
		// Player inventory
		for ( int y = 0; y < PLAYER_INVENTORY_ROW_COUNT; y++ ) {
			for ( int x = 0; x < PLAYER_INVENTORY_COLUMN_COUNT; x++ ) {
				int slotNumber = HOTBAR_SLOT_COUNT + y * PLAYER_INVENTORY_COLUMN_COUNT + x;
				int xPos = PLAYER_INVENTORY_XPOS + x * SLOT_X_SPACING;
				int yPos = PLAYER_INVENTORY_YPOS + y * SLOT_Y_SPACING;
				this.addSlotToContainer( new Slot( invPlayer, slotNumber, xPos, yPos ) );
			}
		}
		
		final int FUEL_SLOTS_XPOS = 53;
		final int FUEL_SLOTS_YPOS = 96;
		// Tile entity fuel slots
		for ( int x = 0; x < FUEL_SLOTS_COUNT; x++ ) {
			int slotNumber = x + FIRST_FUEL_SLOT_NUMBER;
			this.addSlotToContainer( new SlotFuel( te, slotNumber, FUEL_SLOTS_XPOS + SLOT_X_SPACING * x, FUEL_SLOTS_YPOS ) );
		}
		
		final int INPUT_SLOTS_XPOS = 26;
		final int INPUT_SLOTS_YPOS = 24;
		// Tile entity input slots
		for ( int y = 0; y < INPUT_SLOTS_COUNT; y++ ) {
			int slotNumber = y + FIRST_INPUT_SLOT_NUMBER;
			this.addSlotToContainer( new SlotSmeltableInput( te, slotNumber, INPUT_SLOTS_XPOS, INPUT_SLOTS_YPOS + SLOT_Y_SPACING * y ) );	
		}
		
		final int OUTPUT_SLOTS_XPOS = 134;
		final int OUTPUT_SLOTS_YPOS = 24;
		// Tile entity output slots
		for ( int y = 0; y < OUTPUT_SLOTS_COUNT; y++ ) {
			int slotNumber = y + FIRST_OUTPUT_SLOT_NUMBER;
			this.addSlotToContainer( new SlotOutput( te, slotNumber, OUTPUT_SLOTS_XPOS, OUTPUT_SLOTS_YPOS + SLOT_Y_SPACING * y ) );	
		}
	}

	@Override
	public boolean canInteractWith( EntityPlayer playerIn ) {
		return this.te.isUseableByPlayer( playerIn );
	}

	@Override
	public ItemStack transferStackInSlot( EntityPlayer playerIn, int fromSlot ) {
		Slot slot = (Slot) this.inventorySlots.get( fromSlot );
		if ( slot == null || !slot.getHasStack() ) {
			return null;
		}
		ItemStack stack = slot.getStack();
		ItemStack stackCopy = stack.copy();
		
		if ( fromSlot >= FIRST_VANILLA_SLOT_INDEX && fromSlot < FIRST_VANILLA_SLOT_INDEX + VANILLA_SLOT_COUNT ) {
			// Merge from player inventory into tile entity
			if ( te.getSmeltingResultForItem( stack ) != null ) {
				if ( ! this.mergeItemStack( stack, FIRST_INPUT_SLOT_INDEX, FIRST_INPUT_SLOT_INDEX + INPUT_SLOTS_COUNT, false ) ) {
					return null;
				}
			} else if ( te.getItemBurnTime( stack ) > 0 ) {
				if ( ! this.mergeItemStack( stack, FIRST_FUEL_SLOT_INDEX, FIRST_FUEL_SLOT_INDEX + FUEL_SLOTS_COUNT, true ) ) {
					return null;
				}
			} else {
				return null;
			}
		} else if ( fromSlot >= FIRST_FUEL_SLOT_INDEX && fromSlot < FIRST_FUEL_SLOT_INDEX + FURNACE_SLOTS_COUNT ) {
			if ( ! this.mergeItemStack( stack, FIRST_VANILLA_SLOT_INDEX, FIRST_VANILLA_SLOT_INDEX + VANILLA_SLOT_COUNT, false ) ) {
				return null;
			}
		} else {
			System.err.print( "Invalid slotIndex: " + fromSlot );
		}
		
		if ( stack.stackSize == 0 ) {
			slot.putStack( null );
		} else {
			slot.onSlotChanged();
		}
			
		slot.onPickupFromSlot( playerIn, stack );
		return stackCopy;
	}

	@Override
	public void detectAndSendChanges() {
		super.detectAndSendChanges();
		
		boolean allFieldsHaveChanged = false;
		boolean fieldHasChanged[] = new boolean[ te.getFieldCount() ];
		if ( cachedFields == null ) {
			cachedFields =  new int[ te.getFieldCount() ];
			allFieldsHaveChanged = true;
		}
		for ( int i = 0; i < cachedFields.length; i++ ) {
			if ( allFieldsHaveChanged || cachedFields[i] != te.getField( i ) ) {
				cachedFields[i] = te.getField( i );
				fieldHasChanged[i] = true;
			}
		}
		
		for ( int i = 0; i < this.crafters.size(); i++ ) {
			ICrafting icrafting = (ICrafting)this.crafters.get( i );
			for ( int fieldID = 0; fieldID < te.getFieldCount(); fieldID++ ) {
				if ( fieldHasChanged[ fieldID ] ) {
					icrafting.sendProgressBarUpdate( this, fieldID, cachedFields[ fieldID ] );
				}
			}
		}
	}

	@SideOnly( Side.CLIENT )
	@Override
	public void updateProgressBar( int id, int data ) {
		te.setField( id, data );
	}

	public class SlotFuel extends Slot {
		public SlotFuel( IInventory inventoryIn, int index, int xPos, int yPos ) {
			super( inventoryIn, index, xPos, yPos );
		}
		
		@Override
		public boolean isItemValid( ItemStack stack ) {
			return te.isItemValidForFuelSlot( stack );
		}
	}
	
	public class SlotSmeltableInput extends Slot {
		public SlotSmeltableInput( IInventory inventoryIn, int index, int xPos, int yPos ) {
			super( inventoryIn, index, xPos, yPos );
		}
		
		@Override
		public boolean isItemValid( ItemStack stack ) {
			return te.isItemValidForInputSlot( stack );
		}
	}

	public class SlotOutput extends Slot {
		public SlotOutput( IInventory inventoryIn, int index, int xPos, int yPos ) {
			super( inventoryIn, index, xPos, yPos );
		}
		
		@Override
		public boolean isItemValid( ItemStack stack ) {
			return te.isItemValidForOutputSlot( stack );
		}
	}
	
}
