package net.mebahel.antiquebeasts.particle;

import net.fabricmc.fabric.api.particle.v1.FabricParticleTypes;
import net.mebahel.antiquebeasts.AntiqueBeasts;
import net.minecraft.particle.DefaultParticleType;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;

public class ModParticles {
    public static final DefaultParticleType ROCKSPLASH_PARTICLE = FabricParticleTypes.simple();
    public static final DefaultParticleType SNOWROCKSPLASH_PARTICLE = FabricParticleTypes.simple();

    public static void registerParticles() {
        Registry.register(Registries.PARTICLE_TYPE, new Identifier(AntiqueBeasts.MOD_ID, "rocksplash_particle"),
                ROCKSPLASH_PARTICLE);
        Registry.register(Registries.PARTICLE_TYPE, new Identifier(AntiqueBeasts.MOD_ID, "snowrocksplash_particle"),
                SNOWROCKSPLASH_PARTICLE);
    }
}
