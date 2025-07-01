package net.mebahel.antiquebeasts.util;

import com.mojang.serialization.Codec;
import net.mebahel.antiquebeasts.AntiqueBeasts;
import net.minecraft.structure.StructurePlacementData;
import net.minecraft.structure.StructureTemplate;
import net.minecraft.structure.processor.StructureProcessor;
import net.minecraft.structure.processor.StructureProcessorType;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.world.WorldView;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class WaterRemovalProcessor extends StructureProcessor {

    // Utilisation d'une Map pour stocker les zones déjà traitées avec un hash pour identifier les Box
    private final Map<Integer, Box> scheduledBoxes = new HashMap<>();
    private StructurePlacementData previousPlacementData = null;

    @Override
    public StructureTemplate.StructureBlockInfo process(
            WorldView world,
            BlockPos pos,
            BlockPos pivot,
            StructureTemplate.StructureBlockInfo originalBlockInfo,
            StructureTemplate.StructureBlockInfo currentBlockInfo,
            StructurePlacementData placementData) {

        // Si le placementData a changé, on recalcule les coordonnées de la structure
        if (previousPlacementData == null || placementData != previousPlacementData) {
            BlockPos minPos = new BlockPos(
                    placementData.getBoundingBox().getMinX(),
                    placementData.getBoundingBox().getMinY(),
                    placementData.getBoundingBox().getMinZ()
            );
            BlockPos maxPos = new BlockPos(
                    placementData.getBoundingBox().getMaxX(),
                    placementData.getBoundingBox().getMaxY(),
                    placementData.getBoundingBox().getMaxZ()
            );
            Box structureBox = new Box(minPos, maxPos);

            int boxHash = Objects.hash(minPos.asLong(), maxPos.asLong());

            if (!scheduledBoxes.containsKey(boxHash)) {
                scheduledBoxes.put(boxHash, structureBox);
                AntiqueBeasts.getWaterRemovalScheduler().schedule(minPos, maxPos, 1);
            }
        }

        previousPlacementData = placementData;

        return currentBlockInfo;
    }

    public static final Codec<WaterRemovalProcessor> CODEC = Codec.unit(WaterRemovalProcessor::new);

    @Override
    protected StructureProcessorType<?> getType() {
        return MyProcessors.WATER_REMOVAL_PROCESSOR;
    }
}