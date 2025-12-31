package net.mebahel.antiquebeasts.entity.ai.other;

import net.mebahel.antiquebeasts.entity.ModEntities;
import net.mebahel.antiquebeasts.entity.custom.other.DraugrEntity;
import net.mebahel.antiquebeasts.entity.custom.other.DraugrOverlordEntity;
import net.mebahel.antiquebeasts.sound.ModSounds;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.SpawnReason;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.ServerWorldAccess;

import java.util.List;
import java.util.Objects;

import static net.mebahel.antiquebeasts.entity.custom.other.DraugrOverlordEntity.AreaCrackedGround;


public class DraugrOverlordSpecialAttackGoal extends Goal {
    private final DraugrOverlordEntity overlord;

    public DraugrOverlordSpecialAttackGoal(DraugrOverlordEntity overlord) {
        this.overlord = overlord;
    }

    public boolean canStart() {
        LivingEntity livingEntity = this.overlord.getTarget();

        if (livingEntity instanceof PlayerEntity) {
            PlayerEntity playerEntity = (PlayerEntity) livingEntity;
            if (playerEntity.isCreative() || playerEntity.isSpectator()) {
                return false;
            }
        }
        return livingEntity != null && livingEntity.isAlive() && !this.overlord.isBlocking() && !this.overlord.isSpinning();
    }

    public void start() {
        if (this.overlord.getSpecialCooldown() < 60)
            this.overlord.setSpecialCooldown(60);
    }

    public void stop() {
        Objects.requireNonNull(this.overlord.getAttributeInstance(EntityAttributes.GENERIC_MOVEMENT_SPEED)).setBaseValue(this.overlord.speed);
        this.overlord.setSpecial(false);
        this.overlord.setSpecialCooldown(80 + this.overlord.getRandom().nextInt(40));
    }

    public boolean shouldContinue() {
        LivingEntity livingEntity = this.overlord.getTarget();

        if (livingEntity instanceof PlayerEntity) {
            PlayerEntity playerEntity = (PlayerEntity) livingEntity;
            if (playerEntity.isCreative() || playerEntity.isSpectator()) {
                return false;
            }
        }
        return livingEntity != null && livingEntity.isAlive() && this.overlord.getSpecialCooldown() != 0
                && !this.overlord.isBlocking() && !this.overlord.isSpinning();
    }

    public void tick() {
        if (!this.overlord.isSwinging())
            this.overlord.setSpecialCooldown(this.overlord.getSpecialCooldown() - 1);

        switch (this.overlord.getSpecialCooldown()) {
            case 0 ->
                this.stop();
            case 4 -> {
                if (this.overlord.getRandom().nextInt(2) == 0) { // 1 chance sur 5
                    if (this.overlord.getRandom().nextBoolean()) {
                        this.overlord.playSound(ModSounds.DRAUGR_TAUNT_1, 1.0F, 1.0F);
                    } else {
                        this.overlord.playSound(ModSounds.DRAUGR_TAUNT_2, 1.0F, 1.0F);
                    }
                }
            }

            case 10-> {
                this.executeSpecialAttack(10, 1f);
                AreaCrackedGround(this.overlord, this.overlord.getWorld(), this.overlord.getBlockPos(), 12);
                this.overlord.playSound(ModSounds.WEAPON_GROUND_IMPACT, 1.0f, 0.8f);
                spawnReinforcements(10);
            }
            case 17 -> {
                this.executeSpecialAttack(6,0.3f);
                AreaCrackedGround(this.overlord, this.overlord.getWorld(), this.overlord.getBlockPos(), 6);
                this.overlord.playSound(ModSounds.WEAPON_CRACKED_GROUND, 1.0f, 0.8f);
            }
            case 25 -> {
                Objects.requireNonNull(this.overlord.getAttributeInstance(EntityAttributes.GENERIC_MOVEMENT_SPEED)).setBaseValue(0f);
                this.overlord.triggerAnim("attacking", "sl_attack_quake");
                this.overlord.setSpecial(true);
            }
        }
    }

