package net.mebahel.antiquebeasts.particle.custom;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.particle.*;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.particle.DefaultParticleType;

public class SnowFlakeParticle extends SpriteBillboardParticle {
    protected SnowFlakeParticle(ClientWorld clientWorld, double xCord, double yCord, double zCord,
                                double xd, double yd, double zd) {
        super(clientWorld, xCord, yCord, zCord, xd, yd, zd);

        this.velocityMultiplier = 0.99F;  // Ralentissement léger chaque tick
        this.scale *= 0.8F;  // Taille du flocon ajustée
        this.maxAge = 20;  // Durée de vie plus longue, entre 100 et 120 ticks

        this.red = 1.0F;
        this.green = 1.0F;
        this.blue = 1.0F;

        this.gravityStrength = 0.002F;  // Gravité réduite pour une chute très lente
        this.velocityX = (this.random.nextFloat() * 2.0F - 1.0F) * 0.005F;  // Mouvement horizontal aléatoire léger
        this.velocityY = -0.025F;  // Vitesse de chute plus lente
        this.velocityZ = (this.random.nextFloat() * 2.0F - 1.0F) * 0.005F;  // Mouvement horizontal aléatoire léger
    }

    @Override
    public void tick() {
        super.tick();

        // Appliquer la gravité douce pour ralentir la chute
        if (!this.onGround) {
            this.velocityY -= this.gravityStrength;
        }

        // Ajouter des mouvements horizontaux subtils
        if (this.random.nextFloat() < 0.1F) {
            this.velocityX += (this.random.nextFloat() * 2.0F - 1.0F) * 0.002F;
            this.velocityZ += (this.random.nextFloat() * 2.0F - 1.0F) * 0.002F;
        }

        // Appliquer un léger ralentissement avec velocityMultiplier
        this.velocityX *= this.velocityMultiplier;
        this.velocityY *= this.velocityMultiplier;
        this.velocityZ *= this.velocityMultiplier;

        // Diminuer progressivement la transparence pour un effet de disparition
        fadeOut();
    }

    private void fadeOut() {
        this.alpha = Math.max(0, 1.0F - (float)this.age / (float)this.maxAge);  // Disparition progressive
    }

    @Override
    public ParticleTextureSheet getType() {
        return ParticleTextureSheet.PARTICLE_SHEET_TRANSLUCENT;  // Flocon de neige translucide
    }

    @Environment(EnvType.CLIENT)
    public static class Factory implements ParticleFactory<DefaultParticleType> {
        private final SpriteProvider sprites;

        public Factory(SpriteProvider spriteSet) {
            this.sprites = spriteSet;
        }

        public Particle createParticle(DefaultParticleType particleType, ClientWorld level, double x, double y, double z,
                                       double dx, double dy, double dz) {
            SnowFlakeParticle particle = new SnowFlakeParticle(level, x, y, z, dx, dy, dz);
            particle.setSprite(this.sprites);
            return particle;
        }
    }
}
