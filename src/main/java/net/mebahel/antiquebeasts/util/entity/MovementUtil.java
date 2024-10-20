package net.mebahel.antiquebeasts.util.entity;

import net.mebahel.antiquebeasts.entity.custom.other.DraugrScourgeEntity;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;

public class MovementUtil {
    public boolean movingToLeft;
    public boolean backward;
    public int combatTicks = -1;
    Vec3d jumpDirection = new Vec3d(0, 0, 0);
    public int jumpCooldown = 0;
    public int jumpDuration = 0;
    public int targetSeeingTicker;
    private final double strafeDistance = 8;
    private Vec3d lastPosition;
    private int stuckTicks = 0;
    private static final int MAX_STUCK_TICKS = 20;

    public MovementUtil(MobEntity actor) {
        this.lastPosition = actor.getPos();
    }

    public void performJump(Vec3d direction, LivingEntity actor) {
        actor.setVelocity(direction);
        actor.velocityDirty = true;
    }
    public void lookAtTarget(LivingEntity target, LivingEntity actor) {
        double dx = target.getX() - actor.getX();
        double dz = target.getZ() - actor.getZ();
        double dy = target.getEyeY() - actor.getEyeY();

        double distance = Math.sqrt(dx * dx + dz * dz);
        float targetYaw = (float) (MathHelper.atan2(dz, dx) * (180F / Math.PI)) - 90.0F;
        float targetPitch = (float) -(MathHelper.atan2(dy, distance) * (180F / Math.PI));

        actor.setYaw(targetYaw);
        actor.setHeadYaw(targetYaw);
        actor.setPitch(targetPitch);
    }

