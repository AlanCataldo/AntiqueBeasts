package net.mebahel.antiquebeasts.particle.custom;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.particle.*;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.particle.DefaultParticleType;

public class FallingFlameParticle extends SpriteBillboardParticle {

    protected FallingFlameParticle(ClientWorld world,
                                   double x, double y, double z,
                                   double xd, double yd, double zd) {
        super(world, x, y, z, xd, yd, zd);

        // Taille plus petite que la FLAME vanilla
        this.scale *= 0.30F + this.random.nextFloat() * 0.10F;

        // Durée de vie courte => moins "dense"
        this.maxAge = 16 + this.random.nextInt(10); // 16..25

        // Ralentissement doux
        this.velocityMultiplier = 0.94F;

        // Couleur : flamme vanilla (tu peux laisser blanc, le sprite donne déjà le rendu)
        this.red = 1.0F;
        this.green = 1.0F;
        this.blue = 1.0F;

        // Gravité + chute nette
        this.gravityStrength = 0.03F; // important : c'est ça qui donne une vraie descente

        // On ignore (en partie) les vitesses input, et on impose une dispersion contrôlée
        double spread = 0.015; // dispersion horizontale
        this.velocityX = (this.random.nextDouble() - 0.5) * spread + xd * 0.25;
        this.velocityZ = (this.random.nextDouble() - 0.5) * spread + zd * 0.25;

        // Vitesse initiale vers le bas (en plus de la gravité)
        this.velocityY = -0.03 + (this.random.nextDouble() * -0.01);

        // Légère transparence
        this.alpha = 0.9F;
    }
    @Override
    public int getBrightness(float tickDelta) {
        // 240 = pleine luminosité (comme torch / flame vanilla)
        return 180;
    }

    @Override
    public void tick() {
        super.tick();

        // Petit drift aléatoire (très léger) pour éviter une trajectoire trop droite
        if (this.random.nextFloat() < 0.15F) {
            this.velocityX += (this.random.nextFloat() * 2F - 1F) * 0.0012F;
            this.velocityZ += (this.random.nextFloat() * 2F - 1F) * 0.0012F;
        }

        // Fade-out propre
        fadeOut();
    }

    private void fadeOut() {
        // fade plus agressif sur la fin
        float life = (float) this.age / (float) this.maxAge;
        this.alpha = Math.max(0.0F, 1.0F - (life * life));
    }

    @Override
    public ParticleTextureSheet getType() {
        // FLAME vanilla est translucide additive, mais translucent marche bien pour ce cas
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
            FallingFlameParticle p = new FallingFlameParticle(world, x, y, z, dx, dy, dz);
            p.setSprite(this.sprites);
            return p;
        }
    }
}
