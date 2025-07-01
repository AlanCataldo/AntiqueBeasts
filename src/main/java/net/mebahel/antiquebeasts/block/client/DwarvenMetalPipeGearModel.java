package net.mebahel.antiquebeasts.block.client;


import net.mebahel.antiquebeasts.AntiqueBeasts;
import net.mebahel.antiquebeasts.block.entity.DwarvenMetalPipeGearBlockEntity;
import net.minecraft.util.Identifier;
import software.bernie.geckolib.model.GeoModel;

public class DwarvenMetalPipeGearModel extends GeoModel<DwarvenMetalPipeGearBlockEntity> {

    @Override
    public Identifier getModelResource(DwarvenMetalPipeGearBlockEntity animatable) {
        return new Identifier(AntiqueBeasts.MOD_ID, "geo/dwemer_metal_pipe_gear.geo.json");
    }

    @Override
    public Identifier getTextureResource(DwarvenMetalPipeGearBlockEntity animatable) {
        return new Identifier(AntiqueBeasts.MOD_ID, "textures/block/dwemer_metal_pipe.png");
    }

    @Override
    public Identifier getAnimationResource(DwarvenMetalPipeGearBlockEntity animatable) {
        return new Identifier(AntiqueBeasts.MOD_ID, "animations/dwemer_metal_pipe_gear.animation.json");
    }
}
