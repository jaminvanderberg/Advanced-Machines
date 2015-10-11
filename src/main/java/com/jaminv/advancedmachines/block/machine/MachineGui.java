package com.jaminv.advancedmachines.block.machine;

import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.inventory.IInventory;
import com.jaminv.advancedmachines.block.machine.MachineTileEntity;


public class MachineGui extends GuiContainer {
	
	public MachineGui( IInventory playerInv, MachineTileEntity te ) {
		super( new MachineContainer( playerInv, te ) );
		
		this.xSize = 176;
		this.ySize = 166;
	}

	@Override
	protected void drawGuiContainerBackgroundLayer( float partialTicks, int mouseX, int mouseY ) {
	}

}
