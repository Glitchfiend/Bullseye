package bullseye.init;

import net.minecraft.block.BlockDispenser;
import net.minecraft.item.ItemStack;
import net.minecraft.util.WeightedRandomChestContent;
import net.minecraftforge.common.ChestGenHooks;
import bullseye.api.BEItems;
import bullseye.entities.projectiles.dispenser.DispenserBehaviorBEArrow;
import bullseye.item.ItemBEArrow;

public class ModVanillaCompat
{
    public static void init()
    {
    	registerDispenserBehaviors();
    	addDungeonLoot();
    }
    
    private static void registerDispenserBehaviors()
    {
    	BlockDispenser.dispenseBehaviorRegistry.putObject(BEItems.arrow, new DispenserBehaviorBEArrow());
    }
    
	private static void addDungeonLoot()
	{
		ChestGenHooks dungeon = ChestGenHooks.getInfo(ChestGenHooks.DUNGEON_CHEST);

		dungeon.addItem(new WeightedRandomChestContent(new ItemStack(BEItems.arrow, 1, ItemBEArrow.ArrowType.FIRE_ARROW.ordinal()), 1, 4, 5));
		dungeon.addItem(new WeightedRandomChestContent(new ItemStack(BEItems.arrow, 1, ItemBEArrow.ArrowType.ICE_ARROW.ordinal()), 1, 4, 7));
		dungeon.addItem(new WeightedRandomChestContent(new ItemStack(BEItems.arrow, 1, ItemBEArrow.ArrowType.BOMB_ARROW.ordinal()), 1, 4, 3));
		dungeon.addItem(new WeightedRandomChestContent(new ItemStack(BEItems.arrow, 1, ItemBEArrow.ArrowType.LIGHTNING_ARROW.ordinal()), 1, 4, 1));
	}
}
