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
    public static final DefaultParticleType SNOWFLAKE_PARTICLE = FabricParticleTypes.simple();
    public static final DefaultParticleType SNOWFLAKE_HAND_PARTICLE = FabricParticleTypes.simple();
    public static final DefaultParticleType VENOM_PARTICLE = FabricParticleTypes.simple();
    public static final DefaultParticleType VENOM_SLOW_PARTICLE = FabricParticleTypes.simple();
    public static final DefaultParticleType MUMMY_PROJECTILE_PARTICLE = FabricParticleTypes.simple();
    public static final DefaultParticleType MUMMY_HOVERING_PARTICLE = FabricParticleTypes.simple();
    public static final DefaultParticleType HEALING_PARTICLE = FabricParticleTypes.simple();

    public static void registerParticles() {
        Registry.register(Registries.PARTICLE_TYPE, new Identifier(AntiqueBeasts.MOD_ID, "rocksplash_particle"),
                ROCKSPLASH_PARTICLE);
        Registry.register(Registries.PARTICLE_TYPE, new Identifier(AntiqueBeasts.MOD_ID, "snowrocksplash_particle"),
                SNOWROCKSPLASH_PARTICLE);
        Registry.register(Registries.PARTICLE_TYPE, new Identifier(AntiqueBeasts.MOD_ID, "snowflake_particle"),
                SNOWFLAKE_PARTICLE);
        Registry.register(Registries.PARTICLE_TYPE, new Identifier(AntiqueBeasts.MOD_ID, "snowflake_hand_particle"),
                SNOWFLAKE_HAND_PARTICLE);
        Registry.register(Registries.PARTICLE_TYPE, new Identifier(AntiqueBeasts.MOD_ID, "venom_particle"),
                VENOM_PARTICLE);
        Registry.register(Registries.PARTICLE_TYPE, new Identifier(AntiqueBeasts.MOD_ID, "venom_slow_particle"),
                VENOM_SLOW_PARTICLE);
        Registry.register(Registries.PARTICLE_TYPE, new Identifier(AntiqueBeasts.MOD_ID, "mummy_projectile_particle"),
                MUMMY_PROJECTILE_PARTICLE);
        Registry.register(Registries.PARTICLE_TYPE, new Identifier(AntiqueBeasts.MOD_ID, "mummy_hovering_particle"),
                MUMMY_HOVERING_PARTICLE);
        Registry.register(Registries.PARTICLE_TYPE, new Identifier(AntiqueBeasts.MOD_ID, "healing_particle"),
                HEALING_PARTICLE);
        AntiqueBeasts.LOGGER.info("[AntiqueBeasts] Registering particles for " + AntiqueBeasts.MOD_ID + ".");
    }
}
