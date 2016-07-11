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
    public ItemStack dispenseStack(IBlockSource source, ItemStack stack)
    {
        World world = source.getWorld();
        IPosition iposition = BlockDispenser.getDispensePosition(source);
        EnumFacing enumfacing = BlockDispenser.getFacing(source.getBlockMetadata());
        IProjectile iprojectile = this.getProjectileEntity(world, iposition, stack);
        iprojectile.setThrowableHeading((double)enumfacing.getFrontOffsetX(), (double)((float)enumfacing.getFrontOffsetY() + 0.1F), (double)enumfacing.getFrontOffsetZ(), this.func_82500_b(), this.func_82498_a());
        world.spawnEntityInWorld((Entity)iprojectile);
        stack.splitStack(1);
        return stack;
    }
	
	@Override
	protected IProjectile getProjectileEntity(World world, IPosition iPosition, ItemStack stack)
	{
		EntityBEArrow arrow =  new EntityBEArrow(world, iPosition.getX(), iPosition.getY(), iPosition.getZ());
		arrow.setArrowType(ItemBEArrow.ArrowType.fromMeta(stack.getMetadata()));
		return arrow;
	}
}