package bullseye.init;

import bullseye.api.BEItems;
import bullseye.core.Bullseye;
import bullseye.item.ItemBEArrow;
import bullseye.item.ItemDyeArrow;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.init.PotionTypes;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.PotionUtils;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.oredict.ShapedOreRecipe;

public class ModCrafting
{
    public static void init()
    {
        ResourceLocation dye_arrow_location = new ResourceLocation(Bullseye.MOD_ID, "dye_arrow");
        
        registerCraftingRecipe("training_arrow", new ShapedOreRecipe(new ResourceLocation(Bullseye.MOD_ID, "training_arrow"), new ItemStack(BEItems.arrow, 4, ItemBEArrow.ArrowType.TRAINING.ordinal()), new Object [] {"R", "S", "F", 'R', Items.RABBIT_HIDE, 'S', "stickWood", 'F', Items.FEATHER}));
        registerCraftingRecipe("egg_arrow", new ShapedOreRecipe(new ResourceLocation(Bullseye.MOD_ID, "egg_arrow"), new ItemStack(BEItems.arrow, 4, ItemBEArrow.ArrowType.EGG.ordinal()), new Object [] {"E", "S", "F", 'E', Items.EGG, 'S', "stickWood", 'F', Items.FEATHER}));
        registerCraftingRecipe("extinguishing_arrow", new ShapedOreRecipe(new ResourceLocation(Bullseye.MOD_ID, "extinguishing_arrow"), new ItemStack(BEItems.arrow, 4, ItemBEArrow.ArrowType.EXTINGUISHING.ordinal()), new Object [] {"T", "S", "F", 'T', PotionUtils.addPotionToItemStack(new ItemStack(Items.POTIONITEM), PotionTypes.WATER), 'S', "stickWood", 'F', Items.FEATHER}));
        registerCraftingRecipe("diamond_arrow", new ShapedOreRecipe(new ResourceLocation(Bullseye.MOD_ID, "diamond_arrow"), new ItemStack(BEItems.arrow, 4, ItemBEArrow.ArrowType.DIAMOND.ordinal()), new Object [] {"D", "S", "F", 'D', Items.DIAMOND, 'S', "stickWood", 'F', Items.FEATHER}));
        registerCraftingRecipe("bomb_arrow", new ShapedOreRecipe(new ResourceLocation(Bullseye.MOD_ID, "bomb_arrow"), new ItemStack(BEItems.arrow, 4, ItemBEArrow.ArrowType.BOMB.ordinal()), new Object [] {"T", "S", "F", 'T', Blocks.TNT, 'S', "stickWood", 'F', Items.FEATHER}));
        registerCraftingRecipe("fire_arrow", new ShapedOreRecipe(new ResourceLocation(Bullseye.MOD_ID, "fire_arrow"), new ItemStack(BEItems.arrow, 4, ItemBEArrow.ArrowType.FIRE.ordinal()), new Object [] {"T", "S", "F", 'T', Items.BLAZE_POWDER, 'S', "stickWood", 'F', Items.FEATHER}));
        registerCraftingRecipe("ice_arrow", new ShapedOreRecipe(new ResourceLocation(Bullseye.MOD_ID, "ice_arrow"), new ItemStack(BEItems.arrow, 4, ItemBEArrow.ArrowType.ICE.ordinal()), new Object [] {"T", "S", "F", 'T', Blocks.ICE, 'S', "stickWood", 'F', Items.FEATHER}));
        registerCraftingRecipe("lightning_arrow", new ShapedOreRecipe(new ResourceLocation(Bullseye.MOD_ID, "lightning_arrow"), new ItemStack(BEItems.arrow, 4, ItemBEArrow.ArrowType.LIGHTNING.ordinal()), new Object [] {"T", "S", "F", 'T', Items.NETHER_STAR, 'S', "stickWood", 'F', Items.FEATHER}));
        registerCraftingRecipe("prismarine_arrow", new ShapedOreRecipe(new ResourceLocation(Bullseye.MOD_ID, "prismarine_arrow"), new ItemStack(BEItems.arrow, 4, ItemBEArrow.ArrowType.PRISMARINE.ordinal()), new Object [] {"T", "S", "F", 'T', Items.PRISMARINE_SHARD, 'S', "stickWood", 'F', Items.FEATHER}));
        registerCraftingRecipe("ender_arrow", new ShapedOreRecipe(new ResourceLocation(Bullseye.MOD_ID, "ender_arrow"), new ItemStack(BEItems.arrow, 4, ItemBEArrow.ArrowType.ENDER.ordinal()), new Object [] {"T", "S", "F", 'T', Items.ENDER_PEARL, 'S', "stickWood", 'F', Items.FEATHER}));
        
        registerCraftingRecipe("red_dye_arrow", new ShapedOreRecipe(dye_arrow_location, new ItemStack(BEItems.dye_arrow, 4, ItemDyeArrow.DyeType.RED.ordinal()), new Object [] {"D", "S", "F", 'D', "dyeRed", 'S', "stickWood", 'F', Items.FEATHER}));
        registerCraftingRecipe("orange_dye_arrow", new ShapedOreRecipe(dye_arrow_location, new ItemStack(BEItems.dye_arrow, 4, ItemDyeArrow.DyeType.ORANGE.ordinal()), new Object [] {"D", "S", "F", 'D', "dyeOrange", 'S', "stickWood", 'F', Items.FEATHER}));
        registerCraftingRecipe("yellow_dye_arrow", new ShapedOreRecipe(dye_arrow_location, new ItemStack(BEItems.dye_arrow, 4, ItemDyeArrow.DyeType.YELLOW.ordinal()), new Object [] {"D", "S", "F", 'D', "dyeYellow", 'S', "stickWood", 'F', Items.FEATHER}));
        registerCraftingRecipe("lime_dye_arrow", new ShapedOreRecipe(dye_arrow_location, new ItemStack(BEItems.dye_arrow, 4, ItemDyeArrow.DyeType.LIME.ordinal()), new Object [] {"D", "S", "F", 'D', "dyeLime", 'S', "stickWood", 'F', Items.FEATHER}));
        registerCraftingRecipe("green_dye_arrow", new ShapedOreRecipe(dye_arrow_location, new ItemStack(BEItems.dye_arrow, 4, ItemDyeArrow.DyeType.GREEN.ordinal()), new Object [] {"D", "S", "F", 'D', "dyeGreen", 'S', "stickWood", 'F', Items.FEATHER}));
        registerCraftingRecipe("cyan_dye_arrow", new ShapedOreRecipe(dye_arrow_location, new ItemStack(BEItems.dye_arrow, 4, ItemDyeArrow.DyeType.CYAN.ordinal()), new Object [] {"D", "S", "F", 'D', "dyeCyan", 'S', "stickWood", 'F', Items.FEATHER}));
        registerCraftingRecipe("light_blue_dye_arrow", new ShapedOreRecipe(dye_arrow_location, new ItemStack(BEItems.dye_arrow, 4, ItemDyeArrow.DyeType.LIGHT_BLUE.ordinal()), new Object [] {"D", "S", "F", 'D', "dyeLightBlue", 'S', "stickWood", 'F', Items.FEATHER}));
        registerCraftingRecipe("blue_dye_arrow", new ShapedOreRecipe(dye_arrow_location, new ItemStack(BEItems.dye_arrow, 4, ItemDyeArrow.DyeType.BLUE.ordinal()), new Object [] {"D", "S", "F", 'D', "dyeBlue", 'S', "stickWood", 'F', Items.FEATHER}));
        registerCraftingRecipe("purple_dye_arrow", new ShapedOreRecipe(dye_arrow_location, new ItemStack(BEItems.dye_arrow, 4, ItemDyeArrow.DyeType.PURPLE.ordinal()), new Object [] {"D", "S", "F", 'D', "dyePurple", 'S', "stickWood", 'F', Items.FEATHER}));
        registerCraftingRecipe("magenta_dye_arrow", new ShapedOreRecipe(dye_arrow_location, new ItemStack(BEItems.dye_arrow, 4, ItemDyeArrow.DyeType.MAGENTA.ordinal()), new Object [] {"D", "S", "F", 'D', "dyeMagenta", 'S', "stickWood", 'F', Items.FEATHER}));
        registerCraftingRecipe("pink_dye_arrow", new ShapedOreRecipe(dye_arrow_location, new ItemStack(BEItems.dye_arrow, 4, ItemDyeArrow.DyeType.PINK.ordinal()), new Object [] {"D", "S", "F", 'D', "dyePink", 'S', "stickWood", 'F', Items.FEATHER}));
        registerCraftingRecipe("brown_dye_arrow", new ShapedOreRecipe(dye_arrow_location, new ItemStack(BEItems.dye_arrow, 4, ItemDyeArrow.DyeType.BROWN.ordinal()), new Object [] {"D", "S", "F", 'D', "dyeBrown", 'S', "stickWood", 'F', Items.FEATHER}));
        registerCraftingRecipe("white_dye_arrow", new ShapedOreRecipe(dye_arrow_location, new ItemStack(BEItems.dye_arrow, 4, ItemDyeArrow.DyeType.WHITE.ordinal()), new Object [] {"D", "S", "F", 'D', "dyeWhite", 'S', "stickWood", 'F', Items.FEATHER}));
        registerCraftingRecipe("silver_dye_arrow", new ShapedOreRecipe(dye_arrow_location, new ItemStack(BEItems.dye_arrow, 4, ItemDyeArrow.DyeType.SILVER.ordinal()), new Object [] {"D", "S", "F", 'D', "dyeLightGray", 'S', "stickWood", 'F', Items.FEATHER}));
        registerCraftingRecipe("gray_dye_arrow", new ShapedOreRecipe(dye_arrow_location, new ItemStack(BEItems.dye_arrow, 4, ItemDyeArrow.DyeType.GRAY.ordinal()), new Object [] {"D", "S", "F", 'D', "dyeGray", 'S', "stickWood", 'F', Items.FEATHER}));
        registerCraftingRecipe("black_dye_arrow", new ShapedOreRecipe(dye_arrow_location, new ItemStack(BEItems.dye_arrow, 4, ItemDyeArrow.DyeType.BLACK.ordinal()), new Object [] {"D", "S", "F", 'D', "dyeBlack", 'S', "stickWood", 'F', Items.FEATHER}));
    }

    public static void registerCraftingRecipe(String recipeName, ShapedOreRecipe recipe)
    {
        ResourceLocation location = new ResourceLocation(Bullseye.MOD_ID, recipeName);
        GameRegistry.register(recipe, location);
    }
}
