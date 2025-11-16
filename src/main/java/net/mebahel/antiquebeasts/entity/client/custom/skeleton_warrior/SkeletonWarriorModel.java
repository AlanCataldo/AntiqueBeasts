package net.mebahel.antiquebeasts.entity.client.custom.skeleton_warrior;

import com.google.common.collect.Maps;
import net.mebahel.antiquebeasts.AntiqueBeasts;
import net.mebahel.antiquebeasts.entity.custom.other.DraugrWightEntity;
import net.mebahel.antiquebeasts.entity.custom.other.SkeletonWarriorEntity;
import net.mebahel.antiquebeasts.entity.variant.DraugrWightVariant;
import net.mebahel.antiquebeasts.entity.variant.SkeletonWarriorVariant;
import net.minecraft.util.Identifier;
import net.minecraft.util.Util;
import software.bernie.geckolib.constant.DataTickets;
import software.bernie.geckolib.core.animatable.model.CoreGeoBone;
import software.bernie.geckolib.core.animation.AnimationState;
import software.bernie.geckolib.model.GeoModel;
import software.bernie.geckolib.model.data.EntityModelData;

import java.util.Map;

public class SkeletonWarriorModel extends GeoModel<SkeletonWarriorEntity> {
    public static final Map<SkeletonWarriorVariant, Identifier> LOCATION_BY_VARIANT =
            Util.make(Maps.newEnumMap(SkeletonWarriorVariant.class), (map) -> {
                map.put(SkeletonWarriorVariant.ONE,
                        new Identifier(AntiqueBeasts.MOD_ID, "textures/entity/skeleton_warrior/skeleton_warrior.png"));
                map.put(SkeletonWarriorVariant.TWO,
                        new Identifier(AntiqueBeasts.MOD_ID, "textures/entity/skeleton_warrior/skeleton_warrior_2.png"));
                map.put(SkeletonWarriorVariant.THREE,
                        new Identifier(AntiqueBeasts.MOD_ID, "textures/entity/skeleton_warrior/skeleton_warrior_3.png"));
            });
    @Override
    public Identifier getModelResource(SkeletonWarriorEntity object) {
        return new Identifier(AntiqueBeasts.MOD_ID, "geo/skeleton_warrior.geo.json");
    }

    @Override
    public Identifier getTextureResource(SkeletonWarriorEntity animatable) {
        return LOCATION_BY_VARIANT.get(animatable.getSkeletonWarriorVariant());
    }
    @Override
    public Identifier getAnimationResource(SkeletonWarriorEntity animatable) {
        return new Identifier(AntiqueBeasts.MOD_ID, "animations/skeleton_warrior.animation.json");
    }
    @Override
    public void setCustomAnimations(SkeletonWarriorEntity entity, long uniqueID, AnimationState<SkeletonWarriorEntity> customPredicate) {
        CoreGeoBone head2 = this.getBone("Head").orElse(null);
        EntityModelData extraData = customPredicate.getData(DataTickets.ENTITY_MODEL_DATA);
        float yawAngle = 0.125F * extraData.netHeadYaw() * 0.017453292F;
        float pitchAngle = 0.125F * extraData.headPitch() * 0.017453292F;
        if (Math.abs(yawAngle) > 0.6F) {
            yawAngle = 0.0F;
        }
        if (head2 != null) {
            head2.setRotY(4.0F * yawAngle);
            head2.setRotX(4.0F * pitchAngle);
        }
    }

}