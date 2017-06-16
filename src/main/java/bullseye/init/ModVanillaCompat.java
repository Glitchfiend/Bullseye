package bullseye.init;

import javax.annotation.Nullable;

import bullseye.api.BEItems;
import bullseye.entities.projectile.EntityBEArrow;
import bullseye.entities.projectile.EntityDyeArrow;
import bullseye.item.ItemBEArrow;
import bullseye.item.ItemDyeArrow;
import net.minecraft.block.BlockDispenser;
import net.minecraft.dispenser.BehaviorProjectileDispense;
import net.minecraft.dispenser.IPosition;
import net.minecraft.entity.Entity;
import net.minecraft.entity.IProjectile;
import net.minecraft.entity.projectile.EntityArrow;
import net.minecraft.item.ItemStack;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EntityDamageSourceIndirect;
import net.minecraft.util.datafix.DataFixer;
import net.minecraft.world.World;

public class ModVanillaCompat
{
    public static void init()
    {
    	registerDispenserBehaviors();

    	DataFixer datafixer = new DataFixer(1139);
        datafixer = new net.minecraftforge.common.util.CompoundDataFixer(datafixer);
    	EntityDyeArrow.registerFixesDyeArrow(datafixer);
    	EntityBEArrow.registerFixesBEArrow(datafixer);
    }
    
    private static void registerDispenserBehaviors()
    {
        BlockDispenser.DISPENSE_BEHAVIOR_REGISTRY.putObject(BEItems.arrow, new BehaviorProjectileDispense()
        {
            protected IProjectile getProjectileEntity(World worldIn, IPosition position, ItemStack stackIn)
            {
                EntityBEArrow entitybearrow = new EntityBEArrow(worldIn, position.getX(), position.getY(), position.getZ());
                entitybearrow.setArrowType(ItemBEArrow.ArrowType.fromMeta(stackIn.getMetadata()));
                entitybearrow.pickupStatus = EntityBEArrow.PickupStatus.ALLOWED;
                return entitybearrow;
            }
        });
        BlockDispenser.DISPENSE_BEHAVIOR_REGISTRY.putObject(BEItems.dye_arrow, new BehaviorProjectileDispense()
        {
            protected IProjectile getProjectileEntity(World worldIn, IPosition position, ItemStack stackIn)
            {
                EntityDyeArrow entitydyearrow = new EntityDyeArrow(worldIn, position.getX(), position.getY(), position.getZ());
                entitydyearrow.setDyeType(ItemDyeArrow.DyeType.fromMeta(stackIn.getMetadata()));
                entitydyearrow.pickupStatus = EntityDyeArrow.PickupStatus.ALLOWED;
                return entitydyearrow;
            }
        });
    }
}
