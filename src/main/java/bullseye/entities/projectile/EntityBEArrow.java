package bullseye.entities.projectile;

import java.util.List;

import javax.annotation.Nullable;

import com.google.common.base.Predicate;
import com.google.common.base.Predicates;

import bullseye.api.BEItems;
import bullseye.config.ConfigurationHandler;
import bullseye.core.Bullseye;
import bullseye.item.ItemBEArrow;
import bullseye.particle.BEParticleTypes;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.block.Block;
import net.minecraft.block.BlockLiquid;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.IProjectile;
import net.minecraft.entity.effect.EntityLightningBolt;
import net.minecraft.entity.item.EntityTNTPrimed;
import net.minecraft.entity.monster.EntityEnderman;
import net.minecraft.entity.monster.EntityEndermite;
import net.minecraft.entity.passive.EntityChicken;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.entity.projectile.EntityArrow;
import net.minecraft.init.Blocks;
import net.minecraft.init.Enchantments;
import net.minecraft.init.Items;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.Item;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.network.play.server.SPacketChangeGameState;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityEndGateway;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EntitySelectors;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.datafix.DataFixer;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.registry.IThrowableEntity;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class EntityBEArrow extends EntityArrow implements IProjectile, IThrowableEntity
{    
	private static final Predicate<Entity> ARROW_TARGETS = Predicates.and(new Predicate[] {EntitySelectors.NOT_SPECTATING, EntitySelectors.IS_ALIVE, new Predicate<Entity>()
	{
	    public boolean apply(@Nullable Entity p_apply_1_)
	    {
	        return p_apply_1_.canBeCollidedWith();
	    }
	}
	                                                                                       });
	private static final DataParameter<Byte> CRITICAL = EntityDataManager.<Byte>createKey(EntityBEArrow.class, DataSerializers.BYTE);
	private static final DataParameter<Byte> ARROW_TYPE = EntityDataManager.<Byte>createKey(EntityBEArrow.class, DataSerializers.BYTE);
    private int xTile;
    private int yTile;
    private int zTile;
    private Block inTile;
    private int inData;
    protected boolean inGround;
    protected int timeInGround;
    public EntityBEArrow.PickupStatus pickupStatus;
    public int arrowShake;
    public Entity shootingEntity;
    private int ticksInGround;
    private int ticksInAir;
    private double damage;
    private int knockbackStrength;

    public EntityBEArrow(World worldIn)
    {
        super(worldIn);
        this.xTile = -1;
        this.yTile = -1;
        this.zTile = -1;
        this.pickupStatus = EntityBEArrow.PickupStatus.DISALLOWED;
        this.damage = 2.0D;
        this.setSize(0.5F, 0.5F);
    }

    public EntityBEArrow(World worldIn, double x, double y, double z)
    {
        this(worldIn);
        this.setPosition(x, y, z);
    }

    public EntityBEArrow(World worldIn, EntityLivingBase shooter)
    {
        this(worldIn, shooter.posX, shooter.posY + (double)shooter.getEyeHeight() - 0.10000000149011612D, shooter.posZ);
        this.shootingEntity = shooter;

        if (shooter instanceof EntityPlayer)
        {
            this.pickupStatus = EntityBEArrow.PickupStatus.ALLOWED;
        }
    }
    
    @Override
    @SideOnly(Side.CLIENT)
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
        this.dataManager.register(ARROW_TYPE, Byte.valueOf((byte)0));
    }
    
    public void setArrowType(ItemBEArrow.ArrowType arrowType)
    {
        dataManager.set(ARROW_TYPE, (byte)arrowType.ordinal());
    }
    
    public ItemBEArrow.ArrowType getArrowType()
    {
        return ItemBEArrow.ArrowType.values()[dataManager.get(ARROW_TYPE)];
    }
    
    @Override
    public void shoot(Entity shooter, float pitch, float yaw, float p_184547_4_, float velocity, float inaccuracy)
    {
        float f = -MathHelper.sin(yaw * 0.017453292F) * MathHelper.cos(pitch * 0.017453292F);
        float f1 = -MathHelper.sin(pitch * 0.017453292F);
        float f2 = MathHelper.cos(yaw * 0.017453292F) * MathHelper.cos(pitch * 0.017453292F);
        this.shoot((double)f, (double)f1, (double)f2, velocity, inaccuracy);
        this.motionX += shooter.motionX;
        this.motionZ += shooter.motionZ;

        if (!shooter.onGround)
        {
            this.motionY += shooter.motionY;
        }
    }

    @Override
    public void shoot(double x, double y, double z, float velocity, float inaccuracy)
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

    @Override
    @SideOnly(Side.CLIENT)
    public void setPositionAndRotationDirect(double x, double y, double z, float yaw, float pitch, int posRotationIncrements, boolean teleport)
    {
        this.setPosition(x, y, z);
        this.setRotation(yaw, pitch);
    }

    @Override
    @SideOnly(Side.CLIENT)
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
            	//Normal Arrows
            	ItemBEArrow.ArrowType arrowType = this.getArrowType();
            	
            	if (arrowType == ItemBEArrow.ArrowType.TRAINING || arrowType == ItemBEArrow.ArrowType.DIAMOND || arrowType == ItemBEArrow.ArrowType.PRISMARINE)
            	{
                	++this.ticksInGround;

                    if (this.ticksInGround >= 1200)
                    {
                        this.setDead();
                    }
            	}
            	
            	this.arrowLand(blockpos);
            }
            
            ++this.timeInGround;
        }
        else
        {
            ItemBEArrow.ArrowType arrowType = this.getArrowType();
            
            this.timeInGround = 0;
            ++this.ticksInAir;
            Vec3d vec3d1 = new Vec3d(this.posX, this.posY, this.posZ);
            Vec3d vec3d = new Vec3d(this.posX + this.motionX, this.posY + this.motionY, this.posZ + this.motionZ);
            RayTraceResult raytraceresult = this.world.rayTraceBlocks(vec3d1, vec3d, (arrowType == ItemBEArrow.ArrowType.FIRE || arrowType == ItemBEArrow.ArrowType.ICE || arrowType == ItemBEArrow.ArrowType.EXTINGUISHING), (arrowType != ItemBEArrow.ArrowType.FIRE && arrowType != ItemBEArrow.ArrowType.ICE && arrowType != ItemBEArrow.ArrowType.EXTINGUISHING), false);
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
            
            //Particles
            if (arrowType == ItemBEArrow.ArrowType.TRAINING || arrowType == ItemBEArrow.ArrowType.EGG || arrowType == ItemBEArrow.ArrowType.DIAMOND || arrowType == ItemBEArrow.ArrowType.PRISMARINE)
            {
	            if (this.getIsCritical())
	            {
	                for (int k = 0; k < 4; ++k)
	                {
	                    this.world.spawnParticle(EnumParticleTypes.CRIT, this.posX + this.motionX * (double)k / 4.0D, this.posY + this.motionY * (double)k / 4.0D, this.posZ + this.motionZ * (double)k / 4.0D, -this.motionX, -this.motionY + 0.2D, -this.motionZ);
	                }
	            }
            }
            if (arrowType == ItemBEArrow.ArrowType.FIRE)
            {
            	for (int k = 0; k < 4; ++k)
                {
            	    this.world.spawnParticle(EnumParticleTypes.FLAME, this.posX + this.motionX * (double)k / 8.0D, this.posY + this.motionY * (double)k / 8.0D, this.posZ + this.motionZ * (double)k / 8.0D, 0.0D, 0.0D, 0.0D, new int[0]);
                }
            }
            if (arrowType == ItemBEArrow.ArrowType.ICE)
            {
            	for (int k = 0; k < 4; ++k)
                {
            		Bullseye.proxy.spawnParticle(BEParticleTypes.SNOWFLAKE, this.posX + this.motionX * (double)k / 8.0D, this.posY + this.motionY * (double)k / 8.0D, this.posZ + this.motionZ * (double)k / 8.0D, 0.0D, 0.0D, 0.0D, new int[0]);
                }
            }
            if (arrowType == ItemBEArrow.ArrowType.LIGHTNING)
            {
            	for (int k = 0; k < 4; ++k)
                {
            		this.world.spawnParticle(EnumParticleTypes.END_ROD, this.posX + this.motionX * (double)k / 8.0D, this.posY + this.motionY * (double)k / 8.0D, this.posZ + this.motionZ * (double)k / 8.0D, 0.0D, 0.0D, 0.0D, new int[0]);
                }
            }
            if (arrowType == ItemBEArrow.ArrowType.BOMB)
            {
            	for (int k = 0; k < 4; ++k)
                {
            		this.world.spawnParticle(EnumParticleTypes.SMOKE_NORMAL, this.posX + this.motionX * (double)k / 8.0D, this.posY + this.motionY * (double)k / 8.0D, this.posZ + this.motionZ * (double)k / 8.0D, 0.0D, 0.0D, 0.0D, new int[0]);
                }
            }
            if (arrowType == ItemBEArrow.ArrowType.ENDER)
            {
                for (int k = 0; k < 8; ++k)
                {
                    this.world.spawnParticle(EnumParticleTypes.PORTAL, this.posX, this.posY, this.posZ, this.rand.nextGaussian(), 0.0D, this.rand.nextGaussian());
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

                if (arrowType != ItemBEArrow.ArrowType.PRISMARINE)
                {
                    f1 = 0.6F;
                }
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
        ItemBEArrow.ArrowType arrowType = this.getArrowType();

        if (raytraceResultIn != null)
        {
            if (raytraceResultIn.typeOfHit == RayTraceResult.Type.BLOCK)
            {
                BlockPos blockpos = raytraceResultIn.getBlockPos();
                IBlockState iblockstate = this.world.getBlockState(blockpos);
                
                //Extinguishing Arrows
                if (arrowType == ItemBEArrow.ArrowType.EXTINGUISHING)
                {
                    if (iblockstate.getMaterial() == Material.LAVA && ((Integer)iblockstate.getValue(BlockLiquid.LEVEL)).intValue() == 0)
                    {
                        this.world.playSound((EntityPlayer)null, blockpos, SoundEvents.BLOCK_GLASS_BREAK, SoundCategory.BLOCKS, 0.5F, 2.6F + (this.world.rand.nextFloat() - this.world.rand.nextFloat()) * 0.8F);
                        for (int i = 0; i < 8; ++i)
                        {
                            this.world.spawnParticle(EnumParticleTypes.WATER_SPLASH, (this.posX - 0.5D) + Math.random(), this.posY + 0.25D, (this.posZ - 0.5D) + Math.random(), 0.0D, 0.0D, 0.0D, new int[0]);
                        }
                        this.world.setBlockState(blockpos, Blocks.OBSIDIAN.getDefaultState());
                        this.setDead();
                    }
                }
                
                //Ice Arrows
                if (arrowType == ItemBEArrow.ArrowType.ICE)
                {
                    if (iblockstate.getMaterial() == Material.WATER && ((Integer)iblockstate.getValue(BlockLiquid.LEVEL)).intValue() == 0)
                    {
                        this.world.setBlockState(blockpos, Blocks.FROSTED_ICE.getDefaultState(), 2);
                        this.setDead();
                    }
                }
                
                //Fire Arrows
                if (arrowType == ItemBEArrow.ArrowType.FIRE)
                {
                    if (iblockstate.getMaterial() == Material.WATER && ((Integer)iblockstate.getValue(BlockLiquid.LEVEL)).intValue() == 0)
                    {
                        this.world.playSound((EntityPlayer)null, blockpos, SoundEvents.BLOCK_FIRE_EXTINGUISH, SoundCategory.BLOCKS, 0.5F, 2.6F + (this.world.rand.nextFloat() - this.world.rand.nextFloat()) * 0.8F);
                        for (int i = 0; i < 8; ++i)
                        {
                            this.world.spawnParticle(EnumParticleTypes.SMOKE_LARGE, (this.posX - 0.5D) + Math.random(), this.posY + 0.25D, (this.posZ - 0.5D) + Math.random(), 0.0D, 0.0D, 0.0D, new int[0]);
                        }
                        this.setDead();
                    }
                    
                    BlockPos pos = raytraceResultIn.getBlockPos().offset(raytraceResultIn.sideHit);
                    
                    if ((world.isAirBlock(pos) || iblockstate.getBlock().isReplaceable(world, pos)) && this.world.isBlockFullCube(blockpos) && iblockstate.getMaterial() != Material.LAVA && iblockstate.getMaterial() != Material.WATER && iblockstate != Blocks.ICE.getDefaultState() && iblockstate != Blocks.SNOW_LAYER.getDefaultState() && iblockstate != Blocks.SNOW.getDefaultState() && iblockstate != Blocks.PACKED_ICE.getDefaultState() && iblockstate != Blocks.TNT.getDefaultState())
                    {
                        if (ConfigurationHandler.burnFireArrows)
                        {
                            this.world.setBlockState(pos, Blocks.FIRE.getDefaultState(), 11);
                        }
                        
                        this.setDead();
                    }
                }
            }
        }
        
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

    				if (!this.world.isRemote)
    				{
    				    if (arrowType != ItemBEArrow.ArrowType.BOMB && arrowType != ItemBEArrow.ArrowType.LIGHTNING && arrowType != ItemBEArrow.ArrowType.EGG && arrowType != ItemBEArrow.ArrowType.EXTINGUISHING && arrowType != ItemBEArrow.ArrowType.TRAINING && arrowType != ItemBEArrow.ArrowType.ENDER)
    				    {
    				        entitylivingbase.setArrowCountInEntity(entitylivingbase.getArrowCountInEntity() + 1);
    				    }
    				}

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
    				if (arrowType != ItemBEArrow.ArrowType.TRAINING)
    				{
    				    this.setDead();
    				}
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
    				if (this.pickupStatus == EntityBEArrow.PickupStatus.ALLOWED)
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

    @Override
    protected void arrowHit(EntityLivingBase living)
    {
        ItemBEArrow.ArrowType arrowType = this.getArrowType();
        
        //Extinguishing Arrows
        if (arrowType == ItemBEArrow.ArrowType.EXTINGUISHING)
        {
            if (living.isBurning())
            {
                living.extinguish();
            }
            
            if (living instanceof EntityPlayer)
            {
                EntityPlayer entityplayer = (EntityPlayer)living;

                for (ItemStack itemstack : entityplayer.inventory.armorInventory)
                {
                    if (itemstack != null && (itemstack.getItem() == Items.LEATHER_HELMET || itemstack.getItem() == Items.LEATHER_CHESTPLATE || itemstack.getItem() == Items.LEATHER_LEGGINGS || itemstack.getItem() == Items.LEATHER_BOOTS))
                    {
                        ItemArmor itemarmor = (ItemArmor)itemstack.getItem();
                        itemarmor.removeColor(itemstack);
                    }
                }
            }

            this.world.playSound((EntityPlayer)null, living.getPosition(), SoundEvents.BLOCK_GLASS_BREAK, SoundCategory.BLOCKS, 0.5F, 2.6F + (this.world.rand.nextFloat() - this.world.rand.nextFloat()) * 0.8F);
            
            for (int i1 = 0; i1 < 8; ++i1)
            {
                this.world.spawnParticle(EnumParticleTypes.WATER_SPLASH, (this.posX - 0.5D) + Math.random(), this.posY + 0.25D, (this.posZ - 0.5D) + Math.random(), 0.0D, 0.0D, 0.0D, new int[0]);
            }
            int itemId = Item.getIdFromItem(BEItems.arrow);
            int itemMeta = this.getArrowType().ordinal();
            for (int ii = 0; ii < 16; ++ii)
            {
                this.world.spawnParticle(EnumParticleTypes.ITEM_CRACK, this.posX, this.posY, this.posZ, ((double)this.rand.nextFloat() - 0.5D) * 0.08D, ((double)this.rand.nextFloat() - 0.5D) * 0.08D, ((double)this.rand.nextFloat() - 0.5D) * 0.08D, new int[] {itemId, itemMeta});                
            }
            
            this.setDead();
        }
        
        //Fire Arrows
        if (arrowType == ItemBEArrow.ArrowType.FIRE)
        {
            living.setFire(10);
            
            this.world.playSound((EntityPlayer)null, living.getPosition(), SoundEvents.BLOCK_FIRE_EXTINGUISH, SoundCategory.BLOCKS, 0.5F, 2.6F + (this.world.rand.nextFloat() - this.world.rand.nextFloat()) * 0.8F);
            
            for (int i1 = 0; i1 < 8; ++i1)
            {
                this.world.spawnParticle(EnumParticleTypes.SMOKE_LARGE, (this.posX - 0.5D) + Math.random(), this.posY + 0.25D, (this.posZ - 0.5D) + Math.random(), 0.0D, 0.0D, 0.0D, new int[0]);
            }
            int itemId = Item.getIdFromItem(BEItems.arrow);
            int itemMeta = this.getArrowType().ordinal();
            for (int ii = 0; ii < 16; ++ii)
            {
                this.world.spawnParticle(EnumParticleTypes.ITEM_CRACK, this.posX, this.posY, this.posZ, ((double)this.rand.nextFloat() - 0.5D) * 0.08D, ((double)this.rand.nextFloat() - 0.5D) * 0.08D, ((double)this.rand.nextFloat() - 0.5D) * 0.08D, new int[] {itemId, itemMeta});                
            }
            
            this.setDead();
        }
        
        //Ice Arrows
        if (arrowType == ItemBEArrow.ArrowType.ICE)
        {
            for (int i1 = 0; i1 < 8; ++i1)
            {
                Bullseye.proxy.spawnParticle(BEParticleTypes.SNOWFLAKE, (this.posX - 0.5D) + Math.random(), this.posY + 0.25D, (this.posZ - 0.5D) + Math.random(), 0.0D, 0.0D, 0.0D, new int[0]);
            }
            int itemId = Item.getIdFromItem(BEItems.arrow);
            int itemMeta = this.getArrowType().ordinal();
            for (int ii = 0; ii < 16; ++ii)
            {
                this.world.spawnParticle(EnumParticleTypes.ITEM_CRACK, this.posX, this.posY, this.posZ, ((double)this.rand.nextFloat() - 0.5D) * 0.08D, ((double)this.rand.nextFloat() - 0.5D) * 0.08D, ((double)this.rand.nextFloat() - 0.5D) * 0.08D, new int[] {itemId, itemMeta});                
            }
            
            this.setDead();
        }
        
        //Bomb Arrows
        if (arrowType == ItemBEArrow.ArrowType.BOMB)
        {
            if (!this.world.isRemote)
            {   
                float f1 = 1.8F;
                this.world.createExplosion(this, this.posX, this.posY, this.posZ, f1, ConfigurationHandler.explodeBombArrows);
            }
            int itemId = Item.getIdFromItem(BEItems.arrow);
            int itemMeta = this.getArrowType().ordinal();
            for (int ii = 0; ii < 16; ++ii)
            {
                this.world.spawnParticle(EnumParticleTypes.ITEM_CRACK, this.posX, this.posY, this.posZ, ((double)this.rand.nextFloat() - 0.5D) * 0.08D, ((double)this.rand.nextFloat() - 0.5D) * 0.08D, ((double)this.rand.nextFloat() - 0.5D) * 0.08D, new int[] {itemId, itemMeta});                
            }
            
            this.setDead();
        }
        
        //Lightning Arrows
        if (arrowType == ItemBEArrow.ArrowType.LIGHTNING)
        {
            if (!this.world.isRemote)
            {   
                EntityLightningBolt entityLightningBolt = new EntityLightningBolt(this.world, this.posX, this.posY, this.posZ, !(ConfigurationHandler.fireLightningArrows));
                this.world.addWeatherEffect(entityLightningBolt);
                
                if (!(ConfigurationHandler.fireLightningArrows))
                {
                    double d0 = 3.0D;
                    List<Entity> list = this.world.getEntitiesWithinAABBExcludingEntity(this, new AxisAlignedBB(entityLightningBolt.posX - d0, entityLightningBolt.posY - d0, entityLightningBolt.posZ - d0, entityLightningBolt.posX + d0, entityLightningBolt.posY + 6.0D + d0, entityLightningBolt.posZ + d0));

                    for (int i1 = 0; i1 < list.size(); ++i1)
                    {
                        Entity entity1 = (Entity)list.get(i1);
                        if (!net.minecraftforge.event.ForgeEventFactory.onEntityStruckByLightning(entity1, entityLightningBolt))
                            entity1.onStruckByLightning(entityLightningBolt);
                    }
                }
            }
            
            int itemId = Item.getIdFromItem(BEItems.arrow);
            int itemMeta = this.getArrowType().ordinal();
            for (int ii = 0; ii < 16; ++ii)
            {
                this.world.spawnParticle(EnumParticleTypes.ITEM_CRACK, this.posX, this.posY, this.posZ, ((double)this.rand.nextFloat() - 0.5D) * 0.08D, ((double)this.rand.nextFloat() - 0.5D) * 0.08D, ((double)this.rand.nextFloat() - 0.5D) * 0.08D, new int[] {itemId, itemMeta});                
            }
            
            this.setDead();
        }
    }
    
    protected void arrowLand(BlockPos blockpos)
    {
        ItemBEArrow.ArrowType arrowType = this.getArrowType();
        
        //Ender Arrow
        if (arrowType == ItemBEArrow.ArrowType.ENDER)
        {
            EntityLivingBase entitylivingbase = (EntityLivingBase)this.shootingEntity;
            TileEntity tileentity = this.world.getTileEntity(blockpos);

            if (tileentity instanceof TileEntityEndGateway)
            {
                TileEntityEndGateway tileentityendgateway = (TileEntityEndGateway)tileentity;

                if (entitylivingbase != null)
                {
                    if (entitylivingbase instanceof EntityPlayerMP)
                    {
                        CriteriaTriggers.ENTER_BLOCK.trigger((EntityPlayerMP)entitylivingbase, this.world.getBlockState(blockpos));
                    }

                    tileentityendgateway.teleportEntity(entitylivingbase);
                    this.setDead();
                    return;
                }

                tileentityendgateway.teleportEntity(this);
                return;
            }
            
            for (int i = 0; i < 32; ++i)
            {
                this.world.spawnParticle(EnumParticleTypes.PORTAL, this.posX, this.posY + this.rand.nextDouble() * 2.0D, this.posZ, this.rand.nextGaussian(), 0.0D, this.rand.nextGaussian());
            }

            if (!this.world.isRemote)
            {
                if (entitylivingbase instanceof EntityPlayerMP)
                {
                    EntityPlayerMP entityplayermp = (EntityPlayerMP)entitylivingbase;

                    if (entityplayermp.connection.getNetworkManager().isChannelOpen() && entityplayermp.world == this.world && !entityplayermp.isPlayerSleeping())
                    {
                        net.minecraftforge.event.entity.living.EnderTeleportEvent event = new net.minecraftforge.event.entity.living.EnderTeleportEvent(entityplayermp, this.posX, this.posY, this.posZ, 5.0F);
                        if (!net.minecraftforge.common.MinecraftForge.EVENT_BUS.post(event))
                        { // Don't indent to lower patch size
                        if (this.rand.nextFloat() < 0.05F && this.world.getGameRules().getBoolean("doMobSpawning"))
                        {
                            EntityEndermite entityendermite = new EntityEndermite(this.world);
                            entityendermite.setSpawnedByPlayer(true);
                            entityendermite.setLocationAndAngles(entitylivingbase.posX, entitylivingbase.posY, entitylivingbase.posZ, entitylivingbase.rotationYaw, entitylivingbase.rotationPitch);
                            this.world.spawnEntity(entityendermite);
                        }

                        if (entitylivingbase.isRiding())
                        {
                            entitylivingbase.dismountRidingEntity();
                        }

                        entitylivingbase.setPositionAndUpdate(event.getTargetX(), event.getTargetY(), event.getTargetZ());
                        entitylivingbase.fallDistance = 0.0F;
                        entitylivingbase.attackEntityFrom(DamageSource.FALL, event.getAttackDamage());
                        }
                    }
                }
                else if (entitylivingbase != null)
                {
                    entitylivingbase.setPositionAndUpdate(this.posX, this.posY, this.posZ);
                    entitylivingbase.fallDistance = 0.0F;
                }

                this.setDead();
            }
        }
        //Egg Arrows
        if (arrowType == ItemBEArrow.ArrowType.EGG)
        {
            if (!this.world.isRemote)
            {   
                int i = world.rand.nextInt(12);
                if (i == 0)
                {
                    EntityChicken entitychicken = new EntityChicken(this.world);
                    entitychicken.setGrowingAge(-24000);
                    entitychicken.setLocationAndAngles(this.posX, this.posY, this.posZ, this.rotationYaw, 0.0F);
                    this.world.spawnEntity(entitychicken);
                }
            }
            int itemId = Item.getIdFromItem(BEItems.arrow);
            int itemMeta = this.getArrowType().ordinal();
            for (int ii = 0; ii < 16; ++ii)
            {
                this.world.spawnParticle(EnumParticleTypes.ITEM_CRACK, this.posX, this.posY, this.posZ, ((double)this.rand.nextFloat() - 0.5D) * 0.08D, ((double)this.rand.nextFloat() - 0.5D) * 0.08D, ((double)this.rand.nextFloat() - 0.5D) * 0.08D, new int[] {itemId, itemMeta});                
            }
            this.setDead();
        }
        
        //Extinguishing Arrows
        if (arrowType == ItemBEArrow.ArrowType.EXTINGUISHING)
        {
            if (!this.world.isRemote)
            {
                for (int xx = -2; xx <= 2; xx++)
                {
                    for (int yy = -2; yy <= 2; yy++)
                    {
                        for (int zz = -2; zz <= 2; zz++)
                        {
                            BlockPos pos = blockpos.add(xx, yy - 1, zz);
                        
                            if (this.world.getBlockState(pos).getBlock() == Blocks.FIRE)
                            {
                                world.setBlockToAir(pos);
                            }
                            if (this.world.getBlockState(pos).getBlock() == Blocks.TORCH)
                            {
                                world.destroyBlock(pos, true);
                            }
                        }
                    }
                }
            }
            this.world.playSound((EntityPlayer)null, blockpos, SoundEvents.BLOCK_GLASS_BREAK, SoundCategory.BLOCKS, 0.5F, 2.6F + (this.world.rand.nextFloat() - this.world.rand.nextFloat()) * 0.8F);
            
            for (int i = 0; i < 8; ++i)
            {
                this.world.spawnParticle(EnumParticleTypes.WATER_SPLASH, (this.posX - 0.5D) + Math.random(), this.posY + 0.25D, (this.posZ - 0.5D) + Math.random(), 0.0D, 0.0D, 0.0D, new int[0]);
            }
            
            int itemId = Item.getIdFromItem(BEItems.arrow);
            int itemMeta = this.getArrowType().ordinal();
            for (int ii = 0; ii < 16; ++ii)
            {
                this.world.spawnParticle(EnumParticleTypes.ITEM_CRACK, this.posX, this.posY, this.posZ, ((double)this.rand.nextFloat() - 0.5D) * 0.08D, ((double)this.rand.nextFloat() - 0.5D) * 0.08D, ((double)this.rand.nextFloat() - 0.5D) * 0.08D, new int[] {itemId, itemMeta});                
            }
            
            this.setDead();
        }
        
        //Fire Arrows
        if (arrowType == ItemBEArrow.ArrowType.FIRE)
        {
            if (!this.world.isRemote)
            {
                if (world.getBlockState(blockpos) == Blocks.ICE.getDefaultState())
                {
                    this.world.setBlockState(blockpos, Blocks.WATER.getDefaultState());
                }
                if (world.getBlockState(blockpos) == Blocks.FROSTED_ICE.getDefaultState())
                {
                    this.world.setBlockState(blockpos, Blocks.WATER.getDefaultState());
                }
                if (world.getBlockState(blockpos) == Blocks.SNOW_LAYER.getDefaultState())
                {
                    this.world.setBlockState(blockpos, Blocks.AIR.getDefaultState());
                }
                if (world.getBlockState(blockpos) == Blocks.SNOW.getDefaultState())
                {
                    this.world.setBlockState(blockpos, Blocks.AIR.getDefaultState());
                }
                if (world.getBlockState(blockpos) == Blocks.PACKED_ICE.getDefaultState())
                {
                    this.world.setBlockState(blockpos, Blocks.ICE.getDefaultState());
                }
                if (world.getBlockState(blockpos) == Blocks.TNT.getDefaultState())
                {
                    EntityTNTPrimed entitytntprimed = new EntityTNTPrimed(this.world, (double)((float)blockpos.getX() + 0.5F), (double)blockpos.getY(), (double)((float)blockpos.getZ() + 0.5F), this.shootingEntity instanceof EntityLivingBase ? (EntityLivingBase)this.shootingEntity : null);
                    this.world.spawnEntity(entitytntprimed);
                    this.world.playSound((EntityPlayer)null, entitytntprimed.posX, entitytntprimed.posY, entitytntprimed.posZ, SoundEvents.ENTITY_TNT_PRIMED, SoundCategory.BLOCKS, 1.0F, 1.0F);
                    this.world.setBlockToAir(blockpos);
                }
            }
            
            this.world.playSound((EntityPlayer)null, blockpos, SoundEvents.BLOCK_FIRE_EXTINGUISH, SoundCategory.BLOCKS, 0.5F, 2.6F + (this.world.rand.nextFloat() - this.world.rand.nextFloat()) * 0.8F);
            
            for (int i = 0; i < 8; ++i)
            {
                this.world.spawnParticle(EnumParticleTypes.SMOKE_LARGE, (this.posX - 0.5D) + Math.random(), this.posY + 0.25D, (this.posZ - 0.5D) + Math.random(), 0.0D, 0.0D, 0.0D, new int[0]);
            }
            
            int itemId = Item.getIdFromItem(BEItems.arrow);
            int itemMeta = this.getArrowType().ordinal();
            for (int ii = 0; ii < 16; ++ii)
            {
                this.world.spawnParticle(EnumParticleTypes.ITEM_CRACK, this.posX, this.posY, this.posZ, ((double)this.rand.nextFloat() - 0.5D) * 0.08D, ((double)this.rand.nextFloat() - 0.5D) * 0.08D, ((double)this.rand.nextFloat() - 0.5D) * 0.08D, new int[] {itemId, itemMeta});                
            }
            
            this.setDead();
        }
        
        //Ice Arrows
        if (arrowType == ItemBEArrow.ArrowType.ICE)
        {
            if (!this.world.isRemote)
            {
                if (world.getBlockState(blockpos) == Blocks.ICE.getDefaultState())
                {
                    this.world.setBlockState(blockpos, Blocks.PACKED_ICE.getDefaultState());
                }
                if (world.isAirBlock(blockpos.up()) && world.isBlockFullCube(blockpos))
                {
                    this.world.setBlockState(blockpos.up(), Blocks.SNOW_LAYER.getDefaultState());
                }
            }
            
            for (int i = 0; i < 8; ++i)
            {
                Bullseye.proxy.spawnParticle(BEParticleTypes.SNOWFLAKE, (this.posX - 0.5D) + Math.random(), this.posY + 0.25D, (this.posZ - 0.5D) + Math.random(), 0.0D, 0.0D, 0.0D, new int[0]);
            }
            
            int itemId = Item.getIdFromItem(BEItems.arrow);
            int itemMeta = this.getArrowType().ordinal();
            for (int ii = 0; ii < 16; ++ii)
            {
                this.world.spawnParticle(EnumParticleTypes.ITEM_CRACK, this.posX, this.posY, this.posZ, ((double)this.rand.nextFloat() - 0.5D) * 0.08D, ((double)this.rand.nextFloat() - 0.5D) * 0.08D, ((double)this.rand.nextFloat() - 0.5D) * 0.08D, new int[] {itemId, itemMeta});                
            }
            
            this.setDead();
        }
        
        //Bomb Arrows
        if (arrowType == ItemBEArrow.ArrowType.BOMB)
        {
            if (!this.world.isRemote)
            {   
                float f = 1.8F;
                this.world.createExplosion(this, this.posX, this.posY, this.posZ, f, ConfigurationHandler.explodeBombArrows);
            }
            
            int itemId = Item.getIdFromItem(BEItems.arrow);
            int itemMeta = this.getArrowType().ordinal();
            for (int ii = 0; ii < 16; ++ii)
            {
                this.world.spawnParticle(EnumParticleTypes.ITEM_CRACK, this.posX, this.posY, this.posZ, ((double)this.rand.nextFloat() - 0.5D) * 0.08D, ((double)this.rand.nextFloat() - 0.5D) * 0.08D, ((double)this.rand.nextFloat() - 0.5D) * 0.08D, new int[] {itemId, itemMeta});                
            }
            
            this.setDead();
        }
        
        //Lightning Arrows
        if (arrowType == ItemBEArrow.ArrowType.LIGHTNING)
        {
            if (!this.world.isRemote)
            {   
                EntityLightningBolt entityLightningBolt = new EntityLightningBolt(this.world, this.posX, this.posY, this.posZ, !(ConfigurationHandler.fireLightningArrows));
                this.world.addWeatherEffect(entityLightningBolt);
                
                if (!(ConfigurationHandler.fireLightningArrows))
                {
                    double d0 = 3.0D;
                    List<Entity> list = this.world.getEntitiesWithinAABBExcludingEntity(this, new AxisAlignedBB(entityLightningBolt.posX - d0, entityLightningBolt.posY - d0, entityLightningBolt.posZ - d0, entityLightningBolt.posX + d0, entityLightningBolt.posY + 6.0D + d0, entityLightningBolt.posZ + d0));

                    for (int i1 = 0; i1 < list.size(); ++i1)
                    {
                        Entity entity1 = (Entity)list.get(i1);
                        if (!net.minecraftforge.event.ForgeEventFactory.onEntityStruckByLightning(entity1, entityLightningBolt))
                            entity1.onStruckByLightning(entityLightningBolt);
                    }
                }
            }
            
            int itemId = Item.getIdFromItem(BEItems.arrow);
            int itemMeta = this.getArrowType().ordinal();
            for (int ii = 0; ii < 16; ++ii)
            {
                this.world.spawnParticle(EnumParticleTypes.ITEM_CRACK, this.posX, this.posY, this.posZ, ((double)this.rand.nextFloat() - 0.5D) * 0.08D, ((double)this.rand.nextFloat() - 0.5D) * 0.08D, ((double)this.rand.nextFloat() - 0.5D) * 0.08D, new int[] {itemId, itemMeta});                
            }
            
            this.setDead();
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

    public static void registerFixesBEArrow(DataFixer fixer)
    {
        EntityArrow.registerFixesArrow(fixer, "BEArrow");
    }
    
    @Override
    public void writeEntityToNBT(NBTTagCompound compound)
    {
        compound.setInteger("xTile", this.xTile);
        compound.setInteger("yTile", this.yTile);
        compound.setInteger("zTile", this.zTile);
        compound.setShort("life", (short)this.ticksInGround);
        ResourceLocation resourcelocation = Block.REGISTRY.getNameForObject(this.inTile);
        compound.setString("inTile", resourcelocation == null ? "" : resourcelocation.toString());
        compound.setByte("inData", (byte)this.inData);
        compound.setByte("shake", (byte)this.arrowShake);
        compound.setByte("inGround", (byte)(this.inGround ? 1 : 0));
        compound.setByte("pickup", (byte)this.pickupStatus.ordinal());
        compound.setDouble("damage", this.damage);
        compound.setBoolean("crit", this.getIsCritical());
        compound.setInteger("arrowType", this.getArrowType().ordinal());
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
            this.pickupStatus = EntityBEArrow.PickupStatus.getByOrdinal(compound.getByte("pickup"));
        }
        else if (compound.hasKey("player", 99))
        {
            this.pickupStatus = compound.getBoolean("player") ? EntityBEArrow.PickupStatus.ALLOWED : EntityBEArrow.PickupStatus.DISALLOWED;
        }

        this.setIsCritical(compound.getBoolean("crit"));
        
        if (compound.hasKey("arrowType", 99))
        {
            this.setArrowType(ItemBEArrow.ArrowType.fromMeta(compound.getInteger("arrowType")));
        }
    }

    @Override
    public void onCollideWithPlayer(EntityPlayer entityIn)
    {
        if (!this.world.isRemote && this.inGround && this.arrowShake <= 0)
        {
            boolean flag = this.pickupStatus == EntityBEArrow.PickupStatus.ALLOWED || this.pickupStatus == EntityBEArrow.PickupStatus.CREATIVE_ONLY && entityIn.capabilities.isCreativeMode;

            if (this.pickupStatus == EntityBEArrow.PickupStatus.ALLOWED && !entityIn.inventory.addItemStackToInventory(this.getArrowStack()))
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
        ItemStack itemstack = new ItemStack(BEItems.arrow);
        itemstack.setItemDamage(this.getArrowType().ordinal());
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

        public static EntityBEArrow.PickupStatus getByOrdinal(int ordinal)
        {
            if (ordinal < 0 || ordinal > values().length)
            {
                ordinal = 0;
            }

            return values()[ordinal];
        }
    }

    @Override
    public Entity getThrower()
    {
        return shootingEntity;
    }

    @Override
    public void setThrower( Entity entity )
    {
        shootingEntity = entity;
    }
}