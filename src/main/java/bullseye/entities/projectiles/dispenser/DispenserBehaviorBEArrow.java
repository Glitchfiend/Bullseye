package bullseye.entities.projectiles.dispenser;

import bullseye.entities.projectile.EntityBEArrow;
import bullseye.item.ItemBEArrow;
import net.minecraft.block.BlockDispenser;
import net.minecraft.dispenser.BehaviorProjectileDispense;
import net.minecraft.dispenser.IBlockSource;
import net.minecraft.dispenser.IPosition;
import net.minecraft.entity.Entity;
import net.minecraft.entity.IProjectile;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.world.World;

public class DispenserBehaviorBEArrow extends BehaviorProjectileDispense
{
	@Override
    protected IProjectile getProjectileEntity(World world, IPosition position, ItemStack stack)
	{
		EntityBEArrow arrow = new EntityBEArrow(world, position.getX(), position.getY(), position.getZ());
		arrow.setArrowType(ItemBEArrow.ArrowType.fromMeta(stack.getMetadata()));
		return arrow;
	}
}