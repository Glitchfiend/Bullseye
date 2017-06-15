package bullseye.init;

import bullseye.api.BEItems;
import bullseye.item.ItemBEArrow;
import bullseye.item.ItemDyeArrow;
import net.minecraft.init.Items;
import net.minecraft.init.PotionTypes;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.PotionUtils;

import static bullseye.util.inventory.CraftingUtil.*;

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
        addShapedRecipe(new ItemStack(BEItems.arrow, 4, ItemBEArrow.ArrowType.TRAINING.ordinal()), new Object [] {"R", "S", "F", 'R', Items.RABBIT_HIDE, 'S', "stickWood", 'F', Items.FEATHER});
        addShapedRecipe(new ItemStack(BEItems.arrow, 4, ItemBEArrow.ArrowType.EGG.ordinal()), new Object [] {"E", "S", "F", 'E', Items.EGG, 'S', "stickWood", 'F', Items.FEATHER});
        addShapedRecipe(new ItemStack(BEItems.arrow, 4, ItemBEArrow.ArrowType.EXTINGUISHING.ordinal()), new Object [] {"T", "S", "F", 'T', PotionUtils.addPotionToItemStack(new ItemStack(Items.POTIONITEM), PotionTypes.WATER), 'S', "stickWood", 'F', Items.FEATHER});
        addShapedRecipe(new ItemStack(BEItems.arrow, 4, ItemBEArrow.ArrowType.DIAMOND.ordinal()), new Object [] {"D", "S", "F", 'D', Items.DIAMOND, 'S', "stickWood", 'F', Items.FEATHER});
        addShapedRecipe(new ItemStack(BEItems.arrow, 4, ItemBEArrow.ArrowType.BOMB.ordinal()), new Object [] {"T", "S", "F", 'T', Items.GUNPOWDER, 'S', "stickWood", 'F', Items.FEATHER});
        addShapedRecipe(new ItemStack(BEItems.arrow, 4, ItemBEArrow.ArrowType.FIRE.ordinal()), new Object [] {"T", "S", "F", 'T', Items.BLAZE_POWDER, 'S', "stickWood", 'F', Items.FEATHER});
        addShapedRecipe(new ItemStack(BEItems.arrow, 4, ItemBEArrow.ArrowType.ICE.ordinal()), new Object [] {"T", "S", "F", 'T', Items.SNOWBALL, 'S', "stickWood", 'F', Items.FEATHER});
        addShapedRecipe(new ItemStack(BEItems.arrow, 4, ItemBEArrow.ArrowType.LIGHTNING.ordinal()), new Object [] {"T", "S", "F", 'T', Items.NETHER_STAR, 'S', "stickWood", 'F', Items.FEATHER});
        addShapedRecipe(new ItemStack(BEItems.arrow, 4, ItemBEArrow.ArrowType.PRISMARINE.ordinal()), new Object [] {"T", "S", "F", 'T', Items.PRISMARINE_SHARD, 'S', "stickWood", 'F', Items.FEATHER});
        
        addShapedRecipe(new ItemStack(BEItems.dye_arrow, 4, ItemDyeArrow.DyeType.RED.ordinal()), new Object [] {"D", "S", "F", 'D', "dyeRed", 'S', "stickWood", 'F', Items.FEATHER});
        addShapedRecipe(new ItemStack(BEItems.dye_arrow, 4, ItemDyeArrow.DyeType.ORANGE.ordinal()), new Object [] {"D", "S", "F", 'D', "dyeOrange", 'S', "stickWood", 'F', Items.FEATHER});
        addShapedRecipe(new ItemStack(BEItems.dye_arrow, 4, ItemDyeArrow.DyeType.YELLOW.ordinal()), new Object [] {"D", "S", "F", 'D', "dyeYellow", 'S', "stickWood", 'F', Items.FEATHER});
        addShapedRecipe(new ItemStack(BEItems.dye_arrow, 4, ItemDyeArrow.DyeType.LIME.ordinal()), new Object [] {"D", "S", "F", 'D', "dyeLime", 'S', "stickWood", 'F', Items.FEATHER});
        addShapedRecipe(new ItemStack(BEItems.dye_arrow, 4, ItemDyeArrow.DyeType.GREEN.ordinal()), new Object [] {"D", "S", "F", 'D', "dyeGreen", 'S', "stickWood", 'F', Items.FEATHER});
        addShapedRecipe(new ItemStack(BEItems.dye_arrow, 4, ItemDyeArrow.DyeType.CYAN.ordinal()), new Object [] {"D", "S", "F", 'D', "dyeCyan", 'S', "stickWood", 'F', Items.FEATHER});
        addShapedRecipe(new ItemStack(BEItems.dye_arrow, 4, ItemDyeArrow.DyeType.LIGHT_BLUE.ordinal()), new Object [] {"D", "S", "F", 'D', "dyeLightBlue", 'S', "stickWood", 'F', Items.FEATHER});
        addShapedRecipe(new ItemStack(BEItems.dye_arrow, 4, ItemDyeArrow.DyeType.BLUE.ordinal()), new Object [] {"D", "S", "F", 'D', "dyeBlue", 'S', "stickWood", 'F', Items.FEATHER});
        addShapedRecipe(new ItemStack(BEItems.dye_arrow, 4, ItemDyeArrow.DyeType.PURPLE.ordinal()), new Object [] {"D", "S", "F", 'D', "dyePurple", 'S', "stickWood", 'F', Items.FEATHER});
        addShapedRecipe(new ItemStack(BEItems.dye_arrow, 4, ItemDyeArrow.DyeType.MAGENTA.ordinal()), new Object [] {"D", "S", "F", 'D', "dyeMagenta", 'S', "stickWood", 'F', Items.FEATHER});
        addShapedRecipe(new ItemStack(BEItems.dye_arrow, 4, ItemDyeArrow.DyeType.PINK.ordinal()), new Object [] {"D", "S", "F", 'D', "dyePink", 'S', "stickWood", 'F', Items.FEATHER});
        addShapedRecipe(new ItemStack(BEItems.dye_arrow, 4, ItemDyeArrow.DyeType.BROWN.ordinal()), new Object [] {"D", "S", "F", 'D', "dyeBrown", 'S', "stickWood", 'F', Items.FEATHER});
        addShapedRecipe(new ItemStack(BEItems.dye_arrow, 4, ItemDyeArrow.DyeType.WHITE.ordinal()), new Object [] {"D", "S", "F", 'D', "dyeWhite", 'S', "stickWood", 'F', Items.FEATHER});
        addShapedRecipe(new ItemStack(BEItems.dye_arrow, 4, ItemDyeArrow.DyeType.SILVER.ordinal()), new Object [] {"D", "S", "F", 'D', "dyeLightGray", 'S', "stickWood", 'F', Items.FEATHER});
        addShapedRecipe(new ItemStack(BEItems.dye_arrow, 4, ItemDyeArrow.DyeType.GRAY.ordinal()), new Object [] {"D", "S", "F", 'D', "dyeGray", 'S', "stickWood", 'F', Items.FEATHER});
        addShapedRecipe(new ItemStack(BEItems.dye_arrow, 4, ItemDyeArrow.DyeType.BLACK.ordinal()), new Object [] {"D", "S", "F", 'D', "dyeBlack", 'S', "stickWood", 'F', Items.FEATHER});
    }
}
