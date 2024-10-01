package net.mebahel.antiquebeasts.particle.custom;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.particle.*;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.particle.DefaultParticleType;

public class HealingParticle extends SpriteBillboardParticle {
    protected HealingParticle(ClientWorld clientWorld, double xCord, double yCord, double zCord,
                              double xd, double yd, double zd) {
        super(clientWorld, xCord, yCord, zCord, xd, yd, zd);

        this.velocityMultiplier = 0.99F;  // Ralentissement léger chaque tick
        this.scale *= 0.4F;  // Taille ajustée pour être plus petite
        this.maxAge = 40;  // Durée de vie plus courte pour des particules plus éphémères

        // Alternance entre deux couleurs, similaire au Totem of Undying
        if (this.random.nextBoolean()) {
            setColor(0x50FF50);  // Vert clair (comme la partie verte du totem)
        } else {
            setColor(0xFFFF00);  // Jaune (comme la partie jaune du totem)
        }

        this.gravityStrength = 0.002F;  // Gravité réduite pour une chute lente
        this.velocityX = (this.random.nextFloat() * 2.0F - 1.0F) * 0.005F;  // Légers mouvements horizontaux
        this.velocityY = -0.02F;  // Particules qui tombent doucement
        this.velocityZ = (this.random.nextFloat() * 2.0F - 1.0F) * 0.005F;  // Légers mouvements en profondeur
    }

    // Méthode pour définir la couleur à partir d'une valeur hexadécimale
    private void setColor(int color) {
        this.red = (float) ((color >> 16) & 255) / 255.0F;
        this.green = (float) ((color >> 8) & 255) / 255.0F;
        this.blue = (float) (color & 255) / 255.0F;
    }

    @Override
    public void tick() {
        super.tick();

        // Appliquer la gravité douce pour ralentir la chute
        if (!this.onGround) {
            this.velocityY -= this.gravityStrength;
        }

        // Légers ajustements de la direction pour simuler une "pluie" de particules
        if (this.random.nextFloat() < 0.1F) {
            this.velocityX += (this.random.nextFloat() * 2.0F - 1.0F) * 0.002F;
            this.velocityZ += (this.random.nextFloat() * 2.0F - 1.0F) * 0.002F;
        }

        // Appliquer un léger ralentissement avec le velocityMultiplier
        this.velocityX *= this.velocityMultiplier;
        this.velocityY *= this.velocityMultiplier;
        this.velocityZ *= this.velocityMultiplier;

        // Diminuer la transparence au fil du temps pour un effet de disparition
        fadeOut();
    }

    private void fadeOut() {
        this.alpha = Math.max(0, 1.0F - (float)this.age / (float)this.maxAge);  // Disparition progressive
    }

    @Override
    public ParticleTextureSheet getType() {
        return ParticleTextureSheet.PARTICLE_SHEET_TRANSLUCENT;  // Utilise une texture translucide pour les particules
    }

    @Environment(EnvType.CLIENT)
    public static class Factory implements ParticleFactory<DefaultParticleType> {
        private final SpriteProvider sprites;

        public Factory(SpriteProvider spriteSet) {
            this.sprites = spriteSet;
        }

        public Particle createParticle(DefaultParticleType particleType, ClientWorld level, double x, double y, double z,
                                       double dx, double dy, double dz) {
            HealingParticle particle = new HealingParticle(level, x, y, z, dx, dy, dz);
            particle.setSprite(this.sprites);
            return particle;
        }
    }
}
