package net.mebahel.antiquebeasts.util.raid;

import net.mebahel.antiquebeasts.entity.ModEntities;
import net.minecraft.entity.EntityType;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.Heightmap;

import java.util.Random;

public class DraugrRaidHelper {
    public static BlockPos findGroundPosition(ServerWorld world, BlockPos pos) {
        return world.getTopPosition(Heightmap.Type.MOTION_BLOCKING_NO_LEAVES, pos);
    }

    public static EntityType chooseEntityType(Random random, int currentWave, int difficultyLevel) {
        int draugrWeight = Math.max(16 - currentWave, 1);
        int skeletonWarriorWeight = Math.max(6 - currentWave, 1);
        int draugrArcherWeight = Math.max(4 - currentWave, 1);
        int draugrWightWeight = Math.min(3 + currentWave, 10);
        int draugrScourgeWeight = Math.min(1 + currentWave, 10);

        if (difficultyLevel == 1) {
            draugrScourgeWeight = 0;
        }

        int totalWeight = draugrWeight + skeletonWarriorWeight + draugrArcherWeight + draugrWightWeight + draugrScourgeWeight;
        int choice = random.nextInt(totalWeight);

        if (choice < draugrWeight) {
            return ModEntities.DRAUGR;
        } else if (choice < draugrWeight + skeletonWarriorWeight) {
            return ModEntities.SKELETON_WARRIOR;
        } else if (choice < draugrWeight + skeletonWarriorWeight + draugrArcherWeight) {
            return ModEntities.DRAUGR_ARCHER;
        } else if (choice < draugrWeight + skeletonWarriorWeight + draugrArcherWeight + draugrWightWeight) {
            return ModEntities.DRAUGR_WIGHT;
        } else {
            return ModEntities.DRAUGR_SCOURGE;
        }
    }
}
