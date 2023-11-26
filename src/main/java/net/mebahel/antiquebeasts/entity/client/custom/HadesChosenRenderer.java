package net.mebahel.antiquebeasts.entity.client.custom;

import com.google.common.collect.Maps;
import net.mebahel.antiquebeasts.AntiqueBeasts;
import net.mebahel.antiquebeasts.entity.custom.HadesChosenEntity;
import net.mebahel.antiquebeasts.entity.custom.HadesShadeEntity;
import net.mebahel.antiquebeasts.entity.custom.HeroHopliteEntity;
import net.mebahel.antiquebeasts.entity.variant.HeroHopliteVariant;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;
import net.minecraft.util.Util;
import org.jetbrains.annotations.Nullable;
import software.bernie.geckolib3.renderers.geo.GeoEntityRenderer;

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
    public RenderLayer getRenderType(HadesChosenEntity animatable, float partialTick, MatrixStack poseStack,
                                     @Nullable VertexConsumerProvider bufferSource, @Nullable VertexConsumer buffer,
                                     int packedLight, Identifier texture) {

        poseStack.scale(1f, 1f, 1f);
        return super.getRenderType(animatable, partialTick, poseStack, bufferSource, buffer, packedLight, texture);
    }
}
