package com.jaminv.advancedmachines.block.machine;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.inventory.IInventory;
import net.minecraft.util.ResourceLocation;

import org.lwjgl.opengl.GL11;

import com.jaminv.advancedmachines.block.machine.MachineTileEntity;


public class MachineGui extends GuiContainer {
	
	private static final ResourceLocation texture = new ResourceLocation( "advancedmachines", "textures/gui/machine.png" );
	private MachineTileEntity te;
	
	public MachineGui( IInventory playerInv, MachineTileEntity te ) {
		super( new MachineContainer( playerInv, te ) );
		
		this.xSize = 176;
		this.ySize = 207;
		
		this.te = te;
	}
	
	final int COOK_BAR_XPOS = 49;
	final int COOK_BAR_YPOS = 60;
	final int COOK_BAR_ICON_U = 0;
	final int COOK_BAR_ICON_V = 207;
	final int COOK_BAR_WIDTH = 80;
	final int COOK_BAR_HEIGHT = 17;
	
	final int FLAME_XPOS = 54;
	final int FLAME_YPOS = 80;
	final int FLAME_ICON_U = 176;
	final int FLAME_ICON_V = 0;
	final int FLAME_WIDTH = 14;
	final int FLAME_HEIGHT = 14;
	final int FLAME_X_SPACING = 18;
	
	final int ENERGY_XPOS = 7;
	final int ENERGY_YPOS = 23;
	final int ENERGY_ICON_U = 187;
	final int ENERGY_ICON_V = 23;
	final int ENERGY_WIDTH = 18;
	final int ENERGY_HEIGHT = 90;

	@Override
	protected void drawGuiContainerBackgroundLayer( float partialTicks, int mouseX, int mouseY ) {
		Minecraft.getMinecraft().getTextureManager().bindTexture( texture );
		this.drawTexturedModalRect( this.guiLeft, this.guiTop, 0, 0, this.xSize, this.ySize );
		
		double cookProgress = te.fractionOfCookTimeComplete();
		this.drawTexturedModalRect(
			this.guiLeft + COOK_BAR_XPOS,
			this.guiTop + COOK_BAR_YPOS,
			COOK_BAR_ICON_U,
			COOK_BAR_ICON_V,
			(int)(cookProgress * COOK_BAR_WIDTH),
			COOK_BAR_HEIGHT
		);
		
		for ( int i = 0; i < te.FUEL_SLOTS_COUNT; i++ ) {
			double burnRemaining = te.fractionOfFuelRemaining( i );
			int yOffset = (int)( ( 1.0 - burnRemaining ) * FLAME_HEIGHT );
			this.drawTexturedModalRect(
				this.guiLeft + FLAME_XPOS + FLAME_X_SPACING * i,
				this.guiTop + FLAME_YPOS + yOffset,
				FLAME_ICON_U,
				FLAME_ICON_V + yOffset,
				FLAME_WIDTH,
				FLAME_HEIGHT - yOffset
			);
		}
		
		double energy = te.getEnergyPercent();
		int yOffset = (int)( ( 1.0 - energy ) * ENERGY_HEIGHT );
		this.drawTexturedModalRect(
			this.guiLeft + ENERGY_XPOS,
			this.guiTop + ENERGY_YPOS + yOffset,
			ENERGY_ICON_U,
			ENERGY_ICON_V + yOffset,
			ENERGY_WIDTH,
			ENERGY_HEIGHT - yOffset
		);
	}

}
