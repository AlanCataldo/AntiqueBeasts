package net.mebahel.antiquebeasts.entity.client.custom;

import net.mebahel.antiquebeasts.AntiqueBeasts;
import net.mebahel.antiquebeasts.entity.custom.HadesChosenEntity;
import net.minecraft.client.MinecraftClient;
import net.minecraft.util.Identifier;
import software.bernie.geckolib.model.GeoModel;

public class HadesChosenModel extends GeoModel<HadesChosenEntity> {
    @Override
    public Identifier getModelResource(HadesChosenEntity object) {
        return new Identifier(AntiqueBeasts.MOD_ID, "geo/hades_chosen.geo.json");
    }
    @Override
    public Identifier getTextureResource(HadesChosenEntity object) {
        return new Identifier(AntiqueBeasts.MOD_ID, "textures/entity/hades_chosen_texture.png");
    }
    @Override
    public Identifier getAnimationResource(HadesChosenEntity animatable) {
        return new Identifier(AntiqueBeasts.MOD_ID, "animations/champion_hoplite.animation.json");
    }
}