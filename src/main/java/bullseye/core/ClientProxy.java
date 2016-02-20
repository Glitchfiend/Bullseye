/*******************************************************************************
 * Copyright 2016, the Biomes O' Plenty Team
 * 
 * This work is licensed under a Creative Commons Attribution-NonCommercial-NoDerivatives 4.0 International Public License.
 * 
 * To view a copy of this license, visit http://creativecommons.org/licenses/by-nc-nd/4.0/.
 ******************************************************************************/
package bullseye.core;

import net.minecraft.client.Minecraft;
import net.minecraft.client.particle.EntityFX;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.resources.model.ModelBakery;
import net.minecraft.client.resources.model.ModelResourceLocation;
import net.minecraft.entity.Entity;
import net.minecraft.item.Item;
import net.minecraft.util.MathHelper;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.fml.client.registry.IRenderFactory;
import net.minecraftforge.fml.client.registry.RenderingRegistry;
import bullseye.entities.projectile.EntityBEArrow;
import bullseye.entities.projectile.RenderBEArrow;
import bullseye.particle.BEParticleTypes;
import bullseye.particle.EntitySnowflakeFX;


public class ClientProxy extends CommonProxy
{
	public static ResourceLocation particleTexturesLocation = new ResourceLocation("bullseye:textures/particles/particles.png");

    @Override
    public void registerRenderers()
    {
        registerEntityRenderer(EntityBEArrow.class, RenderBEArrow.class);
    }
    
    @Override
    public void registerItemVariantModel(Item item, String name, int metadata) 
    {
        if (item != null) 
        { 
            ModelBakery.registerItemVariants(item, new ResourceLocation("bullseye:" + name));
            ModelLoader.setCustomModelResourceLocation(item, metadata, new ModelResourceLocation(Bullseye.MOD_ID + ":" + name, "inventory"));
        }
    }
    
    @Override
    public void spawnParticle(BEParticleTypes type, double x, double y, double z, Object... info)
    {
        Minecraft minecraft = Minecraft.getMinecraft();
        EntityFX entityFx = null;
        switch (type)
        {
        case SNOWFLAKE:
            entityFx = new EntitySnowflakeFX(minecraft.theWorld, x, y, z, MathHelper.getRandomDoubleInRange(minecraft.theWorld.rand, -0.03, 0.03), -0.02D, MathHelper.getRandomDoubleInRange(minecraft.theWorld.rand, -0.03, 0.03));
            break;
        default:
            break;
        }

        if (entityFx != null) {minecraft.effectRenderer.addEffect(entityFx);}
    }
    
    private static <E extends Entity> void registerEntityRenderer(Class<E> entityClass, Class<? extends Render<E>> renderClass)
    {
        RenderingRegistry.registerEntityRenderingHandler(entityClass, new EntityRenderFactory<E>(renderClass));
    }

    private static class EntityRenderFactory<E extends Entity> implements IRenderFactory<E>
    {
        private Class<? extends Render<E>> renderClass;

        private EntityRenderFactory(Class<? extends Render<E>> renderClass)
        {
            this.renderClass = renderClass;
        }

        @Override
        public Render<E> createRenderFor(RenderManager manager) 
        {
            Render<E> renderer = null;

            try 
            {
                renderer = renderClass.getConstructor(RenderManager.class).newInstance(manager);
            } 
            catch (Exception e) 
            {
                e.printStackTrace();
            }

            return renderer;
        }
    }
}
