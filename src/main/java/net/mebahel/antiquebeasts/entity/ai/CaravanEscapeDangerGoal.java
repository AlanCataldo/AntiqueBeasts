package net.mebahel.antiquebeasts.entity.ai;

import net.minecraft.entity.Entity;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.NoPenaltyTargeting;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.entity.mob.PathAwareEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Items;
import net.minecraft.sound.SoundEvents;
import net.minecraft.tag.FluidTags;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.BlockView;
import org.jetbrains.annotations.Nullable;

import java.util.EnumSet;
import java.util.Random;

public class CaravanEscapeDangerGoal extends Goal {
    protected final PathAwareEntity mob;
    protected final double speed;
    protected double targetX;
    protected double targetY;
    protected double targetZ;
    protected boolean active;
    private long startTime;
    private static final long DURATION = 12 * 20; // 12 seconds in ticks
    private long nextGoldIngotTime;
    private boolean attackedByPlayer;
    private static final Random random = new Random();

    public CaravanEscapeDangerGoal(PathAwareEntity mob, double speed) {
        this.mob = mob;
        this.speed = speed;
        this.setControls(EnumSet.of(Control.MOVE));
    }

    @Override
    public boolean canStart() {
        if (!this.isInDanger()) {
            return false;
        } else {
            if (this.mob.isOnFire()) {
                BlockPos blockPos = this.locateClosestWater(this.mob.getWorld(), this.mob, 15);
                if (blockPos != null) {
                    this.targetX = blockPos.getX();
                    this.targetY = blockPos.getY();
                    this.targetZ = blockPos.getZ();
                    return true;
                }
            }
            return this.findTarget();
        }
    }

    protected boolean isInDanger() {
        Entity attacker = this.mob.getAttacker();
        if (attacker instanceof PlayerEntity) {
            this.attackedByPlayer = true;
            return true;
        } else if (attacker instanceof LivingEntity) {
            this.attackedByPlayer = false;
            return true;
        }
        return this.mob.shouldEscapePowderSnow() || this.mob.isOnFire();
    }

    protected boolean findTarget() {
        Vec3d vec3d = NoPenaltyTargeting.find(this.mob, 10, 10);
        if (vec3d == null) {
            return false;
        } else {
            this.targetX = vec3d.x;
            this.targetY = vec3d.y;
            this.targetZ = vec3d.z;
            return true;
        }
    }

    public boolean isActive() {
        return this.active;
    }

    @Override
    public void start() {
        this.mob.getNavigation().startMovingTo(this.targetX, this.targetY, this.targetZ, this.speed);
        this.active = true;
        this.startTime = this.mob.getWorld().getTime();
        this.nextGoldIngotTime = this.startTime + 10 + random.nextInt(80); // First ingot between 2 to 4 seconds
    }

    @Override
    public void stop() {
        this.active = false;
    }

    @Override
    public boolean shouldContinue() {
        long currentTime = this.mob.getWorld().getTime();
        return currentTime - this.startTime < DURATION;
    }

    @Override
    public void tick() {
        long currentTime = this.mob.getWorld().getTime();
        if (currentTime >= this.nextGoldIngotTime) {
            launchGoldIngot();
            this.nextGoldIngotTime = currentTime + 40 + random.nextInt(80); // Next ingot between 2 to 4 seconds
        }

        // Ensure the mob keeps moving
        if (this.mob.getNavigation().isIdle()) {
            this.findTarget();
            this.mob.getNavigation().startMovingTo(this.targetX, this.targetY, this.targetZ, this.speed);
        }
    }

    private void launchGoldIngot() {
        Vec3d position = this.mob.getPos().add(0, 1, 0);
        ItemEntity goldIngot = new ItemEntity(this.mob.getWorld(), position.x, position.y, position.z, Items.GOLD_INGOT.getDefaultStack());
        goldIngot.setVelocity(0, 0.5, 0);
        this.mob.getWorld().spawnEntity(goldIngot);
        this.mob.playSound(SoundEvents.ENTITY_ITEM_PICKUP, 1.0F, 1.0F); // Play item pickup sound
    }

    @Nullable
    protected BlockPos locateClosestWater(BlockView world, Entity entity, int rangeX) {
        BlockPos blockPos = entity.getBlockPos();
        return !world.getBlockState(blockPos).getCollisionShape(world, blockPos).isEmpty() ? null : BlockPos.findClosest(entity.getBlockPos(), rangeX, 1, (pos) -> {
            return world.getFluidState(pos).isIn(FluidTags.WATER);
        }).orElse(null);
    }
}
