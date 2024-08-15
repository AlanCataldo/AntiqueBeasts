package net.mebahel.antiquebeasts.block;

import net.mebahel.antiquebeasts.entity.ModEntities;
import net.mebahel.antiquebeasts.sound.ModSounds;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.mob.PathAwareEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

import java.util.Random;

public class CursedGoldBlock extends Block {

    public CursedGoldBlock(Settings settings) {
        super(settings);
    }

    @Override
    public void onBreak(World world, BlockPos pos, BlockState state, net.minecraft.entity.player.PlayerEntity player) {
        super.onBreak(world, pos, state, player);

        if (!world.isClient) {
            ServerWorld serverWorld = (ServerWorld) world;
            Random random = new Random();

            serverWorld.playSound(null, pos, ModSounds.CURSED_BIRTH, SoundCategory.BLOCKS, 1.0F, 1.0F);

            int spawnType = random.nextInt(3);

            switch (spawnType) {
                case 0 -> {
                    spawnEntity(serverWorld, pos, random, ModEntities.SERVANT);
                    spawnEntity(serverWorld, pos, random, ModEntities.SERVANT);
                    spawnEntity(serverWorld, pos, random, ModEntities.SERVANT);
                }
                case 1 -> {
                    spawnEntity(serverWorld, pos, random, ModEntities.MUMMY);
                    spawnEntity(serverWorld, pos, random, ModEntities.SERVANT);
                    spawnEntity(serverWorld, pos, random, ModEntities.SERVANT);
                }
                case 2 -> {
                    spawnEntity(serverWorld, pos, random, ModEntities.MUMMY);
                    spawnEntity(serverWorld, pos, random, ModEntities.MUMMY);
                    spawnEntity(serverWorld, pos, random, ModEntities.SERVANT);
                }
            }
        }
    }

    private void spawnEntity(ServerWorld serverWorld, BlockPos pos, Random random, EntityType<? extends PathAwareEntity> entityType) {
        double offsetX = (random.nextDouble() * 12) - 6;
        double offsetZ = (random.nextDouble() * 12) - 6;
        Vec3d spawnPosition = new Vec3d(pos.getX() + offsetX, pos.getY(), pos.getZ() + offsetZ);

        PathAwareEntity entity = entityType.create(serverWorld);
        if (entity != null) {
            entity.refreshPositionAndAngles(spawnPosition.x, spawnPosition.y, spawnPosition.z, serverWorld.random.nextFloat() * 360.0F, 0.0F);
            serverWorld.spawnEntity(entity);
        }
    }
}
