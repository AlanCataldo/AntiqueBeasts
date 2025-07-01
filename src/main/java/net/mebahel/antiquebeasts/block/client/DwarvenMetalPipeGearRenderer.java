package net.mebahel.antiquebeasts.block.client;

import net.mebahel.antiquebeasts.block.entity.DwarvenMetalPipeGearBlockEntity;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.block.entity.BlockEntityRendererFactory;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.Nullable;
import software.bernie.geckolib.renderer.GeoBlockRenderer;

public class DwarvenMetalPipeGearRenderer extends GeoBlockRenderer<DwarvenMetalPipeGearBlockEntity> {
    public DwarvenMetalPipeGearRenderer(BlockEntityRendererFactory.Context context) {
        super(new DwarvenMetalPipeGearModel());
    }

    @Override
    public RenderLayer getRenderType(DwarvenMetalPipeGearBlockEntity animatable, Identifier texture, @Nullable VertexConsumerProvider bufferSource, float partialTick) {
        return super.getRenderType(animatable, texture, bufferSource, partialTick);
    }
}

