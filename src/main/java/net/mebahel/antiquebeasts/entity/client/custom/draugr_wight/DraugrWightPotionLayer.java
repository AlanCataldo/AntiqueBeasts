package net.mebahel.antiquebeasts.entity.client.custom.draugr_wight;

import net.mebahel.antiquebeasts.entity.custom.other.DraugrEntity;
import net.mebahel.antiquebeasts.entity.custom.other.DraugrWightEntity;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.model.json.ModelTransformationMode;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.potion.PotionUtil;
import net.minecraft.potion.Potions;
import net.minecraft.util.math.RotationAxis;
import software.bernie.geckolib.cache.object.GeoBone;
import software.bernie.geckolib.renderer.GeoRenderer;
import software.bernie.geckolib.renderer.layer.BlockAndItemGeoLayer;

public class DraugrWightPotionLayer extends BlockAndItemGeoLayer<DraugrWightEntity> {

    public DraugrWightPotionLayer(GeoRenderer<DraugrWightEntity> renderer) {
        super(renderer);
    }

    @Override
    protected ItemStack getStackForBone(GeoBone bone, DraugrWightEntity animatable) {
        if (!"rightItem".equals(bone.getName())) {
            return ItemStack.EMPTY;
        }

        if (!animatable.isUsingPotion()) {
            return ItemStack.EMPTY;
        }

        if (animatable.getHealTicks() <= 15) {
            return new ItemStack(Items.GLASS_BOTTLE);
        }

        int type = animatable.getPotionType();

        return switch (type) {
            case DraugrEntity.POTION_HEAL ->
                    PotionUtil.setPotion(new ItemStack(Items.POTION), Potions.HEALING);

            case DraugrEntity.POTION_STRENGTH ->
                    PotionUtil.setPotion(new ItemStack(Items.POTION), Potions.STRENGTH);

            case DraugrEntity.POTION_RESISTANCE ->
                    PotionUtil.setPotion(new ItemStack(Items.POTION), Potions.WATER_BREATHING);

            case DraugrEntity.POTION_SPEED ->
                    PotionUtil.setPotion(new ItemStack(Items.POTION), Potions.SWIFTNESS);

            case DraugrEntity.POTION_INVISIBILITY ->
                    PotionUtil.setPotion(new ItemStack(Items.POTION), Potions.INVISIBILITY);

            default -> ItemStack.EMPTY;
        };
    }


    @Override
    protected ModelTransformationMode getTransformTypeForStack(GeoBone bone, ItemStack stack,
                                                               DraugrWightEntity animatable) {
        // Vue main droite "classique"
        return ModelTransformationMode.THIRD_PERSON_RIGHT_HAND;
    }

    @Override
    protected void renderStackForBone(MatrixStack poseStack,
                                      GeoBone bone,
                                      ItemStack stack,
                                      DraugrWightEntity animatable,
                                      VertexConsumerProvider bufferSource,
                                      float partialTick,
                                      int packedLight,
                                      int packedOverlay) {

        poseStack.push();
        // Petit ajustement dans la main
        poseStack.multiply(RotationAxis.POSITIVE_X.rotationDegrees(-90f));
        poseStack.translate(0.0F, -0.05F, 0.0F);
        poseStack.scale(0.75F, 0.75F, 0.75F);

        super.renderStackForBone(poseStack, bone, stack, animatable,
                bufferSource, partialTick, packedLight, packedOverlay);

        poseStack.pop();
    }
}