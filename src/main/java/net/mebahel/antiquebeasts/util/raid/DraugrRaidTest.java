package net.mebahel.antiquebeasts.util.raid;

import net.fabricmc.fabric.api.entity.event.v1.ServerLivingEntityEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.mebahel.antiquebeasts.AntiqueBeasts;
import net.mebahel.antiquebeasts.block.ModBlocks;
import net.mebahel.antiquebeasts.block.entity.DraugrChestBlockEntity;
import net.mebahel.antiquebeasts.entity.custom.other.DraugrEntity;
import net.mebahel.antiquebeasts.entity.custom.other.DraugrOverlordEntity;
import net.mebahel.antiquebeasts.item.TickScheduler;
import net.mebahel.antiquebeasts.sound.ModSounds;
import net.minecraft.block.Blocks;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.ChestBlockEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LightningEntity;
import net.minecraft.entity.SpawnReason;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.boss.BossBar;
import net.minecraft.entity.boss.ServerBossBar;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtList;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;

import java.util.*;

import static net.mebahel.antiquebeasts.AntiqueBeasts.worldDifficultyLevels;
import static net.mebahel.antiquebeasts.util.raid.DraugrRaidHelper.chooseEntityType;
import static net.mebahel.antiquebeasts.util.raid.DraugrRaidHelper.findGroundPosition;

public class DraugrRaidTest {
    private boolean rewardChestSpawned;
    private BlockPos chestPos;
    private ServerWorld world;
    int currentWave = 1;
    int draugrKilledInWave = 0;
    private static final int MAX_WAVES = 3;
    private static int NUMBER_OF_WAVES = 3;
    BlockPos centralPos;
    private PlayerEntity targetPlayer;
    float initialMaxHealth;
    public boolean raidInProgress;
    boolean waveEntitiesSpawned;
    private ServerBossBar raidBossBar;
    List<DraugrEntity> activeMobs;
    public List<UUID> raidEntityUuid;
    public UUID raidUuid;
    UUID targetPlayerUuid;
    boolean raidCompleted = false;

    // Constructor
    public DraugrRaidTest(PlayerEntity player, ServerWorld world) {
        this.raidUuid = UUID.randomUUID();
        this.rewardChestSpawned = false;
        this.raidEntityUuid = new ArrayList<>();
        this.world = world;
        this.targetPlayer = player;
        this.currentWave = 0;
        this.targetPlayerUuid = player.getUuid();
        this.waveEntitiesSpawned = false;
        this.activeMobs = new ArrayList<>();
        this.raidBossBar = new ServerBossBar(Text.translatable("Draugr Raid - Wave..."), BossBar.Color.WHITE, BossBar.Style.NOTCHED_10);
        this.raidInProgress = false;
        raidBossBar.addPlayer((ServerPlayerEntity) targetPlayer);
        //raidBossBar.setVisible(true);
        raidBossBar.setName(Text.translatable("Draugr Raid - Wave " + currentWave));
        registerEvents();
    }

    public boolean isRaidInProgress() {
        return raidInProgress;
    }
    public boolean isRaidCompleted() {
        return raidCompleted;
    }
    public void setTargetPlayer(PlayerEntity player) {
        this.targetPlayer = player;
    }

    public void startRaid() {
        this.raidInProgress = true;
        if (worldDifficultyLevels.getOrDefault(world, 1) == 2)
            NUMBER_OF_WAVES = 4;
        spawnNextWave();
    }

    void registerEvents() {
        ServerLivingEntityEvents.AFTER_DEATH.register((entity, source) -> {
            if (entity instanceof DraugrEntity draugr && draugr.isPartOfRaid()) {
                this.setWorld((ServerWorld) entity.getWorld());
                activeMobs.removeIf(draugrEntity -> !draugrEntity.isAlive());

                updateRaidHealthBar();
            }
        });

        ServerTickEvents.END_SERVER_TICK.register((serverTick) -> {
            ServerWorld serverWorld = serverTick.getOverworld();

            if (serverWorld.getDifficulty().getId() == 0) {
                stopAllRaids();
                return;
            }

            if (!raidInProgress || raidCompleted) return;

            if (AntiqueBeasts.ongoingRaids.stream().noneMatch(raid -> raid.raidUuid.equals(this.raidUuid))) {
                AntiqueBeasts.ongoingRaids.add(this);
                //System.out.println("Raid ajouté à ongoingRaids : " + raidUuid);
            }

            if (raidBossBar.getPlayers().isEmpty()) {
                updateBossBarForPlayers(serverWorld);
            }
            updateBossBarProximity(serverWorld);
        });
    }
    private static void stopAllRaids() {
        for (DraugrRaidTest raid : new ArrayList<>(AntiqueBeasts.ongoingRaids)) {
            raid.endRaid();
        }

        AntiqueBeasts.ongoingRaids.clear();
    }


