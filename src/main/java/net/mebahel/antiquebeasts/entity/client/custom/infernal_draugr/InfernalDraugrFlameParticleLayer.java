package net.mebahel.antiquebeasts.entity.client.custom.infernal_draugr;

import net.mebahel.antiquebeasts.entity.custom.other.InfernalDraugrEntity;
import net.mebahel.antiquebeasts.particle.ModParticles;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.world.World;
import org.joml.Vector3d;
import software.bernie.geckolib.cache.object.GeoBone;
import software.bernie.geckolib.renderer.GeoRenderer;
import software.bernie.geckolib.renderer.layer.GeoRenderLayer;

import java.util.HashMap;
import java.util.Map;

public class InfernalDraugrFlameParticleLayer extends GeoRenderLayer<InfernalDraugrEntity> {

    private static final String BONE_RIGHT_HAND = "right_hand";
    private static final String BONE_SUMMON_HAND = "right_hand_summoning";

    // ===== Mode normal (goutte à goutte) =====
    private static final float NORMAL_SPAWN_CHANCE_PER_TICK = 0.12f;
    private static final double NORMAL_OFFSET_RADIUS_XZ = 0.10;
    private static final double NORMAL_OFFSET_RADIUS_Y  = 0.03;
    private static final int    NORMAL_PARTICLE_COUNT_MIN = 1;
    private static final int    NORMAL_PARTICLE_COUNT_MAX = 2;

    // ===== Mode summoning (plus dense, plus large, Y plus haut) =====
    // En summoning, on force quasi chaque tick (tu peux baisser si trop)
    private static final float SUMMON_SPAWN_CHANCE_PER_TICK = 0.75f;

    // Nuage plus large et plus "haut"
    private static final double SUMMON_OFFSET_RADIUS_XZ = 0.35;
    private static final double SUMMON_OFFSET_RADIUS_Y  = 0.35;

    // Plus de particules par tick
    private static final int SUMMON_PARTICLE_COUNT_MIN = 1;
    private static final int SUMMON_PARTICLE_COUNT_MAX = 2;

    // Drift/chute (un peu plus vivant en summon)
    private static final double NORMAL_DRIFT_XZ = 0.0012;
    private static final double SUMMON_DRIFT_XZ = 0.0035;

    private static final double NORMAL_FALL_MIN = -0.015;
    private static final double NORMAL_FALL_VAR = 0.006;

    private static final double SUMMON_FALL_MIN = -0.010;
    private static final double SUMMON_FALL_VAR = 0.010;

    // Anti-burst : max 1 traitement / tick / bone / entité
    private final Map<Long, Integer> lastTickProcessed = new HashMap<>();

    public InfernalDraugrFlameParticleLayer(GeoRenderer<InfernalDraugrEntity> renderer) {
        super(renderer);
    }

    @Override
    public void renderForBone(MatrixStack poseStack,
                              InfernalDraugrEntity animatable,
                              GeoBone bone,
                              RenderLayer renderType,
                              VertexConsumerProvider bufferSource,
                              VertexConsumer buffer,
                              float partialTick,
                              int packedLight,
                              int packedOverlay) {

        World world = animatable.getWorld();
        if (world == null || !world.isClient) return;
        if (!animatable.getHasSpawned()) return;

        boolean summoning = isSummoning(animatable);

        // On choisit le bone attendu selon l'état
        String expectedBone = summoning ? BONE_SUMMON_HAND : BONE_RIGHT_HAND;
        String name = bone.getName();
        if (!expectedBone.equals(name)) return;

        int tick = animatable.age;

        long key = (((long) animatable.getId()) << 32) ^ (long) name.hashCode();
        Integer last = lastTickProcessed.get(key);
        if (last != null && last == tick) return;
        lastTickProcessed.put(key, tick);

        // Gating chance
        float chance = summoning ? SUMMON_SPAWN_CHANCE_PER_TICK : NORMAL_SPAWN_CHANCE_PER_TICK;
        if (animatable.getRandom().nextFloat() > chance) return;

        Vector3d wp = bone.getWorldPosition();
        double x = wp.x;
        double y = wp.y;
        double z = wp.z;

        // Spawn
        spawnFlamesAroundBone(world, animatable, x, y, z, summoning);

        // Nettoyage map (évite gonflement)
        if (lastTickProcessed.size() > 4096 && (tick % 200) == 0) {
            lastTickProcessed.entrySet().removeIf(e -> (tick - e.getValue()) > 200);
        }
    }

    /**
     * Détecte ton état "summoning".
     * Remplace ici par ton vrai flag (dataTracker / méthode / cooldown / anim state).
     */
    private boolean isSummoning(InfernalDraugrEntity e) {
        // Exemple 1 (idéal) : tu as une méthode dédiée
        // return e.isSummoning();

        // Exemple 2 : tu utilises un flag existant (à adapter)
        // return e.isShooting(); // NON, juste un exemple

        // Placeholder: à remplacer par ta vraie condition
        return e.isShooting(); // <-- CHANGE ÇA
    }

    private void spawnFlamesAroundBone(World world, InfernalDraugrEntity e,
                                       double bx, double by, double bz,
                                       boolean summoning) {

        double radiusXZ = summoning ? SUMMON_OFFSET_RADIUS_XZ : NORMAL_OFFSET_RADIUS_XZ;
        double radiusY  = summoning ? SUMMON_OFFSET_RADIUS_Y  : NORMAL_OFFSET_RADIUS_Y;

        int minCount = summoning ? SUMMON_PARTICLE_COUNT_MIN : NORMAL_PARTICLE_COUNT_MIN;
        int maxCount = summoning ? SUMMON_PARTICLE_COUNT_MAX : NORMAL_PARTICLE_COUNT_MAX;
        int count = (maxCount <= minCount)
                ? minCount
                : (minCount + e.getRandom().nextInt((maxCount - minCount) + 1));

        double driftXZ = summoning ? SUMMON_DRIFT_XZ : NORMAL_DRIFT_XZ;

        double fallMin = summoning ? SUMMON_FALL_MIN : NORMAL_FALL_MIN;
        double fallVar = summoning ? SUMMON_FALL_VAR : NORMAL_FALL_VAR;

        for (int i = 0; i < count; i++) {
            // Random uniforme dans un disque (pas un carré)
            double u = e.getRandom().nextDouble();
            double r = Math.sqrt(u) * radiusXZ;
            double theta = e.getRandom().nextDouble() * (Math.PI * 2.0);

            double ox = Math.cos(theta) * r;
            double oz = Math.sin(theta) * r;

            // Y: plus étendu en summoning
            double oy = (e.getRandom().nextDouble() - 0.5) * radiusY;

            double px = bx + ox;
            double py = by + oy;
            double pz = bz + oz;

            // Drift + chute
            double vx = (e.getRandom().nextDouble() - 0.5) * driftXZ;
            double vz = (e.getRandom().nextDouble() - 0.5) * driftXZ;
            double vy = fallMin - e.getRandom().nextDouble() * fallVar;

            world.addParticle(ModParticles.FLAME_ATRONACH_HAND_PARTICLE, px, py, pz, vx, vy, vz);

            // Ash un peu plus fréquent en summon (optionnel)
            float ashChance = summoning ? 0.08f : 0.02f;
            if (e.getRandom().nextFloat() < ashChance) {
                world.addParticle(ParticleTypes.ASH, px, py, pz, 0.0, -0.02, 0.0);
            }
        }
    }
}
