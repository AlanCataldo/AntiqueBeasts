package net.mebahel.antiquebeasts.block.entity.base;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.screenhandler.v1.ExtendedScreenHandlerFactory;
import net.mebahel.antiquebeasts.block.screenhandlers.DraugrChestScreenHandler;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
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
    private boolean spawnAnimationPlaying = false;
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

    private DefaultedList<ItemStack> copyInv(DefaultedList<ItemStack> src) {
        DefaultedList<ItemStack> out = DefaultedList.ofSize(src.size(), ItemStack.EMPTY);
        for (int i = 0; i < src.size(); i++) out.set(i, src.get(i).copy());
        return out;
    }

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
            // inventaire commun
            this.personalLoots.put(id, this.getInvStackList());
            return;
        }

        if (!generatedLootPlayers.contains(id)) {
            generateLootFor(player);
            generatedLootPlayers.add(id);
            this.markDirty();
        }

        DefaultedList<ItemStack> loot = personalLoots.get(id);
    }

    private void generateLootFor(PlayerEntity player) {
        if (!(world instanceof ServerWorld serverWorld)) return;

        // Détermine la loot_table à utiliser et la fixe en "base"
        if (this.baseLootTableId == null) {
            if (this.lootTableId != null) {
                this.baseLootTableId = this.lootTableId;
                this.lootTableId = null; // évite génération globale vanilla sur le coffre "commun"
            } else {
                return;
            }
            this.markDirty();
        }

        // Seed perso (comme tu faisais)
        long seed = world.getTime()
                ^ player.getUuid().getMostSignificantBits()
                ^ pos.asLong()
                ^ world.random.nextLong();

        // --- Génération robuste via pipeline vanilla ---
        // 1) backup de l'inventaire actuel de la BE
        DefaultedList<ItemStack> backup = copyInv(this.getInvStackList());

        // 2) on remplace l'inventaire par un inventaire vide
        DefaultedList<ItemStack> temp = DefaultedList.ofSize(this.size(), ItemStack.EMPTY);
        this.setInvStackList(temp);

        // 3) on arme temporairement la loot table vanilla sur la BE
        Identifier prevLootId = this.lootTableId;
        long prevSeed = this.lootTableSeed;

        this.lootTableId = this.baseLootTableId;
        this.lootTableSeed = seed;

        try {
            // Méthode vanilla de dépack : remplir l'inventaire de la BE à partir de lootTableId/seed.
            // ChestBlockEntity hérite d'un container lootable, donc ça existe côté vanilla.
            this.checkLootInteraction(player);
        } finally {
            // 4) copie le résultat dans ton loot perso
            DefaultedList<ItemStack> loot = copyInv(this.getInvStackList());
            this.personalLoots.put(player.getUuid(), loot);

            // 5) restore état BE (inventaire + loot table)
            this.setInvStackList(backup);
            this.lootTableId = prevLootId;
            this.lootTableSeed = prevSeed;
        }
    }

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

        // 1) Le serveur a demandé un spawn => on démarre l'anim UNE SEULE FOIS
        if (this.shouldDoSpawnAnimation && !this.spawnAnimationPlaying) {
            this.spawnAnimationPlaying = true;

            controller.forceAnimationReset();
            controller.setAnimation(
                    RawAnimation.begin()
                            .then("spawn", Animation.LoopType.PLAY_ONCE)
            );
        }

        // 2) Tant que l'anim de spawn est en cours, on reste dans ce controller
        if (this.spawnAnimationPlaying) {
            // Tant que l'anim n'est pas à l'arrêt, on balance les particules
            if (controller.getAnimationState() != AnimationController.State.STOPPED) {
                spawnChestParticles();
            }

            // Quand Geckolib signale que l'anim est finie, on coupe tout côté client
            if (controller.hasAnimationFinished()) {
                this.spawnAnimationPlaying = false;
                this.shouldDoSpawnAnimation = false; // ⚠️ client-side uniquement
            }

            return PlayState.CONTINUE;
        }

        // 3) Si rien n'est en cours et le flag n'est pas posé, ce controller ne fait rien
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

    public void serverTick() {
        // Gestion fermeture auto
        if (viewerCount <= 0 && isOpened) {
            closeCooldown--;
            if (closeCooldown <= 0) {
                isOpened = false;
                hasBeenOpened = false;
                markDirty();
            }
        }

        // 🔽 AJOUT : le spawn est one-shot côté serveur aussi
        if (this.shouldDoSpawnAnimation) {
            this.shouldDoSpawnAnimation = false;
            this.markDirty(); // on persiste l'état "plus de spawn" pour les prochains joueurs/reco
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

    @Override
    public void readNbt(NbtCompound tag) {
        super.readNbt(tag);

        if (tag.contains("LootTable", 8) && !tag.getString("LootTable").isEmpty()) {
            Identifier vanillaLoot = new Identifier(tag.getString("LootTable"));
            if (this.customLootTableId == null) {
                this.customLootTableId = vanillaLoot;
            }
        }

        if (tag.contains("CustomLootTable", 8)) {
            this.customLootTableId = new Identifier(tag.getString("CustomLootTable"));
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

    public void setShouldDoSpawnAnimation(boolean value) {
        this.shouldDoSpawnAnimation = value;
        this.markDirty();
        this.sync();
    }

    public DefaultedList<ItemStack> getInternalInventory() {
        return this.getInvStackList();
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
    public NbtCompound toInitialChunkDataNbt() {
        return this.createNbt();
    }

    // 🔁 Utilisé quand tu fais sync() / markForUpdate()
    @Override
    public Packet<ClientPlayPacketListener> toUpdatePacket() {
        // on force l'utilisation de toInitialChunkDataNbt pour écrire tout le NBT
        return BlockEntityUpdateS2CPacket.create(this, BlockEntity::toInitialChunkDataNbt);
    }

    @Override
    public void markDirty() {
        super.markDirty();
        // pas de sync() automatique ici pour éviter d’écraser l’inventaire client
    }

    public boolean hasLootTable() {
        return this.baseLootTableId != null || this.lootTableId != null;
    }
}