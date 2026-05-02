package net.mebahel.antiquebeasts.util.entity;

import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;

public class MovementUtil {

    // =========================
    // CONFIG
    // =========================
    private final MobEntity actor;
    private double strafeDistance;
    private double tolerance = 2.0;
    private double approachSpeed = 1.0;

    // =========================
    // MODE / HYSTERESIS
    // =========================
    private enum Mode { APPROACH, COMBAT }
    private Mode mode = Mode.COMBAT;

    // Il faut que la condition soit vraie N ticks avant de demander un switch
    private int approachWantedTicks = 0;
    private int combatWantedTicks = 0;

    private static final int SWITCH_CONFIRM_TICKS = 8; // ~0.4s
    private static final double HYST = 1.5;            // marge en blocs (tweak)

    // =========================
    // "STOP 1s AVANT SWITCH"
    // =========================
    private static final int MODE_SWITCH_PAUSE_TICKS = 1; // 1 seconde
    private int modeSwitchPauseTicks = 0;
    private Mode pendingMode = null;

    // =========================
    // STATE (ancien)
    // =========================
    public boolean movingToLeft;
    public boolean backward;
    public int combatTicks = -1;

    // Jump (EXACT ancien)
    private Vec3d jumpDirection = Vec3d.ZERO;
    public int jumpCooldown = 0;
    public int jumpDuration = 0;

    // Stuck
    private Vec3d lastPosition;
    private int stuckTicks = 0;
    private static final int MAX_STUCK_TICKS = 20;

    // Vision ticker
    public int targetSeeingTicker;

    public MovementUtil(MobEntity actor, double strafeDistance) {
        this.actor = actor;
        this.strafeDistance = strafeDistance;
        this.lastPosition = actor.getPos();
    }

    public MovementUtil(MobEntity actor) {
        this.actor = actor;
        this.strafeDistance = 7;
        this.lastPosition = actor.getPos();
    }

    // =========================
    // SETTERS
    // =========================
    public void setTolerance(double tolerance) {
        this.tolerance = Math.max(0.0, tolerance);
    }

    public void setApproachSpeed(double approachSpeed) {
        this.approachSpeed = Math.max(0.05, approachSpeed);
    }

    public void setStrafeDistance(double strafeDistance) {
        this.strafeDistance = strafeDistance;
    }

    // =========================
    // DRIVER
    // =========================
    public void maintainRangedPosition(LivingEntity target) {

        // -------------------------
        // 0) PAUSE "ARRÊT TOTAL" AVANT SWITCH
        // -------------------------
        if (modeSwitchPauseTicks > 0) {
            modeSwitchPauseTicks--;

            // stop total (aucun mouvement)
            stopTotally(target);

            // fin de pause -> commit du mode
            if (modeSwitchPauseTicks == 0 && pendingMode != null) {
                mode = pendingMode;
                pendingMode = null;

                // purge au moment du commit (sécurité)
                actor.getNavigation().stop();
                actor.getMoveControl().strafeTo(0.0F, 0.0F);
                cancelJumpAndStuckWhileApproaching();
            }
            return;
        }

        double d = actor.distanceTo(target);
        double min = strafeDistance - tolerance;
        double max = strafeDistance + tolerance;

        // -------------------------
        // Hystérésis (2 seuils)
        // -------------------------
        double enterApproach = max;
        double exitApproach  = Math.max(0.0, max - HYST);

        double enterBackoff = min;
        double exitBackoff  = min + HYST;

        // -------------------------
        // DECISION STABLE (anti ping-pong)
        // -------------------------
        boolean wantsApproach = (mode == Mode.APPROACH) ? (d > exitApproach) : (d > enterApproach);

        if (wantsApproach) {
            approachWantedTicks++;
            combatWantedTicks = 0;
        } else {
            combatWantedTicks++;
            approachWantedTicks = 0;
        }

        // -------------------------
        // Demande de switch -> déclenche pause 1s AVANT le switch effectif
        // -------------------------
        if (mode == Mode.COMBAT && approachWantedTicks >= SWITCH_CONFIRM_TICKS) {
            requestModeSwitch(Mode.APPROACH, target);
            approachWantedTicks = 0;
            return;
        }

        if (mode == Mode.APPROACH && combatWantedTicks >= SWITCH_CONFIRM_TICKS) {
            requestModeSwitch(Mode.COMBAT, target);
            combatWantedTicks = 0;
            return;
        }

        // =========================
        // NAV APPROACH (vanilla)
        // =========================
        if (mode == Mode.APPROACH) {
            // ✅ IMPORTANT: en "avance", on interdit TOUT jump
            cancelJumpAndStuckWhileApproaching();

            // purge inputs strafe
            actor.getMoveControl().strafeTo(0.0F, 0.0F);

            // look + nav
            actor.getLookControl().lookAt(target, 30.0F, 30.0F);
            actor.getNavigation().startMovingTo(target, approachSpeed);
            return;
        }

        // =========================
        // COMBAT: systèmes anciens
        // =========================

        // stuck peut déclencher jumpDirection/jumpDuration
        checkIfStuck(target, actor);

        // ✅ Jump tick EXACT ancien (prioritaire)
        if (handleJumpTick(target, actor)) return;

        // =========================
        // STRAFE / BACKOFF
        // =========================
        actor.getNavigation().stop();
        lookAtTargetStrafe(target, actor);

        // Backoff avec hystérésis (ton système)
        boolean shouldBackoff = (d < enterBackoff) || (backward && d < exitBackoff);

        if (shouldBackoff) {
            backward = true;
            moveBackward(target, actor);
        } else {
            backward = false;
            strafeInZone(target, actor);
        }
    }

