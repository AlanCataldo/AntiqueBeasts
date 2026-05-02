package net.mebahel.antiquebeasts.entity.client.custom.flame_atronach;

import net.mebahel.antiquebeasts.entity.custom.other.FlameAtronachEntity;
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

public class FlameAtronachFlameParticleLayer extends GeoRenderLayer<FlameAtronachEntity> {

    private static final String BONE_RIGHT_ARM = "fx_right_arm";
    private static final String BONE_LEFT_ARM  = "fx_left_arm";
    private static final String BONE_LEGS_1      = "fx_legs_1";
    private static final String BONE_LEGS_2      = "fx_legs_2";

    // "Goutte à goutte" : chance par tick (20 TPS)
    private static final float SPAWN_CHANCE_PER_TICK = 0.12f;

    // Rayon du nuage autour du bone (en blocs). 0.03 = subtil, 0.06 = visible.
    private static final double OFFSET_RADIUS_XZ = 0.25;
    private static final double OFFSET_RADIUS_Y  = 0.03;

    private static final int LOG_EVERY_N_TICKS = 60;

    // Anti-burst : max 1 tentative / tick / bone / entité
    private final Map<Long, Integer> lastTickProcessed = new HashMap<>();

    public FlameAtronachFlameParticleLayer(GeoRenderer<FlameAtronachEntity> renderer) {
        super(renderer);
    }

    @Override
    public void renderForBone(MatrixStack poseStack,
                              FlameAtronachEntity animatable,
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

        String name = bone.getName();
        if (!BONE_RIGHT_ARM.equals(name) && !BONE_LEFT_ARM.equals(name)
                && !BONE_LEGS_1.equals(name)  && !BONE_LEGS_2.equals(name)) return;

        int tick = animatable.age;

        long key = (((long) animatable.getId()) << 32) ^ (long) name.hashCode();
        Integer last = lastTickProcessed.get(key);
        if (last != null && last == tick) return;
        lastTickProcessed.put(key, tick);

        // Goutte à goutte
        if (animatable.getRandom().nextFloat() > SPAWN_CHANCE_PER_TICK) return;

        Vector3d wp = bone.getWorldPosition();
        double x = wp.x;
        double y = wp.y;
        double z = wp.z;

        // Petits offsets fixes par bone (si besoin)
        if (BONE_LEGS_1.equals(name)) y -= 0.12;
        else y -= 0.06;

        spawnSoftFallingFlameAroundBone(world, animatable, x, y, z, name);

        if (lastTickProcessed.size() > 4096 && (tick % 200) == 0) {
            lastTickProcessed.entrySet().removeIf(e -> (tick - e.getValue()) > 200);
        }
    }

    /**
     * Spawn aléatoire autour du bone (disque XZ + petit Y),
     * puis chute vers le bas + un drift très subtil.
     */
    private void spawnSoftFallingFlameAroundBone(World world, FlameAtronachEntity e,
                                                 double bx, double by, double bz,
                                                 String boneName) {

        // Rayon éventuellement différent selon le bone
        double radiusXZ = OFFSET_RADIUS_XZ;
        double radiusY  = OFFSET_RADIUS_Y;

        if (BONE_LEGS_1.equals(boneName)) {
            radiusXZ *= 1.15; // un peu plus “diffus” au sol
            radiusY  *= 0.85;
        }

        // Random uniforme dans un disque (pas un carré)
        // r = sqrt(u) * R pour une densité uniforme
        double u = e.getRandom().nextDouble();
        double r = Math.sqrt(u) * radiusXZ;
        double theta = e.getRandom().nextDouble() * (Math.PI * 2.0);

        double ox = Math.cos(theta) * r;
        double oz = Math.sin(theta) * r;
        double oy = (e.getRandom().nextDouble() - 0.5) * radiusY;

        double px = bx + ox;
        double py = by + oy;
        double pz = bz + oz;

        // Drift très faible + chute
        double vx = (e.getRandom().nextDouble() - 0.5) * 0.0012;
        double vz = (e.getRandom().nextDouble() - 0.5) * 0.0012;
        double vy = -0.015 - e.getRandom().nextDouble() * 0.006;

        world.addParticle(ModParticles.FLAME_ATRONACH_HAND_PARTICLE, px, py, pz, vx, vy, vz);

        // Ash rare
        if (e.getRandom().nextFloat() < 0.02f) {
            world.addParticle(ParticleTypes.ASH, px, py, pz, 0.0, -0.025, 0.0);
        }
    }

    private static double round3(double v) {
        return Math.round(v * 1000.0) / 1000.0;
    }
}
