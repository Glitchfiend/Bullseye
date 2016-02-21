package bullseye.item;

import java.util.List;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IStringSerializable;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;


public class ItemBEArrow extends Item
{
    public static enum ArrowType implements IStringSerializable
    {
        EGG, DIAMOND, BOMB, FIRE, ICE, LIGHTNING;
        @Override
        public String getName()
        {
            return this.name().toLowerCase() + "_arrow";
        }
        @Override
        public String toString()
        {
            return this.getName();
        }
        
        public double getDamageInflicted()
        {
            switch(this)
            {
	            case EGG:
	                return 0.25D;
	            case DIAMOND:
                    return 4.0D;
                case FIRE:
                    return 0.5D;
                case ICE:
                    return 1.0D;
                case LIGHTNING:
                    return 0.5D;
                case BOMB:
                    return 0.5D;
                default:
                    return 2.0D;
            }
        }
        public static ArrowType fromMeta(int meta)
        {
            return ArrowType.values()[meta % ArrowType.values().length];
        }
    };
    
    public ItemBEArrow()
    {
        this.setHasSubtypes(true);
        this.setMaxDamage(0);
    }
   
    // add all the gem types as separate items in the creative tab
    @Override
    @SideOnly(Side.CLIENT)
    public void getSubItems(Item itemIn, CreativeTabs tab, List subItems)
    {
        for (ArrowType arrowType : ArrowType.values())
        {
            subItems.add(new ItemStack(itemIn, 1, arrowType.ordinal()));
        }
    }
    
    @Override
    public boolean hasEffect(ItemStack stack)
    {
        ArrowType arrowtype = ItemBEArrow.ArrowType.fromMeta(stack.getMetadata());
        switch (arrowtype)
        {
            case FIRE: case ICE: case LIGHTNING:
                return true;
            default:
                return super.hasEffect(stack); 
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
        return "item." + ArrowType.fromMeta(stack.getMetadata()).toString();
    }
    
    
}
