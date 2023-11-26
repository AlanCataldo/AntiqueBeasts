package net.mebahel.antiquebeasts.entity.client.custom;

import net.mebahel.antiquebeasts.AntiqueBeasts;
import net.mebahel.antiquebeasts.entity.custom.ChampionHopliteEntity;
import net.minecraft.util.Identifier;
import software.bernie.geckolib.model.GeoModel;

public class ChampionHopliteModel extends GeoModel<ChampionHopliteEntity> {
    @Override
    public Identifier getModelResource(ChampionHopliteEntity object) {
        return new Identifier(AntiqueBeasts.MOD_ID, "geo/champion_hoplite.geo.json");
    }

    @Override
    public Identifier getTextureResource(ChampionHopliteEntity object) {
        return ChampionHopliteRenderer.LOCATION_BY_VARIANT.get(object.getVariant());
    }

    @Override
    public Identifier getAnimationResource(ChampionHopliteEntity animatable) {
        return new Identifier(AntiqueBeasts.MOD_ID, "animations/champion_hoplite.animation.json");
    }
}