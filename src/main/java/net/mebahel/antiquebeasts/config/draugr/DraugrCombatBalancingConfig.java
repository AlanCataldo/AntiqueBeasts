package net.mebahel.antiquebeasts.config.draugr;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

public class DraugrCombatBalancingConfig {
    private static final String CONFIG_FILE_NAME = "combat_balancing_config.json";
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();

    public static float draugrMinBlockProbability = 8f;
    public static float draugrMaxBlockProbability = 16f;
    public static float draugrOverlordMinBlockProbability = 10f;
    public static float draugrOverlordMaxBlockProbability = 20f;
    public static float draugrSpawnWithPotionProbability = 5f;
    public static boolean draugrRaidScalingDifficulty = true;

    public static void loadConfig(File configDir) {
        if (!configDir.exists()) {
            configDir.mkdirs();
        }

        File configFile = new File(configDir, CONFIG_FILE_NAME);
        if (configFile.exists()) {
            try (FileReader reader = new FileReader(configFile)) {
                ConfigData data = GSON.fromJson(reader, ConfigData.class);

                boolean updated = false;

                if (data.draugrMinBlockProbability <= 0) {
                    data.draugrMinBlockProbability = 8f;
                    updated = true;
                }
                if (data.draugrMaxBlockProbability <= 0) {
                    data.draugrMaxBlockProbability = 16f;
                    updated = true;
                }
                if (data.draugrOverlordMinBlockProbability <= 0) {
                    data.draugrOverlordMinBlockProbability = 10f;
                    updated = true;
                }
                if (data.draugrOverlordMaxBlockProbability <= 0) {
                    data.draugrOverlordMaxBlockProbability = 20f;
                    updated = true;
                }
                if (data.draugrSpawnWithPotionProbability <= 0 || data.draugrSpawnWithPotionProbability > 100) {
                    data.draugrSpawnWithPotionProbability = 5f;
                    updated = true;
                }
                if (data.draugrRaidScalingDifficulty == null) {
                    data.draugrRaidScalingDifficulty = true;
                    updated = true;
                }

                draugrMinBlockProbability = data.draugrMinBlockProbability;
                draugrMaxBlockProbability = data.draugrMaxBlockProbability;
                draugrOverlordMinBlockProbability = data.draugrOverlordMinBlockProbability;
                draugrOverlordMaxBlockProbability = data.draugrOverlordMaxBlockProbability;
                draugrSpawnWithPotionProbability = data.draugrSpawnWithPotionProbability;
                draugrRaidScalingDifficulty = data.draugrRaidScalingDifficulty;

                if (updated) {
                    saveConfig(configDir);
                }
            } catch (IOException e) {
                System.err.println("Failed to load config file: " + e.getMessage());
            }
        } else {
            saveConfig(configDir);
        }
    }

    public static void saveConfig(File configDir) {
        File configFile = new File(configDir, CONFIG_FILE_NAME);
        ConfigData data = new ConfigData(draugrMinBlockProbability, draugrMaxBlockProbability,
                draugrOverlordMinBlockProbability, draugrOverlordMaxBlockProbability,
                draugrSpawnWithPotionProbability, draugrRaidScalingDifficulty);
        try (FileWriter writer = new FileWriter(configFile)) {
            GSON.toJson(data, writer);
        } catch (IOException e) {
            System.err.println("Failed to save config file: " + e.getMessage());
        }
    }

    private static class ConfigData {
        float draugrMinBlockProbability;
        float draugrMaxBlockProbability;
        float draugrOverlordMinBlockProbability;
        float draugrOverlordMaxBlockProbability;
        float draugrSpawnWithPotionProbability;
        Boolean draugrRaidScalingDifficulty;

        ConfigData(float draugrMinBlockProbability, float draugrMaxBlockProbability,float draugrOverlordMinBlockProbability,
                   float draugrOverlordMaxBlockProbability, float draugrSpawnWithPotionProbability,
                   boolean draugrRaidScalingDifficulty) {
            this.draugrMinBlockProbability = draugrMinBlockProbability;
            this.draugrMaxBlockProbability = draugrMaxBlockProbability;
            this.draugrOverlordMinBlockProbability = draugrOverlordMinBlockProbability;
            this.draugrOverlordMaxBlockProbability = draugrOverlordMaxBlockProbability;
            this.draugrSpawnWithPotionProbability = draugrSpawnWithPotionProbability;
            this.draugrRaidScalingDifficulty = draugrRaidScalingDifficulty;
        }
    }
}
