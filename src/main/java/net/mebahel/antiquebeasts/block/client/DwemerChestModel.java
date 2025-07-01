package net.mebahel.antiquebeasts.block.client;

import net.mebahel.antiquebeasts.AntiqueBeasts;
import net.mebahel.antiquebeasts.block.entity.DwemerChestBlockEntity;
import net.minecraft.util.Identifier;
import software.bernie.geckolib.model.GeoModel;

public class DwemerChestModel extends GeoModel<DwemerChestBlockEntity> {

    @Override
    public Identifier getModelResource(DwemerChestBlockEntity animatable) {
        return new Identifier(AntiqueBeasts.MOD_ID, "geo/dwemer_chest.geo.json");
    }

    @Override
    public Identifier getTextureResource(DwemerChestBlockEntity animatable) {
        return new Identifier(AntiqueBeasts.MOD_ID, "textures/block/dwemer_chest.png");
    }

    @Override
    public Identifier getAnimationResource(DwemerChestBlockEntity animatable) {
        return new Identifier(AntiqueBeasts.MOD_ID, "animations/dwemer_chest.animation.json");
    }
}
