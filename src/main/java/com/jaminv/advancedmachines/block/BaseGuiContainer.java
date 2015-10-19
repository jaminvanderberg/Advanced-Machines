package com.jaminv.advancedmachines.block;

import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.inventory.Container;
import net.minecraft.util.ChatComponentTranslation;

public abstract class BaseGuiContainer extends GuiContainer {
	
	public BaseGuiContainer( Container c ) {
		super( c );
	}
	
	protected void drawLangString( String lang, int x, int y, int color ) {
		String str = new ChatComponentTranslation( lang ).getUnformattedText();
		this.fontRendererObj.drawString( str, x, y, color );
	}
	
	protected void drawCenteredLangString( String lang, int x, int y, int width, int color ) {
		String str = new ChatComponentTranslation( lang ).getUnformattedText();
		int strw = this.fontRendererObj.getStringWidth( str );
		this.fontRendererObj.drawString( str, x + ( width / 2 - strw / 2 ), y, color );
	}

}
