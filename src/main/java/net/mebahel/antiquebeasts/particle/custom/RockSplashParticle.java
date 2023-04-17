package net.mebahel.antiquebeasts.particle.custom;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.particle.*;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.particle.DefaultParticleType;

public class RockSplashParticle extends SpriteBillboardParticle {
    protected RockSplashParticle(ClientWorld clientWorld, double xCord, double yCord, double zCord,
                                 double xd, double yd, double zd) {
        super(clientWorld, xCord, yCord, zCord, xd, yd, zd);

        this.velocityMultiplier = 0.92F;
        this.x = xd;
        this.y = yd;
        this.z = zd;
        this.scale *= 1.85F;
        this.maxAge = 8;

        this.red = 1;
        this.green = 1;
        this.blue = 1;
    }

    @Override
    public void tick() {
        super.tick();
        fadeOut();
    }

    private void fadeOut() {
        this.alpha = (-(1/(float)maxAge) * age +1);
    }

    @Override
    public ParticleTextureSheet getType() {
        return ParticleTextureSheet.PARTICLE_SHEET_OPAQUE;
    }

    @Environment(EnvType.CLIENT)
    public static class Factory implements ParticleFactory<DefaultParticleType> {
        private final SpriteProvider sprites;

        public Factory(SpriteProvider spriteSet) {
            this.sprites = spriteSet;
        }

        public Particle createParticle(DefaultParticleType particleType, ClientWorld level, double x, double y, double z,
                                       double dx, double dy, double dz) {
            RockSplashParticle rockSplashParticle = new RockSplashParticle(level, x, y, z, dx, dy, dz);
            rockSplashParticle.setAlpha(0.95F);
            rockSplashParticle.setSprite(this.sprites);
            return rockSplashParticle;
        }
    }
}
