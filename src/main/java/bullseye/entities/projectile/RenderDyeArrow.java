package bullseye.entities.projectile;

import org.lwjgl.opengl.GL11;

import bullseye.item.ItemDyeArrow;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.VertexBuffer;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class RenderDyeArrow extends Render<EntityDyeArrow>
{
    protected ResourceLocation[] textures;
    
    public RenderDyeArrow(RenderManager renderManager)
    {
        super(renderManager);
        int n = ItemDyeArrow.DyeType.values().length;
        this.textures = new ResourceLocation[n];
        for (int i = 0; i < n; ++i)
        {
            this.textures[i] = new ResourceLocation("bullseye:textures/entity/projectile/" + ItemDyeArrow.DyeType.values()[i].getName() + ".png");
        }
    }

    public void doRender(EntityDyeArrow arrow, double x, double y, double z, float entityYaw, float partialTicks)
    {
        this.bindEntityTexture(arrow);
        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
        GlStateManager.pushMatrix();
        GlStateManager.disableLighting();
        GlStateManager.translate((float)x, (float)y, (float)z);
        GlStateManager.rotate(arrow.prevRotationYaw + (arrow.rotationYaw - arrow.prevRotationYaw) * partialTicks - 90.0F, 0.0F, 1.0F, 0.0F);
        GlStateManager.rotate(arrow.prevRotationPitch + (arrow.rotationPitch - arrow.prevRotationPitch) * partialTicks, 0.0F, 0.0F, 1.0F);
        Tessellator tessellator = Tessellator.getInstance();
        VertexBuffer vertexbuffer = tessellator.getBuffer();
        int i = 0;
        float f = 0.0F;
        float f1 = 0.5F;
        float f2 = (float)(0 + i * 10) / 32.0F;
        float f3 = (float)(5 + i * 10) / 32.0F;
        float f4 = 0.0F;
        float f5 = 0.15625F;
        float f6 = (float)(5 + i * 10) / 32.0F;
        float f7 = (float)(10 + i * 10) / 32.0F;
        float f8 = 0.05625F;
        GlStateManager.enableRescaleNormal();
        float f9 = (float)arrow.arrowShake - partialTicks;

        if (f9 > 0.0F)
        {
            float f10 = -MathHelper.sin(f9 * 3.0F) * f9;
            GlStateManager.rotate(f10, 0.0F, 0.0F, 1.0F);
        }

        GlStateManager.rotate(45.0F, 1.0F, 0.0F, 0.0F);
        GlStateManager.scale(f8, f8, f8);
        GlStateManager.translate(-4.0F, 0.0F, 0.0F);

        if (this.renderOutlines)
        {
            GlStateManager.enableColorMaterial();
            GlStateManager.enableOutlineMode(this.getTeamColor(arrow));
        }

        GlStateManager.glNormal3f(f8, 0.0F, 0.0F);
        vertexbuffer.begin(7, DefaultVertexFormats.POSITION_TEX);
        vertexbuffer.pos(-7.0D, -2.0D, -2.0D).tex((double)f4, (double)f6).endVertex();
        vertexbuffer.pos(-7.0D, -2.0D, 2.0D).tex((double)f5, (double)f6).endVertex();
        vertexbuffer.pos(-7.0D, 2.0D, 2.0D).tex((double)f5, (double)f7).endVertex();
        vertexbuffer.pos(-7.0D, 2.0D, -2.0D).tex((double)f4, (double)f7).endVertex();
        tessellator.draw();
        GlStateManager.glNormal3f(-f8, 0.0F, 0.0F);
        vertexbuffer.begin(7, DefaultVertexFormats.POSITION_TEX);
        vertexbuffer.pos(-7.0D, 2.0D, -2.0D).tex((double)f4, (double)f6).endVertex();
        vertexbuffer.pos(-7.0D, 2.0D, 2.0D).tex((double)f5, (double)f6).endVertex();
        vertexbuffer.pos(-7.0D, -2.0D, 2.0D).tex((double)f5, (double)f7).endVertex();
        vertexbuffer.pos(-7.0D, -2.0D, -2.0D).tex((double)f4, (double)f7).endVertex();
        tessellator.draw();

        for (int j = 0; j < 4; ++j)
        {
            GlStateManager.rotate(90.0F, 1.0F, 0.0F, 0.0F);
            GlStateManager.glNormal3f(0.0F, 0.0F, f8);
            vertexbuffer.begin(7, DefaultVertexFormats.POSITION_TEX);
            vertexbuffer.pos(-8.0D, -2.0D, 0.0D).tex((double)f, (double)f2).endVertex();
            vertexbuffer.pos(8.0D, -2.0D, 0.0D).tex((double)f1, (double)f2).endVertex();
            vertexbuffer.pos(8.0D, 2.0D, 0.0D).tex((double)f1, (double)f3).endVertex();
            vertexbuffer.pos(-8.0D, 2.0D, 0.0D).tex((double)f, (double)f3).endVertex();
            tessellator.draw();
        }

        if (this.renderOutlines)
        {
            GlStateManager.disableOutlineMode();
            GlStateManager.disableColorMaterial();
        }

        GlStateManager.disableRescaleNormal();
        GlStateManager.enableLighting();
        GlStateManager.popMatrix();
        super.doRender(arrow, x, y, z, entityYaw, partialTicks);
    }

    @Override
    protected ResourceLocation getEntityTexture(EntityDyeArrow arrow)
    {
        return this.textures[arrow.getDyeType().ordinal()];
    }
}