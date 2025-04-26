package net.mebahel.antiquebeasts.entity.client.custom;

import com.google.common.collect.Maps;
import net.mebahel.antiquebeasts.AntiqueBeasts;
import net.mebahel.antiquebeasts.entity.custom.CyclopsEntity;
import net.mebahel.antiquebeasts.entity.custom.egyptian.ElephantRiderEntity;
import net.mebahel.antiquebeasts.entity.custom.greek.EliteHopliteEntity;
import net.mebahel.antiquebeasts.entity.variant.EliteHopliteVariant;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;
import net.minecraft.util.Util;
import org.jetbrains.annotations.Nullable;
import software.bernie.geckolib.cache.object.BakedGeoModel;
import software.bernie.geckolib.renderer.GeoEntityRenderer;

import java.util.Map;

public class EliteHopliteRenderer extends GeoEntityRenderer<EliteHopliteEntity> {
    public static final Map<EliteHopliteVariant, Identifier> LOCATION_BY_VARIANT =
            Util.make(Maps.newEnumMap(EliteHopliteVariant.class), (map) -> {
                map.put(EliteHopliteVariant.DEFAULT,
                        new Identifier(AntiqueBeasts.MOD_ID, "textures/entity/elite_hoplite_1.png"));
                map.put(EliteHopliteVariant.CLOAK,
                        new Identifier(AntiqueBeasts.MOD_ID, "textures/entity/elite_hoplite_2.png"));
            });
    public EliteHopliteRenderer(EntityRendererFactory.Context renderManager) {
        super(renderManager, new EliteHopliteModel());
        this.shadowRadius = 0.5f;
    }

    @Override
    public RenderLayer getRenderType(EliteHopliteEntity animatable, Identifier texture, @Nullable VertexConsumerProvider bufferSource, float partialTick) {

        return super.getRenderType(animatable, texture, bufferSource, partialTick);
    }

    public void preRender(MatrixStack poseStack, EliteHopliteEntity animatable, BakedGeoModel model, VertexConsumerProvider bufferSource, VertexConsumer buffer, boolean isReRender, float partialTick, int packedLight, int packedOverlay, float red, float green, float blue,
                          float alpha) {
        poseStack.scale(0.88f, 0.88f, 0.88f);
        super.preRender(poseStack, animatable, model, bufferSource, buffer, isReRender, partialTick, packedLight, packedOverlay, red, green, blue, alpha);
    }
    @Override
    public float getMotionAnimThreshold(EliteHopliteEntity animatable) {
        return 0.008F;
    }
}
