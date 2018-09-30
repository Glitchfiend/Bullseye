package bullseye.init;

import java.util.Map;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityList.EntityEggInfo;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.ModContainer;
import net.minecraftforge.fml.common.registry.EntityRegistry;
import net.minecraftforge.fml.common.registry.EntityRegistry.EntityRegistration;
import bullseye.core.Bullseye;
import bullseye.entities.projectile.EntityBEArrow;
import bullseye.entities.projectile.EntityDyeArrow;

import com.google.common.collect.Maps;

public class ModEntities
{
	public static final Map<Integer, EntityEggInfo> entityEggs = Maps.<Integer, EntityEggInfo>newLinkedHashMap();
    public static final Map<Integer, String> idToBEEntityName = Maps.<Integer, String>newLinkedHashMap();

    private static int nextBEEntityId = 1;
    
    public static void init()
    {
        // projectiles
    	registerBEEntity(EntityDyeArrow.class, "dye_arrow", 64, 20, false);
        registerBEEntity(EntityBEArrow.class, "arrow", 64, 20, false);
    }

    // register an entity
    public static int registerBEEntity(Class<? extends Entity> entityClass, String entityName, int trackingRange, int updateFrequency, boolean sendsVelocityUpdates)
    {
        int beEntityId = nextBEEntityId;
        nextBEEntityId++;
        EntityRegistry.registerModEntity(new ResourceLocation(Bullseye.MOD_ID, entityName), entityClass, entityName, beEntityId, Bullseye.instance, trackingRange, updateFrequency, sendsVelocityUpdates);
        idToBEEntityName.put(beEntityId, entityName);
        return beEntityId;
    }
    
    public static Entity createEntityByID(int tanEntityId, World worldIn)
    {
        Entity entity = null;
        ModContainer mc = FMLCommonHandler.instance().findContainerFor(Bullseye.instance);
        EntityRegistration er = EntityRegistry.instance().lookupModSpawn(mc, tanEntityId);
        if (er != null)
        {
            Class<? extends Entity> clazz = er.getEntityClass();
            try
            {
                if (clazz != null)
                {
                    entity = (Entity)clazz.getConstructor(new Class[] {World.class}).newInstance(new Object[] {worldIn});
                }
            }
            catch (Exception exception)
            {
                exception.printStackTrace();
            }            
        }
        if (entity == null)
        {
        	Bullseye.logger.warn("Skipping BE Entity with id " + tanEntityId);
        }        
        return entity;
    }
    
    
}