    private void requestModeSwitch(Mode newMode, LivingEntity target) {
        if (this.pendingMode == newMode) return;

        this.pendingMode = newMode;
        this.modeSwitchPauseTicks = MODE_SWITCH_PAUSE_TICKS;

        // arrêt total immédiat dès qu'on demande le switch
        stopTotally(target);
    }

    private void stopTotally(LivingEntity target) {
        // stop nav + strafe
        actor.getNavigation().stop();
        actor.getMoveControl().strafeTo(0.0F, 0.0F);

        // zero velocity XZ pour éviter "glisse" / accumulation
        Vec3d v = actor.getVelocity();
        actor.setVelocity(0.0, v.y, 0.0);
        actor.velocityDirty = true;

        // ne surtout pas laisser un jump armé pendant la pause
        jumpDuration = 0;
        jumpCooldown = 0;
        jumpDirection = Vec3d.ZERO;
        stuckTicks = 0;
        lastPosition = actor.getPos();

        // Optionnel: regarder la cible pendant l'arrêt (ça fait "pose/visée")
        actor.getLookControl().lookAt(target, 30.0F, 30.0F);
    }

    private void cancelJumpAndStuckWhileApproaching() {
        // stoppe un jump déjà armé
        jumpDuration = 0;
        jumpCooldown = 0;
        jumpDirection = Vec3d.ZERO;

        // empêche l'anti-stuck d'armer un jump pendant qu'on avance
        stuckTicks = 0;
        lastPosition = actor.getPos();
    }

    // =========================
    // LOOK (strafe exact)
    // =========================
    public void lookAtTargetStrafe(LivingEntity target, LivingEntity actor) {
        double dx = target.getX() - actor.getX();
        double dz = target.getZ() - actor.getZ();
        double dy = target.getEyeY() - actor.getEyeY();

        double distance = Math.sqrt(dx * dx + dz * dz);
        float yaw = (float) (MathHelper.atan2(dz, dx) * (180D / Math.PI)) - 90.0F;
        float pitch = (float) (-(MathHelper.atan2(dy, distance) * (180D / Math.PI)));

        actor.setYaw(yaw);
        actor.setHeadYaw(yaw);
        actor.setPitch(pitch);
    }

    // =========================
    // JUMP SYSTEM (EXACT ancien)
    // =========================
    private boolean handleJumpTick(LivingEntity target, MobEntity actor) {
        if (jumpDuration > 0) {

            actor.getNavigation().stop();
            actor.getMoveControl().strafeTo(0.0F, 0.0F);

            --jumpDuration;
            performJump(jumpDirection, actor);

            if (jumpDuration == 0) {
                jumpCooldown = 18;
            }

            lookAtTargetStrafe(target, actor);
            return true;
        }
        return false;
    }

    public void performJump(Vec3d direction, LivingEntity actor) {
        actor.setVelocity(direction);
        actor.velocityDirty = true;
    }

