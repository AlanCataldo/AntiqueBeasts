package net.mebahel.antiquebeasts.entity.client.custom.dwemer_spider_guardian;

import net.mebahel.antiquebeasts.entity.custom.dwemer.DwemerSpiderGuardianEntity;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.Nullable;
import software.bernie.geckolib.renderer.GeoEntityRenderer;

public class DwemerSpiderGuardianRenderer extends GeoEntityRenderer<DwemerSpiderGuardianEntity> {

    public DwemerSpiderGuardianRenderer(EntityRendererFactory.Context renderManager) {
        super(renderManager, new DwemerSpidersGuardianModel());
        this.shadowRadius = 0.32f;
    }

    @Override
    public RenderLayer getRenderType(DwemerSpiderGuardianEntity animatable, Identifier texture, @Nullable VertexConsumerProvider bufferSource, float partialTick) {
        return super.getRenderType(animatable, texture, bufferSource, partialTick);
    }

    @Override
    public float getMotionAnimThreshold(DwemerSpiderGuardianEntity animatable) {
        return 0.008F;
    }
}
