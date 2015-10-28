package com.jaminv.advancedmachines.block.mobfarm;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Slot;
import net.minecraft.util.ChatComponentTranslation;
import net.minecraft.util.ResourceLocation;

import org.lwjgl.opengl.GL11;

import com.jaminv.advancedmachines.block.BaseGuiContainer;
import com.jaminv.advancedmachines.block.mobfarm.MobFarmTileEntity;
import com.jaminv.advancedmachines.block.mobfarm.MobFarmContainer.SlotOutput;
import com.jaminv.advancedmachines.block.mobfarm.MobFarmContainer.SlotSoulcage;

import cpw.mods.fml.common.registry.LanguageRegistry;


public class MobFarmGui extends BaseGuiContainer {
	
	private static final ResourceLocation[] texture = { 
		new ResourceLocation( "advancedmachines", "textures/gui/mobfarm-basic.png" ),
		new ResourceLocation( "advancedmachines", "textures/gui/mobfarm-advanced.png" ),
		new ResourceLocation( "advancedmachines", "textures/gui/mobfarm-ultimate.png" )
	};
	private MobFarmTileEntity te;
	
	public MobFarmGui( IInventory playerInv, MobFarmTileEntity te ) {
		super( new MobFarmContainer( playerInv, te ) );
		
		this.xSize = DIALOG_WIDTH[te.getBlockMetadata()];
		this.ySize = DIALOG_HEIGHT[te.getBlockMetadata()];
		
		System.out.println( "Meta = " + te.getBlockMetadata() + ", xSize = " + xSize + ", ySize = " + ySize );
		
		this.te = te;
	}
	
	public static final int SOULCAGE_SLOTS_ROW_COUNT = 2;
	public static final int SOULCAGE_SLOTS_COL_COUNT = 2;
	public static final int SOULCAGE_SLOTS_COUNT = SOULCAGE_SLOTS_ROW_COUNT * SOULCAGE_SLOTS_COL_COUNT;
	
	public static final int OUTPUT_SLOTS_ROW_COUNT = 3;
	public static final int OUTPUT_SLOTS_COL_COUNT = 6;
	public static final int OUTPUT_SLOTS_COUNT = OUTPUT_SLOTS_ROW_COUNT * OUTPUT_SLOTS_COL_COUNT;
	
	public static final int VOID_SLOTS_ROW_COUNT = 1;
	public static final int VOID_SLOTS_COL_COUNT = 9;
	public static final int VOID_SLOTS_COUNT = VOID_SLOTS_ROW_COUNT * VOID_SLOTS_COL_COUNT;
	
	public static final int TOTAL_MACHINE_SLOTS_COUNT = SOULCAGE_SLOTS_COUNT + OUTPUT_SLOTS_COUNT + VOID_SLOTS_COUNT;
	
	public static final int FIRST_SOULCAGE_SLOT_NUMBER = 0;
	public static final int FIRST_OUTPUT_SLOT_NUMBER = FIRST_SOULCAGE_SLOT_NUMBER + SOULCAGE_SLOTS_COUNT;
	public static final int FIRST_VOID_SLOT_NUMBER = FIRST_OUTPUT_SLOT_NUMBER + OUTPUT_SLOTS_COUNT;
	
	public static final int[] ACTUAL_SOULCAGE_SLOTS = { 1, 2, 4 };
	
	public static final int[] DIALOG_WIDTH = { 200, 200, 200 };
	public static final int[] DIALOG_HEIGHT = { 221, 241, 241 };
	
	final public static int[] SLOT_X_SPACING = { 18, 18, 18 };
	final public static int[] SLOT_Y_SPACING = { 18, 18, 18 };
	final public static int[] HOTBAR_XPOS = { 32, 32, 32 };
	final public static int[] HOTBAR_YPOS = { 197, 217, 217 };
	
	final public static int[] PLAYER_INVENTORY_XPOS = { 32, 32, 32 };
	final public static int[] PLAYER_INVENTORY_YPOS = { 139, 159, 159 };
	
	final public static int[] SOULCAGE_SLOTS_XPOS = { 32, 32, 32 };
	final public static int[] SOULCAGE_SLOTS_YPOS = { 19, 19, 19 };
	final public static int[] SOULCAGE_SLOTS_X_SPACING = { 83, 83, 83 };
	final public static int[] SOULCAGE_SLOTS_Y_SPACING = { 20, 20, 20 };

	final public static int[] OUTPUT_SLOTS_XPOS = { 59, 59, 59 };
	final public static int[] OUTPUT_SLOTS_YPOS = { 45, 65, 65 };
	
	final public static int[] VOID_SLOTS_XPOS = { 32, 32, 32 };
	final public static int[] VOID_SLOTS_YPOS = { 110, 130, 130 };	
	
	final public static int[] HP_BAR_XPOS = { 53, 53, 51 };
	final public static int[] HP_BAR_YPOS = { 30, 30, 30 };
	final public static int[] HP_BAR_ICON_U = { 0, 0, 0 };
	final public static int[] HP_BAR_ICON_V = { 251, 251, 251 };
	final public static int[] HP_BAR_WIDTH = { 140, 140, 59 };
	final public static int[] HP_BAR_HEIGHT = { 5, 5, 5 };
	final public static int[] HP_BAR_X_SPACING = { 83, 83, 83 };
	final public static int[] HP_BAR_Y_SPACING = { 20, 20, 20 };
	
