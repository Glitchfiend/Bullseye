package bullseye.entities.projectiles.dispenser;

import bullseye.entities.projectile.EntityDyeArrow;
import bullseye.item.ItemDyeArrow;
import net.minecraft.dispenser.BehaviorProjectileDispense;
import net.minecraft.dispenser.IPosition;
import net.minecraft.entity.IProjectile;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

public class DispenserBehaviorDyeArrow extends BehaviorProjectileDispense
{
	@Override
    protected IProjectile getProjectileEntity(World world, IPosition position, ItemStack stack)
	{
		EntityDyeArrow arrow = new EntityDyeArrow(world, position.getX(), position.getY(), position.getZ());
		arrow.setDyeType(ItemDyeArrow.DyeType.fromMeta(stack.getMetadata()));
		return arrow;
	}
}