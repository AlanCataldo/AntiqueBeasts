package net.mebahel.antiquebeasts.entity.client.custom;

import net.mebahel.antiquebeasts.AntiqueBeasts;
import net.mebahel.antiquebeasts.entity.custom.HeroHopliteEntity;
import net.minecraft.util.Identifier;
import software.bernie.geckolib.model.GeoModel;

public class HeroHopliteModel extends GeoModel<HeroHopliteEntity> {
    @Override
    public Identifier getModelResource(HeroHopliteEntity object) {
        return new Identifier(AntiqueBeasts.MOD_ID, "geo/hero_hoplite.geo.json");
    }

    @Override
    public Identifier getTextureResource(HeroHopliteEntity object) {
        return HeroHopliteRenderer.LOCATION_BY_VARIANT.get(object.getVariant());
    }

    @Override
    public Identifier getAnimationResource(HeroHopliteEntity animatable) {
        return new Identifier(AntiqueBeasts.MOD_ID, "animations/champion_hoplite.animation.json");
    }
}