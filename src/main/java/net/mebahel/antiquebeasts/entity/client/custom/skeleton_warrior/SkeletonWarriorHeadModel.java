package net.mebahel.antiquebeasts.entity.client.custom.skeleton_warrior;

import net.mebahel.antiquebeasts.AntiqueBeasts;
import net.mebahel.antiquebeasts.entity.custom.other.SkeletonWarriorHeadEntity;
import net.minecraft.util.Identifier;
import software.bernie.geckolib.model.GeoModel;

public class SkeletonWarriorHeadModel extends GeoModel<SkeletonWarriorHeadEntity> {
    @Override
    public Identifier getModelResource(SkeletonWarriorHeadEntity object) {
        return new Identifier(AntiqueBeasts.MOD_ID, "geo/skeleton_warrior_head.geo.json");
    }

    @Override
    public Identifier getTextureResource(SkeletonWarriorHeadEntity animatable) {
        return new Identifier(AntiqueBeasts.MOD_ID, "textures/entity/skeleton_warrior/skeleton_warrior.png");
    }
    @Override
    public Identifier getAnimationResource(SkeletonWarriorHeadEntity animatable) {
        return new Identifier(AntiqueBeasts.MOD_ID, "animations/skeleton_warrior_head.animation.json");
    }
}