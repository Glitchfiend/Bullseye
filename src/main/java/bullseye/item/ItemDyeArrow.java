package bullseye.item;

import java.util.List;

import bullseye.entities.projectile.EntityDyeArrow;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.projectile.EntityArrow;
import net.minecraft.item.Item;
import net.minecraft.item.ItemArrow;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IStringSerializable;
import net.minecraft.util.NonNullList;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;


public class ItemDyeArrow extends ItemArrow
{
    public static enum DyeType implements IStringSerializable
    {
    	WHITE, ORANGE, MAGENTA, LIGHT_BLUE, YELLOW, LIME, PINK, GRAY, SILVER, CYAN, PURPLE, BLUE, BROWN, GREEN, RED, BLACK;
        @Override
        public String getName()
        {
            return "dye_arrow_" + this.name().toLowerCase();
        }
        @Override
        public String toString()
        {
            return this.getName();
        }
        
        public static DyeType fromMeta(int meta)
        {
            return DyeType.values()[meta % DyeType.values().length];
        }
    };
    
    public ItemDyeArrow()
    {
        this.setHasSubtypes(true);
        this.setMaxDamage(0);
    }
    
    @Override
    public EntityArrow createArrow(World worldIn, ItemStack stack, EntityLivingBase shooter)
    {
        EntityDyeArrow entityDyeArrow = new EntityDyeArrow(worldIn, shooter);
        entityDyeArrow.setDyeType(ItemDyeArrow.DyeType.fromMeta(stack.getMetadata()));
        return entityDyeArrow;
    }
   
    // add all the gem types as separate items in the creative tab
    @Override
    @SideOnly(Side.CLIENT)
    public void getSubItems(CreativeTabs tab, NonNullList<ItemStack> subItems)
    {
        if (this.isInCreativeTab(tab))
        {
            for (DyeType dyeType : DyeType.values())
            {
                subItems.add(new ItemStack(this, 1, dyeType.ordinal()));
            }
        }
    }
    
    // default behavior in Item is to return 0, but the meta value is important here because it determines which dart type to use
    @Override
    public int getMetadata(int metadata)
    {
        return metadata;
    }

    // get the correct name for this item by looking up the meta value in the DartType enum
    @Override
    public String getUnlocalizedName(ItemStack stack)
    {
        return "item." + DyeType.fromMeta(stack.getMetadata()).toString();
    }
    
    
}