    private void updateBossBarForPlayers(ServerWorld world) {
        List<ServerPlayerEntity> playersInWorld = world.getPlayers();

        for (ServerPlayerEntity player : playersInWorld) {
            if (!raidBossBar.getPlayers().contains(player)) {
                raidBossBar.addPlayer(player);
            }
        }
    }

    private void updateBossBarProximity(ServerWorld world) {
        List<ServerPlayerEntity> playersInWorld = world.getPlayers();
        List<ServerPlayerEntity> playersInBossBar = new ArrayList<>(raidBossBar.getPlayers());
        activeMobs.clear();
        float totalHealth = 0.0F;
        for (UUID uuid : raidEntityUuid) {
            DraugrEntity draugr = findDraugrByUUID(world, uuid);
            if (draugr != null && draugr.isAlive()) {
                activeMobs.add(draugr);
                totalHealth += draugr.getHealth();
            }
        }
        raidBossBar.setPercent(totalHealth / initialMaxHealth);
        raidBossBar.setName(Text.translatable("Draugr Raid - Wave " + currentWave));

        for (ServerPlayerEntity player : playersInWorld) {
            boolean isWithinRange = false;

            for (UUID entityUuid : raidEntityUuid) {
                DraugrEntity draugr = findDraugrByUUID(world, entityUuid);
                if (draugr != null && draugr.isAlive()) {
                    double distanceSquared = player.squaredDistanceTo(draugr.getX(), draugr.getY(), draugr.getZ());
                    if (distanceSquared <= 40 * 40) {
                        isWithinRange = true;
                        break;
                    }
                }
            }

            if (isWithinRange) {
                if (!raidBossBar.getPlayers().contains(player)) {
                    raidBossBar.addPlayer(player);
                }
            } else {
                if (raidBossBar.getPlayers().contains(player)) {
                    raidBossBar.removePlayer(player);
                }
            }

            playersInBossBar.remove(player);
        }

        for (ServerPlayerEntity playerToRemove : playersInBossBar) {
            raidBossBar.removePlayer(playerToRemove);
        }
    }
    public void setWorld(ServerWorld world) {
        this.world = world;
    }

    public UUID getTargetPlayerUuid() {
        return targetPlayerUuid;
    }

    public DraugrEntity findDraugrByUUID(ServerWorld world, UUID uuid) {
        Entity entity = world.getEntity(uuid);

        if (entity instanceof DraugrEntity) {
            return (DraugrEntity) entity;
        } else {
            return null;
        }
    }

    private void spawnNextWave() {
        if (currentWave >= NUMBER_OF_WAVES) {
            endRaid();
            return;
        }

        waveEntitiesSpawned = false;
        currentWave++;
        raidBossBar.setName(Text.translatable("Draugr Raid - Wave " + currentWave));

        Random random = new Random();
        double angle = random.nextDouble() * 2 * Math.PI;
        double distance = 15 + random.nextDouble() * 10;
        this.centralPos = new BlockPos((int) (targetPlayer.getX() + Math.cos(angle) * distance),
                (int) targetPlayer.getY(),
                (int) (targetPlayer.getZ() + Math.sin(angle) * distance));

        AntiqueBeasts.getTickScheduler().schedule(world, 20, this::spawnLightning);
        AntiqueBeasts.getTickScheduler().schedule(world, 30, this::spawnWaveMobs);
    }

    public void updateRaidHealthBar() {
        if (!waveEntitiesSpawned || raidBossBar == null) return;
        float totalHealth = 0.0F;

        activeMobs.clear();
        for (UUID uuid : raidEntityUuid) {
            DraugrEntity draugr = findDraugrByUUID(world, uuid);
            if (draugr != null && draugr.isAlive()) {
                activeMobs.add(draugr);
                totalHealth += draugr.getHealth();
            }
        }

        if (totalHealth > 0) {
            raidBossBar.setPercent(totalHealth / initialMaxHealth);
            raidBossBar.setName(Text.translatable("Draugr Raid - Wave " + currentWave));
        } else {
            raidBossBar.setPercent(0.0F);
            if (currentWave >= MAX_WAVES) {
                if (!rewardChestSpawned) {
                    spawnRewardChest();
                    rewardChestSpawned = true;
                }
                endRaid();
            } else {
                waveEntitiesSpawned = false;
                spawnNextWave();
            }
        }
    }

