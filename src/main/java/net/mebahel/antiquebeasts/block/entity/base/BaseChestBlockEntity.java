package net.mebahel.antiquebeasts.block.entity.base;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.fabricmc.fabric.api.screenhandler.v1.ExtendedScreenHandlerFactory;
import net.mebahel.antiquebeasts.block.screenhandlers.DraugrChestScreenHandler;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.ChestBlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.Inventories;
import net.minecraft.item.ItemStack;
import net.minecraft.loot.LootTable;
import net.minecraft.loot.context.LootContextParameterSet;
import net.minecraft.loot.context.LootContextParameters;
import net.minecraft.loot.context.LootContextTypes;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtList;
import net.minecraft.nbt.NbtString;
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
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import org.jetbrains.annotations.Nullable;
import software.bernie.geckolib.animatable.GeoBlockEntity;
import software.bernie.geckolib.core.animatable.GeoAnimatable;
import software.bernie.geckolib.core.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.core.animatable.instance.SingletonAnimatableInstanceCache;
import software.bernie.geckolib.core.animation.*;
import software.bernie.geckolib.core.object.PlayState;
import software.bernie.geckolib.util.RenderUtils;

import java.util.*;

public abstract class BaseChestBlockEntity extends ChestBlockEntity
        implements GeoBlockEntity, ExtendedScreenHandlerFactory {

    // === Geckolib ===
    protected final AnimatableInstanceCache cache = new SingletonAnimatableInstanceCache(this);

    // === état d’anim / ouverture ===
    public boolean isOpened = false;
    public boolean hasBeenOpened = false;
    public boolean shouldDoSpawnAnimation = false;
    public int closeCooldown = 0;
    public int age = 0;

    protected int viewerCount = 0; // pour NBT + serverTick
    protected int viewers = 0;     // pour onOpen/onClose multi

    // === loot perso ===
    public final Map<UUID, DefaultedList<ItemStack>> personalLoots = new HashMap<>();
    protected final Set<UUID> generatedLootPlayers = new HashSet<>();

    // === loot tables ===
    @Nullable
    protected Identifier baseLootTableId;   // version persistée de la loot_table d'origine
    @Nullable
    protected Identifier customLootTableId = null;

    private final int chestSize;

    protected BaseChestBlockEntity(net.minecraft.block.entity.BlockEntityType<?> type,
                                   BlockPos pos,
                                   BlockState state,
                                   int chestSize) {
        super(type, pos, state);
        this.chestSize = chestSize;
        this.setInvStackList(DefaultedList.ofSize(chestSize, ItemStack.EMPTY));
    }

    // --------- Hooks spécifiques à chaque coffre ---------

    /** Titre du coffre (affichage & container name). */
    protected abstract Text getChestTitle();

    /** Préfixe de logs : "[DraugrChest]" / "[GreekChest]" etc. */
    protected abstract String getLogPrefix();

    // =====================================================
    //               LOGIQUE LOOT PAR JOUEUR
    // =====================================================

    public boolean isSharedChest() {
        return this.baseLootTableId == null && this.lootTableId == null;
    }

    public void handlePlayerLoot(PlayerEntity player) {
        if (this.world == null || this.world.isClient) return;
        UUID id = player.getUuid();

        if (this.baseLootTableId == null && this.lootTableId == null) {
            System.out.println(getLogPrefix() + " Player-placed chest detected -> shared inventory only");
            // inventaire commun
            this.personalLoots.put(id, this.getInvStackList());
            return;
        }

        System.out.println(getLogPrefix() + " Player " + player.getName().getString() + " (" + id + ") interacting with chest at " + this.pos);

        if (!generatedLootPlayers.contains(id)) {
            System.out.println(getLogPrefix() + " -> No loot yet for this player. Generating new loot...");
            generateLootFor(player);
            generatedLootPlayers.add(id);
            this.markDirty();
        } else {
            System.out.println(getLogPrefix() + " -> Existing loot found for player.");
        }

        DefaultedList<ItemStack> loot = personalLoots.get(id);
        if (loot != null) {
            System.out.println(getLogPrefix() + " -> Loaded loot inventory for player with " + loot.size() + " slots.");
        } else {
            System.out.println(getLogPrefix() + " -> WARNING: No loot found for this player, something went wrong!");
        }
    }

    private void generateLootFor(PlayerEntity player) {
        if (!(world instanceof ServerWorld serverWorld)) return;

        // Détermine la loot_table à utiliser
        if (this.baseLootTableId == null) {
            if (this.lootTableId != null) {
                this.baseLootTableId = this.lootTableId;
                this.lootTableId = null; // évite la génération vanilla globale
                System.out.println(getLogPrefix() + " Stored structure loot_table_id: " + this.baseLootTableId);
            } else {
                System.out.println(getLogPrefix() + " No lootTableId found — shared chest only.");
                return;
            }
            this.markDirty();
        }

        System.out.println(getLogPrefix() + " Generating loot for " + player.getName().getString()
                + " using table: " + this.baseLootTableId);

        LootTable lootTable = serverWorld.getServer().getLootManager().getLootTable(this.baseLootTableId);

        long seed = world.getTime()
                ^ player.getUuid().getMostSignificantBits()
                ^ pos.asLong()
                ^ world.random.nextLong();

        LootContextParameterSet.Builder paramBuilder = new LootContextParameterSet.Builder(serverWorld)
                .add(LootContextParameters.ORIGIN, Vec3d.ofCenter(this.pos))
                .add(LootContextParameters.THIS_ENTITY, player);

        LootContextParameterSet lootContext = paramBuilder.build(LootContextTypes.CHEST);

        net.minecraft.inventory.SimpleInventory tempInventory = new net.minecraft.inventory.SimpleInventory(this.size());
        lootTable.supplyInventory(tempInventory, lootContext, seed);

        DefaultedList<ItemStack> loot = DefaultedList.ofSize(this.size(), ItemStack.EMPTY);
        for (int i = 0; i < this.size(); i++) {
            ItemStack stack = tempInventory.getStack(i);
            if (!stack.isEmpty()) {
                loot.set(i, stack.copy());
            }
        }

        personalLoots.put(player.getUuid(), loot);

        System.out.println(getLogPrefix() + " ✅ Loot generated for " + player.getName().getString());
    }

    // =====================================================
    //                      GECKOLIB
    // =====================================================

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllerRegistrar) {
        controllerRegistrar.add(new AnimationController<>(this, "openController", 0, this::openPredicate));
        controllerRegistrar.add(new AnimationController<>(this, "spawnController", 0, this::spawnPredicate));
    }

    private <T extends GeoAnimatable> PlayState openPredicate(AnimationState<T> event) {
        AnimationController<?> controller = event.getController();

        if (this.shouldDoSpawnAnimation)
            return PlayState.STOP;

        if (this.isOpened) {
            controller.setAnimation(RawAnimation.begin()
                    .then("open", Animation.LoopType.PLAY_ONCE)
                    .then("isOpened", Animation.LoopType.LOOP));
        } else if (this.hasBeenOpened) {
            controller.setAnimation(RawAnimation.begin()
                    .then("close", Animation.LoopType.PLAY_ONCE)
                    .then("isClosed", Animation.LoopType.LOOP));
        } else {
            controller.setAnimation(RawAnimation.begin()
                    .then("isClosed", Animation.LoopType.LOOP));
        }

        return PlayState.CONTINUE;
    }

    private <T extends GeoAnimatable> PlayState spawnPredicate(AnimationState<T> event) {
        AnimationController<?> controller = event.getController();
        if (controller.getAnimationState() != AnimationController.State.STOPPED) spawnChestParticles();
        if (this.shouldDoSpawnAnimation) {
            controller.setAnimation(RawAnimation.begin().then("spawn", Animation.LoopType.PLAY_ONCE));
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

    // =====================================================
    //                  TICK / OUVERTURE
    // =====================================================

    public void serverTick() {
        if (viewerCount <= 0 && isOpened) {
            closeCooldown--;
            if (closeCooldown <= 0) {
                isOpened = false;
                hasBeenOpened = false;
                markDirty();
            }
        }
    }

    @Environment(EnvType.CLIENT)
    public void clientTick() {
        this.age++;
        if (this.age < 10) this.hasBeenOpened = false;
        if (this.closeCooldown > 0) this.closeCooldown--;
    }

    public void sync() {
        if (this.world instanceof ServerWorld serverWorld)
            serverWorld.getChunkManager().markForUpdate(this.pos);
    }

    @Override
    protected Text getContainerName() {
        return getChestTitle();
    }

    @Override
    public int size() {
        return chestSize;
    }

    @Environment(EnvType.CLIENT)
    public int countViewers() {
        return viewerCount;
    }

    @Override
    public void onOpen(PlayerEntity player) {
        if (world.isClient) return;
        if (player.isSpectator()) return;

        int oldViewers = this.viewers++;
        if (oldViewers == 0) {
            this.isOpened = true;
            this.hasBeenOpened = true;
            markDirty();
            // le packet sera traité côté client selon le type concret (Draugr/Greek)
            net.mebahel.antiquebeasts.util.packet.ChestOpenSync.sendToAll((ServerWorld) world, this.pos, true);
        }
    }

    @Override
    public void onClose(PlayerEntity player) {
        if (world.isClient) return;
        if (player.isSpectator()) return;

        int oldViewers = this.viewers--;
        if (oldViewers == 1) { // 1 -> 0
            this.isOpened = false;
            markDirty();
            net.mebahel.antiquebeasts.util.packet.ChestOpenSync.sendToAll((ServerWorld) world, this.pos, false);
        }

        if (this.viewers < 0) this.viewers = 0;
    }

    // =====================================================
    //                       NBT
    // =====================================================

    @Override
    public void readNbt(NbtCompound tag) {
        super.readNbt(tag);

        if (tag.contains("LootTable", 8) && !tag.getString("LootTable").isEmpty()) {
            Identifier vanillaLoot = new Identifier(tag.getString("LootTable"));
            if (this.customLootTableId == null) {
                this.customLootTableId = vanillaLoot;
                System.out.println(getLogPrefix() + " Copied vanilla loot table to custom: " + vanillaLoot);
            }
        }

        if (tag.contains("CustomLootTable", 8)) {
            this.customLootTableId = new Identifier(tag.getString("CustomLootTable"));
            System.out.println(getLogPrefix() + " Restored saved custom loot table: " + this.customLootTableId);
        }

        this.isOpened = tag.getBoolean("isOpened");
        this.hasBeenOpened = tag.getBoolean("hasBeenOpened");
        this.shouldDoSpawnAnimation = tag.getBoolean("shouldDoSpawnAnimation");
        this.closeCooldown = tag.getInt("closeCooldown");
        this.viewerCount = tag.getInt("viewers");

        generatedLootPlayers.clear();
        NbtList list = tag.getList("GeneratedLootPlayers", NbtCompound.STRING_TYPE);
        for (int i = 0; i < list.size(); i++)
            generatedLootPlayers.add(UUID.fromString(list.getString(i)));

        if (tag.contains("BaseLootTable", 8)) {
            this.baseLootTableId = new Identifier(tag.getString("BaseLootTable"));
            System.out.println(getLogPrefix() + " Restored baseLootTableId: " + this.baseLootTableId);
        }
        if (tag.contains("PersonalLoots", 9)) {
            NbtList lootsList = tag.getList("PersonalLoots", 10);
            for (int i = 0; i < lootsList.size(); i++) {
                NbtCompound playerLootTag = lootsList.getCompound(i);
                UUID playerId = UUID.fromString(playerLootTag.getString("Player"));

                DefaultedList<ItemStack> items = DefaultedList.ofSize(this.size(), ItemStack.EMPTY);
                if (playerLootTag.contains("Items", 10)) {
                    Inventories.readNbt(playerLootTag.getCompound("Items"), items);
                }
                personalLoots.put(playerId, items);
            }
        }
    }

    @Override
    public void writeNbt(NbtCompound tag) {
        super.writeNbt(tag);

        if (this.customLootTableId != null) {
            tag.putString("CustomLootTable", this.customLootTableId.toString());
        }

        tag.putBoolean("isOpened", this.isOpened);
        tag.putBoolean("hasBeenOpened", this.hasBeenOpened);
        tag.putBoolean("shouldDoSpawnAnimation", this.shouldDoSpawnAnimation);
        tag.putInt("closeCooldown", this.closeCooldown);
        tag.putInt("viewers", this.viewerCount);

        NbtList list = new NbtList();
        for (UUID id : generatedLootPlayers)
            list.add(NbtString.of(id.toString()));
        tag.put("GeneratedLootPlayers", list);
        if (this.baseLootTableId != null) {
            tag.putString("BaseLootTable", this.baseLootTableId.toString());
        }

        NbtList lootsList = new NbtList();
        for (Map.Entry<UUID, DefaultedList<ItemStack>> entry : personalLoots.entrySet()) {
            NbtCompound playerLootTag = new NbtCompound();
            playerLootTag.putString("Player", entry.getKey().toString());

            NbtCompound itemsTag = new NbtCompound();
            Inventories.writeNbt(itemsTag, entry.getValue());
            playerLootTag.put("Items", itemsTag);

            lootsList.add(playerLootTag);
        }
        tag.put("PersonalLoots", lootsList);
    }

    // =====================================================
    //                 DIVERS / UTILITAIRES
    // =====================================================

    public void setShouldDoSpawnAnimation(boolean value) {
        this.shouldDoSpawnAnimation = value;
        this.markDirty();
        this.sync();
    }

    public DefaultedList<ItemStack> getInternalInventory() {
        return this.getInvStackList();
    }

    private void syncToServer() {
        if (this.world != null && this.world.isClient) {
            PacketByteBuf buf = PacketByteBufs.create();
            buf.writeBlockPos(this.pos);
            buf.writeBoolean(this.shouldDoSpawnAnimation);
            ClientPlayNetworking.send(new Identifier("antiquebeasts", "update_chest"), buf);
        }
    }

    @Environment(EnvType.CLIENT)
    public void playChestSound(SoundEvent soundEvent) {
        if (this.world == null) return;
        double x = this.pos.getX() + 0.5;
        double y = this.pos.getY() + 0.5;
        double z = this.pos.getZ() + 0.5;
        this.world.playSound(
                x, y, z,
                soundEvent,
                SoundCategory.BLOCKS,
                1.0F,
                this.world.random.nextFloat() * 0.1F + 0.9F,
                false
        );
    }

    @Environment(EnvType.CLIENT)
    public void playOpenSound() {
        playChestSound(SoundEvents.BLOCK_CHEST_OPEN);
    }

    @Environment(EnvType.CLIENT)
    public void playCloseSound() {
        playChestSound(SoundEvents.BLOCK_CHEST_CLOSE);
    }

    public void spawnChestParticles() {
        double posX = this.pos.getX() + 0.5;
        double posY = this.pos.getY();
        double posZ = this.pos.getZ() + 0.5;
        BlockPos blockPos = this.pos.down();
        BlockState blockState = this.world.getBlockState(blockPos);

        if (!blockState.isAir()) {
            for (int i = 0; i < 6; i++) {
                double offsetX = (this.world.random.nextDouble() - 0.5) * 0.3;
                double offsetZ = (this.world.random.nextDouble() - 0.5) * 0.3;
                double velocityY = 0.15;
                this.world.addParticle(new BlockStateParticleEffect(ParticleTypes.BLOCK, blockState),
                        posX + offsetX, posY, posZ + offsetZ,
                        0.0, velocityY, 0.0);
            }
        }
    }

    @Override
    public void writeScreenOpeningData(ServerPlayerEntity player, PacketByteBuf buf) {
        buf.writeBlockPos(this.getPos());
    }

    @Override
    public Text getDisplayName() {
        return getChestTitle();
    }

    @Override
    public ScreenHandler createMenu(int syncId, PlayerInventory playerInventory, PlayerEntity player) {
        DefaultedList<ItemStack> loot = personalLoots.get(player.getUuid());
        if (loot == null) {
            loot = DefaultedList.ofSize(this.size(), ItemStack.EMPTY);
            personalLoots.put(player.getUuid(), loot);
        }
        return new DraugrChestScreenHandler(syncId, playerInventory, loot, this.pos);
    }

    public ExtendedScreenHandlerFactory createPersonalScreenHandlerFactory(PlayerEntity player) {
        return new ExtendedScreenHandlerFactory() {
            @Override
            public void writeScreenOpeningData(ServerPlayerEntity serverPlayer, PacketByteBuf buf) {
                buf.writeBlockPos(getPos());
            }

            @Override
            public Text getDisplayName() {
                return getChestTitle();
            }

            @Override
            public ScreenHandler createMenu(int syncId, PlayerInventory playerInventory, PlayerEntity playerEntity) {
                if (isSharedChest()) {
                    System.out.println(getLogPrefix() + " Opening shared chest for " + playerEntity.getName().getString());
                    return new DraugrChestScreenHandler(syncId, playerInventory, BaseChestBlockEntity.this);
                }

                DefaultedList<ItemStack> loot = personalLoots.get(player.getUuid());
                if (loot == null) {
                    loot = DefaultedList.ofSize(size(), ItemStack.EMPTY);
                    personalLoots.put(player.getUuid(), loot);
                }
                return new DraugrChestScreenHandler(syncId, playerInventory, loot, getPos());
            }
        };
    }

    @Override
    public Packet<ClientPlayPacketListener> toUpdatePacket() {
        return BlockEntityUpdateS2CPacket.create(this);
    }

    @Override
    public void markDirty() {
        super.markDirty();
        // pas de sync() automatique ici pour éviter d’écraser l’inventaire client
    }

    public boolean hasLootTable() {
        return this.baseLootTableId != null || this.lootTableId != null;
    }

    @Nullable
    public Identifier getBaseLootTableId() {
        if (this.baseLootTableId != null) return this.baseLootTableId;
        return this.lootTableId;
    }
}
