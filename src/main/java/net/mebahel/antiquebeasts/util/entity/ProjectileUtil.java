package net.mebahel.antiquebeasts.util.entity;

import net.mebahel.antiquebeasts.entity.projectiles.DraugrWightProjectileEntity;
import net.mebahel.antiquebeasts.entity.projectiles.FrostSpikeEntity;
import net.mebahel.antiquebeasts.item.custom.ModItems;
import net.mebahel.antiquebeasts.sound.ModSounds;
import net.minecraft.entity.LivingEntity;
import net.minecraft.sound.SoundCategory;
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

    public void scrollIceSpike(World world, LivingEntity actor, float iceSpikeDamage) {
        FrostSpikeEntity iceSpikeEntity = new FrostSpikeEntity(world, actor, iceSpikeDamage);
        Vec3d direction = actor.getRotationVec(1.0F);

        // Vérifier si l'item est dans la main droite ou gauche
        boolean isRightHand = (actor.getMainHandStack().getItem() == ModItems.ICE_SPIKE_SCROLL); // Assurez-vous que ModItems.SCROLL est l'item de votre parchemin

        // Décalage en fonction de la main active
        double sideOffset = isRightHand ? 0.4 : -0.4; // Décalage positif pour la main droite, négatif pour la main gauche
        double offsetX = -direction.z * sideOffset;
        double offsetZ = direction.x * sideOffset;
        double offsetY = 1.4; // Hauteur par rapport à la main

        Vec3d handPosition = actor.getPos().add(offsetX, offsetY, offsetZ);

        iceSpikeEntity.setVelocity(direction.x, direction.y, direction.z, 1F, 0);
        iceSpikeEntity.setPosition(handPosition.x, handPosition.y, handPosition.z);
        world.playSound(null, handPosition.x, handPosition.y, handPosition.z,
                ModSounds.DRAUGR_ICE_SPIKE, SoundCategory.NEUTRAL, 0.8F, 1.0F);
        world.spawnEntity(iceSpikeEntity);
    }

    public void scrollFrostbite(World world, LivingEntity actor, float iceSpikeDamage) {
        DraugrWightProjectileEntity frostbiteEntity = new DraugrWightProjectileEntity(world, actor, iceSpikeDamage);
        Vec3d direction = actor.getRotationVec(1.0F);

        // Vérifier si l'item est dans la main droite ou gauche
        boolean isRightHand = (actor.getMainHandStack().getItem() == ModItems.FROSTBITE_SCROLL);

        // Décalage en fonction de la main active
        double sideOffset = isRightHand ? 0.4 : -0.4;
        double offsetX = -direction.z * sideOffset;
        double offsetZ = direction.x * sideOffset;
        double offsetY = 1.4;

        Vec3d handPosition = actor.getPos().add(offsetX, offsetY, offsetZ);

        frostbiteEntity.setVelocity(direction.x, direction.y, direction.z, 1F, 0);
        frostbiteEntity.setPosition(handPosition.x, handPosition.y, handPosition.z);
        world.spawnEntity(frostbiteEntity);
    }
}
