package com.jaminv.advancedmachines.block.ore;

import com.jaminv.advancedmachines.ModObject;
import com.jaminv.advancedmachines.block.machine.MachineBlock;
import com.jaminv.advancedmachines.block.machine.MachineTileEntity;
import com.jaminv.advancedmachines.item.ModItems;

import cpw.mods.fml.common.registry.GameRegistry;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.item.ItemStack;
import net.minecraftforge.oredict.OreDictionary;

public class BlockOreSetup {
	
	public static Block blockOre;
	public static ItemStack oreTitanium;

	public static void setupBlocks() {
		GameRegistry.registerBlock( blockOre = new BlockOre(), ItemBlockOre.class, ModObject.blockOre.unlocalizedName );

		oreTitanium = new ItemStack( blockOre, 1, 0 );
		OreDictionary.registerOre( "oreTitanium", oreTitanium );
		GameRegistry.registerCustomItemStack( "oreTitanium", oreTitanium );
	}
	
	public static void setupCrafting() {
		GameRegistry.addSmelting( oreTitanium.getItem(), ModItems.ingotTitanium, 1.0F );
	}
	
}
