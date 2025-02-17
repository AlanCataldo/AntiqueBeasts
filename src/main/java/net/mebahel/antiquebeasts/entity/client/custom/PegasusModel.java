package net.mebahel.antiquebeasts.entity.client.custom;

import com.google.common.collect.Maps;
import net.mebahel.antiquebeasts.AntiqueBeasts;
import net.mebahel.antiquebeasts.entity.custom.ChimeraEntity;
import net.mebahel.antiquebeasts.entity.custom.HadesShadeEntity;
import net.mebahel.antiquebeasts.entity.custom.PegasusEntity;
import net.mebahel.antiquebeasts.entity.variant.ChimeraVariant;
import net.mebahel.antiquebeasts.entity.variant.PegasusVariant;
import net.minecraft.util.Identifier;
import net.minecraft.util.Util;
import software.bernie.geckolib.constant.DataTickets;
import software.bernie.geckolib.core.animatable.model.CoreGeoBone;
import software.bernie.geckolib.core.animation.AnimationState;
import software.bernie.geckolib.model.GeoModel;
import software.bernie.geckolib.model.data.EntityModelData;

import java.util.Map;

public class PegasusModel extends GeoModel<PegasusEntity> {
    public static final Map<PegasusVariant, Identifier> LOCATION_BY_VARIANT =
            Util.make(Maps.newEnumMap(PegasusVariant.class), (map) -> {
                map.put(PegasusVariant.DEFAULT,
                        new Identifier(AntiqueBeasts.MOD_ID, "textures/entity/pegasus_texture.png"));
                map.put(PegasusVariant.CLOAK,
                        new Identifier(AntiqueBeasts.MOD_ID, "textures/entity/pegasus_saddle_texture.png"));
                map.put(PegasusVariant.UNMOUNT,
                        new Identifier(AntiqueBeasts.MOD_ID, "textures/entity/pegasus_saddle_unmount_texture.png"));
                map.put(PegasusVariant.LEATHER_UNMOUNT,
                        new Identifier(AntiqueBeasts.MOD_ID, "textures/entity/pegasus_leather_armor_texture.png"));
                map.put(PegasusVariant.IRON_UNMOUNT,
                        new Identifier(AntiqueBeasts.MOD_ID, "textures/entity/pegasus_iron_armor_texture.png"));
                map.put(PegasusVariant.GOLD_UNMOUNT,
                        new Identifier(AntiqueBeasts.MOD_ID, "textures/entity/pegasus_gold_armor_texture.png"));
                map.put(PegasusVariant.DIAMOND_UNMOUNT,
                        new Identifier(AntiqueBeasts.MOD_ID, "textures/entity/pegasus_diamond_armor_texture.png"));
                map.put(PegasusVariant.LEATHER_SADDLED_UNMOUNTED,
                        new Identifier(AntiqueBeasts.MOD_ID, "textures/entity/pegasus_leather_armor_saddled_unmounted_texture.png"));
                map.put(PegasusVariant.IRON_SADDLED_UNMOUNTED,
                        new Identifier(AntiqueBeasts.MOD_ID, "textures/entity/pegasus_iron_armor_saddled_unmounted_texture.png"));
                map.put(PegasusVariant.GOLD_SADDLED_UNMOUNTED,
                        new Identifier(AntiqueBeasts.MOD_ID, "textures/entity/pegasus_gold_armor_saddled_unmounted_texture.png"));
                map.put(PegasusVariant.DIAMOND_SADDLED_UNMOUNTED,
                        new Identifier(AntiqueBeasts.MOD_ID, "textures/entity/pegasus_diamond_armor_saddled_unmounted_texture.png"));
                map.put(PegasusVariant.LEATHER_SADDLED_MOUNTED,
                        new Identifier(AntiqueBeasts.MOD_ID, "textures/entity/pegasus_leather_armor_saddled_mounted_texture.png"));
                map.put(PegasusVariant.IRON_SADDLED_MOUNTED,
                        new Identifier(AntiqueBeasts.MOD_ID, "textures/entity/pegasus_iron_armor_saddled_mounted_texture.png"));
                map.put(PegasusVariant.GOLD_SADDLED_MOUNTED,
                        new Identifier(AntiqueBeasts.MOD_ID, "textures/entity/pegasus_gold_armor_saddled_mounted_texture.png"));
                map.put(PegasusVariant.DIAMOND_SADDLED_MOUNTED,
                        new Identifier(AntiqueBeasts.MOD_ID, "textures/entity/pegasus_diamond_armor_saddled_mounted_texture.png"));
            });
    @Override
    public Identifier getModelResource(PegasusEntity object) {
        return new Identifier(AntiqueBeasts.MOD_ID, "geo/pegasus.geo.json");
    }
    public Identifier getTextureResource(PegasusEntity animatable) {
        return LOCATION_BY_VARIANT.get(animatable.getPegasusVariant());
    }
    @Override
    public Identifier getAnimationResource(PegasusEntity animatable) {
        return new Identifier(AntiqueBeasts.MOD_ID, "animations/pegasus.animation.json");
    }
    @Override
    public void setCustomAnimations(PegasusEntity entity, long uniqueID, AnimationState<PegasusEntity> customPredicate) {
        float growthFactor = entity.getDataTracker().get(PegasusEntity.SCALE_TRACKER);
        this.getBone("horse").ifPresent(body -> {
            body.setScaleX(growthFactor);
            body.setScaleY(growthFactor);
            body.setScaleZ(growthFactor);
        });


        // ✅ Gestion de la tête
        if (!entity.isAttacking()) {
            CoreGeoBone head = this.getBone("head").orElse(null);
            EntityModelData extraData = customPredicate.getData(DataTickets.ENTITY_MODEL_DATA);
            float yawAngle = 0.125F * extraData.netHeadYaw() * 0.017453292F;
            float pitchAngle = 0.125F * extraData.headPitch() * 0.017453292F;
            if (Math.abs(yawAngle) > 0.6F) {
                yawAngle = 0.0F;
            }
            if (head != null) {
                head.setRotY(3.0F * yawAngle);
                head.setRotX(3.0F * pitchAngle);
            }
        }
    }

}