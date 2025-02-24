package net.mebahel.antiquebeasts.entity.client.custom;

import com.google.common.collect.Maps;
import net.mebahel.antiquebeasts.AntiqueBeasts;
import net.mebahel.antiquebeasts.entity.custom.HadesShadeEntity;
import net.mebahel.antiquebeasts.entity.custom.greek.HeroHopliteEntity;
import net.mebahel.antiquebeasts.entity.variant.HeroHopliteVariant;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.util.Identifier;
import net.minecraft.util.Util;
import org.jetbrains.annotations.Nullable;
import software.bernie.geckolib.renderer.GeoEntityRenderer;

import java.util.Map;

public class HeroHopliteRenderer extends GeoEntityRenderer<HeroHopliteEntity> {
    public static final Map<HeroHopliteVariant, Identifier> LOCATION_BY_VARIANT =
            Util.make(Maps.newEnumMap(HeroHopliteVariant.class), (map) -> {
                map.put(HeroHopliteVariant.DEFAULT,
                        new Identifier(AntiqueBeasts.MOD_ID, "textures/entity/hero_hoplite_1.png"));
                map.put(HeroHopliteVariant.CLOAK,
                        new Identifier(AntiqueBeasts.MOD_ID, "textures/entity/hero_hoplite_1.png"));
            });
    public HeroHopliteRenderer(EntityRendererFactory.Context renderManager) {
        super(renderManager, new HeroHopliteModel());
        this.shadowRadius = 0.5f;
    }

    @Override
    public Identifier getTextureLocation(HeroHopliteEntity animatable) {
        return LOCATION_BY_VARIANT.get(animatable.getVariant());
    }
    @Override
    public RenderLayer getRenderType(HeroHopliteEntity animatable, Identifier texture, @Nullable VertexConsumerProvider bufferSource, float partialTick) {
        return super.getRenderType(animatable, texture, bufferSource, partialTick);
    }
    @Override
    public float getMotionAnimThreshold(HeroHopliteEntity animatable) {
        return 0.008F;
    }
}
