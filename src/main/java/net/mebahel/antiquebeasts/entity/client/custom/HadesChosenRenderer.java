package net.mebahel.antiquebeasts.entity.client.custom;

import net.mebahel.antiquebeasts.AntiqueBeasts;
import net.mebahel.antiquebeasts.entity.custom.FrostCyclopsEntity;
import net.mebahel.antiquebeasts.entity.custom.HadesChosenEntity;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.Nullable;
import software.bernie.geckolib.renderer.GeoEntityRenderer;


import java.util.Map;

public class HadesChosenRenderer extends GeoEntityRenderer<HadesChosenEntity> {
    public HadesChosenRenderer(EntityRendererFactory.Context renderManager) {
        super(renderManager, new HadesChosenModel());
        this.shadowRadius = 0.25f;
    }
    @Override
    public Identifier getTextureLocation(HadesChosenEntity animatable) {
        return new Identifier(AntiqueBeasts.MOD_ID, "textures/entity/hades_chosen_texture.png");
    }
    @Override
    public RenderLayer getRenderType(HadesChosenEntity animatable, Identifier texture, @Nullable VertexConsumerProvider bufferSource, float partialTick) {
        return super.getRenderType(animatable, texture, bufferSource, partialTick);
    }

}
