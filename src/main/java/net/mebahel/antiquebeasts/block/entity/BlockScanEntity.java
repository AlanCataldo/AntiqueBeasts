package net.mebahel.antiquebeasts.block.entity;

import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtHelper;
import net.minecraft.registry.Registries;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import software.bernie.geckolib.core.animatable.GeoAnimatable;
import software.bernie.geckolib.core.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.core.animatable.instance.SingletonAnimatableInstanceCache;
import software.bernie.geckolib.core.animation.AnimatableManager;

public class BlockScanEntity extends Entity implements GeoAnimatable {
    private final AnimatableInstanceCache factory = new SingletonAnimatableInstanceCache(this);
    private BlockState blockState;
    private boolean blockStateSet = false;
    private int timerTodespawn = 0;

    public BlockScanEntity(EntityType<?> type, World world) {
        super(type, world);
        this.blockState = Blocks.STONE.getDefaultState(); // Default block state
    }

    public void setBlockState(BlockState blockState) {
        this.blockState = blockState;
    }

    @Override
    public void tick() {
        super.tick();
        this.timerTodespawn++;

        // ✅ Stocke le bloc en dessous uniquement lors du premier tick
        if (!blockStateSet) {
            int upOrDown = Math.random() < 0.5 ? 1 : 2;
            BlockPos posBelow;
            if (upOrDown == 1)
                posBelow = this.getBlockPos().up();
            else
                posBelow = this.getBlockPos();
            this.blockState = this.getWorld().getBlockState(posBelow);
            this.blockStateSet = true;
        }

        if (timerTodespawn <= 6) {
            // ✅ Monte légèrement pendant les 10 premiers ticks
            this.setPosition(this.getX(), this.getY() + 0.08, this.getZ());
        } else {
            // ✅ Tremblement aléatoire
            double shakeX = (random.nextDouble() - 0.5) * 0.02;
            double shakeZ = (random.nextDouble() - 0.5) * 0.02;

            // ✅ Descend progressivement avec tremblement
            this.setPosition(this.getX() + shakeX, this.getY() - 0.03, this.getZ() + shakeZ);
        }

        if(timerTodespawn >= 30){
            this.discard();
            this.timerTodespawn = 0;
        }
    }
    public BlockState getBlockState() {
        return blockState;
    }
    @Override
    protected void initDataTracker() {

    }

    @Override
    protected void readCustomDataFromNbt(NbtCompound nbt) {
        // Read block state from NBT
        this.blockState = NbtHelper.toBlockState(Registries.BLOCK.getReadOnlyWrapper(), nbt.getCompound("BlockState"));
    }

    @Override
    protected void writeCustomDataToNbt(NbtCompound nbt) {
        // Write block state to NBT
        nbt.put("BlockState", NbtHelper.fromBlockState(this.blockState));
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllerRegistrar) {

    }

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return factory;
    }

    @Override
    public double getTick(Object o) {
        return 0;
    }
}