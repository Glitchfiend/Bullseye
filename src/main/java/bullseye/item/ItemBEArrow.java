package bullseye.item;

import java.util.List;

import bullseye.entities.projectile.EntityBEArrow;
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


public class ItemBEArrow extends ItemArrow
{
    public static enum ArrowType implements IStringSerializable
    {
        TRAINING, EGG, EXTINGUISHING, DIAMOND, BOMB, FIRE, ICE, LIGHTNING, PRISMARINE;
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
	            case TRAINING:
	                return 0.0D;
	            case EGG:
	                return 0.25D;
	            case EXTINGUISHING:
	                return 1.0D;
	            case DIAMOND:
                    return 4.0D;
                case FIRE:
                    return 1.5D;
                case ICE:
                    return 1.0D;
                case LIGHTNING:
                    return 0.5D;
                case BOMB:
                    return 1.25D;
                case PRISMARINE:
                    return 1.75D;
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
    
    @Override
    public EntityArrow createArrow(World worldIn, ItemStack stack, EntityLivingBase shooter)
    {
        EntityBEArrow entityBEArrow = new EntityBEArrow(worldIn, shooter);
        entityBEArrow.setArrowType(ItemBEArrow.ArrowType.fromMeta(stack.getMetadata()));
        return entityBEArrow;
    }
   
    // add all the gem types as separate items in the creative tab
    @Override
    @SideOnly(Side.CLIENT)
    public void getSubItems(CreativeTabs tab, NonNullList<ItemStack> subItems)
    {
        if (this.func_194125_a(tab))
        {
            for (ArrowType arrowType : ArrowType.values())
            {
                subItems.add(new ItemStack(this, 1, arrowType.ordinal()));
            }
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
