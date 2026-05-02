package net.mebahel.antiquebeasts.entity.projectiles;

import net.mebahel.antiquebeasts.entity.custom.other.FlameAtronachEntity;
import net.mebahel.antiquebeasts.particle.ModParticles;
import net.mebahel.antiquebeasts.sound.ModSounds;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.EndGatewayBlockEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.projectile.ProjectileUtil;
import net.minecraft.entity.projectile.thrown.ThrownItemEntity;
import net.minecraft.item.Item;
import net.minecraft.item.Items;
import net.minecraft.particle.BlockStateParticleEffect;
import net.minecraft.particle.ParticleEffect;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.core.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.core.animatable.instance.SingletonAnimatableInstanceCache;
import software.bernie.geckolib.core.animation.*;
import software.bernie.geckolib.core.object.PlayState;

import java.util.Random;

import static net.mebahel.antiquebeasts.entity.ModEntities.FIREBOLT_PROJECTILE;

public class FireboltEntity extends ThrownItemEntity implements GeoEntity {
    float damage;

    public FireboltEntity(World world, LivingEntity owner, float damage) {
        super(FIREBOLT_PROJECTILE, owner, world);
        this.damage = damage;
    }

    public FireboltEntity(EntityType<? extends FireboltEntity> entityType, World world) {
        super(entityType, world);
    }

    public FireboltEntity(World world, float damage) {
        super(FIREBOLT_PROJECTILE, world);
        this.damage = damage;
    }

