package bullseye.handler;

import bullseye.api.BEItems;
import bullseye.entities.projectile.EntityBEArrow;
import bullseye.entities.projectile.EntityDyeArrow;
import bullseye.item.ItemBEArrow;
import bullseye.item.ItemDyeArrow;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Enchantments;
import net.minecraft.init.Items;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.SoundCategory;
import net.minecraft.world.World;
import net.minecraftforge.event.entity.player.ArrowLooseEvent;
import net.minecraftforge.event.entity.player.ArrowNockEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class ArrowEventHandler
{
	@SubscribeEvent
    public void onArrowNock(ArrowNockEvent event)
    {
		EntityPlayer player = event.getEntityPlayer();
		ItemStack itemstack = event.getBow();
		Item item = itemstack.getItem();
		
		if (player.inventory.hasItemStack(new ItemStack(BEItems.arrow)) || player.inventory.hasItemStack(new ItemStack(BEItems.dye_arrow)))
		{
            for (int k = 0; k < player.inventory.mainInventory.length; ++k)
            {
                ItemStack current = player.inventory.mainInventory[k];
                if (current != null)
                {
                	if (current.getItem() == Items.ARROW)
                	{
                		break;
                	}
                	if (current.getItem() == Items.TIPPED_ARROW)
                	{
                		break;
                	}
                	if (current.getItem() == BEItems.arrow)
                	{
                		itemstack = new ItemStack(item, item.getMaxItemUseDuration(itemstack));
                		break;
                	}
                	if (current.getItem() == BEItems.dye_arrow)
                	{
                		itemstack = new ItemStack(item, item.getMaxItemUseDuration(itemstack));
                		break;
                	}
                }
            }
		}
    }
	
	@SubscribeEvent
	public void onArrowLoose(ArrowLooseEvent event)
	{
		EntityPlayer player = event.getEntityPlayer();
		World world = player.worldObj;
		ItemStack itemstack = event.getBow();
		Item item = itemstack.getItem();
		
		if (itemstack.getItem() == Items.BOW)
		{		
	        boolean flag = player.capabilities.isCreativeMode || EnchantmentHelper.getEnchantmentLevel(Enchantments.INFINITY, itemstack) > 0;
	
	        if (flag || player.inventory.hasItemStack(new ItemStack(BEItems.arrow)) || player.inventory.hasItemStack(new ItemStack(BEItems.dye_arrow)))
	        {
	        	if (!world.isRemote)
	        	{
	        		int bestArrowSlot = -1;
		            ItemBEArrow.ArrowType bestAvailableArrowType = null;
		            ItemDyeArrow.DyeType bestAvailableDyeType = null;
		            for (int k = 0; k < player.inventory.mainInventory.length; ++k)
		            {
		                ItemStack current = player.inventory.mainInventory[k];
		                if (current != null)
		                {
		                	if (current.getItem() == Items.ARROW)
		                	{
		                		bestAvailableArrowType = null;
		                		bestArrowSlot = -1;
		                		break;
		                	}
		                	if (current.getItem() == Items.TIPPED_ARROW)
		                	{
		                		bestAvailableArrowType = null;
		                		bestArrowSlot = -1;
		                		break;
		                	}
		                	if (current.getItem() == BEItems.arrow)
		                	{
			                	bestAvailableArrowType = ItemBEArrow.ArrowType.fromMeta(current.getMetadata());
			                	bestAvailableDyeType = null;
			                    bestArrowSlot = k;
		                        break;
		                	}
		                	if (current.getItem() == BEItems.dye_arrow)
		                	{
			                	bestAvailableDyeType = ItemDyeArrow.DyeType.fromMeta(current.getMetadata());
			                	bestAvailableArrowType = null;
			                    bestArrowSlot = k;
		                        break;
		                	}
		                }
		            }
	        	
		            if (bestArrowSlot > -1 && (bestAvailableArrowType != null || bestAvailableDyeType != null))
		            {
			            int i = item.getMaxItemUseDuration(itemstack) - event.getCharge();
			            ArrowLooseEvent looseevent = new ArrowLooseEvent(player, itemstack, world, i, flag);
			            if (net.minecraftforge.common.MinecraftForge.EVENT_BUS.post(looseevent)) return;
			            i = looseevent.getCharge();
			            float f = (float)i / 20.0F;
			            f = (f * f + f * 2.0F) / 3.0F;
			
			            if ((double)f < 0.1D)
			            {
			                return;
			            }
			
			            if (f > 1.0F)
			            {
			                f = 1.0F;
			            }
			
			            if (bestAvailableArrowType != null)
			            {
			            	EntityBEArrow entitybearrow = new EntityBEArrow(world, player, f * 2.0F);
				            entitybearrow.setArrowType(bestAvailableArrowType);
				            if (f == 1.0F)
				            {
				                entitybearrow.setIsCritical(true);
				            }
				            
				            int j = EnchantmentHelper.getEnchantmentLevel(Enchantments.POWER, itemstack);

				            if (j > 0)
				            {
				            	if (bestAvailableArrowType != ItemBEArrow.ArrowType.TRAINING)
				            	{
				            		entitybearrow.setDamage(entitybearrow.getDamage() + (double)j * 0.5D + 0.5D);
				            	}
				            }

				            int k = EnchantmentHelper.getEnchantmentLevel(Enchantments.PUNCH, itemstack);

				            if (k > 0)
				            {
				                entitybearrow.setKnockbackStrength(k);
				            }
				            
				            if (EnchantmentHelper.getEnchantmentLevel(Enchantments.FLAME, itemstack) > 0)
				            {
				            	if (bestAvailableArrowType == ItemBEArrow.ArrowType.DIAMOND)
				            	{
				            		entitybearrow.setFire(100);	
				            	}
				            }
				
				            itemstack.damageItem(1, player);
				            world.playSound((EntityPlayer)null, player.getPosition(), SoundEvents.ENTITY_ARROW_SHOOT, SoundCategory.PLAYERS, 1.0F, 1.0F / (world.rand.nextFloat() * 0.4F + 1.2F) + f * 0.5F);
				            
				            if (flag)
				            {
				            	entitybearrow.pickupStatus = EntityBEArrow.PickupStatus.CREATIVE_ONLY;
				            }
				            else
				            {
				            	player.inventory.decrStackSize(bestArrowSlot, 1);
				            }
				
				            //player.triggerAchievement(StatList.objectUseStats[Item.getIdFromItem(this)]);
				
				            world.spawnEntityInWorld(entitybearrow);
				            
				            if (bestAvailableArrowType == ItemBEArrow.ArrowType.FIRE)
				            {
				            	world.playSound((EntityPlayer)null, entitybearrow.getPosition(), SoundEvents.ITEM_FIRECHARGE_USE, SoundCategory.NEUTRAL,  1.0F, (world.rand.nextFloat() - world.rand.nextFloat()) * 0.2F + 1.0F);
				            }
				            if (bestAvailableArrowType == ItemBEArrow.ArrowType.BOMB)
				            {
				            	world.playSound((EntityPlayer)null, entitybearrow.getPosition(), SoundEvents.ENTITY_TNT_PRIMED, SoundCategory.NEUTRAL,  1.0F, 1.0F);
				            }
			            }
			            if (bestAvailableDyeType != null)
			            {
			            	EntityDyeArrow entitydyearrow = new EntityDyeArrow(world, player, f * 2.0F);
				            entitydyearrow.setDyeType(bestAvailableDyeType);
				            
				            if (f == 1.0F)
				            {
				            	entitydyearrow.setIsCritical(true);
				            }
				            
				            int j = EnchantmentHelper.getEnchantmentLevel(Enchantments.POWER, itemstack);

				            if (j > 0)
				            {
				            	entitydyearrow.setDamage(entitydyearrow.getDamage() + (double)j * 0.5D + 0.5D);
				            }

				            int k = EnchantmentHelper.getEnchantmentLevel(Enchantments.PUNCH, itemstack);

				            if (k > 0)
				            {
				            	entitydyearrow.setKnockbackStrength(k);
				            }
				
				            itemstack.damageItem(1, player);
				            world.playSound((EntityPlayer)null, player.getPosition(), SoundEvents.ENTITY_ARROW_SHOOT, SoundCategory.PLAYERS, 1.0F, 1.0F / (world.rand.nextFloat() * 0.4F + 1.2F) + f * 0.5F);
				            
				            if (flag)
				            {
				            	entitydyearrow.pickupStatus = EntityDyeArrow.PickupStatus.CREATIVE_ONLY;
				            }
				            else
				            {
				            	player.inventory.decrStackSize(bestArrowSlot, 1);
				            }
				
				            //player.triggerAchievement(StatList.objectUseStats[Item.getIdFromItem(this)]);
				
				            world.spawnEntityInWorld(entitydyearrow);
			            }
		            
			            event.setCanceled(true);
		            }
	        	}
	        }
		}
	}
}