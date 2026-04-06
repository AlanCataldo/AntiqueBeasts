package net.mebahel.antiquebeasts.util.entity;

import net.mebahel.antiquebeasts.entity.projectiles.DraugrWightProjectileEntity;
import net.mebahel.antiquebeasts.entity.projectiles.FrostSpikeEntity;
import net.mebahel.antiquebeasts.entity.projectiles.SteamProjectileEntity;
import net.mebahel.antiquebeasts.sound.ModSounds;
import net.minecraft.entity.LivingEntity;
import net.minecraft.sound.SoundCategory;
import net.minecraft.util.Hand;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

public class ProjectileUtil {
    public void shootFrostBiteProjectile(LivingEntity target, LivingEntity actor, float frostBiteDamage, Vec3d leftHandOffset) {
        double speed = 1.5;

        World world = actor.getWorld();
        DraugrWightProjectileEntity projectile = new DraugrWightProjectileEntity(world, actor, frostBiteDamage);
        Vec3d leftHandPosition = actor.getPos()
                .add(leftHandOffset.rotateY(-actor.getYaw() * ((float) Math.PI / 180)))
                .add(0, actor.getHeight() / 1.6, 0);
        Vec3d targetPosition = target.getEyePos();
        Vec3d direction = targetPosition.subtract(leftHandPosition).normalize();
        projectile.setVelocity(direction.x * speed, direction.y * speed, direction.z * speed, (float) speed, 0.0f);
        projectile.setPosition(leftHandPosition.x, leftHandPosition.y, leftHandPosition.z);
        world.spawnEntity(projectile);
    }
    public void shootSteamProjectile(World world, LivingEntity actor, Vec3d shootDirection, float damage) {
        double speed = 1.5;

        // Create your projectile entity
        SteamProjectileEntity projectile = new SteamProjectileEntity(world, actor, damage);

        // Define an offset for the nozzle — adjust as needed
        Vec3d nozzleOffset = new Vec3d(0.3, 0.3, -0.6);
        Vec3d origin = actor.getPos()
                .add(nozzleOffset.rotateY(-actor.getYaw() * ((float) Math.PI / 180)))
                .add(0, actor.getHeight()- 0.95f, 0);

        // Set projectile's position and velocity based on the fixed direction
        projectile.setVelocity(shootDirection.normalize().multiply(speed));
        projectile.setPosition(origin.x, origin.y, origin.z);

        world.spawnEntity(projectile);
    }

    public void shootProjectile(LivingEntity target, LivingEntity actor, float iceSpikeDamage, Vec3d leftHandOffset) {
        double distanceToTarget = actor.distanceTo(target);
        double speed = distanceToTarget > 12 ? 1.1 : 0.85;

        World world = actor.getWorld();
        FrostSpikeEntity projectile = new FrostSpikeEntity(world, actor, iceSpikeDamage);
        Vec3d leftHandPosition = actor.getPos()
                .add(leftHandOffset.rotateY(-actor.getYaw() * ((float) Math.PI / 180)))
                .add(0, actor.getHeight() / 1.6, 0);
        Vec3d targetPosition = target.getEyePos();
        Vec3d direction = targetPosition.subtract(leftHandPosition).normalize();
        projectile.setVelocity(direction.x * speed, direction.y * speed, direction.z * speed, (float) speed, 0.0f);
        projectile.setPosition(leftHandPosition.x, leftHandPosition.y, leftHandPosition.z);
        world.spawnEntity(projectile);
    }

    public void staffIceSpike(World world, LivingEntity actor, Hand hand, float iceSpikeDamage) {
        FrostSpikeEntity iceSpikeEntity = new FrostSpikeEntity(world, actor, iceSpikeDamage);
        Vec3d direction = actor.getRotationVec(1.0F);
        boolean isRightHand = (hand == Hand.MAIN_HAND);

        Vec3d upVector = new Vec3d(0, 1, 0);

        // ✅ Décale légèrement la position du spawn pour correspondre à la main
        Vec3d sideOffsetVec = upVector.crossProduct(direction).normalize().multiply(isRightHand ? -0.5 : 0.5);

        double offsetY = actor.getEyeY() - 0.6;
        double forwardOffset = 0.6;
        Vec3d frontOffset = direction.multiply(forwardOffset);

        Vec3d handPosition = actor.getPos().add(sideOffsetVec).add(frontOffset).add(0, offsetY - actor.getY(), 0);

        iceSpikeEntity.setPosition(handPosition.x, handPosition.y + 0.5, handPosition.z);

        // ✅ Dévier légèrement la trajectoire selon la main utilisée
        Vec3d sideVelocityOffset = upVector.crossProduct(direction).normalize().multiply(isRightHand ? 0.03 : -0.03);
        direction = direction.add(sideVelocityOffset).normalize();

        // ✅ Appliquer la vélocité ajustée
        iceSpikeEntity.setVelocity(direction.x, direction.y, direction.z, 1F, 0);

        world.playSound(null, handPosition.x, handPosition.y, handPosition.z,
                ModSounds.DRAUGR_ICE_SPIKE, SoundCategory.NEUTRAL, 0.8F, 1.0F);

        world.spawnEntity(iceSpikeEntity);
    }


    public void scrollFrostbite(World world, LivingEntity actor, Hand hand, float iceSpikeDamage) {
        DraugrWightProjectileEntity frostbiteEntity = new DraugrWightProjectileEntity(world, actor, iceSpikeDamage);
        Vec3d direction = actor.getRotationVec(1.0F);
        boolean isRightHand = (hand == Hand.MAIN_HAND);

        Vec3d upVector = new Vec3d(0, 1, 0);

        // ✅ Décale légèrement la position du spawn pour correspondre à la main
        Vec3d sideOffsetVec = upVector.crossProduct(direction).normalize().multiply(isRightHand ? -0.85 : 0.85);

        double offsetY = actor.getEyeY() - 0.6;
        double forwardOffset = 0.05;
        Vec3d frontOffset = direction.multiply(forwardOffset);

        Vec3d handPosition = actor.getPos().add(sideOffsetVec).add(frontOffset).add(0, offsetY - actor.getY(), 0);

        frostbiteEntity.setPosition(handPosition.x, handPosition.y + 1, handPosition.z);

        // ✅ Dévier légèrement la trajectoire selon la main utilisée
        Vec3d sideVelocityOffset = upVector.crossProduct(direction).normalize().multiply(isRightHand ? 0.05 : -0.05);
        direction = direction.add(sideVelocityOffset).normalize();

        // ✅ Appliquer la vélocité ajustée
        frostbiteEntity.setVelocity(direction.x, direction.y, direction.z, 1F, 0);

        world.spawnEntity(frostbiteEntity);
    }

}
