package net.mebahel.antiquebeasts.entity.client.custom;

import net.mebahel.antiquebeasts.AntiqueBeasts;
import net.mebahel.antiquebeasts.entity.custom.greek.ChampionHopliteEntity;
import net.minecraft.util.Identifier;
import software.bernie.geckolib.constant.DataTickets;
import software.bernie.geckolib.core.animatable.model.CoreGeoBone;
import software.bernie.geckolib.core.animation.AnimationState;
import software.bernie.geckolib.model.GeoModel;
import software.bernie.geckolib.model.data.EntityModelData;

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
        return new Identifier(AntiqueBeasts.MOD_ID, "animations/hoplite.animation.json");
    }
    @Override
    public void setCustomAnimations(ChampionHopliteEntity entity, long uniqueID, AnimationState<ChampionHopliteEntity> customPredicate) {
        String currentAnimation = entity.getCurrentAnimation();

        if (!"idle3".equals(currentAnimation)) {
            CoreGeoBone head = this.getBone("Head").orElse(null);
            EntityModelData extraData = customPredicate.getData(DataTickets.ENTITY_MODEL_DATA);
            if (head != null) {
                // ✅ Appliquer la rotation du joueur SEULEMENT si ce n'est PAS idle3
                float yawAngle = 0.125F * extraData.netHeadYaw() * 0.017453292F;
                float pitchAngle = 0.125F * extraData.headPitch() * 0.017453292F;

                if (Math.abs(yawAngle) > 0.6F) {
                    yawAngle = 0.0F;
                }

                head.setRotY(7.0F * yawAngle);
                head.setRotX(7.0F * pitchAngle);
            }
        }
    }
}