    private void executeSpecialAttack(int radius, float velocity) {
        List<LivingEntity> entities = this.overlord.getWorld().getEntitiesByClass(
                LivingEntity.class,
                this.overlord.getBoundingBox().expand(radius),
                entity -> !(entity instanceof DraugrEntity) && entity.isAlive()
        );

        for (LivingEntity entity : entities) {
            entity.damage(this.overlord.getWorld().getDamageSources().mobAttack(this.overlord), 20.0f);
            entity.addStatusEffect(new StatusEffectInstance(StatusEffects.SLOWNESS, 6 * 20, 2));

            // ✅ Calculer la direction de repoussement
            double deltaX = entity.getX() - this.overlord.getX();
            double deltaZ = entity.getZ() - this.overlord.getZ();
            double distance = Math.sqrt(deltaX * deltaX + deltaZ * deltaZ);

            if (distance > 0) {
                entity.setVelocity(0, velocity, 0);
            }
        }
    }

    private void spawnReinforcements(int radius) {
        var world = overlord.getWorld();
        if (world.isClient) return;

        float hpRatio = overlord.getHealth() / overlord.getMaxHealth();
        var random = overlord.getRandom();

        // ---- Combien de mobs ? ----
        int count;
        if (hpRatio > 0.70f) {
            // 50% de chance d'en avoir 1, sinon 0
            count = random.nextBoolean() ? 1 : 0;
        } else if (hpRatio > 0.40f) {
            // Toujours 1
            count = 1;
        } else {
            // 1 ou 2 (50/50)
            count = 1 + random.nextInt(2); // 1–2
        }

        if (count <= 0) return;

        for (int i = 0; i < count; i++) {

            // ---- Choix du type selon HP ----
            float roll = random.nextFloat();
            EntityType<? extends DraugrEntity> entityType;

            if (hpRatio > 0.70f) {
                // Early : que du Draugr normal
                entityType = ModEntities.DRAUGR;

            } else if (hpRatio > 0.40f) {
                // Mid HP : mix Draugr / Wight / Scourge
                // 50% Draugr, 30% Wight, 20% Scourge
                if (roll < 0.5f) {
                    entityType = ModEntities.DRAUGR;
                } else if (roll < 0.8f) {
                    entityType = ModEntities.DRAUGR_WIGHT;
                } else {
                    entityType = ModEntities.DRAUGR_SCOURGE; // adapte le nom si besoin
                }

            } else {
                // Low HP : plus de mobs, plus d'élites
                // 25% Draugr, 40% Wight, 35% Scourge
                if (roll < 0.25f) {
                    entityType = ModEntities.DRAUGR;
                } else if (roll < 0.65f) {
                    entityType = ModEntities.DRAUGR_WIGHT;
                } else {
                    entityType = ModEntities.DRAUGR_SCOURGE; // adapte le nom si besoin
                }
            }

            DraugrEntity draugr = entityType.create(world);
            if (draugr == null) continue;

            // --- Position dans un cercle autour du boss ---
            double angle = random.nextDouble() * Math.PI * 2;
            double distance = 2 + random.nextDouble() * (radius - 2);

            double x = overlord.getX() + Math.cos(angle) * distance;
            double z = overlord.getZ() + Math.sin(angle) * distance;
            double y = overlord.getY();

            BlockPos spawnPos = new BlockPos((int)x, (int)y, (int)z);

            draugr.refreshPositionAndAngles(x, y, z, random.nextFloat() * 360F, 0);

            draugr.initialize(
                    (ServerWorldAccess) world,
                    world.getLocalDifficulty(spawnPos),
                    SpawnReason.MOB_SUMMONED,
                    null,
                    null
            );

            // Anim de spawn + état spécial si tu veux
            draugr.setHasSpawned(false);

            world.spawnEntity(draugr);

            LivingEntity target = overlord.getTarget();
            if (target != null) {
                draugr.setTarget(target);
            }
        }
    }
}