    private final AnimatableInstanceCache factory = new SingletonAnimatableInstanceCache(this);

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return factory;
    }

    private PlayState predicate(AnimationState animationState) {
        animationState.getController().setAnimation(RawAnimation.begin().then("idle", Animation.LoopType.LOOP));
        return PlayState.CONTINUE;
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
        controllers.add(new AnimationController(this, "controller",0, this::predicate));
    }

    @Override
    protected Item getDefaultItem() {
        return Items.FIRE_CHARGE;
    }
    protected void onCollision(HitResult hitResult) {
        super.onCollision(hitResult);
        if (!this.getWorld().isClient) {
            this.getWorld().sendEntityStatus(this, (byte)3);
            this.discard();
        }
    }

    @Override
    protected void onEntityHit(EntityHitResult entityHitResult) {
        Entity hit = entityHitResult.getEntity();

        // 1) Friendly-fire: si le projectile vient d'un FlameAtronach summoned
        // et touche son owner -> on discard sans rien faire (pas de dégâts, pas de feu, pas de particules)
        Entity shooter = this.getOwner();
        if (!this.getWorld().isClient && shooter instanceof FlameAtronachEntity fa && fa.isSummoned()) {
            LivingEntity ownerPlayer = fa.getOwnerEntity();
            if (ownerPlayer != null && hit == ownerPlayer) {
                this.discard();
                return;
            }
        }

        // 2) comportement normal
        if (hit instanceof LivingEntity target) {
            target.damage(this.getDamageSources().thrown(this, this.getOwner()), damage);

            if (!target.isBlocking()) {
                target.setOnFireFor(3);
            }
        }

        // particules break / discard standard
        if (!this.getWorld().isClient) {
            this.getWorld().sendEntityStatus(this, (byte) 3);
        }
        this.discard();
    }


    @Override
    public void handleStatus(byte status) {
        super.handleStatus(status);
        if (status == 3) { // Vérifie si c'est le signal pour les particules
            this.generateBreakParticles();
        }
    }
    @Override
    protected void onBlockHit(BlockHitResult blockHitResult) {
        BlockState blockState = this.getWorld().getBlockState(blockHitResult.getBlockPos());
        blockState.onProjectileHit(this.getWorld(), blockState, blockHitResult, this);

        // ✅ Envoyer un signal au client pour générer les particules
        if (!this.getWorld().isClient) {
            this.getWorld().sendEntityStatus(this, (byte) 3);
        }

        this.playIceBreakSound();
        this.discard();
    }

    private void generateParticles() {
        ParticleEffect particleEffect = ModParticles.FLAME_ATRONACH_HAND_PARTICLE;
        for (int i = 0; i < 2; i++) {
            double offsetX = this.random.nextGaussian() * 0.2;
            double offsetY = this.random.nextGaussian() * 0.2;
            double offsetZ = this.random.nextGaussian() * 0.2;
            this.getWorld().addParticle(particleEffect,
                    this.getX() + offsetX,
                    this.getY() + offsetY,
                    this.getZ() + offsetZ,
                    0, 0, 0);
        }
    }

    private void generateBreakParticles() {
        if (this.getWorld().isClient) {
            for (int i = 0; i < 15; i++) { // Nombre de particules
                double offsetX = (this.random.nextDouble() - 0.5) * 0.5;
                double offsetY = (this.random.nextDouble() - 0.5) * 0.5;
                double offsetZ = (this.random.nextDouble() - 0.5) * 0.5;

                this.getWorld().addParticle(
                        new BlockStateParticleEffect(ParticleTypes.BLOCK, Blocks.LAVA.getDefaultState()),
                        this.getX() + offsetX,
                        this.getY() + offsetY,
                        this.getZ() + offsetZ,
                        0, 0.1, 0
                );
            }
        }
    }

    @Override
    public void tick() {
        super.tick();

        if (this.age >= 60) {
            this.discard();
        }

        BlockPos pos = this.getBlockPos();
        BlockState state = this.getWorld().getBlockState(pos);

        if (state.isOf(Blocks.WATER)) {
            freezeWater(getWorld(), pos);

            // ✅ Gèle aussi les blocs adjacents
            for (BlockPos adjacentPos : new BlockPos[]{
                    pos.north(), pos.south(), pos.east(), pos.west(), pos.up(), pos.down()
            }) {
                if (getWorld().getBlockState(adjacentPos).isOf(Blocks.WATER)) {
                    freezeWater(getWorld(), adjacentPos);
                }
            }
        }

        HitResult hitResult = ProjectileUtil.getCollision(this, this::canHit);
        boolean bl = false;
        if (hitResult.getType() == HitResult.Type.BLOCK) {
            BlockPos blockPos = ((BlockHitResult)hitResult).getBlockPos();
            BlockState blockState = this.getWorld().getBlockState(blockPos);
            if (blockState.isOf(Blocks.NETHER_PORTAL)) {
                this.setInNetherPortal(blockPos);
                bl = true;
            } else if (blockState.isOf(Blocks.END_GATEWAY)) {
                BlockEntity blockEntity = this.getWorld().getBlockEntity(blockPos);
                if (blockEntity instanceof EndGatewayBlockEntity && EndGatewayBlockEntity.canTeleport(this)) {
                    EndGatewayBlockEntity.tryTeleportingEntity(this.getWorld(), blockPos, blockState, this, (EndGatewayBlockEntity)blockEntity);
                }
                bl = true;
            }
        }
        if (this.getWorld().isClient) {
            this.generateParticles();
        }

        if (hitResult.getType() != HitResult.Type.MISS && !bl) {
            this.onCollision(hitResult);
        }

        this.checkBlockCollision();
        Vec3d vec3d = this.getVelocity();
        double d = this.getX() + vec3d.x;
        double e = this.getY() + vec3d.y;
        double f = this.getZ() + vec3d.z;
        this.updateRotation();
        float h;
        if (this.isTouchingWater()) {
            for (int i = 0; i < 4; ++i) {
                this.getWorld().addParticle(ParticleTypes.BUBBLE, d - vec3d.x * 0.25, e - vec3d.y * 0.25, f - vec3d.z * 0.25, vec3d.x, vec3d.y, vec3d.z);
            }

            h = 0.8F;
        } else {
            h = 0.99F;
        }
        this.setVelocity(vec3d.multiply(h));
        if (!this.hasNoGravity()) {
            Vec3d vec3d2 = this.getVelocity();
            this.setVelocity(vec3d2.x, vec3d2.y, vec3d2.z); // Appliquer la gravité si activée
        }
        this.setPosition(d, e, f);
    }

    private void freezeWater(World world, BlockPos pos) {
        world.setBlockState(pos, Blocks.COBBLESTONE.getDefaultState());
        if (pos != null && !this.getWorld().isClient) {
            this.getWorld().setBlockState(pos, Blocks.COBBLESTONE.getDefaultState(), 3);

            ServerWorld sw = (ServerWorld) this.getWorld();
            sw.spawnParticles(
                    ParticleTypes.SMOKE,
                    pos.getX() + 0.5,
                    pos.getY() + 1.02,
                    pos.getZ() + 0.5,
                    6,
                    0.15, 0.05, 0.15,
                    0.0
            );
            this.getWorld().playSound(null, pos, SoundEvents.BLOCK_FIRE_EXTINGUISH, SoundCategory.BLOCKS, 0.2F, 1.2F);
        }
    }

    private void playIceBreakSound() {
        Random rand = new Random();
        this.getWorld().playSound(
                null, // Jouer le son pour tous les joueurs
                this.getX(), this.getY(), this.getZ(), // Position de l'entité
                ModSounds.FIREBOLT_IMPACT, // Son de glace cassée
                SoundCategory.HOSTILE, // Catégorie de son
                0.4F + rand.nextFloat() * 0.2F,
                0.8F + rand.nextFloat() * 0.4F
        );
    }

    @Override
    protected float getGravity() {
        return 0.005f;
    }

}
