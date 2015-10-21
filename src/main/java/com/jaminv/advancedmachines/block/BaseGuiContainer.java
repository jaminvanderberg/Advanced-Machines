package com.jaminv.advancedmachines.block;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.inventory.Container;
import net.minecraft.util.ChatComponentTranslation;

public abstract class BaseGuiContainer extends GuiContainer {
	
	public BaseGuiContainer( Container c ) {
		super( c );
	}
	
	int mouseX;
	int mouseY;
	int dialogX;
	int dialogY;
	
	public void drawScreen( int par1, int par2, float par3 ) {
        this.mouseX = par1 - this.guiLeft;
        this.mouseY = par2 - this.guiTop;
        
        super.drawScreen( par1, par2, par3 );
    }	
	
	protected void drawLangString( String lang, int x, int y, int color ) {
		String str = new ChatComponentTranslation( lang ).getUnformattedText();
		this.fontRendererObj.drawString( str, x, y, color );
	}
	
	protected void drawCenteredLangString( String lang, int x, int y, int width, int color ) {
		String str = new ChatComponentTranslation( lang ).getUnformattedText();
		this.drawCenteredString( str,  x, y, width, color );
	}
	
	protected void drawCenteredString( String str, int x, int y, int width, int color ) {
		int strw = this.fontRendererObj.getStringWidth( str );
		this.fontRendererObj.drawString( str, x + ( width / 2 - strw / 2 ), y, color );
	}
	
	protected void drawTooltip( int x, int y, int w, int h, String str ) {
		if ( this.mouseX >= x && this.mouseX < x + w ) {
			if ( this.mouseY >= y && this.mouseY < y + h ) {
				List list = new ArrayList();
				list.add( str );
				this.drawHoveringText( list, mouseX, mouseY, this.fontRendererObj );
			}
		}
	}

}