    public void moveBackward(LivingEntity target, MobEntity actor) {
        Vec3d directionToTarget = new Vec3d(target.getX() - actor.getX(), 0, target.getZ() - actor.getZ()).normalize();
        Vec3d backwardDirection = directionToTarget.multiply(-1);  // Inverser la direction pour reculer
        Vec3d backwardDirection2 = directionToTarget.multiply(-2);  // Reculer de deux blocs
        Vec3d lateralDirection = new Vec3d(-backwardDirection.z, 0, backwardDirection.x);  // Direction perpendiculaire pour gauche et droite

        double distanceToTarget = actor.distanceTo(target);

        Vec3d backwardsVec = actor.getPos().add(backwardDirection);
        Vec3d backwardsVec2 = actor.getPos().add(backwardDirection2);

        Vec3d leftVec = backwardsVec.add(lateralDirection);
        Vec3d rightVec = backwardsVec.add(lateralDirection.multiply(-1));
        Vec3d leftVec2 = backwardsVec2.add(lateralDirection);
        Vec3d rightVec2 = backwardsVec2.add(lateralDirection.multiply(-1));

        BlockPos blockBehindPos = new BlockPos((int) backwardsVec.x, (int) actor.getY(), (int) backwardsVec.z);
        BlockPos blockBehindPos2 = new BlockPos((int) backwardsVec2.x, (int) actor.getY(), (int) backwardsVec2.z);
        BlockPos blockLeftPos = new BlockPos((int) leftVec.x, (int) actor.getY(), (int) leftVec.z);
        BlockPos blockRightPos = new BlockPos((int) rightVec.x, (int) actor.getY(), (int) rightVec.z);
        BlockPos blockLeftPos2 = new BlockPos((int) leftVec2.x, (int) actor.getY(), (int) leftVec2.z);
        BlockPos blockRightPos2 = new BlockPos((int) rightVec2.x, (int) actor.getY(), (int) rightVec2.z);

        BlockState blockBehindState = actor.getWorld().getBlockState(blockBehindPos);
        BlockState blockBehindState2 = actor.getWorld().getBlockState(blockBehindPos2);
        BlockState blockLeftState = actor.getWorld().getBlockState(blockLeftPos);
        BlockState blockRightState = actor.getWorld().getBlockState(blockRightPos);
        BlockState blockLeftState2 = actor.getWorld().getBlockState(blockLeftPos2);
        BlockState blockRightState2 = actor.getWorld().getBlockState(blockRightPos2);

        BlockState blockAboveBehindState = actor.getWorld().getBlockState(blockBehindPos.up());
        BlockState blockBelowBehindState = actor.getWorld().getBlockState(blockBehindPos.down());
        BlockState blockBelowBehindState2 = actor.getWorld().getBlockState(blockBehindPos2.down());
        BlockState blockBelowLeftState = actor.getWorld().getBlockState(blockLeftPos.down());
        BlockState blockBelowRightState = actor.getWorld().getBlockState(blockRightPos.down());
        BlockState blockBelowLeftState2 = actor.getWorld().getBlockState(blockLeftPos2.down());
        BlockState blockBelowRightState2 = actor.getWorld().getBlockState(blockRightPos2.down());

        if (!blockBehindState.getFluidState().isEmpty() || blockBehindState.getBlock() == Blocks.CACTUS
                || blockBehindState2.getBlock() == Blocks.CACTUS || blockLeftState.getBlock() == Blocks.CACTUS
                || blockRightState.getBlock() == Blocks.CACTUS || blockLeftState2.getBlock() == Blocks.CACTUS
                || blockRightState2.getBlock() == Blocks.CACTUS) {
            strafeAroundTarget(target, actor);
            return;
        }

        if (jumpDuration > 0) {
            jumpDuration--;
            this.performJump(jumpDirection, actor);
            if (jumpDuration == 0) {
                jumpCooldown = 18;
            }
            return;
        }
        if (jumpCooldown > 0 && distanceToTarget <= this.strafeDistance) {
            actor.getMoveControl().strafeTo(-0.35F, 0);
            jumpCooldown--;
            return;
        } else if (jumpCooldown > 0 && distanceToTarget > this.strafeDistance) {
            jumpCooldown--;
            return;
        }

        if ((!blockBehindState.isAir() || !blockBehindState2.isAir())
                && (blockAboveBehindState.isAir() || !blockAboveBehindState.isFullCube(actor.getWorld(), blockBehindPos.up()))
                && (blockBehindState.isFullCube(actor.getWorld(), blockBehindPos) || blockBehindState2.isFullCube(actor.getWorld(), blockBehindPos2))) {
            jumpDirection = new Vec3d(backwardDirection.x, 0.9, backwardDirection.z).normalize().multiply(0.25);
            jumpDuration = 8;
            this.performJump(jumpDirection, actor);
        } else if (blockBelowBehindState.isAir() || !blockBelowBehindState.isFullCube(actor.getWorld(), blockBehindPos.down(1))
                || blockBelowBehindState2.isAir() || !blockBelowBehindState2.isFullCube(actor.getWorld(), blockBehindPos2.down(1))
                || blockBelowLeftState.isAir() || !blockBelowLeftState.isFullCube(actor.getWorld(), blockLeftPos.down())
                || blockBelowRightState.isAir() || !blockBelowRightState.isFullCube(actor.getWorld(), blockRightPos.down())
                || blockBelowLeftState2.isAir() || !blockBelowLeftState2.isFullCube(actor.getWorld(), blockLeftPos2.down())
                || blockBelowRightState2.isAir() || !blockBelowRightState2.isFullCube(actor.getWorld(), blockRightPos2.down())) {
            jumpDirection = new Vec3d(backwardDirection.x, 0, backwardDirection.z).normalize().multiply(0.2);
            jumpDuration = 8;
            this.performJump(jumpDirection, actor);
        } else if ((blockBehindState.isAir() || !blockBehindState.isFullCube(actor.getWorld(), blockBehindPos)) ||
                !blockBehindState.isFullCube(actor.getWorld(), blockBehindPos)) {
            actor.getMoveControl().strafeTo(-0.35F, 0);
        } else {
            jumpDuration = 8;
            this.performJump(jumpDirection, actor);
        }
        lookAtTarget(target, actor);
    }
    public void strafeAroundTarget(LivingEntity target, MobEntity actor) {
        boolean canSeeTarget = actor.getVisibilityCache().canSee(target);
        boolean sawTargetRecently = this.targetSeeingTicker > 0;

        if (canSeeTarget != sawTargetRecently) {
            this.targetSeeingTicker = 0;
        }

        if (canSeeTarget) {
            ++this.targetSeeingTicker;
        } else {
            --this.targetSeeingTicker;
        }

        if (this.targetSeeingTicker >= 20) {
            actor.getNavigation().stop();
            ++this.combatTicks;
        } else {
            this.combatTicks = -1;
        }

        if (jumpDuration > 0) {
            jumpDuration--;
            this.performJump(jumpDirection, actor);
            if (jumpDuration == 0) {
                jumpCooldown = 18;
            }
            return;
        }

        if (jumpCooldown > 0) {
            jumpCooldown--;
            return;
        }

        // Changer la direction du strafe toutes les 20 ticks
        if (this.combatTicks >= 20) {
            if (actor.getRandom().nextFloat() < 0.3) {
                this.movingToLeft = !this.movingToLeft;  // Changer de côté pour le strafe
            }
            if (actor.getRandom().nextFloat() < 0.3) {
                this.backward = !this.backward;  // Alterner entre avancer et reculer
            }
            this.combatTicks = 0;
        }

        // Direction du strafe et du déplacement avant/arrière
        float strafeDirection = this.movingToLeft ? 0.35f : -0.35f;  // Gauche si true, droite sinon
        double distanceToTarget = actor.distanceTo(target);
        float forwardDirection;
        if (distanceToTarget <= this.strafeDistance + 3)
            forwardDirection = -0.35f;
        else if (distanceToTarget >= this.strafeDistance + 10)
            forwardDirection = 0.35f;
        else
            forwardDirection = this.backward ? -0.35f : 0.35f;  // Reculer si true, avancer sinon

        // Correction: Utiliser `strafeDirection` pour ajuster correctement la direction latérale
        Vec3d lateralDirection = new Vec3d(strafeDirection, 0, 0);  // Gauche ou droite
        Vec3d strafeVec = actor.getPos().add(lateralDirection);

        // Vérifier les blocs devant le strafe (à gauche ou à droite)
        BlockPos strafeBlockPos = new BlockPos((int) strafeVec.x, (int) actor.getY(), (int) strafeVec.z);
        BlockState strafeBlockState = actor.getWorld().getBlockState(strafeBlockPos);
        BlockState blockAboveStrafeState = actor.getWorld().getBlockState(strafeBlockPos.up());
        BlockState blockBelowStrafeState = actor.getWorld().getBlockState(strafeBlockPos.down());

        // Mécanique de saut pendant le strafe
        if (!strafeBlockState.isAir() && (blockAboveStrafeState.isAir() || !blockAboveStrafeState.isFullCube(actor.getWorld(), strafeBlockPos.up()))
                && strafeBlockState.isFullCube(actor.getWorld(), strafeBlockPos)) {
            // Correction: Utiliser correctement `lateralDirection` pour le saut latéral
            jumpDirection = new Vec3d(strafeDirection, 0.9, 0).normalize().multiply(0.25);
            jumpDuration = 8;
            this.performJump(jumpDirection, actor);
        } else if (blockBelowStrafeState.isAir() || !blockBelowStrafeState.isFullCube(actor.getWorld(), strafeBlockPos.down())) {
            // Si l'entité détecte un vide, sauter pour éviter de tomber
            jumpDirection = new Vec3d(strafeDirection, 0.04, 0).normalize().multiply(0.12);
            jumpDuration = 8;
            this.performJump(jumpDirection, actor);
        } else if (strafeBlockState.isAir() || !strafeBlockState.isFullCube(actor.getWorld(), strafeBlockPos)) {
            // Si l'espace est libre, continuer le strafe
            actor.getMoveControl().strafeTo(forwardDirection, strafeDirection);
        } else {
            // Sinon, continuer le saut pour contourner l'obstacle
            jumpDuration = 8;
            this.performJump(jumpDirection, actor);
        }

        // Mettre à jour la direction de l'entité vers la cible
        lookAtTarget(target, actor);
    }

