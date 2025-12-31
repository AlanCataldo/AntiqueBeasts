package net.mebahel.antiquebeasts.config.draugr;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

public class DraugrSpawnRateConfig {
    private static final String CONFIG_FILE_NAME = "spawn_rate_config.json";
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();

    // Configuration properties with defaults
    public static int draugrSpawnRate = 10;
    public static int draugrArcherSpawnRate = 9;
    public static int draugrWightSpawnRate = 7;
    public static int draugrScourgeSpawnRate = 5;
    public static int skeletonWarriorSpawnRate = 8;

    public static void loadConfig(File configDir) {
        if (!configDir.exists()) {
            configDir.mkdirs();
        }

        File configFile = new File(configDir, CONFIG_FILE_NAME);
        if (configFile.exists()) {
            try (FileReader reader = new FileReader(configFile)) {
                ConfigData data = GSON.fromJson(reader, ConfigData.class);
                boolean updated = false;

                if (data.draugrSpawnRate == null || data.draugrSpawnRate > 10 || data.draugrSpawnRate < 0) {
                    data.draugrSpawnRate = 10;
                    updated = true;
                }
                if (data.draugrArcherSpawnRate == null || data.draugrArcherSpawnRate > 10 || data.draugrArcherSpawnRate < 0) {
                    data.draugrArcherSpawnRate = 9;
                    updated = true;
                }
                if (data.draugrWightSpawnRate == null || data.draugrWightSpawnRate > 10 || data.draugrWightSpawnRate < 0) {
                    data.draugrWightSpawnRate = 7;
                    updated = true;
                }
                if (data.draugrScourgeSpawnRate == null || data.draugrScourgeSpawnRate > 10 || data.draugrScourgeSpawnRate < 0) {
                    data.draugrScourgeSpawnRate = 5;
                    updated = true;
                }
                if (data.skeletonWarriorSpawnRate == null || data.skeletonWarriorSpawnRate > 10 || data.skeletonWarriorSpawnRate < 0) {
                    data.skeletonWarriorSpawnRate = 8;
                    updated = true;
                }
                draugrSpawnRate = data.draugrSpawnRate;
                draugrArcherSpawnRate = data.draugrArcherSpawnRate;
                draugrWightSpawnRate = data.draugrWightSpawnRate;
                draugrScourgeSpawnRate = data.draugrScourgeSpawnRate;
                skeletonWarriorSpawnRate = data.skeletonWarriorSpawnRate;

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
        ConfigData data = new ConfigData(draugrSpawnRate, draugrArcherSpawnRate,
                draugrWightSpawnRate, draugrScourgeSpawnRate, skeletonWarriorSpawnRate);
        try (FileWriter writer = new FileWriter(configFile)) {
            GSON.toJson(data, writer);
        } catch (IOException e) {
            System.err.println("Failed to save config file: " + e.getMessage());
        }
    }

    private static class ConfigData {
        Integer draugrSpawnRate;
        Integer draugrArcherSpawnRate;
        Integer draugrWightSpawnRate;
        Integer draugrScourgeSpawnRate;
        Integer skeletonWarriorSpawnRate;

        ConfigData(int draugrSpawnRate, int draugrArcherSpawnRate, int draugrWightSpawnRate,
                   int draugrScourgeSpawnRate,  int skeletonWarriorSpawnRate) {
            this.draugrSpawnRate = draugrSpawnRate;
            this.draugrArcherSpawnRate = draugrArcherSpawnRate;
            this.draugrWightSpawnRate = draugrWightSpawnRate;
            this.draugrScourgeSpawnRate = draugrScourgeSpawnRate;
            this.skeletonWarriorSpawnRate = skeletonWarriorSpawnRate;
        }
    }
}