    // =========================
    // STRAFE ZONE (ancien)
    // =========================
    private void strafeInZone(LivingEntity target, MobEntity actor) {
        if (jumpCooldown > 0) --jumpCooldown;

        if (combatTicks >= 20) {
            if (actor.getRandom().nextFloat() < 0.3F) movingToLeft = !movingToLeft;
            combatTicks = 0;
        }
        combatTicks = Math.max(combatTicks, 0) + 1;

        float strafeDirection = movingToLeft ? 1.0F : -1.0F;
        float forwardDirection = 0.0F;

        Vec3d lateralDirection = new Vec3d(strafeDirection, 0.0, 0.0);
        Vec3d strafeVec = actor.getPos().add(lateralDirection);

        BlockPos strafeBlockPos = new BlockPos((int) strafeVec.x, (int) actor.getY(), (int) strafeVec.z);
        BlockState strafeBlockState = actor.getWorld().getBlockState(strafeBlockPos);
        BlockState blockAboveStrafeState = actor.getWorld().getBlockState(strafeBlockPos.up());
        BlockState blockBelowStrafeState = actor.getWorld().getBlockState(strafeBlockPos.down());

        if (jumpCooldown <= 0) {
            if (!strafeBlockState.isAir()
                    && (blockAboveStrafeState.isAir() || !blockAboveStrafeState.isFullCube(actor.getWorld(), strafeBlockPos.up()))
                    && strafeBlockState.isFullCube(actor.getWorld(), strafeBlockPos)) {

                jumpDirection = (new Vec3d(strafeDirection, 0.9, 0.0)).normalize().multiply(0.25);
                jumpDuration = 8;
                performJump(jumpDirection, actor);
                return;
            }

            if (blockBelowStrafeState.isAir() || !blockBelowStrafeState.isFullCube(actor.getWorld(), strafeBlockPos.down())) {
                jumpDirection = (new Vec3d(strafeDirection, 0.04, 0.0)).normalize().multiply(0.12);
                jumpDuration = 8;
                performJump(jumpDirection, actor);
                return;
            }
        }

        actor.getMoveControl().strafeTo(forwardDirection, strafeDirection);
    }

