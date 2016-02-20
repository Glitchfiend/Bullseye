package bullseye.init;

import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.oredict.ShapedOreRecipe;
import bullseye.api.BEItems;
import bullseye.item.ItemArrowTopper;
import bullseye.item.ItemBEArrow;

public class ModCrafting
{
    public static void init()
    {
        addCraftingRecipies();
    }
    
    private static void addCraftingRecipies()
    {
    	// Register crafting recipes

    	// Arrows
    	GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(BEItems.arrow, 4, ItemBEArrow.ArrowType.FIRE_ARROW.ordinal()), new Object [] {"T", "S", "F", 'T', new ItemStack(BEItems.arrow_topper, 1, ItemArrowTopper.TopperType.FIRE_ARROW_TOPPER.ordinal()), 'S', "stickWood", 'F', Items.feather}));
    	GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(BEItems.arrow, 4, ItemBEArrow.ArrowType.ICE_ARROW.ordinal()), new Object [] {"T", "S", "F", 'T', new ItemStack(BEItems.arrow_topper, 1, ItemArrowTopper.TopperType.ICE_ARROW_TOPPER.ordinal()), 'S', "stickWood", 'F', Items.feather}));
    	GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(BEItems.arrow, 4, ItemBEArrow.ArrowType.LIGHTNING_ARROW.ordinal()), new Object [] {"T", "S", "F", 'T', new ItemStack(BEItems.arrow_topper, 1, ItemArrowTopper.TopperType.LIGHTNING_ARROW_TOPPER.ordinal()), 'S', "stickWood", 'F', Items.feather}));
    	GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(BEItems.arrow, 4, ItemBEArrow.ArrowType.BOMB_ARROW.ordinal()), new Object [] {"T", "S", "F", 'T', new ItemStack(BEItems.arrow_topper, 1, ItemArrowTopper.TopperType.BOMB_ARROW_TOPPER.ordinal()), 'S', "stickWood", 'F', Items.feather}));
    	
    	// Arrow Toppers
    	GameRegistry.addShapedRecipe(new ItemStack(BEItems.arrow_topper, 1, ItemArrowTopper.TopperType.FIRE_ARROW_TOPPER.ordinal()), new Object [] {" B ", "BCB", " B ", 'B', Items.blaze_powder, 'C', Items.coal});
    	GameRegistry.addShapedRecipe(new ItemStack(BEItems.arrow_topper, 1, ItemArrowTopper.TopperType.ICE_ARROW_TOPPER.ordinal()), new Object [] {" F ", "FIF", " F ", 'F', Items.snowball, 'I', Blocks.ice});
    	GameRegistry.addShapedRecipe(new ItemStack(BEItems.arrow_topper, 1, ItemArrowTopper.TopperType.LIGHTNING_ARROW_TOPPER.ordinal()), new Object [] {" P ", "PNP", " P ", 'P', Items.prismarine_crystals, 'N', Items.nether_star});
    	GameRegistry.addShapedRecipe(new ItemStack(BEItems.arrow_topper, 1, ItemArrowTopper.TopperType.BOMB_ARROW_TOPPER.ordinal()), new Object [] {" C ", "CGC", " C ", 'C', Items.clay_ball, 'G', Items.gunpowder});
    }
}
