package net.mebahel.antiquebeasts.block.client;


import net.mebahel.antiquebeasts.AntiqueBeasts;
import net.mebahel.antiquebeasts.block.entity.GreekChestBlockEntity;
import net.minecraft.util.Identifier;
import software.bernie.geckolib.model.GeoModel;

public class GreekChestModel extends GeoModel<GreekChestBlockEntity> {

    @Override
    public Identifier getModelResource(GreekChestBlockEntity animatable) {
        return new Identifier(AntiqueBeasts.MOD_ID, "geo/greek_chest.geo.json");
    }

    @Override
    public Identifier getTextureResource(GreekChestBlockEntity animatable) {
        return new Identifier(AntiqueBeasts.MOD_ID, "textures/block/greek_chest_texture.png");
    }

    @Override
    public Identifier getAnimationResource(GreekChestBlockEntity animatable) {
        return new Identifier(AntiqueBeasts.MOD_ID, "animations/draugr_chest.animation.json");
    }
}
