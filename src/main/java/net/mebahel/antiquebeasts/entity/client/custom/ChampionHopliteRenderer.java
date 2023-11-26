package net.mebahel.antiquebeasts.entity.client.custom;

import com.google.common.collect.Maps;
import net.mebahel.antiquebeasts.AntiqueBeasts;
import net.mebahel.antiquebeasts.entity.custom.ChampionHopliteEntity;
import net.mebahel.antiquebeasts.entity.variant.ChampionHopliteVariant;
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

public class ChampionHopliteRenderer extends GeoEntityRenderer<ChampionHopliteEntity> {
    public static final Map<ChampionHopliteVariant, Identifier> LOCATION_BY_VARIANT =
            Util.make(Maps.newEnumMap(ChampionHopliteVariant.class), (map) -> {
                map.put(ChampionHopliteVariant.DEFAULT,
                        new Identifier(AntiqueBeasts.MOD_ID, "textures/entity/champion_hoplite_1.png"));
                map.put(ChampionHopliteVariant.CLOAK,
                        new Identifier(AntiqueBeasts.MOD_ID, "textures/entity/champion_hoplite_2.png"));
            });
    public ChampionHopliteRenderer(EntityRendererFactory.Context renderManager) {
        super(renderManager, new ChampionHopliteModel());
        this.shadowRadius = 0.5f;
    }

    @Override
    public Identifier getTextureLocation(ChampionHopliteEntity animatable) {
        return LOCATION_BY_VARIANT.get(animatable.getVariant());
    }
    @Override
    public RenderLayer getRenderType(ChampionHopliteEntity animatable, float partialTick, MatrixStack poseStack,
                                     @Nullable VertexConsumerProvider bufferSource, @Nullable VertexConsumer buffer,
                                     int packedLight, Identifier texture) {

        poseStack.scale(1f, 1f, 1f);
        return super.getRenderType(animatable, partialTick, poseStack, bufferSource, buffer, packedLight, texture);
    }

}
