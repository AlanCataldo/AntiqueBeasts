package net.mebahel.antiquebeasts.entity.client.custom.dwarven_spider_guardian;

import net.mebahel.antiquebeasts.entity.custom.dwarven.DwarvenSpiderGuardianEntity;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.Nullable;
import software.bernie.geckolib.renderer.GeoEntityRenderer;

public class DwarvenSpiderGuardianRenderer extends GeoEntityRenderer<DwarvenSpiderGuardianEntity> {

    public DwarvenSpiderGuardianRenderer(EntityRendererFactory.Context renderManager) {
        super(renderManager, new DwarvenSpidersGuardianModel());
        this.shadowRadius = 0.32f;
    }

    @Override
    public RenderLayer getRenderType(DwarvenSpiderGuardianEntity animatable, Identifier texture, @Nullable VertexConsumerProvider bufferSource, float partialTick) {
        return super.getRenderType(animatable, texture, bufferSource, partialTick);
    }

    @Override
    public float getMotionAnimThreshold(DwarvenSpiderGuardianEntity animatable) {
        return 0.008F;
    }
}
