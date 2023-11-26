package net.mebahel.antiquebeasts.entity.client.projectiles;

import net.mebahel.antiquebeasts.AntiqueBeasts;
import net.mebahel.antiquebeasts.entity.projectiles.ThrowingRockEntity;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.Nullable;
import software.bernie.geckolib.renderer.GeoEntityRenderer;

public class ThrowingRockRenderer extends GeoEntityRenderer<ThrowingRockEntity> {

    public ThrowingRockRenderer(EntityRendererFactory.Context renderManager) {
        super(renderManager, new ThrowingRockModel());
    }

    @Override
    public Identifier getTextureLocation(ThrowingRockEntity animatable) {
        return new Identifier(AntiqueBeasts.MOD_ID, "textures/entity/cyclops_texture.png");
    }

    @Override
    public RenderLayer getRenderType(ThrowingRockEntity animatable, Identifier texture, @Nullable VertexConsumerProvider bufferSource, float partialTick) {
        return super.getRenderType(animatable, texture, bufferSource, partialTick);
    }

}

