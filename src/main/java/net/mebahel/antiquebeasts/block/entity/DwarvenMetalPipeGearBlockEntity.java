package net.mebahel.antiquebeasts.block.entity;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.mebahel.antiquebeasts.block.ModBlockEntities;
import net.mebahel.antiquebeasts.sound.ModSounds;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.sound.PositionedSoundInstance;
import net.minecraft.client.sound.SoundInstance;
import net.minecraft.sound.SoundCategory;
import net.minecraft.util.math.BlockPos;
import software.bernie.geckolib.animatable.GeoBlockEntity;
import software.bernie.geckolib.core.animatable.GeoAnimatable;
import software.bernie.geckolib.core.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.core.animation.*;
import software.bernie.geckolib.core.object.PlayState;
import software.bernie.geckolib.util.GeckoLibUtil;
import software.bernie.geckolib.util.RenderUtils;

public class DwarvenMetalPipeGearBlockEntity extends BlockEntity implements GeoBlockEntity {
    private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);

    public int age = 0;

    public DwarvenMetalPipeGearBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.DWARVEN_METAL_PIPE_GEAR, pos, state);
    }

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return this.cache;
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
        controllers.add(new AnimationController<>(this, "controller", 0, this::predicate));
    }

    private <T extends GeoAnimatable> PlayState predicate(AnimationState<T> event) {
        event.getController().setAnimation(RawAnimation.begin()
                .then("idle", Animation.LoopType.LOOP));
        return PlayState.CONTINUE;
    }
    @Environment(EnvType.CLIENT)
    private PositionedSoundInstance currentSound;


    @Environment(EnvType.CLIENT)
    private void playLoopingSound() {
        if (currentSound == null) {
            currentSound = new PositionedSoundInstance(
                    ModSounds.DWARVEN_GEAR_MULTIPLE.getId(),
                    SoundCategory.BLOCKS,
                    0.3F,
                    1.15F,
                    SoundInstance.createRandom(),
                    true,  // loop
                    0,     // no delay between repeats
                    SoundInstance.AttenuationType.LINEAR,
                    pos.getX() + 0.5,
                    pos.getY() + 0.5,
                    pos.getZ() + 0.5,
                    false // not relative to player
            );

            MinecraftClient.getInstance().getSoundManager().play(currentSound);
        }
    }


    @Environment(EnvType.CLIENT)
    public void clientTick() {
        if (age == 0) {
            playLoopingSound();
        }
        age++;
    }
    @Environment(EnvType.CLIENT)
    @Override
    public void markRemoved() {
        super.markRemoved();
        stopLoopingSound();
    }
    @Environment(EnvType.CLIENT)
    private void stopLoopingSound() {
        if (currentSound != null) {
            MinecraftClient.getInstance().getSoundManager().stop(currentSound);
            currentSound = null;
        }
    }


    @Override
    public double getTick(Object blockEntity) {
        return RenderUtils.getCurrentTick();
    }
}

