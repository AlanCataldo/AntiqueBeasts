package net.mebahel.antiquebeasts.entity.client.custom;

import net.mebahel.antiquebeasts.AntiqueBeasts;
import net.mebahel.antiquebeasts.entity.custom.EliteHopliteEntity;
import net.minecraft.client.MinecraftClient;
import net.minecraft.util.Identifier;
import software.bernie.geckolib.model.GeoModel;

public class EliteHopliteModel extends GeoModel<EliteHopliteEntity> {
    @Override
    public Identifier getModelResource(EliteHopliteEntity object) {
        return new Identifier(AntiqueBeasts.MOD_ID, "geo/elite_hoplite.geo.json");
    }

    @Override
    public Identifier getTextureResource(EliteHopliteEntity object) {
        return EliteHopliteRenderer.LOCATION_BY_VARIANT.get(object.getVariant());
    }

    @Override
    public Identifier getAnimationResource(EliteHopliteEntity animatable) {
        return new Identifier(AntiqueBeasts.MOD_ID, "animations/champion_hoplite.animation.json");
    }
}