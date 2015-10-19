package com.jaminv.advancedmachines.block.mobfarm;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.inventory.IInventory;
import net.minecraft.util.ChatComponentTranslation;
import net.minecraft.util.ResourceLocation;

import org.lwjgl.opengl.GL11;

import com.jaminv.advancedmachines.block.BaseGuiContainer;
import com.jaminv.advancedmachines.block.mobfarm.MobFarmTileEntity;

import cpw.mods.fml.common.registry.LanguageRegistry;


public class MobFarmGui extends BaseGuiContainer {
	
	private static final ResourceLocation texture = new ResourceLocation( "advancedmachines", "textures/gui/mobfarm.png" );
	private MobFarmTileEntity te;
	
	public MobFarmGui( IInventory playerInv, MobFarmTileEntity te ) {
		super( new MobFarmContainer( playerInv, te ) );
		
		this.xSize = 176;
		this.ySize = 213;
		
		this.te = te;
	}
	
	final int HP_BAR_XPOS = 29;
	final int HP_BAR_YPOS = 30;
	final int HP_BAR_ICON_U = 0;
	final int HP_BAR_ICON_V = 213;
	final int HP_BAR_WIDTH = 140;
	final int HP_BAR_HEIGHT = 5;
	
	final int ENERGY_XPOS = 9;
	final int ENERGY_YPOS = 49;
	final int ENERGY_ICON_U = 176;
	final int ENERGY_ICON_V = 0;
	final int ENERGY_WIDTH = 14;
	final int ENERGY_HEIGHT = 68;

	@Override
	protected void drawGuiContainerBackgroundLayer( float partialTicks, int mouseX, int mouseY ) {
		Minecraft.getMinecraft().getTextureManager().bindTexture( texture );
		this.drawTexturedModalRect( this.guiLeft, this.guiTop, 0, 0, this.xSize, this.ySize );
		
		//double cookProgress = te.fractionOfCookTimeComplete();
		//this.drawTexturedModalRect(
		//	this.guiLeft + COOK_BAR_XPOS,
		//	this.guiTop + COOK_BAR_YPOS,
		//	COOK_BAR_ICON_U,
		//	COOK_BAR_ICON_V,
		//	(int)(cookProgress * COOK_BAR_WIDTH),
		//	COOK_BAR_HEIGHT
		//);
		
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

	@Override
	protected void drawGuiContainerForegroundLayer(int param1, int param2) {
		this.drawCenteredLangString( "tile.blockMobFarm.name", 7, 7, 162, 0x404040 );
		
		this.drawLangString( "gui.inventory.label", 7,  121, 0x404040 );
	}
}
