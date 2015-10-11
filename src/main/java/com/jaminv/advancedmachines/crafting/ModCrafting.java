package com.jaminv.advancedmachines.crafting;

import com.jaminv.advancedmachines.AdvancedMachines;
import com.jaminv.advancedmachines.block.ModBlocks;
import com.jaminv.advancedmachines.block.machine.MachineSetup;
import com.jaminv.advancedmachines.item.ModItems;

import cpw.mods.fml.common.registry.GameRegistry;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;

public class ModCrafting {
	
	public static final void init() {
		System.out.println( "[" + AdvancedMachines.MODNAME + "].ModCrafting Initialization Start." );
		
		GameRegistry.addRecipe(
			new ItemStack( ModBlocks.testBlock ),
			"###", "###", "###",
			'#', ModItems.testItem
		);
		
		GameRegistry.addSmelting(
			Items.diamond,
			new ItemStack( ModItems.testItem ),
			1.0F
		);
		
		MachineSetup.setupCrafting();

		System.out.println( "[" + AdvancedMachines.MODNAME + "].ModCrafting Initialization End." );
	}

}
