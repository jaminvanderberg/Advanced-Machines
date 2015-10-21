package com.jaminv.advancedmachines.block.mobfarm;

import com.jaminv.advancedmachines.item.ItemSoulCage;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.ICrafting;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;


public class MobFarmContainer extends Container {
	
	private MobFarmTileEntity te;
	
	private int[] cachedFields;	
	//private String[] cachedNames;
	
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
	
	public final int SOULCAGE_SLOTS_COUNT = 1;
	public final int OUTPUT_SLOTS_ROW_COUNT = 4;
	public final int OUTPUT_SLOTS_COLUMN_COUNT = 5;
	public final int OUTPUT_SLOTS_COUNT = OUTPUT_SLOTS_ROW_COUNT * OUTPUT_SLOTS_COLUMN_COUNT;
	public final int VOID_SLOTS_ROW_COUNT = 4;
	public final int VOID_SLOTS_COLUMN_COUNT = 2;
	public final int VOID_SLOTS_COUNT = VOID_SLOTS_ROW_COUNT * VOID_SLOTS_COLUMN_COUNT;
	public final int MACHINE_SLOTS_COUNT = SOULCAGE_SLOTS_COUNT + OUTPUT_SLOTS_COUNT + VOID_SLOTS_COUNT;
	
	private final int FIRST_VANILLA_SLOT_INDEX = 0;
	private final int FIRST_SOULCAGE_SLOT_INDEX = FIRST_VANILLA_SLOT_INDEX + VANILLA_SLOT_COUNT;
	private final int FIRST_OUTPUT_SLOT_INDEX = FIRST_SOULCAGE_SLOT_INDEX + SOULCAGE_SLOTS_COUNT;
	private final int FIRST_VOID_SLOT_INDEX =  FIRST_OUTPUT_SLOT_INDEX + OUTPUT_SLOTS_COUNT;
	
	private final int FIRST_SOULCAGE_SLOT_NUMBER = 0;
	private final int FIRST_OUTPUT_SLOT_NUMBER = FIRST_SOULCAGE_SLOT_NUMBER + SOULCAGE_SLOTS_COUNT;
	private final int FIRST_VOID_SLOT_NUMBER = FIRST_OUTPUT_SLOT_NUMBER + OUTPUT_SLOTS_COUNT;
	
	public MobFarmContainer(IInventory invPlayer, MobFarmTileEntity te) {
		this.te = te;
		
		final int SLOT_X_SPACING = 18;
		final int SLOT_Y_SPACING = 18;
		final int HOTBAR_XPOS = 8;
		final int HOTBAR_YPOS = 189;
		// Player hotbar
		for ( int x = 0; x < HOTBAR_SLOT_COUNT; x++ ) {
			int slotNumber = x;
			this.addSlotToContainer( new Slot( invPlayer, slotNumber, HOTBAR_XPOS + SLOT_X_SPACING * x, HOTBAR_YPOS ) );
		}
		
		final int PLAYER_INVENTORY_XPOS = 8;
		final int PLAYER_INVENTORY_YPOS = 131;
		// Player inventory
		for ( int y = 0; y < PLAYER_INVENTORY_ROW_COUNT; y++ ) {
			for ( int x = 0; x < PLAYER_INVENTORY_COLUMN_COUNT; x++ ) {
				int slotNumber = HOTBAR_SLOT_COUNT + y * PLAYER_INVENTORY_COLUMN_COUNT + x;
				int xPos = PLAYER_INVENTORY_XPOS + x * SLOT_X_SPACING;
				int yPos = PLAYER_INVENTORY_YPOS + y * SLOT_Y_SPACING;
				this.addSlotToContainer( new Slot( invPlayer, slotNumber, xPos, yPos ) );
			}
		}
		
		final int SOULCAGE_SLOTS_XPOS = 8;
		final int SOULCAGE_SLOTS_YPOS = 19;
		// Tile entity fuel slots
		for ( int x = 0; x < SOULCAGE_SLOTS_COUNT; x++ ) {
			int slotNumber = x + FIRST_SOULCAGE_SLOT_NUMBER;
			this.addSlotToContainer( new SlotSoulcage( te, slotNumber, SOULCAGE_SLOTS_XPOS + SLOT_X_SPACING * x, SOULCAGE_SLOTS_YPOS ) );
		}
		
		final int OUTPUT_SLOTS_XPOS = 35;
		final int OUTPUT_SLOTS_YPOS = 48;
		// Tile entity input slots
		for ( int y = 0; y < OUTPUT_SLOTS_ROW_COUNT; y++ ) {
			for ( int x = 0; x < OUTPUT_SLOTS_COLUMN_COUNT; x++ ) {
				int xPos = OUTPUT_SLOTS_XPOS + x * SLOT_X_SPACING;
				int yPos = OUTPUT_SLOTS_YPOS + y * SLOT_Y_SPACING;				
				int slotNumber = FIRST_OUTPUT_SLOT_NUMBER + y * OUTPUT_SLOTS_COLUMN_COUNT + x;
				this.addSlotToContainer( new SlotOutput( te, slotNumber, xPos, yPos ) );
			}
		}
		
		final int VOID_SLOTS_XPOS = 134;
		final int VOID_SLOTS_YPOS = 48;
		// Tile entity output slots
		for ( int y = 0; y < VOID_SLOTS_ROW_COUNT; y++ ) {
			for ( int x = 0; x < VOID_SLOTS_COLUMN_COUNT; x++ ) {
				int slotNumber = FIRST_VOID_SLOT_NUMBER + y * VOID_SLOTS_COLUMN_COUNT + x;
				int xPos = VOID_SLOTS_XPOS + x * SLOT_X_SPACING;
				int yPos = VOID_SLOTS_YPOS + y * SLOT_Y_SPACING;				
				this.addSlotToContainer( new SlotVoidItem( te, slotNumber, xPos, yPos ) );
			}
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
			if ( te.isItemValidForSoulcage( stack ) ) { 
				boolean empty = false;
				for ( int i = FIRST_SOULCAGE_SLOT_INDEX; i < FIRST_SOULCAGE_SLOT_INDEX + SOULCAGE_SLOTS_COUNT; i++ ) {
					if ( ! ( (Slot)this.inventorySlots.get( i ) ).getHasStack() ) {
						empty = true;
						break;
					}
				}
				if ( ! empty ) { return null; }
	
				if ( ! this.mergeItemStack( stack,  FIRST_SOULCAGE_SLOT_INDEX, FIRST_SOULCAGE_SLOT_INDEX + SOULCAGE_SLOTS_COUNT, false ) ) {
					return null;
				}
			} else {
				return null;
			}
		} else if ( fromSlot >= FIRST_SOULCAGE_SLOT_INDEX && fromSlot < FIRST_OUTPUT_SLOT_INDEX + OUTPUT_SLOTS_COUNT ) {
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

	public class SlotSoulcage extends Slot {
		public SlotSoulcage( IInventory inventoryIn, int index, int xPos, int yPos ) {
			super( inventoryIn, index, xPos, yPos );
		}
		
		@Override
		public boolean isItemValid( ItemStack stack ) {
			return te.isItemValidForSoulcage( stack );
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

	public class SlotVoidItem extends Slot {
		public SlotVoidItem( IInventory inventoryIn, int index, int xPos, int yPos ) {
			super( inventoryIn, index, xPos, yPos );
		}
		
		@Override
		public boolean isItemValid( ItemStack stack ) {
			return true;
		}
	}	
	
}
