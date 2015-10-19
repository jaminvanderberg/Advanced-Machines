package com.jaminv.advancedmachines.gui;

import com.jaminv.advancedmachines.AdvancedMachines;
import com.jaminv.advancedmachines.block.machine.MachineContainer;
import com.jaminv.advancedmachines.block.machine.MachineGui;
import com.jaminv.advancedmachines.block.machine.MachineTileEntity;
import com.jaminv.advancedmachines.block.mobfarm.MobFarmContainer;
import com.jaminv.advancedmachines.block.mobfarm.MobFarmGui;
import com.jaminv.advancedmachines.block.mobfarm.MobFarmTileEntity;

import cpw.mods.fml.common.network.IGuiHandler;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.world.World;

public class ModGuiHandler implements IGuiHandler {

	@Override
	public Object getServerGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z ) {
		if ( ID == AdvancedMachines.GUI_MACHINE ) {
			return new MachineContainer( player.inventory, (MachineTileEntity) world.getTileEntity( x, y, z ) );
		} else if ( ID == AdvancedMachines.GUI_MOBFARM ) {
			return new MobFarmContainer( player.inventory, (MobFarmTileEntity) world.getTileEntity( x, y, z ) );
		}
		return null;
	}

	@Override
	public Object getClientGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z) {
		if ( ID == AdvancedMachines.GUI_MACHINE ) {
			return new MachineGui( player.inventory, (MachineTileEntity) world.getTileEntity( x, y, z ) );
		} else if ( ID == AdvancedMachines.GUI_MOBFARM ) {
			return new MobFarmGui( player.inventory, (MobFarmTileEntity) world.getTileEntity( x, y, z ) );
		}
		return null;
	}
}
