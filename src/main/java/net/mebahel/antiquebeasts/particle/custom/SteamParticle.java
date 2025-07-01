package net.mebahel.antiquebeasts.particle.custom;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.particle.*;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.particle.DefaultParticleType;

public class SteamParticle extends SpriteBillboardParticle {

    protected SteamParticle(ClientWorld world, double x, double y, double z, double xd, double yd, double zd) {
        super(world, x, y, z, xd, yd, zd);

        // === Apparence générale ===
        this.scale = 0.08F + this.random.nextFloat() * 0.13F; // taille initiale légèrement aléatoire
        this.maxAge = 10 + random.nextInt(8);               // durée de vie entre 10–18 ticks
        this.velocityMultiplier = 0.8F;                     // inertie douce

        // === Couleur gris-blanc POOF-like ===
        float base = 0.85F + this.random.nextFloat() * 0.15F; // 0.85 à 1.0
        this.red = base;
        this.green = base;
        this.blue = base;

        // === Pas d’alpha fixé (OPAQUE sheet s’en charge automatiquement) ===
    }

    @Override
    public void tick() {
        super.tick();

        // === Rétrécissement progressif ===
        this.scale *= 0.96F;

        // === Disparition douce via alpha calculée, même si pas utilisée en OPAQUE ===
        this.alpha = 1.0F - ((float) this.age / (float) this.maxAge);

        // === Optionnel : oscillation subtile pour donner vie ===
        if (this.random.nextFloat() < 0.05F) {
            this.velocityX += (this.random.nextFloat() - 0.5F) * 0.01;
            this.velocityZ += (this.random.nextFloat() - 0.5F) * 0.01;
        }
    }

    @Override
    public ParticleTextureSheet getType() {
        return ParticleTextureSheet.PARTICLE_SHEET_OPAQUE;
    }

    @Environment(EnvType.CLIENT)
    public static class Factory implements ParticleFactory<DefaultParticleType> {
        private final SpriteProvider spriteSet;

        public Factory(SpriteProvider spriteSet) {
            this.spriteSet = spriteSet;
        }

        @Override
        public Particle createParticle(DefaultParticleType type, ClientWorld world, double x, double y, double z,
                                       double dx, double dy, double dz) {
            SteamParticle particle = new SteamParticle(world, x, y, z, dx, dy, dz);
            particle.setSprite(this.spriteSet);
            return particle;
        }
    }
}
