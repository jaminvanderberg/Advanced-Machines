package com.jaminv.advancedmachines.block;

import net.minecraft.server.gui.IUpdatePlayerListBox;
import net.minecraft.tileentity.TileEntity;

public class ModTileEntity extends TileEntity implements IUpdatePlayerListBox {

	@Override
	public void updateEntity() {
		System.out.println( "Hello, I'm a TileEntity!" );
	}

}
