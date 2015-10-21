package com.jaminv.advancedmachines.item;

import java.util.ArrayList;
import java.util.List;

import com.jaminv.advancedmachines.AdvancedMachines;
import com.jaminv.advancedmachines.ModObject;
import com.jaminv.advancedmachines.config.Config;

import cpw.mods.fml.common.registry.GameRegistry;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.block.Block;
import net.minecraft.block.BlockFence;
import net.minecraft.block.BlockWall;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityList;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.boss.EntityWither;
import net.minecraft.entity.boss.IBossDisplayData;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ChatComponentTranslation;
import net.minecraft.util.Facing;
import net.minecraft.util.IChatComponent;
import net.minecraft.util.IIcon;
import net.minecraft.util.StatCollector;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;

public class ItemSoulCage extends Item {
	
	private List<String> blacklist;
	
	private IIcon filledIcon;
	
	public ItemSoulCage() {
		setCreativeTab( CreativeTabs.tabMisc );
		setUnlocalizedName( ModObject.itemSoulCage.unlocalizedName );
		setMaxStackSize( 16 );
		blacklist = new ArrayList<String>();
		for ( String entity : Config.mobfarmBlacklist ) {
			blacklist.add( entity );
		}		
	}

	@Override
	@SideOnly( Side.CLIENT )
	public void registerIcons( IIconRegister ir ) {
		itemIcon = ir.registerIcon( AdvancedMachines.MODID + ":soulcage" );
		filledIcon = ir.registerIcon( AdvancedMachines.MODID + ":soulcage-filled" );
	}
	
	public boolean hasSoul( ItemStack item ) {
		if ( item == null ) {
			return false;
		}
		if ( item.getItem() != this ) {
			return false;
		}
		return item.stackTagCompound != null && item.stackTagCompound.hasKey( "id" );
	}
	
	public String getMobType( ItemStack item ) {
		if ( ! hasSoul( item ) ) {
			return null;
		}
		return item.stackTagCompound.getString( "id" );
		}



	@Override
	public IIcon getIcon(ItemStack stack, int renderPass, EntityPlayer player, ItemStack usingItem, int useRemaining ) {
		if ( hasSoul( stack ) ) {
			return filledIcon;
		}
		return itemIcon;
	}

	@Override
	@SideOnly( Side.CLIENT )
	public IIcon getIconIndex( ItemStack item ) {
		if ( hasSoul( item ) ) {
			return filledIcon;
		}
		return itemIcon;
	}

	@Override
	@SideOnly( Side.CLIENT )
	public boolean hasEffect(ItemStack par1ItemStack, int pass) {
		return false;
	}
	
	private boolean isBlacklisted( String entityId ) {
		for ( String str : blacklist ) {
			if ( str != null && str.equals( entityId ) ) {
				return true;
			}
		}
		return false;
	}
	
	private void setDisplayName( ItemStack item, Entity ent ) {
		if ( ent instanceof EntityLiving ) {
			EntityLiving entLiv = (EntityLiving)ent;
			if ( ((EntityLiving) ent).hasCustomNameTag() ) {
				String name = entLiv.getCustomNameTag();
				if ( name.length() > 0 ) {
					item.setStackDisplayName( name );
				}
			}
		}
	}
	
	public Entity getSoulEntity( ItemStack item, World world ) {
		if ( ! this.hasSoul( item ) ) { return null; }

		Entity mob;
		NBTTagCompound root = item.stackTagCompound;
		if ( root.hasKey( "isStub" ) ) {
			String entityId = root.getString( "id" );
			mob = EntityList.createEntityByName( entityId,  world );
		} else {
			mob = EntityList.createEntityFromNBT( root, world );
		}
		mob.readFromNBT( root );
		
		return mob;
	}