    public void checkIfStuck(LivingEntity target, LivingEntity actor) {
        Vec3d currentPosition = actor.getPos();

        // Utiliser Math.round() pour arrondir les coordonnées au plus proche entier
        int currentX = Math.round((float) currentPosition.x);
        int currentY = Math.round((float) currentPosition.y);
        int currentZ = Math.round((float) currentPosition.z);

        int lastX = Math.round((float) lastPosition.x);
        int lastY = Math.round((float) lastPosition.y);
        int lastZ = Math.round((float) lastPosition.z);
        Vec3d directionToTarget = new Vec3d(target.getX() - actor.getX(), 0, target.getZ() - actor.getZ()).normalize();
        Vec3d backwardDirection = directionToTarget.multiply(-1);  // Inverser la direction pour reculer

        // Vérifier si l'entité est à la même position qu'au tick précédent en comparant les entiers arrondis
        if (currentX == lastX && currentY == lastY && currentZ == lastZ) {
            stuckTicks++;
            if (stuckTicks >= MAX_STUCK_TICKS) {
                jumpDirection = new Vec3d(backwardDirection.x, 0.6, backwardDirection.z).normalize().multiply(0.25);
                jumpDuration = 8;
                this.performJump(jumpDirection, actor);
                stuckTicks = 0;  // Réinitialiser le compteur après le saut
            }
        } else {
            // Si l'entité a bougé, réinitialiser le compteur de blocage
            stuckTicks = 0;
        }

        // Mettre à jour la dernière position connue
        lastPosition = currentPosition;
        lookAtTarget(target, actor);
    }
}
