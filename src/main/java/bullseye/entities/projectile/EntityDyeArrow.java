package bullseye.entities.projectile;

import java.util.List;

import javax.annotation.Nullable;

import com.google.common.base.Predicate;
import com.google.common.base.Predicates;

import bullseye.api.BEItems;
import bullseye.item.ItemDyeArrow;
import net.minecraft.block.Block;
import net.minecraft.block.BlockBed;
import net.minecraft.block.BlockColored;
import net.minecraft.block.BlockConcretePowder;
import net.minecraft.block.BlockGlazedTerracotta;
import net.minecraft.block.BlockHorizontal;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.IProjectile;
import net.minecraft.entity.monster.EntityEnderman;
import net.minecraft.entity.passive.EntitySheep;
import net.minecraft.entity.passive.EntityWolf;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.entity.projectile.EntityArrow;
import net.minecraft.init.Blocks;
import net.minecraft.init.Enchantments;
import net.minecraft.init.Items;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.EnumDyeColor;
import net.minecraft.item.Item;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.network.play.server.SPacketChangeGameState;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityBed;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EntitySelectors;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.datafix.DataFixer;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class EntityDyeArrow extends EntityArrow implements IProjectile
{    
	private static final Predicate<Entity> ARROW_TARGETS = Predicates.and(new Predicate[] {EntitySelectors.NOT_SPECTATING, EntitySelectors.IS_ALIVE, new Predicate<Entity>()
	{
	    public boolean apply(@Nullable Entity p_apply_1_)
	    {
	        return p_apply_1_.canBeCollidedWith();
	    }
	}
	                                                                                       });
	private static final DataParameter<Byte> CRITICAL = EntityDataManager.<Byte>createKey(EntityDyeArrow.class, DataSerializers.BYTE);
	private static final DataParameter<Byte> DYE_TYPE = EntityDataManager.<Byte>createKey(EntityDyeArrow.class, DataSerializers.BYTE);
    private int xTile;
    private int yTile;
    private int zTile;
    private Block inTile;
    private int inData;
    protected boolean inGround;
    protected int timeInGround;
    public EntityDyeArrow.PickupStatus pickupStatus;
    public int arrowShake;
    public Entity shootingEntity;
    private int ticksInGround;
    private int ticksInAir;
    private double damage;
    private int knockbackStrength;

    public EntityDyeArrow(World worldIn)
    {
        super(worldIn);
        this.xTile = -1;
        this.yTile = -1;
        this.zTile = -1;
        this.pickupStatus = EntityDyeArrow.PickupStatus.DISALLOWED;
        this.damage = 2.0D;
        this.setSize(0.5F, 0.5F);
    }

    public EntityDyeArrow(World worldIn, double x, double y, double z)
    {
        this(worldIn);
        this.setPosition(x, y, z);
    }

    public EntityDyeArrow(World worldIn, EntityLivingBase shooter)
    {
        this(worldIn, shooter.posX, shooter.posY + (double)shooter.getEyeHeight() - 0.10000000149011612D, shooter.posZ);
        this.shootingEntity = shooter;

        if (shooter instanceof EntityPlayer)
        {
            this.pickupStatus = EntityDyeArrow.PickupStatus.ALLOWED;
        }
    }
    
    @SideOnly(Side.CLIENT)
    @Override
    public boolean isInRangeToRenderDist(double distance)
    {
        double d0 = this.getEntityBoundingBox().getAverageEdgeLength() * 10.0D;

        if (Double.isNaN(d0))
        {
            d0 = 1.0D;
        }

        d0 = d0 * 64.0D * getRenderDistanceWeight();
        return distance < d0 * d0;
    }
 
    @Override
    protected void entityInit()
    {
        this.dataManager.register(CRITICAL, Byte.valueOf((byte)0));
        this.dataManager.register(DYE_TYPE, Byte.valueOf((byte)0));
    }
    
    public void setDyeType(ItemDyeArrow.DyeType dyeType)
    {
    	dataManager.set(DYE_TYPE, (byte)dyeType.ordinal());
    }
    
    public ItemDyeArrow.DyeType getDyeType()
    {
    	return ItemDyeArrow.DyeType.values()[dataManager.get(DYE_TYPE)];
    }
    
    @Override
    public void setAim(Entity shooter, float pitch, float yaw, float p_184547_4_, float velocity, float inaccuracy)
    {
        float f = -MathHelper.sin(yaw * 0.017453292F) * MathHelper.cos(pitch * 0.017453292F);
        float f1 = -MathHelper.sin(pitch * 0.017453292F);
        float f2 = MathHelper.cos(yaw * 0.017453292F) * MathHelper.cos(pitch * 0.017453292F);
        this.setThrowableHeading((double)f, (double)f1, (double)f2, velocity, inaccuracy);
        this.motionX += shooter.motionX;
        this.motionZ += shooter.motionZ;

        if (!shooter.onGround)
        {
            this.motionY += shooter.motionY;
        }
    }
    
    @Override
    public void setThrowableHeading(double x, double y, double z, float velocity, float inaccuracy)
    {
        float f = MathHelper.sqrt(x * x + y * y + z * z);
        x = x / (double)f;
        y = y / (double)f;
        z = z / (double)f;
        x = x + this.rand.nextGaussian() * 0.007499999832361937D * (double)inaccuracy;
        y = y + this.rand.nextGaussian() * 0.007499999832361937D * (double)inaccuracy;
        z = z + this.rand.nextGaussian() * 0.007499999832361937D * (double)inaccuracy;
        x = x * (double)velocity;
        y = y * (double)velocity;
        z = z * (double)velocity;
        this.motionX = x;
        this.motionY = y;
        this.motionZ = z;
        float f1 = MathHelper.sqrt(x * x + z * z);
        this.rotationYaw = (float)(MathHelper.atan2(x, z) * (180D / Math.PI));
        this.rotationPitch = (float)(MathHelper.atan2(y, (double)f1) * (180D / Math.PI));
        this.prevRotationYaw = this.rotationYaw;
        this.prevRotationPitch = this.rotationPitch;
        this.ticksInGround = 0;
    }

    @SideOnly(Side.CLIENT)
    @Override
    public void setPositionAndRotationDirect(double x, double y, double z, float yaw, float pitch, int posRotationIncrements, boolean teleport)
    {
        this.setPosition(x, y, z);
        this.setRotation(yaw, pitch);
    }

    @SideOnly(Side.CLIENT)
    @Override
    public void setVelocity(double x, double y, double z)
    {
        this.motionX = x;
        this.motionY = y;
        this.motionZ = z;

        if (this.prevRotationPitch == 0.0F && this.prevRotationYaw == 0.0F)
        {
            float f = MathHelper.sqrt(x * x + z * z);
            this.rotationPitch = (float)(MathHelper.atan2(y, (double)f) * (180D / Math.PI));
            this.rotationYaw = (float)(MathHelper.atan2(x, z) * (180D / Math.PI));
            this.prevRotationPitch = this.rotationPitch;
            this.prevRotationYaw = this.rotationYaw;
            this.setLocationAndAngles(this.posX, this.posY, this.posZ, this.rotationYaw, this.rotationPitch);
            this.ticksInGround = 0;
        }
    }
    
    @Override
    public void onUpdate()
    {
        if (!this.world.isRemote)
        {
            this.setFlag(6, this.isGlowing());
        }

        this.onEntityUpdate();
        
        if (this.prevRotationPitch == 0.0F && this.prevRotationYaw == 0.0F)
        {
            float f = MathHelper.sqrt(this.motionX * this.motionX + this.motionZ * this.motionZ);
            this.rotationYaw = (float)(MathHelper.atan2(this.motionX, this.motionZ) * (180D / Math.PI));
            this.rotationPitch = (float)(MathHelper.atan2(this.motionY, (double)f) * (180D / Math.PI));
            this.prevRotationYaw = this.rotationYaw;
            this.prevRotationPitch = this.rotationPitch;
        }

        BlockPos blockpos = new BlockPos(this.xTile, this.yTile, this.zTile);
        IBlockState iblockstate = this.world.getBlockState(blockpos);
        Block block = iblockstate.getBlock();

        if (iblockstate.getMaterial() != Material.AIR)
        {
        	AxisAlignedBB axisalignedbb = iblockstate.getCollisionBoundingBox(this.world, blockpos);

            if (axisalignedbb != Block.NULL_AABB && axisalignedbb.offset(blockpos).contains(new Vec3d(this.posX, this.posY, this.posZ)))
            {
                this.inGround = true;
            }
        }

        if (this.arrowShake > 0)
        {
            --this.arrowShake;
        }

        if (this.inGround)
        {
            int j = block.getMetaFromState(iblockstate);

            if ((block != this.inTile || j != this.inData) && !this.world.collidesWithAnyBlock(this.getEntityBoundingBox().grow(0.05D)))
            {
                this.inGround = false;
                this.motionX *= (double)(this.rand.nextFloat() * 0.2F);
                this.motionY *= (double)(this.rand.nextFloat() * 0.2F);
                this.motionZ *= (double)(this.rand.nextFloat() * 0.2F);
                this.ticksInGround = 0;
                this.ticksInAir = 0;
            }
            else
            {
            	this.arrowLand(blockpos);
            }
            
            ++this.timeInGround;
        }
        else
        {
            ItemDyeArrow.DyeType dyeType = this.getDyeType();
            
            this.timeInGround = 0;
            ++this.ticksInAir;
            this.damage = dyeType.getDamageInflicted();
            Vec3d vec3d1 = new Vec3d(this.posX, this.posY, this.posZ);
            Vec3d vec3d = new Vec3d(this.posX + this.motionX, this.posY + this.motionY, this.posZ + this.motionZ);
            RayTraceResult raytraceresult = this.world.rayTraceBlocks(vec3d1, vec3d, false, true, false);
            vec3d1 = new Vec3d(this.posX, this.posY, this.posZ);
            vec3d = new Vec3d(this.posX + this.motionX, this.posY + this.motionY, this.posZ + this.motionZ);

            if (raytraceresult != null)
            {
                vec3d = new Vec3d(raytraceresult.hitVec.x, raytraceresult.hitVec.y, raytraceresult.hitVec.z);
            }
            
            Entity entity = this.findEntityOnPath(vec3d1, vec3d);

            if (entity != null)
            {
                raytraceresult = new RayTraceResult(entity);
            }

            if (raytraceresult != null && raytraceresult.entityHit instanceof EntityPlayer)
            {
                EntityPlayer entityplayer = (EntityPlayer)raytraceresult.entityHit;

                if (this.shootingEntity instanceof EntityPlayer && !((EntityPlayer)this.shootingEntity).canAttackPlayer(entityplayer))
                {
                    raytraceresult = null;
                }
            }

            if (raytraceresult != null)
            {
                this.onHit(raytraceresult);
            }
            
            if (this.getIsCritical())
            {
                for (int k = 0; k < 4; ++k)
                {
                    this.world.spawnParticle(EnumParticleTypes.CRIT, this.posX + this.motionX * (double)k / 4.0D, this.posY + this.motionY * (double)k / 4.0D, this.posZ + this.motionZ * (double)k / 4.0D, -this.motionX, -this.motionY + 0.2D, -this.motionZ, new int[0]);
                }
            }

            this.posX += this.motionX;
            this.posY += this.motionY;
            this.posZ += this.motionZ;
            float f4 = MathHelper.sqrt(this.motionX * this.motionX + this.motionZ * this.motionZ);
            this.rotationYaw = (float)(MathHelper.atan2(this.motionX, this.motionZ) * (180D / Math.PI));

            for (this.rotationPitch = (float)(MathHelper.atan2(this.motionY, (double)f4) * (180D / Math.PI)); this.rotationPitch - this.prevRotationPitch < -180.0F; this.prevRotationPitch -= 360.0F)
            {
                ;
            }

            while (this.rotationPitch - this.prevRotationPitch >= 180.0F)
            {
                this.prevRotationPitch += 360.0F;
            }

            while (this.rotationYaw - this.prevRotationYaw < -180.0F)
            {
                this.prevRotationYaw -= 360.0F;
            }

            while (this.rotationYaw - this.prevRotationYaw >= 180.0F)
            {
                this.prevRotationYaw += 360.0F;
            }

            this.rotationPitch = this.prevRotationPitch + (this.rotationPitch - this.prevRotationPitch) * 0.2F;
            this.rotationYaw = this.prevRotationYaw + (this.rotationYaw - this.prevRotationYaw) * 0.2F;
            float f1 = 0.99F;
            float f2 = 0.05F;

            if (this.isInWater())
            {
                for (int i = 0; i < 4; ++i)
                {
                    float f3 = 0.25F;
                    this.world.spawnParticle(EnumParticleTypes.WATER_BUBBLE, this.posX - this.motionX * 0.25D, this.posY - this.motionY * 0.25D, this.posZ - this.motionZ * 0.25D, this.motionX, this.motionY, this.motionZ);
                }

                f1 = 0.6F;
            }

            if (this.isWet())
            {
                this.extinguish();
            }

            this.motionX *= (double)f1;
            this.motionY *= (double)f1;
            this.motionZ *= (double)f1;
            
            if (!this.hasNoGravity())
            {
                this.motionY -= 0.05000000074505806D;
            }
            
            this.setPosition(this.posX, this.posY, this.posZ);
            this.doBlockCollisions();
        }
    }

    @Override
    protected void onHit(RayTraceResult raytraceResultIn)
    {
        Entity entity = raytraceResultIn.entityHit;

        if (entity != null)
        {
            float f = MathHelper.sqrt(this.motionX * this.motionX + this.motionY * this.motionY + this.motionZ * this.motionZ);
            int i = MathHelper.ceil((double)f * this.damage);

            if (this.getIsCritical())
            {
                i += this.rand.nextInt(i / 2 + 2);
            }

            DamageSource damagesource;

            if (this.shootingEntity == null)
            {
                damagesource = DamageSource.causeArrowDamage(this, this);
            }
            else
            {
                damagesource = DamageSource.causeArrowDamage(this, this.shootingEntity);
            }

            if (this.isBurning() && !(entity instanceof EntityEnderman))
            {
                entity.setFire(5);
            }

            if (entity.attackEntityFrom(damagesource, (float)i))
            {
                
                if (entity instanceof EntityLivingBase)
                {
                    EntityLivingBase entitylivingbase = (EntityLivingBase)entity;

                    if (this.knockbackStrength > 0)
                    {
                        float f1 = MathHelper.sqrt(this.motionX * this.motionX + this.motionZ * this.motionZ);

                        if (f1 > 0.0F)
                        {
                            entitylivingbase.addVelocity(this.motionX * (double)this.knockbackStrength * 0.6000000238418579D / (double)f1, 0.1D, this.motionZ * (double)this.knockbackStrength * 0.6000000238418579D / (double)f1);
                        }
                    }

                    if (this.shootingEntity instanceof EntityLivingBase)
                    {
                        EnchantmentHelper.applyThornEnchantments(entitylivingbase, this.shootingEntity);
                        EnchantmentHelper.applyArthropodEnchantments((EntityLivingBase)this.shootingEntity, entitylivingbase);
                    }

                    this.arrowHit(entitylivingbase);

                    if (this.shootingEntity != null && entitylivingbase != this.shootingEntity && entitylivingbase instanceof EntityPlayer && this.shootingEntity instanceof EntityPlayerMP)
                    {
                        ((EntityPlayerMP)this.shootingEntity).connection.sendPacket(new SPacketChangeGameState(6, 0.0F));
                    }
                }

                this.playSound(SoundEvents.ENTITY_ARROW_HIT, 1.0F, 1.2F / (this.rand.nextFloat() * 0.2F + 0.9F));

                if (!(entity instanceof EntityEnderman))
                {
                    this.setDead();
                }
            }
            else
            {
                this.motionX *= -0.10000000149011612D;
                this.motionY *= -0.10000000149011612D;
                this.motionZ *= -0.10000000149011612D;
                this.rotationYaw += 180.0F;
                this.prevRotationYaw += 180.0F;
                this.ticksInAir = 0;

                if (!this.world.isRemote && this.motionX * this.motionX + this.motionY * this.motionY + this.motionZ * this.motionZ < 0.0010000000474974513D)
                {
                    if (this.pickupStatus == EntityDyeArrow.PickupStatus.ALLOWED)
                    {
                        this.entityDropItem(this.getArrowStack(), 0.1F);
                    }

                    this.setDead();
                }
            }
        }
        else
        {
            BlockPos blockpos = raytraceResultIn.getBlockPos();
            this.xTile = blockpos.getX();
            this.yTile = blockpos.getY();
            this.zTile = blockpos.getZ();
            IBlockState iblockstate = this.world.getBlockState(blockpos);
            this.inTile = iblockstate.getBlock();
            this.inData = this.inTile.getMetaFromState(iblockstate);
            this.motionX = (double)((float)(raytraceResultIn.hitVec.x - this.posX));
            this.motionY = (double)((float)(raytraceResultIn.hitVec.y - this.posY));
            this.motionZ = (double)((float)(raytraceResultIn.hitVec.z - this.posZ));
            float f2 = MathHelper.sqrt(this.motionX * this.motionX + this.motionY * this.motionY + this.motionZ * this.motionZ);
            this.posX -= this.motionX / (double)f2 * 0.05000000074505806D;
            this.posY -= this.motionY / (double)f2 * 0.05000000074505806D;
            this.posZ -= this.motionZ / (double)f2 * 0.05000000074505806D;
            this.playSound(SoundEvents.ENTITY_ARROW_HIT, 1.0F, 1.2F / (this.rand.nextFloat() * 0.2F + 0.9F));
            this.inGround = true;
            this.arrowShake = 7;
            this.setIsCritical(false);

            if (iblockstate.getMaterial() != Material.AIR)
            {
                this.inTile.onEntityCollidedWithBlock(this.world, blockpos, iblockstate, this);
            }
        }
    }
    
    protected void arrowLand(BlockPos blockpos)
    {
        IBlockState iblockstate = this.world.getBlockState(blockpos);
        Block block = iblockstate.getBlock();
        
        ItemDyeArrow.DyeType dyeType = this.getDyeType();
        
        if (!this.world.isRemote)
        {
            if (block == Blocks.WOOL)
            {
                world.setBlockState(blockpos, iblockstate.withProperty(BlockColored.COLOR, EnumDyeColor.byMetadata(dyeType.ordinal())));
            }
            if (block == Blocks.CARPET)
            {
                world.setBlockState(blockpos, iblockstate.withProperty(BlockColored.COLOR, EnumDyeColor.byMetadata(dyeType.ordinal())));
            }
            if (block == Blocks.STAINED_HARDENED_CLAY)
            {
                world.setBlockState(blockpos, iblockstate.withProperty(BlockColored.COLOR, EnumDyeColor.byMetadata(dyeType.ordinal())));
            }
            if (block == Blocks.STAINED_GLASS)
            {
                world.setBlockState(blockpos, iblockstate.withProperty(BlockColored.COLOR, EnumDyeColor.byMetadata(dyeType.ordinal())));
            }
            if (block == Blocks.CONCRETE)
            {
                world.setBlockState(blockpos, iblockstate.withProperty(BlockColored.COLOR, EnumDyeColor.byMetadata(dyeType.ordinal())));
            }
            if (block == Blocks.CONCRETE_POWDER)
            {
                world.setBlockState(blockpos, iblockstate.withProperty(BlockConcretePowder.COLOR, EnumDyeColor.byMetadata(dyeType.ordinal())));
            }
            if (block == Blocks.STAINED_GLASS_PANE)
            {
                world.setBlockState(blockpos, iblockstate.withProperty(BlockColored.COLOR, EnumDyeColor.byMetadata(dyeType.ordinal())));
            }
            if (block == Blocks.GLASS)
            {
                world.setBlockState(blockpos, Blocks.STAINED_GLASS.getDefaultState().withProperty(BlockColored.COLOR, EnumDyeColor.byMetadata(dyeType.ordinal())));
            }
            if (block == Blocks.HARDENED_CLAY)
            {
                world.setBlockState(blockpos, Blocks.STAINED_HARDENED_CLAY.getDefaultState().withProperty(BlockColored.COLOR, EnumDyeColor.byMetadata(dyeType.ordinal())));
            }
            if (block == Blocks.GLASS_PANE)
            {
                world.setBlockState(blockpos, Blocks.STAINED_GLASS_PANE.getDefaultState().withProperty(BlockColored.COLOR, EnumDyeColor.byMetadata(dyeType.ordinal())));
            }
            /*
            if (block == Blocks.BED)
            {
                EnumFacing enumfacing = (EnumFacing)iblockstate.getValue(BlockBed.FACING);
                
                if (iblockstate.getValue(BlockBed.PART) == BlockBed.EnumPartType.FOOT)
                {
                    BlockPos blockpos2 = blockpos.offset(enumfacing);
                    
                    TileEntity bed1 = world.getTileEntity(blockpos);
                    TileEntity bed2 = world.getTileEntity(blockpos2);
                    
                    if (bed1 != null && bed1 instanceof TileEntityBed)
                    {
                        ((TileEntityBed)bed1).setColor(EnumDyeColor.byMetadata(dyeType.ordinal()));
                    }
                    if (bed2 != null && bed2 instanceof TileEntityBed)
                    {
                        ((TileEntityBed)bed2).setColor(EnumDyeColor.byMetadata(dyeType.ordinal()));
                    }
                }
                
                if (iblockstate.getValue(BlockBed.PART) == BlockBed.EnumPartType.HEAD)
                {
                    BlockPos blockpos2 = blockpos.offset(enumfacing.getOpposite());
                    
                    TileEntity bed1 = world.getTileEntity(blockpos);
                    TileEntity bed2 = world.getTileEntity(blockpos2);
                    
                    if (bed1 != null && bed1 instanceof TileEntityBed)
                    {
                        ((TileEntityBed)bed1).setColor(EnumDyeColor.byMetadata(dyeType.ordinal()));
                    }
                    if (bed2 != null && bed2 instanceof TileEntityBed)
                    {
                        ((TileEntityBed)bed2).setColor(EnumDyeColor.byMetadata(dyeType.ordinal()));
                    }
                }
            }
            */
            if (block instanceof BlockGlazedTerracotta)
            {   
                EnumFacing facing = iblockstate.getValue(BlockGlazedTerracotta.FACING);
                
                if (dyeType == ItemDyeArrow.DyeType.BLACK) { world.setBlockState(blockpos, Blocks.BLACK_GLAZED_TERRACOTTA.getDefaultState().withProperty(BlockGlazedTerracotta.FACING, facing)); }
                if (dyeType == ItemDyeArrow.DyeType.BLUE) { world.setBlockState(blockpos, Blocks.BLUE_GLAZED_TERRACOTTA.getDefaultState().withProperty(BlockGlazedTerracotta.FACING, facing)); }
                if (dyeType == ItemDyeArrow.DyeType.BROWN) { world.setBlockState(blockpos, Blocks.BROWN_GLAZED_TERRACOTTA.getDefaultState().withProperty(BlockGlazedTerracotta.FACING, facing)); }
                if (dyeType == ItemDyeArrow.DyeType.CYAN) { world.setBlockState(blockpos, Blocks.CYAN_GLAZED_TERRACOTTA.getDefaultState().withProperty(BlockGlazedTerracotta.FACING, facing)); }
                if (dyeType == ItemDyeArrow.DyeType.GRAY) { world.setBlockState(blockpos, Blocks.GRAY_GLAZED_TERRACOTTA.getDefaultState().withProperty(BlockGlazedTerracotta.FACING, facing)); }
                if (dyeType == ItemDyeArrow.DyeType.GREEN) { world.setBlockState(blockpos, Blocks.GREEN_GLAZED_TERRACOTTA.getDefaultState().withProperty(BlockGlazedTerracotta.FACING, facing)); }
                if (dyeType == ItemDyeArrow.DyeType.LIGHT_BLUE) { world.setBlockState(blockpos, Blocks.LIGHT_BLUE_GLAZED_TERRACOTTA.getDefaultState().withProperty(BlockGlazedTerracotta.FACING, facing)); }
                if (dyeType == ItemDyeArrow.DyeType.LIME) { world.setBlockState(blockpos, Blocks.LIME_GLAZED_TERRACOTTA.getDefaultState().withProperty(BlockGlazedTerracotta.FACING, facing)); }
                if (dyeType == ItemDyeArrow.DyeType.MAGENTA) { world.setBlockState(blockpos, Blocks.MAGENTA_GLAZED_TERRACOTTA.getDefaultState().withProperty(BlockGlazedTerracotta.FACING, facing)); }
                if (dyeType == ItemDyeArrow.DyeType.ORANGE) { world.setBlockState(blockpos, Blocks.ORANGE_GLAZED_TERRACOTTA.getDefaultState().withProperty(BlockGlazedTerracotta.FACING, facing)); }
                if (dyeType == ItemDyeArrow.DyeType.PINK) { world.setBlockState(blockpos, Blocks.PINK_GLAZED_TERRACOTTA.getDefaultState().withProperty(BlockGlazedTerracotta.FACING, facing)); }
                if (dyeType == ItemDyeArrow.DyeType.PURPLE) { world.setBlockState(blockpos, Blocks.PURPLE_GLAZED_TERRACOTTA.getDefaultState().withProperty(BlockGlazedTerracotta.FACING, facing)); }
                if (dyeType == ItemDyeArrow.DyeType.RED) { world.setBlockState(blockpos, Blocks.RED_GLAZED_TERRACOTTA.getDefaultState().withProperty(BlockGlazedTerracotta.FACING, facing)); }
                if (dyeType == ItemDyeArrow.DyeType.SILVER) { world.setBlockState(blockpos, Blocks.SILVER_GLAZED_TERRACOTTA.getDefaultState().withProperty(BlockGlazedTerracotta.FACING, facing)); }
                if (dyeType == ItemDyeArrow.DyeType.WHITE) { world.setBlockState(blockpos, Blocks.WHITE_GLAZED_TERRACOTTA.getDefaultState().withProperty(BlockGlazedTerracotta.FACING, facing)); }
                if (dyeType == ItemDyeArrow.DyeType.YELLOW) { world.setBlockState(blockpos, Blocks.YELLOW_GLAZED_TERRACOTTA.getDefaultState().withProperty(BlockGlazedTerracotta.FACING, facing)); }
            }
        }
        
        int itemId = Item.getIdFromItem(BEItems.dye_arrow);
        int itemMeta = this.getDyeType().ordinal();
        for (int ii = 0; ii < 16; ++ii)
        {
            this.world.spawnParticle(EnumParticleTypes.ITEM_CRACK, this.posX, this.posY, this.posZ, ((double)this.rand.nextFloat() - 0.5D) * 0.08D, ((double)this.rand.nextFloat() - 0.5D) * 0.08D, ((double)this.rand.nextFloat() - 0.5D) * 0.08D, new int[] {itemId, itemMeta});                
        }
        
        this.setDead();
    }

    @Override
    protected void arrowHit(EntityLivingBase living)
    {
        ItemDyeArrow.DyeType dyeType = this.getDyeType();
        
        if (living instanceof EntitySheep)
        {
            EntitySheep entitysheep = (EntitySheep)living;

            entitysheep.setFleeceColor(EnumDyeColor.byMetadata(dyeType.ordinal()));
            this.setDead();
        }
        
        if (living instanceof EntityWolf)
        {
            EntityWolf entitywolf = (EntityWolf)living;

            entitywolf.setCollarColor(EnumDyeColor.byMetadata(dyeType.ordinal()));
            this.setDead();
        }
        
        if (living instanceof EntityPlayer)
        {
            EntityPlayer entityplayer = (EntityPlayer)living;

            for (ItemStack itemstack : entityplayer.inventory.armorInventory)
            {
                if (itemstack != null && (itemstack.getItem() == Items.LEATHER_HELMET || itemstack.getItem() == Items.LEATHER_CHESTPLATE || itemstack.getItem() == Items.LEATHER_LEGGINGS || itemstack.getItem() == Items.LEATHER_BOOTS))
                {
                    ItemArmor itemarmor = (ItemArmor)itemstack.getItem();
                    itemarmor.setColor(itemstack, EnumDyeColor.byMetadata(dyeType.ordinal()).getColorValue());
                }
            }
        }
    }

    @Override
    @Nullable
    protected Entity findEntityOnPath(Vec3d start, Vec3d end)
    {
        Entity entity = null;
        List<Entity> list = this.world.getEntitiesInAABBexcluding(this, this.getEntityBoundingBox().expand(this.motionX, this.motionY, this.motionZ).grow(1.0D), ARROW_TARGETS);
        double d0 = 0.0D;

        for (int i = 0; i < list.size(); ++i)
        {
            Entity entity1 = list.get(i);

            if (entity1 != this.shootingEntity || this.ticksInAir >= 5)
            {
                AxisAlignedBB axisalignedbb = entity1.getEntityBoundingBox().grow(0.30000001192092896D);
                RayTraceResult raytraceresult = axisalignedbb.calculateIntercept(start, end);

                if (raytraceresult != null)
                {
                    double d1 = start.squareDistanceTo(raytraceresult.hitVec);

                    if (d1 < d0 || d0 == 0.0D)
                    {
                        entity = entity1;
                        d0 = d1;
                    }
                }
            }
        }

        return entity;
    }

    public static void registerFixesDyeArrow(DataFixer fixer)
    {
        EntityArrow.registerFixesArrow(fixer, "DyeArrow");
    }
    
    @Override
    public void writeEntityToNBT(NBTTagCompound compound)
    {
        compound.setInteger("xTile", this.xTile);
        compound.setInteger("yTile", this.yTile);
        compound.setInteger("zTile", this.zTile);
        compound.setShort("life", (short)this.ticksInGround);
        ResourceLocation resourcelocation = (ResourceLocation)Block.REGISTRY.getNameForObject(this.inTile);
        compound.setString("inTile", resourcelocation == null ? "" : resourcelocation.toString());
        compound.setByte("inData", (byte)this.inData);
        compound.setByte("shake", (byte)this.arrowShake);
        compound.setByte("inGround", (byte)(this.inGround ? 1 : 0));
        compound.setByte("pickup", (byte)this.pickupStatus.ordinal());
        compound.setDouble("damage", this.damage);
        compound.setInteger("dyeType", this.getDyeType().ordinal());
    }

    @Override
    public void readEntityFromNBT(NBTTagCompound compound)
    {
        this.xTile = compound.getInteger("xTile");
        this.yTile = compound.getInteger("yTile");
        this.zTile = compound.getInteger("zTile");
        this.ticksInGround = compound.getShort("life");

        if (compound.hasKey("inTile", 8))
        {
            this.inTile = Block.getBlockFromName(compound.getString("inTile"));
        }
        else
        {
            this.inTile = Block.getBlockById(compound.getByte("inTile") & 255);
        }

        this.inData = compound.getByte("inData") & 255;
        this.arrowShake = compound.getByte("shake") & 255;
        this.inGround = compound.getByte("inGround") == 1;

        if (compound.hasKey("damage", 99))
        {
            this.damage = compound.getDouble("damage");
        }

        if (compound.hasKey("pickup", 99))
        {
            this.pickupStatus = EntityDyeArrow.PickupStatus.getByOrdinal(compound.getByte("pickup"));
        }
        else if (compound.hasKey("player", 99))
        {
            this.pickupStatus = compound.getBoolean("player") ? EntityDyeArrow.PickupStatus.ALLOWED : EntityDyeArrow.PickupStatus.DISALLOWED;
        }
        
        if (compound.hasKey("dyeType", 99))
        {
            this.setDyeType(ItemDyeArrow.DyeType.fromMeta(compound.getInteger("dyeType")));
        }
    }

    @Override
    public void onCollideWithPlayer(EntityPlayer entityIn)
    {
        if (!this.world.isRemote && this.inGround && this.arrowShake <= 0)
        {
            boolean flag = this.pickupStatus == EntityDyeArrow.PickupStatus.ALLOWED || this.pickupStatus == EntityDyeArrow.PickupStatus.CREATIVE_ONLY && entityIn.capabilities.isCreativeMode;

            if (this.pickupStatus == EntityDyeArrow.PickupStatus.ALLOWED && !entityIn.inventory.addItemStackToInventory(this.getArrowStack()))
            {
                flag = false;
            }

            if (flag)
            {
                entityIn.onItemPickup(this, 1);
                this.setDead();
            }
        }
    }

    @Override
    protected ItemStack getArrowStack()
    {
        ItemStack itemstack = new ItemStack(BEItems.dye_arrow);
        itemstack.setItemDamage(this.getDyeType().ordinal());
        return itemstack;
    }

    @Override
    protected boolean canTriggerWalking()
    {
        return false;
    }

    @Override
    public void setDamage(double damageIn)
    {
        this.damage = damageIn;
    }

    @Override
    public double getDamage()
    {
        return this.damage;
    }

    @Override
    public void setKnockbackStrength(int knockbackStrengthIn)
    {
        this.knockbackStrength = knockbackStrengthIn;
    }

    @Override
    public boolean canBeAttackedWithItem()
    {
        return false;
    }

    @Override
    public float getEyeHeight()
    {
        return 0.0F;
    }
    
    @Override
    public void setIsCritical(boolean critical)
    {
        byte b0 = ((Byte)this.dataManager.get(CRITICAL)).byteValue();

        if (critical)
        {
            this.dataManager.set(CRITICAL, Byte.valueOf((byte)(b0 | 1)));
        }
        else
        {
            this.dataManager.set(CRITICAL, Byte.valueOf((byte)(b0 & -2)));
        }
    }

    @Override
    public boolean getIsCritical()
    {
        byte b0 = ((Byte)this.dataManager.get(CRITICAL)).byteValue();
        return (b0 & 1) != 0;
    }
    
    @Override
    public void setEnchantmentEffectsFromEntity(EntityLivingBase p_190547_1_, float p_190547_2_)
    {
        int i = EnchantmentHelper.getMaxEnchantmentLevel(Enchantments.POWER, p_190547_1_);
        int j = EnchantmentHelper.getMaxEnchantmentLevel(Enchantments.PUNCH, p_190547_1_);
        this.setDamage((double)(p_190547_2_ * 2.0F) + this.rand.nextGaussian() * 0.25D + (double)((float)this.world.getDifficulty().getDifficultyId() * 0.11F));

        if (i > 0)
        {
            this.setDamage(this.getDamage() + (double)i * 0.5D + 0.5D);
        }

        if (j > 0)
        {
            this.setKnockbackStrength(j);
        }

        if (EnchantmentHelper.getMaxEnchantmentLevel(Enchantments.FLAME, p_190547_1_) > 0)
        {
            this.setFire(100);
        }
    }

    public static enum PickupStatus
    {
        DISALLOWED,
        ALLOWED,
        CREATIVE_ONLY;

        public static EntityDyeArrow.PickupStatus getByOrdinal(int ordinal)
        {
            if (ordinal < 0 || ordinal > values().length)
            {
                ordinal = 0;
            }

            return values()[ordinal];
        }
    }
}