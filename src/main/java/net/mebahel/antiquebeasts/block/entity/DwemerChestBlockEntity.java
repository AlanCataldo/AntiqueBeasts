package net.mebahel.antiquebeasts.block.entity;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.fabricmc.fabric.api.screenhandler.v1.ExtendedScreenHandlerFactory;
import net.mebahel.antiquebeasts.block.ModBlockEntities;
import net.mebahel.antiquebeasts.block.screenhandlers.DraugrChestScreenHandler;
import net.mebahel.antiquebeasts.sound.ModSounds;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.ChestBlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.s2c.play.BlockEntityUpdateS2CPacket;
import net.minecraft.particle.BlockStateParticleEffect;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvent;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import software.bernie.geckolib.animatable.GeoBlockEntity;
import software.bernie.geckolib.core.animatable.GeoAnimatable;
import software.bernie.geckolib.core.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.core.animatable.instance.SingletonAnimatableInstanceCache;
import software.bernie.geckolib.core.animation.*;
import software.bernie.geckolib.core.object.PlayState;
import software.bernie.geckolib.util.RenderUtils;

public class DwemerChestBlockEntity extends ChestBlockEntity implements GeoBlockEntity, ExtendedScreenHandlerFactory {
    private AnimatableInstanceCache cache = new SingletonAnimatableInstanceCache(this);
    public boolean isOpened = false;
    public boolean hasBeenOpened = false;
    public boolean shouldDoSpawnAnimation = false;
    public int closeCooldown = 0;

