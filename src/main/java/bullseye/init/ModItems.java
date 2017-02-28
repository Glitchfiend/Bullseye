package bullseye.init;

import static bullseye.api.BEItems.arrow;
import static bullseye.api.BEItems.be_icon;
import static bullseye.api.BEItems.dye_arrow;

import java.util.ArrayList;
import java.util.List;

import bullseye.core.Bullseye;
import bullseye.item.ItemBEArrow;
import bullseye.item.ItemDyeArrow;
import bullseye.util.inventory.CreativeTabBE;
import net.minecraft.client.renderer.block.model.ModelBakery;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.fml.relauncher.Side;

public class ModItems
{
    public static void init()
    {
    	registerItems();
        //setupModels();
    }
    
    public static void registerItems()
    {
    	//BE Creative Tab Icon
    	be_icon = registerItem(new Item(), "be_icon");
        be_icon.setCreativeTab(null);
        
        
        //Main Items
        arrow = registerItem(new ItemBEArrow(), "arrow");
        dye_arrow = registerItem(new ItemDyeArrow(), "dye_arrow");
    }
    
    public static Item registerItem(Item item, String name)
    {
        return registerItem(item, name, CreativeTabBE.instance);
    }
    
    public static Item registerItem(Item item, String name, CreativeTabs tab)
    {    
        item.setUnlocalizedName(name);
        if (tab != null)
        {
            item.setCreativeTab(CreativeTabBE.instance);
        }
        GameRegistry.register(item, new ResourceLocation(Bullseye.MOD_ID, name));
        //BECommand.itemCount++;
        
        // register sub types if there are any
        if (FMLCommonHandler.instance().getSide() == Side.CLIENT)
        {
            if (item.getHasSubtypes())
            {
                NonNullList<ItemStack> subItems = NonNullList.create();
                item.getSubItems(item, CreativeTabBE.instance, subItems);
                for (ItemStack subItem : subItems)
                {
                    String subItemName = item.getUnlocalizedName(subItem);
                    subItemName =  subItemName.substring(subItemName.indexOf(".") + 1); // remove 'item.' from the front

                    ModelBakery.registerItemVariants(item, new ResourceLocation(Bullseye.MOD_ID, subItemName));
                    ModelLoader.setCustomModelResourceLocation(item, subItem.getMetadata(), new ModelResourceLocation(Bullseye.MOD_ID + ":" + subItemName, "inventory"));
                }
            }
            else
            {
                ModelLoader.setCustomModelResourceLocation(item, 0, new ModelResourceLocation(Bullseye.MOD_ID + ":" + name, "inventory"));
            }
        }
        
        return item;   
    }
}