    // =========================
    // BACKOFF (ancien)
    // =========================
    public void moveBackward(LivingEntity target, MobEntity actor) {
        Vec3d directionToTarget = (new Vec3d(target.getX() - actor.getX(), 0.0, target.getZ() - actor.getZ())).normalize();
        Vec3d backwardDirection = directionToTarget.multiply(-1.0);
        Vec3d backwardDirection2 = directionToTarget.multiply(-2.0);

        Vec3d lateralDirection = new Vec3d(-backwardDirection.z, 0.0, backwardDirection.x);

        double distanceToTarget = actor.distanceTo(target);

        Vec3d backwardsVec = actor.getPos().add(backwardDirection);
        Vec3d backwardsVec2 = actor.getPos().add(backwardDirection2);

        Vec3d leftVec = backwardsVec.add(lateralDirection);
        Vec3d rightVec = backwardsVec.add(lateralDirection.multiply(-1.0));
        Vec3d leftVec2 = backwardsVec2.add(lateralDirection);
        Vec3d rightVec2 = backwardsVec2.add(lateralDirection.multiply(-1.0));

        BlockPos blockBehindPos  = new BlockPos((int) backwardsVec.x,  (int) actor.getY(), (int) backwardsVec.z);
        BlockPos blockBehindPos2 = new BlockPos((int) backwardsVec2.x, (int) actor.getY(), (int) backwardsVec2.z);
        BlockPos blockLeftPos    = new BlockPos((int) leftVec.x,       (int) actor.getY(), (int) leftVec.z);
        BlockPos blockRightPos   = new BlockPos((int) rightVec.x,      (int) actor.getY(), (int) rightVec.z);
        BlockPos blockLeftPos2   = new BlockPos((int) leftVec2.x,      (int) actor.getY(), (int) leftVec2.z);
        BlockPos blockRightPos2  = new BlockPos((int) rightVec2.x,     (int) actor.getY(), (int) rightVec2.z);

        BlockState blockBehindState  = actor.getWorld().getBlockState(blockBehindPos);
        BlockState blockBehindState2 = actor.getWorld().getBlockState(blockBehindPos2);
        BlockState blockLeftState    = actor.getWorld().getBlockState(blockLeftPos);
        BlockState blockRightState   = actor.getWorld().getBlockState(blockRightPos);
        BlockState blockLeftState2   = actor.getWorld().getBlockState(blockLeftPos2);
        BlockState blockRightState2  = actor.getWorld().getBlockState(blockRightPos2);

        BlockState blockAboveBehindState = actor.getWorld().getBlockState(blockBehindPos.up());
        BlockState blockBelowBehindState = actor.getWorld().getBlockState(blockBehindPos.down());
        BlockState blockBelowBehindState2 = actor.getWorld().getBlockState(blockBehindPos2.down());
        BlockState blockBelowLeftState = actor.getWorld().getBlockState(blockLeftPos.down());
        BlockState blockBelowRightState = actor.getWorld().getBlockState(blockRightPos.down());
        BlockState blockBelowLeftState2 = actor.getWorld().getBlockState(blockLeftPos2.down());
        BlockState blockBelowRightState2 = actor.getWorld().getBlockState(blockRightPos2.down());

        if (!blockBehindState.getFluidState().isEmpty()
                || blockBehindState.getBlock() == Blocks.CACTUS
                || blockBehindState2.getBlock() == Blocks.CACTUS
                || blockLeftState.getBlock() == Blocks.CACTUS
                || blockRightState.getBlock() == Blocks.CACTUS
                || blockLeftState2.getBlock() == Blocks.CACTUS
                || blockRightState2.getBlock() == Blocks.CACTUS) {
            strafeInZone(target, actor);
            return;
        }

        if (jumpCooldown > 0 && distanceToTarget <= this.strafeDistance) {
            actor.getMoveControl().strafeTo(-0.8F, 0.0F);
            --jumpCooldown;
            return;
        } else if (jumpCooldown > 0) {
            --jumpCooldown;
            return;
        }

        if ((!blockBehindState.isAir() || !blockBehindState2.isAir())
                && (blockAboveBehindState.isAir() || !blockAboveBehindState.isFullCube(actor.getWorld(), blockBehindPos.up()))
                && (blockBehindState.isFullCube(actor.getWorld(), blockBehindPos) || blockBehindState2.isFullCube(actor.getWorld(), blockBehindPos2))) {

            jumpDirection = (new Vec3d(backwardDirection.x, 0.9, backwardDirection.z)).normalize().multiply(0.25);
            jumpDuration = 8;
            performJump(jumpDirection, actor);
            return;
        }

        if (blockBelowBehindState.isAir() || !blockBelowBehindState.isFullCube(actor.getWorld(), blockBehindPos.down())
                || blockBelowBehindState2.isAir() || !blockBelowBehindState2.isFullCube(actor.getWorld(), blockBehindPos2.down())
                || blockBelowLeftState.isAir() || !blockBelowLeftState.isFullCube(actor.getWorld(), blockLeftPos.down())
                || blockBelowRightState.isAir() || !blockBelowRightState.isFullCube(actor.getWorld(), blockRightPos.down())
                || blockBelowLeftState2.isAir() || !blockBelowLeftState2.isFullCube(actor.getWorld(), blockLeftPos2.down())
                || blockBelowRightState2.isAir() || !blockBelowRightState2.isFullCube(actor.getWorld(), blockRightPos2.down())) {

            jumpDirection = (new Vec3d(backwardDirection.x, 0.0, backwardDirection.z)).normalize().multiply(0.2);
            jumpDuration = 8;
            performJump(jumpDirection, actor);
            return;
        }

        actor.getMoveControl().strafeTo(-0.8F, 0.0F);
    }

    // =========================
    // STUCK (ancien)
    // =========================
    public void checkIfStuck(LivingEntity target, LivingEntity actor) {
        Vec3d currentPosition = actor.getPos();

        int currentX = Math.round((float) currentPosition.x);
        int currentY = Math.round((float) currentPosition.y);
        int currentZ = Math.round((float) currentPosition.z);

        int lastX = Math.round((float) lastPosition.x);
        int lastY = Math.round((float) lastPosition.y);
        int lastZ = Math.round((float) lastPosition.z);

        Vec3d directionToTarget = (new Vec3d(target.getX() - actor.getX(), 0.0, target.getZ() - actor.getZ())).normalize();
        Vec3d backwardDirection = directionToTarget.multiply(-1.0);

        if (currentX == lastX && currentY == lastY && currentZ == lastZ) {
            ++stuckTicks;
            if (stuckTicks >= MAX_STUCK_TICKS) {
                jumpDirection = (new Vec3d(backwardDirection.x, 0.6, backwardDirection.z)).normalize().multiply(0.25);
                jumpDuration = 8;
                performJump(jumpDirection, actor);
                stuckTicks = 0;
            }
        } else {
            stuckTicks = 0;
        }

        lastPosition = currentPosition;
    }
}