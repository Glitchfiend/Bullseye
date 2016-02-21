package bullseye.entities.projectiles.dispenser;

import net.minecraft.block.BlockDispenser;
import net.minecraft.dispenser.BehaviorProjectileDispense;
import net.minecraft.dispenser.IBlockSource;
import net.minecraft.dispenser.IPosition;
import net.minecraft.entity.Entity;
import net.minecraft.entity.IProjectile;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.world.World;
import bullseye.entities.projectile.EntityDyeArrow;
import bullseye.item.ItemDyeArrow;

public class DispenserBehaviorDyeArrow extends BehaviorProjectileDispense
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
	
	protected IProjectile getProjectileEntity(World world, IPosition iPosition, ItemStack stack)
	{
		EntityDyeArrow arrow =  new EntityDyeArrow(world, iPosition.getX(), iPosition.getY(), iPosition.getZ());
		arrow.setDyeType(ItemDyeArrow.DyeType.fromMeta(stack.getMetadata()));
		return arrow;
	}

	@Override
	protected IProjectile getProjectileEntity(World worldIn, IPosition position) {
		// TODO Auto-generated method stub
		return null;
	}
}