package com.jaminv.advancedmachines.item;

import com.jaminv.advancedmachines.AdvancedMachines;
import com.jaminv.advancedmachines.ModObject;

import cpw.mods.fml.common.registry.GameRegistry;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.oredict.OreDictionary;

public class ModItems {
	
	public static Item itemIngotTitanium;
	public static Item itemSoulCage;
	public static ItemStack ingotTitanium;
	
	public static final void preInit() {
		itemIngotTitanium = new Item()
				.setUnlocalizedName( ModObject.ingotTitanium.unlocalizedName )
				.setCreativeTab( CreativeTabs.tabMaterials )
				.setTextureName( AdvancedMachines.MODID + ":ingotTitanium" );
		GameRegistry.registerItem( itemIngotTitanium, ModObject.ingotTitanium.unlocalizedName );
		
		GameRegistry.registerItem( itemSoulCage = new ItemSoulCage(), ModObject.itemSoulCage.unlocalizedName );
		
		ingotTitanium = new ItemStack( itemIngotTitanium, 1 );
		OreDictionary.registerOre( "ingotTitanium", ingotTitanium );
	}
	
	public static final void init() { }
	
	public static final void postInit() { }

}
