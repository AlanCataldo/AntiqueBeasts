package net.mebahel.antiquebeasts.particle.custom;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.particle.*;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.particle.DefaultParticleType;

public class GroundFlameBurstParticle extends SpriteBillboardParticle {

    protected GroundFlameBurstParticle(ClientWorld world,
                                       double x, double y, double z,
                                       double xd, double yd, double zd) {
        super(world, x, y, z);

        // taille proche de ta flame custom
        this.scale *= 0.28F + this.random.nextFloat() * 0.12F;

        // durée de vie courte
        int base = 12 + this.random.nextInt(10);
        this.maxAge = Math.max(1, (int)(base * 0.75f));// 12..21

        // un peu de frein pour éviter "fusée"
        this.velocityMultiplier = 0.7F;

        // Full bright
        this.red = 1.0F;
        this.green = 1.0F;
        this.blue = 1.0F;
        this.alpha = 0.95F;

        // Gravité: retombe après la montée
        this.gravityStrength = 0.055F;

        // Dispersion horizontale (petit cône)
        double spread = 0.06;
        this.velocityX = (this.random.nextDouble() - 0.5) * spread + xd * 0.15;
        this.velocityZ = (this.random.nextDouble() - 0.5) * spread + zd * 0.15;

        // Impulsion vers le haut (c’est ça le “sort du sol”)
        this.velocityY = 0.12 + this.random.nextDouble() * 0.08 + yd * 0.15;

        // Pour qu'elle ne "flotte" pas trop
        this.collidesWithWorld = true;
    }

    @Override
    public int getBrightness(float tickDelta) {
        return 240;
    }

    @Override
    public void tick() {
        super.tick();

        // léger jitter pour casser la trajectoire parfaite
        if (this.random.nextFloat() < 0.20F) {
            this.velocityX += (this.random.nextFloat() * 2F - 1F) * 0.0020F;
            this.velocityZ += (this.random.nextFloat() * 2F - 1F) * 0.0020F;
        }

        // Fade-out progressif
        float life = (float) this.age / (float) this.maxAge;
        this.alpha = Math.max(0.0F, 1.0F - (life * life));
    }

    @Override
    public ParticleTextureSheet getType() {
        return ParticleTextureSheet.PARTICLE_SHEET_TRANSLUCENT;
    }

    @Environment(EnvType.CLIENT)
    public static class Factory implements ParticleFactory<DefaultParticleType> {
        private final SpriteProvider sprites;

        public Factory(SpriteProvider sprites) {
            this.sprites = sprites;
        }

        @Override
        public Particle createParticle(DefaultParticleType type, ClientWorld world,
                                       double x, double y, double z,
                                       double dx, double dy, double dz) {
            GroundFlameBurstParticle p = new GroundFlameBurstParticle(world, x, y, z, dx, dy, dz);
            p.setSprite(this.sprites);
            return p;
        }
    }
}
