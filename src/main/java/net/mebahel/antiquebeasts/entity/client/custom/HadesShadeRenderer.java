package net.mebahel.antiquebeasts.entity.client.custom;

import net.mebahel.antiquebeasts.AntiqueBeasts;
import net.mebahel.antiquebeasts.entity.custom.HadesShadeEntity;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.Nullable;
import software.bernie.geckolib.renderer.GeoEntityRenderer;
import software.bernie.geckolib.renderer.layer.AutoGlowingGeoLayer;

public class HadesShadeRenderer extends GeoEntityRenderer<HadesShadeEntity> {
    public HadesShadeRenderer(EntityRendererFactory.Context renderManager) {
        super(renderManager, new HadesShadeModel());
        this.shadowRadius = 0.25f;
        this.addRenderLayer(new AutoGlowingGeoLayer<>(this));
    }
    @Override
    public Identifier getTextureLocation(HadesShadeEntity animatable) {
        return new Identifier(AntiqueBeasts.MOD_ID, "textures/entity/hades_shade_texture.png");
    }
    @Override
    public RenderLayer getRenderType(HadesShadeEntity animatable, Identifier texture, @Nullable VertexConsumerProvider bufferSource, float partialTick) {
        return super.getRenderType(animatable, texture, bufferSource, partialTick);
    }
}
