package net.mebahel.antiquebeasts.block.client;


import net.mebahel.antiquebeasts.block.entity.DraugrChestBlockEntity;
import net.minecraft.util.Identifier;
import software.bernie.geckolib.model.GeoModel;
import net.mebahel.antiquebeasts.AntiqueBeasts;

public class DraugrChestModel extends GeoModel<DraugrChestBlockEntity> {

    @Override
    public Identifier getModelResource(DraugrChestBlockEntity animatable) {
        return new Identifier(AntiqueBeasts.MOD_ID, "geo/draugr_chest.geo.json");
    }

    @Override
    public Identifier getTextureResource(DraugrChestBlockEntity animatable) {
        return new Identifier(AntiqueBeasts.MOD_ID, "textures/block/draugr_chest_texture.png");
    }

    @Override
    public Identifier getAnimationResource(DraugrChestBlockEntity animatable) {
        return new Identifier(AntiqueBeasts.MOD_ID, "animations/draugr_chest.animation.json");
    }
}