    public void endRaid() {
        raidBossBar.removePlayer((ServerPlayerEntity) targetPlayer);
        raidBossBar.setVisible(false);

        this.raidCompleted = true;
        this.raidInProgress = false;
        this.removeRaid();
        AntiqueBeasts.ongoingRaids.remove(this);
        //System.out.println("Raid ended for player: " + targetPlayer.getName().getString());
    }



    public void saveRaid() {
        PersistentRaidData data = PersistentRaidData.get(world);

        if (data != null) {
            data.addRaidByUuid(raidUuid, this);
            data.markDirty();
        }
    }


    public void removeRaid() {
        PersistentRaidData data = PersistentRaidData.get(world);

        if (data != null) {
            data.removeRaidByRaid(this);
            data.markDirty();
        }
    }

    public NbtCompound writeNbt(NbtCompound nbt) {
        if (raidCompleted) {
            return new NbtCompound();
        }

        nbt.putInt("currentWave", currentWave);
        nbt.putFloat("initialMaxHealth", initialMaxHealth);
        nbt.putInt("draugrKilledInWave", draugrKilledInWave);
        nbt.putString("raidUuid", raidUuid.toString());
        nbt.putUuid("targetPlayerUuid", targetPlayer.getUuid());
        nbt.putBoolean("raidCompleted", raidCompleted);
        nbt.putBoolean("raidInProgress", raidInProgress);
        nbt.putBoolean("waveEntitiesSpawned", waveEntitiesSpawned);
        nbt.putBoolean("rewardChestSpawned", rewardChestSpawned);

        if (centralPos != null) {
            nbt.putInt("centralPosX", centralPos.getX());
            nbt.putInt("centralPosY", centralPos.getY());
            nbt.putInt("centralPosZ", centralPos.getZ());
        }

        NbtList uuidList = new NbtList();
        for (UUID uuid : raidEntityUuid) {
            NbtCompound uuidCompound = new NbtCompound();
            uuidCompound.putUuid("UUID", uuid);
            uuidList.add(uuidCompound);
        }
        nbt.put("raidEntityUuid", uuidList);

        NbtList activeMobsList = new NbtList();
        for (DraugrEntity draugr : activeMobs) {
            if (draugr != null) {
                NbtCompound draugrCompound = new NbtCompound();
                draugr.saveSelfNbt(draugrCompound);
                activeMobsList.add(draugrCompound);
            }
        }
        nbt.put("activeMobs", activeMobsList);

        return nbt;
    }
    public static DraugrRaidTest fromNbt(NbtCompound nbt, ServerWorld world) {
        UUID playerUuid = nbt.getUuid("targetPlayerUuid");
        PlayerEntity player = world.getPlayerByUuid(playerUuid);
        //System.out.println(world.getPlayers());

        if (player == null) {
            player = world.getPlayers().get(0);
        }

        DraugrRaidTest raid = new DraugrRaidTest(player, world);
        raid.raidUuid = UUID.fromString(nbt.getString("raidUuid"));
        raid.currentWave = nbt.getInt("currentWave");
        raid.draugrKilledInWave = nbt.getInt("draugrKilledInWave");
        raid.raidInProgress = nbt.getBoolean("raidInProgress");
        raid.raidCompleted = nbt.getBoolean("raidCompleted");
        raid.initialMaxHealth = nbt.getFloat("initialMaxHealth");
        raid.waveEntitiesSpawned = nbt.getBoolean("waveEntitiesSpawned");
        raid.rewardChestSpawned = nbt.getBoolean("rewardChestSpawned");

        if (nbt.contains("centralPosX") && nbt.contains("centralPosY") && nbt.contains("centralPosZ")) {
            raid.centralPos = new BlockPos(
                    nbt.getInt("centralPosX"),
                    nbt.getInt("centralPosY"),
                    nbt.getInt("centralPosZ")
            );
        }

        NbtList uuidList = nbt.getList("raidEntityUuid", NbtCompound.COMPOUND_TYPE);
        for (int i = 0; i < uuidList.size(); i++) {
            NbtCompound uuidCompound = uuidList.getCompound(i);
            raid.raidEntityUuid.add(uuidCompound.getUuid("UUID"));
        }

        for (UUID uuid : raid.raidEntityUuid) {
            DraugrEntity draugr = raid.findDraugrByUUID(world, uuid);
            if (draugr != null && !raid.activeMobs.contains(draugr)) {
                raid.activeMobs.add(draugr);
            }
        }

        return raid;
    }

    public ServerWorld getWorld() {
        return this.world;
    }

