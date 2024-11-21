package net.mebahel.antiquebeasts.util.raid;

import net.mebahel.antiquebeasts.entity.ModEntities;
import net.mebahel.antiquebeasts.item.TickScheduler;
import net.mebahel.antiquebeasts.sound.ModSounds;
import net.minecraft.block.Blocks;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.ChestBlockEntity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.Heightmap;

import java.util.Random;

public class DraugrRaidHelper {
    private void endRaidSound(ServerWorld world, BlockPos chestPos, PlayerEntity targetPlayer) {
        world.playSound(null, chestPos, ModSounds.WINNING_RAID_1, targetPlayer.getSoundCategory(), 1.0F, 1.0F);
    }

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
    public void spawnRewardChest(ServerWorld world, PlayerEntity targetPlayer, TickScheduler tickScheduler) {
        if (!world.isClient) {
            BlockPos playerPos = targetPlayer.getBlockPos();
            BlockPos chestPos = playerPos.add(targetPlayer.getHorizontalFacing().getVector());

            while (world.isAir(chestPos.down()) && chestPos.getY() > 0) {
                chestPos = chestPos.down();
            }

            world.setBlockState(chestPos, Blocks.CHEST.getDefaultState());
            BlockEntity blockEntity = world.getBlockEntity(chestPos);
            if (blockEntity instanceof ChestBlockEntity chestBlockEntity) {
                chestBlockEntity.setLootTable(new Identifier("antiquebeasts", "chests/egyptian/egyptian_caravan"), world.getRandom().nextLong());
            }

            System.out.println("A reward chest has spawned at " + chestPos);
            BlockPos finalChestPos = chestPos;
            tickScheduler.schedule(world, 15, (serverWorld) -> endRaidSound(serverWorld, finalChestPos, targetPlayer));
        }
    }
}
