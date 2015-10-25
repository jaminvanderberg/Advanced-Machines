package com.jaminv.advancedmachines.block.mobfarm;

import com.jaminv.advancedmachines.ModObject;

import cpw.mods.fml.common.registry.GameRegistry;
import net.minecraft.item.ItemStack;
import net.minecraftforge.oredict.OreDictionary;

public class MobFarmSetup {
	public static MobFarmBlock blockMobFarm;
	public static ItemStack itemMobFarm;

	public static void setupBlocks() {
		GameRegistry.registerBlock( blockMobFarm = new MobFarmBlock(), MobFarmItem.class, ModObject.blockMobFarm.unlocalizedName );
		GameRegistry.registerTileEntity( MobFarmTileEntity.class, ModObject.blockMobFarm.unlocalizedName + "TileEntity" );
		
		itemMobFarm = new ItemStack( blockMobFarm, 1, 0 );
	}
	
	public static void setupCrafting() {
		
	}
}
