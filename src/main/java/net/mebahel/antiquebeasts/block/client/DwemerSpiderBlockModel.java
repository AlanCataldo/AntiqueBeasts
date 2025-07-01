package net.mebahel.antiquebeasts.block.client;

import net.mebahel.antiquebeasts.AntiqueBeasts;
import net.mebahel.antiquebeasts.block.custom.DwemerSpiderBlock;
import net.mebahel.antiquebeasts.block.entity.DwemerSpiderBlockEntity;
import net.minecraft.util.Identifier;
import software.bernie.geckolib.model.GeoModel;

public class DwemerSpiderBlockModel extends GeoModel<DwemerSpiderBlockEntity> {
    @Override
    public Identifier getModelResource(DwemerSpiderBlockEntity object) {
        return new Identifier(AntiqueBeasts.MOD_ID, "geo/dwemer_spider_block.geo.json");
    }
    public Identifier getTextureResource(DwemerSpiderBlockEntity animatable) {
        int state = animatable.getCachedState().get(DwemerSpiderBlock.STATE);

        return switch (state) {
            case 1 -> new Identifier(AntiqueBeasts.MOD_ID,"textures/block/dwemer_spider_block_damaged.png");
            case 2 -> new Identifier(AntiqueBeasts.MOD_ID,"textures/block/dwemer_spider_block_broken.png");
            default -> new Identifier(AntiqueBeasts.MOD_ID,"textures/block/dwemer_spider_block_intact.png");
        };
    }
    @Override
    public Identifier getAnimationResource(DwemerSpiderBlockEntity animatable) {
        return new Identifier(AntiqueBeasts.MOD_ID, "animations/dwarven_spider.animation.json");
    }
}
