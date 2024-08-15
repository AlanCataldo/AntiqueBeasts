package net.mebahel.antiquebeasts.entity.client.custom;

import com.google.common.collect.Maps;
import net.mebahel.antiquebeasts.AntiqueBeasts;
import net.mebahel.antiquebeasts.entity.custom.CyclopsEntity;
import net.mebahel.antiquebeasts.entity.variant.CyclopsVariant;
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

public class CyclopsRenderer extends GeoEntityRenderer<CyclopsEntity> {
    public static final Map<CyclopsVariant, Identifier> LOCATION_BY_VARIANT =
            Util.make(Maps.newEnumMap(CyclopsVariant.class), (map) -> {
                map.put(CyclopsVariant.DEFAULT,
                        new Identifier(AntiqueBeasts.MOD_ID, "textures/entity/cyclops_texture.png"));
                map.put(CyclopsVariant.CLOAK,
                        new Identifier(AntiqueBeasts.MOD_ID, "textures/entity/cyclops_texture.png"));
            });

    public CyclopsRenderer(EntityRendererFactory.Context renderManager) {
        super(renderManager, new CyclopsModel());
        this.shadowRadius = 1f;
    }

    @Override
    public RenderLayer getRenderType(CyclopsEntity animatable, float partialTick, MatrixStack poseStack,
                                     @Nullable VertexConsumerProvider bufferSource, @Nullable VertexConsumer buffer,
                                     int packedLight, Identifier texture) {

        poseStack.scale(1.5f, 1.5f, 1.5f);
        return super.getRenderType(animatable, partialTick, poseStack, bufferSource, buffer, packedLight, texture);
    }

}
