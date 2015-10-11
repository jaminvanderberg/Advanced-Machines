package com.jaminv.advancedmachines.item;

import com.jaminv.advancedmachines.AdvancedMachines;

import cpw.mods.fml.common.registry.GameRegistry;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;

public class ModItems {
	
	public static Item testItem;
	
	public static final void init() {
		testItem = new Item().setUnlocalizedName( "testItem" ).setCreativeTab( CreativeTabs.tabMisc ).setTextureName( AdvancedMachines.MODID + ":testItem" );
		GameRegistry.registerItem( testItem, "testItem" );
	}

}
