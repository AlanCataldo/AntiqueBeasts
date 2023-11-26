package net.mebahel.antiquebeasts.world.structure;

import net.minecraft.structure.*;
import net.minecraft.structure.pool.StructurePoolBasedGenerator;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.Heightmap;
import net.minecraft.world.gen.feature.StructureFeature;
import net.minecraft.world.gen.feature.StructurePoolFeatureConfig;

import java.util.Optional;
import java.util.Random;

public class TinyCyclopsCave extends StructureFeature<StructurePoolFeatureConfig> {

    public TinyCyclopsCave() {
        super(StructurePoolFeatureConfig.CODEC, TinyCyclopsCave::createPiecesGenerator, PostPlacementProcessor.EMPTY);
    }
    private static boolean isFeatureChunk(StructureGeneratorFactory.Context<StructurePoolFeatureConfig> context) {
        ChunkPos chunkpos = context.chunkPos();
        return !context.chunkGenerator().method_41053(StructureSetKeys.OCEAN_MONUMENTS, context.seed(), chunkpos.x, chunkpos.z, 10);
    }

    public static Optional<StructurePiecesGenerator<StructurePoolFeatureConfig>> createPiecesGenerator(StructureGeneratorFactory.Context<StructurePoolFeatureConfig> context) {
        if (!TinyCyclopsCave.isFeatureChunk(context)) {
            return Optional.empty();
        }
        int minHeight = -60;
        int maxHeight = -35;

        ChunkPos chunkPos = context.chunkPos();
        Random random = new Random();


        int x = random.nextInt(chunkPos.getEndX() - chunkPos.getStartX()) + chunkPos.getStartX();
        int z = random.nextInt(chunkPos.getEndZ() - chunkPos.getStartZ()) + chunkPos.getStartZ();
        int y = random.nextInt(maxHeight - minHeight) + minHeight;

        Optional<StructurePiecesGenerator<StructurePoolFeatureConfig>> structurePiecesGenerator =
                StructurePoolBasedGenerator.generate(
                        context,
                        PoolStructurePiece::new,
                        new BlockPos(x, y, z),
                        false,
                        false
                );

        return structurePiecesGenerator;
    }
}