	@Override
	public boolean onItemUse( ItemStack item, EntityPlayer player, World world, 
			int x, int y, int z, int side, float xOffset, float yOffset, float zOffset) {
		
		if ( world.isRemote ) {
			return true;
		}
		if ( ! hasSoul( item ) ) {
			return false;
		}
		if ( player == null ) {
			return false;
		}
		
		Entity mob = this.getSoulEntity( item, world );
		
		Block blk = world.getBlock( x, y, z );
		double spawnX = x + Facing.offsetsXForSide[side] + 0.5;
		double spawnY = y + Facing.offsetsYForSide[side];
		double spawnZ = z + Facing.offsetsZForSide[side] + 0.5;
		if ( side == ForgeDirection.UP.ordinal() && (blk instanceof BlockFence || blk instanceof BlockWall ) ) {
			spawnY += 0.5;
		}
		mob.setLocationAndAngles( spawnX, spawnY, spawnZ, world.rand.nextFloat() * 360.0F, 0 );

		boolean spaceClear = world.checkNoEntityCollision( mob.boundingBox )
				&& world.getCollidingBoundingBoxes( mob, mob.boundingBox ).isEmpty();
		if ( ! spaceClear ) {
			return false;
		}
		
		if ( item.hasDisplayName() && mob instanceof EntityLiving ) {
			((EntityLiving)mob).setCustomNameTag( item.getDisplayName() );
		}
		
		world.spawnEntityInWorld( mob );
		if ( mob instanceof EntityLiving ) {
			((EntityLiving)mob).playLivingSound();
		}
		
		if ( player == null || ! player.capabilities.isCreativeMode ) {
			if ( item.stackSize > 1 ) {
				item.stackSize--;
				player.inventory.addItemStackToInventory( new ItemStack( this ) );
				player.inventoryContainer.detectAndSendChanges();
			} else {
				item.setTagCompound( null );
			}
		}
		
		return true;
	}

	@Override
	public boolean itemInteractionForEntity( ItemStack item, EntityPlayer player, EntityLivingBase entity ) {

		if ( entity.worldObj.isRemote ) {
			return false;
		}
		if ( hasSoul( item ) ) {
			return false;
		}
		if ( entity instanceof EntityPlayer ) {
			return false;
		}
		
		String entityId = EntityList.getEntityString( entity );
		if ( isBlacklisted( entityId ) ) {
			return false;
		}
		
		if ( ! Config.mobfarmAllowBosses && entity instanceof IBossDisplayData ) {
			return false;
		}
		if( entity.isEntityInvulnerable() ) {
			player.addChatMessage( new ChatComponentTranslation( "msg.soulcage.invulnerable" ) );
			return false;
		}
		if( entity instanceof EntityWither ) {
			if ( ((EntityWither)entity).func_82212_n() > 0 ) {
				player.addChatMessage( new ChatComponentTranslation( "msg.soulcage.invulnerable" ) );
				return false;
			}
		}
		
		NBTTagCompound root = new NBTTagCompound();
		root.setString( "id", entityId );
		entity.writeToNBT( root );
		
		float health = entity.getHealth();
		float maxhealth = entity.getMaxHealth();
		System.out.println( "health: " + health + ", maxhealth: " + maxhealth );
		// Health is reduced by 10 to give a small chance to capture large mobs
		// and a large chance to capture small mobs.
		float failChance = Math.max( 0F, ( health - 10F ) / maxhealth );
		float rand = (float) Math.random();
		System.out.println( "failChance: " + failChance + ", rand: " + rand );
		if ( failChance > rand ) {
			player.addChatMessage( new ChatComponentTranslation( "msg.soulcage.fail" ) );
			return false;
		}
		
		ItemStack filledCage = new ItemStack( ModItems.itemSoulCage );
		filledCage.setTagCompound( root );
		setDisplayName( filledCage, entity );
		
		player.swingItem();
		
		entity.setDead();
		if( entity.isDead ) {
			item.stackSize--;
			if ( ! player.inventory.addItemStackToInventory( filledCage ) ) {
				entity.worldObj.spawnEntityInWorld( new EntityItem( entity.worldObj, entity.posX, entity.posY, entity.posZ, filledCage ) );
			}
			( (EntityPlayerMP)player ).sendContainerToPlayer( player.inventoryContainer );
			return true;
		}
		return false;
	}
	
	public String getSoulEntityName( ItemStack item ) {
		String mobname = getMobType( item );
		if ( mobname == null ) { return null; }
		return StatCollector.translateToLocal( "entity." + mobname + ".name" );		
	}

	@Override
	public void addInformation( ItemStack item, EntityPlayer player, List list, boolean advanced ) {
		if ( item != null ) {
			String mobname = getMobType( item );
			if ( mobname != null ) {
				list.add( StatCollector.translateToLocal( "entity." + mobname + ".name" ) );
				if ( advanced ) {
					list.add( "entity." + mobname );
				}
			} else {
				list.add( StatCollector.translateToLocal( "item.itemSoulCage.tooltip.empty" ) );
			}
		
		}
	}
	
	
}