	final public static int[] EMPTY_WAIT_ICON_U = { 0, 0, 0 };
	final public static int[] EMPTY_WAIT_ICON_V = { 241, 241, 241 };
	final public static int[] WAIT_ICON_U = { 0, 0, 0 };
	final public static int[] WAIT_ICON_V = { 246, 246, 246 };
	
	final public static int[] ENERGY_XPOS = { 33, 33, 33 };
	final public static int[] ENERGY_YPOS = { 46, 66, 66 };
	final public static int[] ENERGY_ICON_U = { 200, 200, 200 };
	final public static int[] ENERGY_ICON_V = { 0, 0, 0 };
	final public static int[] ENERGY_WIDTH = { 14, 14, 14 };
	final public static int[] ENERGY_HEIGHT = { 50, 50, 50 };
	
	final public static int[] FLUID_XPOS = { 178, 178, 178 };
	final public static int[] FLUID_YPOS = { 47, 67, 67 };
	final public static int[] FLUID_WIDTH = { 14, 14, 14 };
	final public static int[] FLUID_HEIGHT = { 50, 50, 50 };
	
	final public static int[] TAB_XPOS = { 0, 0, 0, 0 };
	final public static int[] TAB_YPOS = { 0, 25, 50, 75 };
	final public static int TAB_WIDTH = 24;
	final public static int TAB_HEIGHT = 24;
	
	final public static int[] MOB_NAME_XPOS = { 53, 53, 53 };
	final public static int[] MOB_NAME_YPOS = { 20, 20, 20 };
	final public static int[] MOB_NAME_X_SPACING = { 83, 83, 83 };
	final public static int[] MOB_NAME_Y_SPACING = { 20, 20, 20 };
	final public static int[] MOB_NAME_WIDTH = { 140, 140, 59 };	
	
	final public static int[] VOID_ITEMS_TITLE_XPOS = { 31, 31, 31 };
	final public static int[] VOID_ITEMS_TITLE_YPOS = { 100, 120, 120 };
	
	final public static int[] INVENTORY_TITLE_XPOS = { 31, 31, 31 };
	final public static int[] INVENTORY_TITLE_YPOS = { 129, 149, 149 };
	
	public static int getSoulcageSlotX( int i ) {
		return (int)( i / SOULCAGE_SLOTS_COL_COUNT );
	}
	public static int getSoulcageSlotY( int i ) {
		return i % SOULCAGE_SLOTS_ROW_COUNT;
	}

	@Override
	protected void drawGuiContainerBackgroundLayer( float partialTicks, int mouseX, int mouseY ) {
		int meta = te.getBlockMetadata();
		Minecraft.getMinecraft().getTextureManager().bindTexture( texture[meta] );
		this.drawTexturedModalRect( this.guiLeft, this.guiTop, 0, 0, this.xSize, this.ySize );
		
		for ( int i = 0; i < ACTUAL_SOULCAGE_SLOTS[meta]; i++ ) {
			double hpRemaining = te.getHpPercent( i );
			int width = (int)( hpRemaining * HP_BAR_WIDTH[meta] );
			int x = getSoulcageSlotX( i );
			int y = getSoulcageSlotY( i );
			this.drawTexturedModalRect(
				this.guiLeft + HP_BAR_XPOS[meta] + HP_BAR_X_SPACING[meta] * x,
				this.guiTop + HP_BAR_YPOS[meta] + HP_BAR_Y_SPACING[meta] * y,
				HP_BAR_ICON_U[meta],
				HP_BAR_ICON_V[meta],
				width,
				HP_BAR_HEIGHT[meta]
			);
		}
		
		double energy = te.getEnergyPercent();
		int yOffset = (int)( ( 1.0 - energy ) * ENERGY_HEIGHT[meta] );
		this.drawTexturedModalRect(
			this.guiLeft + ENERGY_XPOS[meta],
			this.guiTop + ENERGY_YPOS[meta] + yOffset,
			ENERGY_ICON_U[meta],
			ENERGY_ICON_V[meta] + yOffset,
			ENERGY_WIDTH[meta],
			ENERGY_HEIGHT[meta] - yOffset
		);
	}

	@Override
	protected void drawGuiContainerForegroundLayer(int param1, int param2) {
		int meta = te.getBlockMetadata();
		
		this.drawCenteredLangString( "tile.blockMobFarm.name", 7, 7, 162, 0x404040 );
		
		this.drawLangString( "gui.voiditems.label",
			VOID_ITEMS_TITLE_XPOS[meta],
			VOID_ITEMS_TITLE_YPOS[meta],
			0x404040
		);
		this.drawLangString( "gui.inventory.label", 
			INVENTORY_TITLE_XPOS[meta],
			INVENTORY_TITLE_YPOS[meta],
			0x404040
		);
		
		for ( int i = 0; i < ACTUAL_SOULCAGE_SLOTS[meta]; i++ ) {
			int x = getSoulcageSlotX( i );
			int y = getSoulcageSlotY( i );
			
			this.drawCenteredString( te.getMobName( i ), 
				MOB_NAME_XPOS[meta] + x * MOB_NAME_X_SPACING[meta],
				MOB_NAME_YPOS[meta] + y * MOB_NAME_Y_SPACING[meta], 
				MOB_NAME_WIDTH[meta],
				0x404040
			);
			
			this.drawTooltip( 29, 18, 140, 18, "HP: " + te.getHp( i ) + "/" + te.getMaxHp( i ) );
		}
		
		this.drawTooltip( 9, 49, 14, 68, 
				te.getEnergyStored( null ) + "/" + te.getMaxEnergyStored( null ) + " RF"
		);
	}
}
