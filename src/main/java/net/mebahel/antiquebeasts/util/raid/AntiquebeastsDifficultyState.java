package net.mebahel.antiquebeasts.util.raid;

import net.mebahel.antiquebeasts.util.config.ModConfig;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.Identifier;
import net.minecraft.world.PersistentState;
import net.minecraft.world.PersistentStateManager;

import java.util.List;

import static net.mebahel.antiquebeasts.AntiqueBeasts.worldDifficultyLevels;

public class AntiquebeastsDifficultyState extends PersistentState {
    private static int netherCheckCounter = 0;
    private static final int NETHER_CHECK_INTERVAL = 300;
    private int difficultyLevel;
    AntiquebeastsDifficultyState difficultyState = null;

    public AntiquebeastsDifficultyState(int difficultyLevel) {
        this.difficultyLevel = difficultyLevel;
    }

    public int getDifficultyLevel() {
        return difficultyLevel;
    }

    public void setDifficultyLevel(int difficultyLevel) {
        this.difficultyLevel = difficultyLevel;
        this.markDirty();
    }

    @Override
    public NbtCompound writeNbt(NbtCompound nbt) {
        nbt.putInt("difficultyLevel", difficultyLevel);
        return nbt;
    }

    public static AntiquebeastsDifficultyState fromNbt(NbtCompound nbt) {
        return new AntiquebeastsDifficultyState(nbt.getInt("difficultyLevel"));
    }

    public void registerDifficultyState(ServerWorld world) {
        if (ModConfig.enableDifficultySystem) {
            PersistentStateManager stateManager = world.getPersistentStateManager();
            this.difficultyState = stateManager.getOrCreate( // ✅ Stocke la valeur dans `this.difficultyState`
                    AntiquebeastsDifficultyState::fromNbt,
                    () -> new AntiquebeastsDifficultyState(1),
                    "zombie_horde_difficulty"
            );
            int difficultyLevel = this.difficultyState.getDifficultyLevel();
            worldDifficultyLevels.put(world, difficultyLevel);
        } else {
            worldDifficultyLevels.put(world, 1);
        }
    }

    public void updateDifficultyState(ServerWorld world) {
        if (ModConfig.enableDifficultySystem) {
            if (worldDifficultyLevels.get(world) == 1) {
                netherCheckCounter++;
                if (netherCheckCounter >= NETHER_CHECK_INTERVAL) {
                    netherCheckCounter = 0;
                    checkNetherVisit(world, this.difficultyState);
                }
            }
        }
    }

    private static void checkNetherVisit(ServerWorld world, AntiquebeastsDifficultyState difficultyState) {
        List<ServerPlayerEntity> players = world.getPlayers();

        for (ServerPlayerEntity player : players) {
            if (player.getAdvancementTracker().getProgress(world.getServer().getAdvancementLoader().get(new Identifier("minecraft", "nether/root"))).isDone()) {
                int difficultyLevel = 2;
                worldDifficultyLevels.put(world, difficultyLevel);
                difficultyState.setDifficultyLevel(difficultyLevel);
                System.out.println("[Mebahel's Antique Beasts] Difficulty increased to 2 due to Nether visit by " + player.getName().getString());
                break;
            }
        }
    }
}
