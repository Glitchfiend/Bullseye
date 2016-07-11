package bullseye.init;

import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.init.PotionTypes;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.PotionUtils;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.oredict.ShapedOreRecipe;
import bullseye.api.BEItems;
import bullseye.item.ItemArrowTopper;
import bullseye.item.ItemBEArrow;
import bullseye.item.ItemDyeArrow;

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
    	GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(BEItems.arrow, 4, ItemBEArrow.ArrowType.TRAINING.ordinal()), new Object [] {"R", "S", "F", 'R', Items.RABBIT_HIDE, 'S', "stickWood", 'F', Items.FEATHER}));
    	GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(BEItems.arrow, 4, ItemBEArrow.ArrowType.EGG.ordinal()), new Object [] {"E", "S", "F", 'E', Items.EGG, 'S', "stickWood", 'F', Items.FEATHER}));
    	GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(BEItems.arrow, 4, ItemBEArrow.ArrowType.EXTINGUISHING.ordinal()), new Object [] {"T", "S", "F", 'T', PotionUtils.addPotionToItemStack(new ItemStack(Items.POTIONITEM), PotionTypes.WATER), 'S', "stickWood", 'F', Items.FEATHER}));
    	GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(BEItems.arrow, 4, ItemBEArrow.ArrowType.DIAMOND.ordinal()), new Object [] {"D", "S", "F", 'D', Items.DIAMOND, 'S', "stickWood", 'F', Items.FEATHER}));
    	GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(BEItems.arrow, 4, ItemBEArrow.ArrowType.BOMB.ordinal()), new Object [] {"T", "S", "F", 'T', new ItemStack(BEItems.arrow_topper, 1, ItemArrowTopper.TopperType.BOMB.ordinal()), 'S', "stickWood", 'F', Items.FEATHER}));
    	GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(BEItems.arrow, 4, ItemBEArrow.ArrowType.FIRE.ordinal()), new Object [] {"T", "S", "F", 'T', new ItemStack(BEItems.arrow_topper, 1, ItemArrowTopper.TopperType.FIRE.ordinal()), 'S', "stickWood", 'F', Items.FEATHER}));
    	GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(BEItems.arrow, 4, ItemBEArrow.ArrowType.ICE.ordinal()), new Object [] {"T", "S", "F", 'T', new ItemStack(BEItems.arrow_topper, 1, ItemArrowTopper.TopperType.ICE.ordinal()), 'S', "stickWood", 'F', Items.FEATHER}));
    	GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(BEItems.arrow, 4, ItemBEArrow.ArrowType.LIGHTNING.ordinal()), new Object [] {"T", "S", "F", 'T', new ItemStack(BEItems.arrow_topper, 1, ItemArrowTopper.TopperType.LIGHTNING.ordinal()), 'S', "stickWood", 'F', Items.FEATHER}));
    	
    	GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(BEItems.dye_arrow, 4, ItemDyeArrow.DyeType.RED.ordinal()), new Object [] {"D", "S", "F", 'D', "dyeRed", 'S', "stickWood", 'F', Items.FEATHER}));
    	GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(BEItems.dye_arrow, 4, ItemDyeArrow.DyeType.ORANGE.ordinal()), new Object [] {"D", "S", "F", 'D', "dyeOrange", 'S', "stickWood", 'F', Items.FEATHER}));
    	GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(BEItems.dye_arrow, 4, ItemDyeArrow.DyeType.YELLOW.ordinal()), new Object [] {"D", "S", "F", 'D', "dyeYellow", 'S', "stickWood", 'F', Items.FEATHER}));
    	GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(BEItems.dye_arrow, 4, ItemDyeArrow.DyeType.LIME.ordinal()), new Object [] {"D", "S", "F", 'D', "dyeLime", 'S', "stickWood", 'F', Items.FEATHER}));
    	GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(BEItems.dye_arrow, 4, ItemDyeArrow.DyeType.GREEN.ordinal()), new Object [] {"D", "S", "F", 'D', "dyeGreen", 'S', "stickWood", 'F', Items.FEATHER}));
    	GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(BEItems.dye_arrow, 4, ItemDyeArrow.DyeType.CYAN.ordinal()), new Object [] {"D", "S", "F", 'D', "dyeCyan", 'S', "stickWood", 'F', Items.FEATHER}));
    	GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(BEItems.dye_arrow, 4, ItemDyeArrow.DyeType.LIGHT_BLUE.ordinal()), new Object [] {"D", "S", "F", 'D', "dyeLightBlue", 'S', "stickWood", 'F', Items.FEATHER}));
    	GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(BEItems.dye_arrow, 4, ItemDyeArrow.DyeType.BLUE.ordinal()), new Object [] {"D", "S", "F", 'D', "dyeBlue", 'S', "stickWood", 'F', Items.FEATHER}));
    	GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(BEItems.dye_arrow, 4, ItemDyeArrow.DyeType.PURPLE.ordinal()), new Object [] {"D", "S", "F", 'D', "dyePurple", 'S', "stickWood", 'F', Items.FEATHER}));
    	GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(BEItems.dye_arrow, 4, ItemDyeArrow.DyeType.MAGENTA.ordinal()), new Object [] {"D", "S", "F", 'D', "dyeMagenta", 'S', "stickWood", 'F', Items.FEATHER}));
    	GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(BEItems.dye_arrow, 4, ItemDyeArrow.DyeType.PINK.ordinal()), new Object [] {"D", "S", "F", 'D', "dyePink", 'S', "stickWood", 'F', Items.FEATHER}));
    	GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(BEItems.dye_arrow, 4, ItemDyeArrow.DyeType.BROWN.ordinal()), new Object [] {"D", "S", "F", 'D', "dyeBrown", 'S', "stickWood", 'F', Items.FEATHER}));
    	GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(BEItems.dye_arrow, 4, ItemDyeArrow.DyeType.WHITE.ordinal()), new Object [] {"D", "S", "F", 'D', "dyeWhite", 'S', "stickWood", 'F', Items.FEATHER}));
    	GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(BEItems.dye_arrow, 4, ItemDyeArrow.DyeType.SILVER.ordinal()), new Object [] {"D", "S", "F", 'D', "dyeLightGray", 'S', "stickWood", 'F', Items.FEATHER}));
    	GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(BEItems.dye_arrow, 4, ItemDyeArrow.DyeType.GRAY.ordinal()), new Object [] {"D", "S", "F", 'D', "dyeGray", 'S', "stickWood", 'F', Items.FEATHER}));
    	GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(BEItems.dye_arrow, 4, ItemDyeArrow.DyeType.BLACK.ordinal()), new Object [] {"D", "S", "F", 'D', "dyeBlack", 'S', "stickWood", 'F', Items.FEATHER}));
    	
    	// Arrow Toppers
    	GameRegistry.addShapedRecipe(new ItemStack(BEItems.arrow_topper, 1, ItemArrowTopper.TopperType.BOMB.ordinal()), new Object [] {" C ", "CGC", " C ", 'C', Items.CLAY_BALL, 'G', Items.GUNPOWDER});
    	GameRegistry.addShapedRecipe(new ItemStack(BEItems.arrow_topper, 1, ItemArrowTopper.TopperType.FIRE.ordinal()), new Object [] {" B ", "BCB", " B ", 'B', Items.BLAZE_POWDER, 'C', Items.COAL});
    	GameRegistry.addShapedRecipe(new ItemStack(BEItems.arrow_topper, 1, ItemArrowTopper.TopperType.ICE.ordinal()), new Object [] {" S ", "SPS", " S ", 'S', Items.SNOWBALL, 'P', Blocks.ICE});
    	GameRegistry.addShapedRecipe(new ItemStack(BEItems.arrow_topper, 1, ItemArrowTopper.TopperType.LIGHTNING.ordinal()), new Object [] {" Q ", "QNQ", " Q ", 'Q', Items.QUARTZ, 'N', Items.NETHER_STAR});
    }
}