    private void spawnRewardChest() {
        if (!world.isClient) {
            BlockPos playerPos = targetPlayer.getBlockPos();
            Direction playerFacing = targetPlayer.getHorizontalFacing();
            chestPos = playerPos.add(playerFacing.getVector().multiply(2));

            while (world.isAir(chestPos.down()) && chestPos.getY() > 0) {
                chestPos = chestPos.down();
            }

            // ✅ Place le coffre Draugr
            world.setBlockState(chestPos, ModBlocks.DRAUGR_CHEST.getDefaultState());

            BlockEntity blockEntity = world.getBlockEntity(chestPos);
            if (blockEntity instanceof DraugrChestBlockEntity draugrChest) {
                draugrChest.setLootTable(new Identifier("antiquebeasts", "chests/raid/draugr_raid"), world.getRandom().nextLong());

                // ✅ Active l'animation et force la synchronisation
                draugrChest.setShouldDoSpawnAnimation(true);
                System.out.println("[DEBUG] Chest spawn animation activated: " + draugrChest.shouldDoSpawnAnimation);
            }

            AntiqueBeasts.getTickScheduler().schedule(world, 15, this::enRaidSound);
        }
    }

    private void enRaidSound(ServerWorld world) {
        world.playSound(null, chestPos, ModSounds.WINNING_RAID_1, targetPlayer.getSoundCategory(), 0.55F, 1.0F);
    }
    private void spawnLightning(ServerWorld world) {
        for (int i = 0; i < 1; i++) {
            LightningEntity lightning = new LightningEntity(EntityType.LIGHTNING_BOLT, world);
            lightning.setPos(centralPos.getX(), centralPos.getY(), centralPos.getZ());
            world.spawnEntity(lightning);
        }
    }
    private void spawnWaveMobs(ServerWorld world) {
        int difficultyLevel = worldDifficultyLevels.getOrDefault(world, 1);;
        activeMobs.clear();
        raidEntityUuid.clear();
        Random random = new Random();
        int mobsToSpawn = 1 + currentWave * 2 + difficultyLevel * 2;
        initialMaxHealth = 0.0F;

        for (int i = 0; i < mobsToSpawn; i++) {
            double xOffset = random.nextDouble() * 20 - 10;
            double zOffset = random.nextDouble() * 20 - 10;
            BlockPos spawnPos = centralPos.add((int) xOffset, 0, (int) zOffset);
            spawnPos = findGroundPosition(world, spawnPos);
            EntityType entityType = chooseEntityType(random, currentWave, difficultyLevel);

            DraugrEntity draugr = (DraugrEntity) entityType.create(world);
            if (draugr != null) {
                draugr.refreshPositionAndAngles(spawnPos.getX(), spawnPos.getY(), spawnPos.getZ(), random.nextFloat() * 360F, 0);
                draugr.initialize(world, world.getLocalDifficulty(spawnPos), SpawnReason.EVENT, null, null);
                draugr.getDataTracker().set(DraugrEntity.IS_PART_OF_RAID, true);
                draugr.setHasSpawned(false);
                activeMobs.add(draugr);
                world.spawnEntity(draugr);
                draugr.setTarget(targetPlayer);
                raidEntityUuid.add(draugr.getUuid());
                initialMaxHealth += (float) draugr.getAttributeValue(EntityAttributes.GENERIC_MAX_HEALTH);
            }
        }
        if (currentWave == NUMBER_OF_WAVES) {

            double xOffset = random.nextDouble() * 10 - 5;
            double zOffset = random.nextDouble() * 10 - 5;
            BlockPos overlordPos = centralPos.add((int) xOffset, 0, (int) zOffset);
            overlordPos = findGroundPosition(world, overlordPos);

            DraugrEntity overlord = (DraugrEntity) EntityType.get("antiquebeasts:draugr_overlord").get().create(world);
            if (overlord != null) {
                overlord.refreshPositionAndAngles(overlordPos.getX(), overlordPos.getY(), overlordPos.getZ(), random.nextFloat() * 360F, 0);
                overlord.initialize(world, world.getLocalDifficulty(overlordPos), SpawnReason.EVENT, null, null);

                overlord.getDataTracker().set(DraugrEntity.IS_PART_OF_RAID, true);
                overlord.setHasSpawned(false);
                activeMobs.add(overlord);
                world.spawnEntity(overlord);
                overlord.setTarget(targetPlayer);
                raidEntityUuid.add(overlord.getUuid());

                initialMaxHealth += (float) overlord.getAttributeValue(EntityAttributes.GENERIC_MAX_HEALTH);
            }
        }

        RaidState raidState = RaidStateManager.getRaidState(world);
        raidState.setActiveMobUUIDs(new ArrayList<>(raidEntityUuid));
        raidState.markDirty(); // Marquer l'état comme "sale" pour assurer sa sauvegarde
        waveEntitiesSpawned = true;
        updateRaidHealthBar();
        saveRaid();
    }
}