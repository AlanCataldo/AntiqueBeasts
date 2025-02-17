package net.mebahel.antiquebeasts.entity.ai.mummy_boss;

import net.mebahel.antiquebeasts.entity.ModEntities;
import net.mebahel.antiquebeasts.entity.custom.egyptian.MummyBossEntity;
import net.mebahel.antiquebeasts.entity.custom.egyptian.MummyEntity;
import net.mebahel.antiquebeasts.entity.custom.egyptian.ServantEntity;
import net.mebahel.antiquebeasts.sound.ModSounds;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

import java.util.Random;

public class MummyBossSummonGoal extends Goal {
    private final MummyBossEntity mummy;
    private final Random random = new Random(); // Générateur aléatoire
    private float speed;

    public MummyBossSummonGoal(MummyBossEntity mummy, float speed) {
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
        this.mummy.setSpawnCooldown(240);
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

        if (this.mummy.secondPhase || this.mummy.thirdPhase) { // Ne spawner que si la momie est en seconde phase
            switch (this.mummy.getSpawnCooldown()) {
                case 0 -> this.stop();
                case 10 -> {
                    World world = this.mummy.getWorld();

                    // Invoquer entre 3 et 4 Servants
                    int numServants = random.nextInt(2) + 3; // Renvoie 3 ou 4
                    for (int i = 0; i < numServants; i++) {
                        double sideOffset = (i % 2 == 0) ? 2 : -2; // Alterner gauche/droite
                        spawnServantAtOffset(world, 3, sideOffset);
                    }

                    // Invoquer 0 ou 1 MummyEntity
                    if (random.nextInt(2) == 1) { // 50% de chances
                        spawnMummyAtOffset(world, 5, 0);
                    }
                }
                case 22 -> {
                    this.mummy.setSpawn(true);
                }
            }
        }
    }

    // Méthode pour invoquer un ServantEntity
    private void spawnServantAtOffset(World world, double forwardOffset, double sideOffset) {
        ServantEntity servant = ModEntities.SERVANT.create(world);
        if (servant != null) {
            Vec3d offsetPosition = getOffsetPosition(forwardOffset, sideOffset);
            servant.refreshPositionAndAngles(offsetPosition.x, offsetPosition.y, offsetPosition.z, this.mummy.getYaw(), this.mummy.getPitch());
            world.spawnEntity(servant);
            servant.playSound(ModSounds.HADES_SHADE_SPAWN, 0.5f, 1);
        }
    }

    // Méthode pour invoquer une MummyEntity
    private void spawnMummyAtOffset(World world, double forwardOffset, double sideOffset) {
        MummyEntity newMummy = ModEntities.MUMMY.create(world);
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
