package net.mebahel.antiquebeasts.particle;

import net.fabricmc.fabric.api.particle.v1.FabricParticleTypes;
import net.mebahel.antiquebeasts.AntiqueBeasts;
import net.minecraft.particle.DefaultParticleType;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

public class ModParticles {
    public static final DefaultParticleType ROCKSPLASH_PARTICLE = FabricParticleTypes.simple();
    public static final DefaultParticleType SNOWROCKSPLASH_PARTICLE = FabricParticleTypes.simple();
    public static final DefaultParticleType VENOM_PARTICLE = FabricParticleTypes.simple();
    public static final DefaultParticleType VENOM_SLOW_PARTICLE = FabricParticleTypes.simple();
    public static final DefaultParticleType MUMMY_PROJECTILE_PARTICLE = FabricParticleTypes.simple();

    public static void registerParticles() {
        Registry.register(Registry.PARTICLE_TYPE, new Identifier(AntiqueBeasts.MOD_ID, "rocksplash_particle"),
                ROCKSPLASH_PARTICLE);
        Registry.register(Registry.PARTICLE_TYPE, new Identifier(AntiqueBeasts.MOD_ID, "snowrocksplash_particle"),
                SNOWROCKSPLASH_PARTICLE);
        Registry.register(Registry.PARTICLE_TYPE, new Identifier(AntiqueBeasts.MOD_ID, "venom_particle"),
                VENOM_PARTICLE);
        Registry.register(Registry.PARTICLE_TYPE, new Identifier(AntiqueBeasts.MOD_ID, "venom_slow_particle"),
                VENOM_SLOW_PARTICLE);
        Registry.register(Registry.PARTICLE_TYPE, new Identifier(AntiqueBeasts.MOD_ID, "mummy_projectile_particle"),
                MUMMY_PROJECTILE_PARTICLE);
        AntiqueBeasts.LOGGER.info("[AntiqueBeasts] Registering particles for " + AntiqueBeasts.MOD_ID + ".");
    }
}
