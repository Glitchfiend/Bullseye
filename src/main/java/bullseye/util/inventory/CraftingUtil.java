package bullseye.util.inventory;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

import bullseye.core.Bullseye;
import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.CraftingManager;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.item.crafting.ShapedRecipes;
import net.minecraft.item.crafting.ShapelessRecipes;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.oredict.OreIngredient;

public class CraftingUtil
{
    public static void addShapelessRecipe(ItemStack output, Object... inputs)
    {
        NonNullList<Ingredient> ingredients = NonNullList.create();

        for (Object input : inputs)
        {
            ingredients.add(asIngredient(input));
        }

        if (ingredients.isEmpty())
        {
            throw new IllegalArgumentException("No ingredients for shapeless recipe");
        }
        else if (ingredients.size() > 9)
        {
            throw new IllegalArgumentException("Too many ingredients for shapeless recipe");
        }

        ShapelessRecipes recipe = new ShapelessRecipes("Bullseye", output, ingredients);
        CraftingManager.func_193372_a(unusedLocForOutput(output), recipe);
    }

    public static void addShapedRecipe(ItemStack output, Object... inputs)
    {
        ArrayList<String> pattern = Lists.newArrayList();
        Map<String, Ingredient> key = Maps.newHashMap();

        parseRecipe(pattern, key, inputs);
        int width = pattern.get(0).length();
        int height = pattern.size();

        NonNullList<Ingredient> ingredients = ShapedRecipes.func_192402_a(pattern.toArray(new String[pattern.size()]), key, width, height);
        ShapedRecipes recipe = new ShapedRecipes("Bullseye", width, height, ingredients, output);
        CraftingManager.func_193372_a(unusedLocForOutput(output), recipe);
    }

    public static void addRecipe(String name, IRecipe recipe)
    {
        CraftingManager.func_193372_a(new ResourceLocation(Bullseye.MOD_ID, name), recipe);
    }

    public static Ingredient asIngredient(Object object)
    {
        if (object instanceof Item)
        {
            return Ingredient.func_193367_a((Item)object);
        }
        else if (object instanceof Block)
        {
            return Ingredient.func_193369_a(new ItemStack((Block)object));
        }
        else if (object instanceof ItemStack)
        {
            return Ingredient.func_193369_a((ItemStack)object);
        }
        else if (object instanceof String)
        {
            return new OreIngredient((String)object);
        }

        throw new IllegalArgumentException("Cannot convert object of type " + object.getClass().toString() + " to an Ingredient!");
    }

    private static void parseRecipe(List<String> pattern, Map<String, Ingredient> key, Object... inputs)
    {
        Iterator itr = Arrays.asList(inputs).iterator();

        while (itr.hasNext())
        {
            Object obj = itr.next();

            if (obj instanceof String)
            {
                String str = (String) obj;

                if (str.length() > 3)
                {
                    throw new IllegalArgumentException("Invalid string length for recipe " + str.length());
                }

                if (pattern.size() <= 2)
                {
                    pattern.add(str);
                }
                else
                {
                    throw new IllegalArgumentException("Recipe has too many crafting rows!");
                }
            }
            else if (obj instanceof Character)
            {
                key.put(((Character)obj).toString(), asIngredient(itr.next()));
            }
            else
            {
                throw new IllegalArgumentException("Unexpected argument of type " + obj.getClass().toString());
            }
        }

        key.put(" ", Ingredient.field_193370_a);
    }

    private static ResourceLocation unusedLocForOutput(ItemStack output)
    {
        ResourceLocation baseLoc = new ResourceLocation(Bullseye.MOD_ID, output.getItem().getRegistryName().getResourcePath());
        ResourceLocation recipeLoc = baseLoc;
        int index = 0;

        // find unused recipe name
        while (CraftingManager.field_193380_a.containsKey(recipeLoc))
        {
            index++;
            recipeLoc = new ResourceLocation(Bullseye.MOD_ID, baseLoc.getResourcePath() + "_" + index);
        }

        return recipeLoc;
    }
}