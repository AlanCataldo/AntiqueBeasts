package net.mebahel.antiquebeasts.entity.ai;

import net.mebahel.antiquebeasts.entity.ModEntities;
import net.mebahel.antiquebeasts.entity.custom.egyptian.MummyEntity;
import net.mebahel.antiquebeasts.entity.custom.egyptian.ServantEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

public class MummySummonGoal extends Goal {
    private final MummyEntity mummy;
    private float speed;

    public MummySummonGoal(MummyEntity mummy, float speed) {
        this.mummy = mummy;
        this.speed = speed;
    }

    public boolean canStart() {
        return this.mummy.getTarget() != null;
    }

    public void start() {
        if (this.mummy.getSpawnCooldown() < 100)
            this.mummy.setSpawnCooldown(100);
    }

    public void stop() {
        this.mummy.setSpawn(false);
        this.mummy.setSpawnCooldown(300);
    }

    public boolean shouldContinue() {
        LivingEntity livingEntity = this.mummy.getTarget();

        if (livingEntity instanceof PlayerEntity) {
            PlayerEntity playerEntity = (PlayerEntity) livingEntity;
            if (playerEntity.isCreative() || playerEntity.isSpectator()) {
                return false;
            }
        }
        return livingEntity != null && livingEntity.isAlive() && this.mummy.getSpawnCooldown() != 0 && livingEntity.isAlive();
    }

    public void tick() {
        this.mummy.setSpawnCooldown(this.mummy.getSpawnCooldown() - 1);
        if (this.mummy.getSpawnCooldown() < 40)
            this.mummy.setCooldown(120);

        switch (this.mummy.getSpawnCooldown()) {
            case 0 -> this.stop();
            case 10 -> {
                World world = this.mummy.getWorld();
                spawnMummyAtOffset(world, 3, 2);
                spawnMummyAtOffset(world, 3, -2);
            }
            case 22 -> {
                this.mummy.setSpawn(true);
            }
        }
    }

    private void spawnMummyAtOffset(World world, double forwardOffset, double sideOffset) {
        ServantEntity newMummy = ModEntities.SERVANT.create(world);
        if (newMummy != null) {
            Vec3d offsetPosition = getOffsetPosition(forwardOffset, sideOffset);
            newMummy.refreshPositionAndAngles(offsetPosition.x, offsetPosition.y, offsetPosition.z, this.mummy.getYaw(), this.mummy.getPitch());
            world.spawnEntity(newMummy);
        }
    }

    private Vec3d getOffsetPosition(double forwardOffset, double sideOffset) {
        double yaw = Math.toRadians(this.mummy.getYaw());
        double sinYaw = Math.sin(yaw);
        double cosYaw = Math.cos(yaw);

        double xOffset = forwardOffset * -sinYaw + sideOffset * cosYaw;
        double zOffset = forwardOffset * cosYaw + sideOffset * sinYaw;

        return new Vec3d(this.mummy.getX() + xOffset, this.mummy.getY(), this.mummy.getZ() + zOffset);
    }
}