    public int age = 0;
    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllerRegistrar) {
        controllerRegistrar.add(new AnimationController<>(this, "openController", 0, this::openPredicate));
        controllerRegistrar.add(new AnimationController<>(this, "spawnController", 0, this::spawnPredicate));
    }

    private <T extends GeoAnimatable> PlayState openPredicate(AnimationState<T> event) {
        if (isOpened && !this.shouldDoSpawnAnimation)
            event.getController().setAnimation(RawAnimation.begin().then("open", Animation.LoopType.PLAY_ONCE)
                    .then("isOpened", Animation.LoopType.LOOP));
        else if (hasBeenOpened && !this.shouldDoSpawnAnimation) {
            event.getController().setAnimation(RawAnimation.begin().then("close", Animation.LoopType.PLAY_ONCE)
                    .then("isClosed", Animation.LoopType.LOOP));
        }
        return PlayState.CONTINUE;
    }

    private <T extends GeoAnimatable> PlayState spawnPredicate(AnimationState<T> event) {
        AnimationController<?> controller = event.getController();
        if (event.getController().getAnimationState() != AnimationController.State.STOPPED) {
            spawnChestParticles();
        }
        if (this.shouldDoSpawnAnimation) {
            event.getController().setAnimation(RawAnimation.begin().then("spawn", Animation.LoopType.PLAY_ONCE));
            if (controller.hasAnimationFinished()) {
                this.setShouldDoSpawnAnimation(false);
                syncToServer();
            }
            return PlayState.CONTINUE;
        }
        return PlayState.STOP;
    }

    @Override
    public double getTick(Object blockEntity) {
        return RenderUtils.getCurrentTick();
    }

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return cache;
    }
    int viewerCount = 0;

    public DwemerChestBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.DWEMER_CHEST_ENTITY, pos, state);
        this.setInvStackList(DefaultedList.ofSize(this.size(), ItemStack.EMPTY));
    }

    @Override
    protected ScreenHandler createScreenHandler(int syncId, PlayerInventory inventory) {
        return new DraugrChestScreenHandler(syncId, inventory, this);

    }

    public void sync() {
        if (this.world instanceof ServerWorld serverWorld) {
            serverWorld.getChunkManager().markForUpdate(this.pos);
        }
    }

    @Override
    protected Text getContainerName() {
        return Text.translatable("Dwemer Chest");
    }

    @Override
    public int size() {
        return 36;
    }

    @Environment(EnvType.CLIENT)
    public int countViewers() {
        return viewerCount;
    }


    @Override
    public void onOpen(PlayerEntity player) {

        this.isOpened = true;
        this.hasBeenOpened = true;
        if (!player.isSpectator()) {
            ++this.viewerCount;
            markDirty();
        }
        System.out.println("onOpen");
    }

    @Override
    public void onClose(PlayerEntity player) {
        if (!player.isSpectator()) {
            --this.viewerCount;
            if (viewerCount == 0 && hasBeenOpened) {
                this.isOpened = false;
                this.closeCooldown = 4;
            }
            markDirty();
        }
    }

    @Override
    public void readNbt(NbtCompound tag) {
        super.readNbt(tag);
        this.isOpened = tag.getBoolean("isOpened");
        this.hasBeenOpened = tag.getBoolean("hasBeenOpened");
        this.shouldDoSpawnAnimation = tag.getBoolean("shouldDoSpawnAnimation");
        this.closeCooldown = tag.getInt("closeCooldown");
        this.viewerCount = tag.getInt("viewers");
    }

    @Override
    public void writeNbt(NbtCompound tag) {
        super.writeNbt(tag);
        tag.putBoolean("isOpened", this.isOpened);
        tag.putBoolean("hasBeenOpened", this.hasBeenOpened);
        tag.putBoolean("shouldDoSpawnAnimation", this.shouldDoSpawnAnimation);
        tag.putInt("closeCooldown", this.closeCooldown);
        tag.putInt("viewers", this.viewerCount);
    }

    @Override
    public void markDirty() {
        super.markDirty();
        if (this.getWorld() != null && !this.getWorld().isClient) {
            this.sync();
        }
    }
    public void setShouldDoSpawnAnimation(boolean value) {
        this.shouldDoSpawnAnimation = value;
        this.markDirty();
        this.sync(); // ✅ Synchroniser avec le client
    }
    @Override
    public Packet<ClientPlayPacketListener> toUpdatePacket() {
        return BlockEntityUpdateS2CPacket.create(this, BlockEntity::toInitialChunkDataNbt);
    }

    @Override
    public NbtCompound toInitialChunkDataNbt() {
        return this.createNbt();
    }

    @Override
    @Environment(EnvType.CLIENT)
    public float getAnimationProgress(float f) {
        return MathHelper.lerp(f, lastAnimationAngle, animationAngle);
    }

    private float animationAngle;
    private float lastAnimationAngle;

    private void syncToServer() {
        if (this.world != null && this.world.isClient) {
            PacketByteBuf buf = PacketByteBufs.create();
            buf.writeBlockPos(this.pos);
            buf.writeBoolean(this.shouldDoSpawnAnimation);
            ClientPlayNetworking.send(new Identifier("antiquebeasts", "update_chest"), buf);
        }
    }
    @Environment(EnvType.CLIENT)
    public void clientTick() {
        this.age++;
        if (this.age < 10)
            this.hasBeenOpened = false;

        if (this.closeCooldown> 0) {
            this.closeCooldown--;
        } else if (closeCooldown == 0 && this.hasBeenOpened && viewerCount == 0) {
            this.isOpened = false;
            this.hasBeenOpened = false;
            this.markDirty();
        }
        if (world != null && world.isClient) {
            int viewerCount = countViewers();
            lastAnimationAngle = animationAngle;
            if (viewerCount > 0 && animationAngle == 0.0F) playSound(ModSounds.DWARVEN_CHEST_OPEN);
            if (viewerCount == 0 && animationAngle > 0.0F || viewerCount > 0 && animationAngle < 1.0F) {
                float float_2 = animationAngle;
                if (viewerCount > 0) animationAngle += 0.1F;
                else animationAngle -= 0.1F;
                animationAngle = MathHelper.clamp(animationAngle, 0, 1);
                if (animationAngle < 0.5F && float_2 >= 0.5F) playSound(ModSounds.DWARVEN_CHEST_CLOSE);
            }
        }
    }

    @Environment(EnvType.CLIENT)
    private void playSound(SoundEvent soundEvent) {
        double d = (double) this.pos.getX() + 0.5D;
        double e = (double) this.pos.getY() + 0.5D;
        double f = (double) this.pos.getZ() + 0.5D;
        this.world.playSound(d, e, f, soundEvent, SoundCategory.BLOCKS, 0.5F, this.world.random.nextFloat() * 0.1F + 0.9F, false);
    }

    public void spawnChestParticles() {
        double posX = this.pos.getX() + 0.5; // Centre du coffre
        double posY = this.pos.getY();       // Position sol du coffre
        double posZ = this.pos.getZ() + 0.5; // Centre du coffre

        BlockPos blockPos = this.pos.down(); // Bloc sous le coffre
        BlockState blockState = this.world.getBlockState(blockPos);

        // Vérifie que le bloc n'est pas de l'air
        if (!blockState.isAir()) {
            for (int i = 0; i < 6; i++) { // Augmente le nombre de particules
                double offsetX = (this.world.random.nextDouble() - 0.5) * 0.3; // Dispersion
                double offsetZ = (this.world.random.nextDouble() - 0.5) * 0.3; // Dispersion
                double velocityY = 0.15; // Légère montée des particules

                this.world.addParticle(
                        new BlockStateParticleEffect(ParticleTypes.BLOCK, blockState), // Particule basée sur le bloc
                        posX + offsetX, posY, posZ + offsetZ, // Position des particules
                        0.0, velocityY, 0.0 // Vélocité des particules
                );
            }
        }
    }

    @Override
    public void writeScreenOpeningData(ServerPlayerEntity player, PacketByteBuf buf) {
        buf.writeBlockPos(this.getPos());
    }
    @Override
    public Text getDisplayName() {
        return this.getContainerName();
    }
    @Override
    public ScreenHandler createMenu(int syncId, PlayerInventory playerInventory, PlayerEntity player) {
        return new DraugrChestScreenHandler(syncId, playerInventory, this);
    }
}