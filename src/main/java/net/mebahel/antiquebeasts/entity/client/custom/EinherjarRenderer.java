package net.mebahel.antiquebeasts.entity.client.custom;

import com.google.common.collect.Maps;
import net.mebahel.antiquebeasts.AntiqueBeasts;

import net.mebahel.antiquebeasts.entity.custom.norse.EinherjarEntity;
import net.mebahel.antiquebeasts.entity.variant.EinherjarVariant;
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

public class EinherjarRenderer extends GeoEntityRenderer<EinherjarEntity> {
    public static final Map<EinherjarVariant, Identifier> LOCATION_BY_VARIANT =
            Util.make(Maps.newEnumMap(EinherjarVariant.class), (map) -> {
                map.put(EinherjarVariant.DEFAULT,
                        new Identifier(AntiqueBeasts.MOD_ID, "textures/entity/einherjar_texture.png"));
                map.put(EinherjarVariant.CLOAK,
                        new Identifier(AntiqueBeasts.MOD_ID, "textures/entity/einherjar_texture.png"));
            });
    public EinherjarRenderer(EntityRendererFactory.Context renderManager) {
        super(renderManager, new EinherjarModel());
        this.shadowRadius = 0.5f;
    }

    @Override
    public Identifier getTextureResource(EinherjarEntity animatable) {
        return LOCATION_BY_VARIANT.get(animatable.getVariant());
    }

    @Override
    public RenderLayer getRenderType(EinherjarEntity animatable, float partialTick, MatrixStack poseStack,
                                     @Nullable VertexConsumerProvider bufferSource, @Nullable VertexConsumer buffer,
                                     int packedLight, Identifier texture) {

        poseStack.scale(1f, 1f, 1f);
        return super.getRenderType(animatable, partialTick, poseStack, bufferSource, buffer, packedLight, texture);
    }

}
