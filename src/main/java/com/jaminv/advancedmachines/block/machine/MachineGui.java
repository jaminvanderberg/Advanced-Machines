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

	@Override
	protected void drawGuiContainerBackgroundLayer( float partialTicks, int mouseX, int mouseY ) {
		Minecraft.getMinecraft().getTextureManager().bindTexture( texture );
		this.drawTexturedModalRect( this.guiLeft, this.guiTop, 0, 0, this.xSize, this.ySize );
	}

}
