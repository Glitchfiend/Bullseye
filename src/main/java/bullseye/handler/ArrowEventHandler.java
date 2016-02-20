package bullseye.handler;

import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import net.minecraftforge.event.entity.player.ArrowNockEvent;
import net.minecraftforge.event.entity.player.PlayerUseItemEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import bullseye.api.BEItems;
import bullseye.entities.projectile.EntityBEArrow;
import bullseye.item.ItemBEArrow;

public class ArrowEventHandler
{
	@SubscribeEvent
    public void onArrowNock(ArrowNockEvent event)
    {
		EntityPlayer player = event.entityPlayer;
		ItemStack itemstack = event.result;
		Item item = itemstack.getItem();
		
		if (player.inventory.hasItem(BEItems.arrow))
		{
			player.setItemInUse(itemstack, item.getMaxItemUseDuration(itemstack));
		}
    }
	
	@SubscribeEvent
	public void onPlayerStopUsingItem(PlayerUseItemEvent.Stop event)
	{
		EntityPlayer player = event.entityPlayer;
		World world = player.worldObj;
		ItemStack itemstack = event.item;
		Item item = itemstack.getItem();
		
		if (itemstack.getItem() == Items.bow)
		{		
	        boolean flag = player.capabilities.isCreativeMode || EnchantmentHelper.getEnchantmentLevel(Enchantment.infinity.effectId, itemstack) > 0;
	
	        if (flag || player.inventory.hasItem(BEItems.arrow))
	        {
	        	if (!world.isRemote)
	        	{
	        		int bestArrowSlot = -1;
		            ItemBEArrow.ArrowType bestAvailableArrowType = null;
		            for (int k = 0; k < player.inventory.mainInventory.length; ++k)
		            {
		                ItemStack current = player.inventory.mainInventory[k];
		                if (current != null)
		                {
		                	if (current.getItem() == Items.arrow)
		                	{
		                		bestAvailableArrowType = null;
		                		bestArrowSlot = -1;
		                		break;
		                	}
		                	if (current.getItem() == BEItems.arrow)
		                	{
			                	bestAvailableArrowType = ItemBEArrow.ArrowType.fromMeta(current.getMetadata());
			                    bestArrowSlot = k;
		                        break;
		                	}
		                }
		            }
	        	
		            if (bestArrowSlot > -1 && bestAvailableArrowType != null)
		            {
			            int i = item.getMaxItemUseDuration(itemstack) - event.duration;
			            net.minecraftforge.event.entity.player.ArrowLooseEvent looseevent = new net.minecraftforge.event.entity.player.ArrowLooseEvent(player, itemstack, i);
			            if (net.minecraftforge.common.MinecraftForge.EVENT_BUS.post(looseevent)) return;
			            i = looseevent.charge;
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
			
			            EntityBEArrow entitybearrow = new EntityBEArrow(world, player, f * 2.0F);
			            entitybearrow.setArrowType(bestAvailableArrowType);
			
			            if (f == 1.0F)
			            {
			                entitybearrow.setIsCritical(true);
			            }
			            
			            int j = EnchantmentHelper.getEnchantmentLevel(Enchantment.power.effectId, itemstack);

			            if (j > 0)
			            {
			                entitybearrow.setDamage(entitybearrow.getDamage() + (double)j * 0.5D + 0.5D);
			            }

			            int k = EnchantmentHelper.getEnchantmentLevel(Enchantment.punch.effectId, itemstack);

			            if (k > 0)
			            {
			                entitybearrow.setKnockbackStrength(k);
			            }
			
			            itemstack.damageItem(1, player);
			            world.playSoundAtEntity(player, "random.bow", 1.0F, 1.0F / (world.rand.nextFloat() * 0.4F + 1.2F) + f * 0.5F);
			            
			            if (flag)
			            {
			                entitybearrow.canBePickedUp = 2;
			            }
			            else
			            {
			            	player.inventory.decrStackSize(bestArrowSlot, 1);
			            }
			
			            //player.triggerAchievement(StatList.objectUseStats[Item.getIdFromItem(this)]);
			
			            world.spawnEntityInWorld(entitybearrow);
			            
			            if (bestAvailableArrowType == ItemBEArrow.ArrowType.FIRE_ARROW)
			            {
			            	world.playSoundAtEntity(entitybearrow, "item.fireCharge.use", 1.0F, (world.rand.nextFloat() - world.rand.nextFloat()) * 0.2F + 1.0F);
			            }
			            if (bestAvailableArrowType == ItemBEArrow.ArrowType.BOMB_ARROW)
			            {
			            	world.playSoundAtEntity(entitybearrow, "game.tnt.primed", 1.0F, 1.0F);
			            }
		            
			            event.setCanceled(true);
		            }
	        	}
	        }
		}
	}
}