package net.mebahel.antiquebeasts.util;

import com.mojang.serialization.Codec;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.fluid.FluidState;
import net.minecraft.fluid.Fluids;
import net.minecraft.state.property.Properties;
import net.minecraft.structure.StructurePlacementData;
import net.minecraft.structure.StructureTemplate;
import net.minecraft.structure.processor.StructureProcessor;
import net.minecraft.structure.processor.StructureProcessorType;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.WorldView;


public class OldWaterRemovalProcessor extends StructureProcessor {

    public static final Codec<OldWaterRemovalProcessor> CODEC = Codec.unit(OldWaterRemovalProcessor::new);

    public OldWaterRemovalProcessor() {}

    @Override
    public StructureTemplate.StructureBlockInfo process(
            WorldView world,
            BlockPos pos,
            BlockPos pivot,
            StructureTemplate.StructureBlockInfo originalBlockInfo,
            StructureTemplate.StructureBlockInfo currentBlockInfo,
            StructurePlacementData placementData) {

        BlockState blockState = currentBlockInfo.state();
        FluidState fluidState = world.getFluidState(currentBlockInfo.pos());

        // Vérifie si le bloc est immergé et waterloggable
        if (blockState.contains(Properties.WATERLOGGED) && fluidState.getFluid() == Fluids.WATER) {
            // Vérifie que world est bien un accès vers un monde en lecture (ServerWorldView implémente ça)
            if (world instanceof net.minecraft.server.world.ServerWorld serverWorld) {
                ChunkPos chunkPos = new ChunkPos(currentBlockInfo.pos());

                // Vérifie si le chunk est chargé ET généré
                if (serverWorld.isChunkLoaded(chunkPos.x, chunkPos.z)) {
                    serverWorld.setBlockState(currentBlockInfo.pos(), Blocks.AIR.getDefaultState(), 2);
                }
            }
        }

        return currentBlockInfo;
    }

    @Override
    protected StructureProcessorType<?> getType() {
        return MyProcessors.WATER_REMOVAL_PROCESSOR;
    }
}
