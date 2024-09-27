package net.mebahel.antiquebeasts.util.config;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

public class ModConfig {
    private static final String CONFIG_FILE_NAME = "antiquebeasts_config.json";
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();

    public static boolean patrolSpawning = true;
    public static int patrolSpawnDelay = 15;

    public static void loadConfig(File configDir) {
        if (!configDir.exists()) {
            configDir.mkdirs();
        }

        File configFile = new File(configDir, CONFIG_FILE_NAME);
        if (configFile.exists()) {
            try (FileReader reader = new FileReader(configFile)) {
                ConfigData data = GSON.fromJson(reader, ConfigData.class);

                boolean updated = false;

                if (data.patrolSpawning == null) {
                    data.patrolSpawning = true;
                    updated = true;
                }
                if (data.patrolSpawnDelay == null || data.patrolSpawnDelay < 1 || data.patrolSpawnDelay > 60) {
                    data.patrolSpawnDelay = 15;
                    updated = true;
                }

                patrolSpawning = data.patrolSpawning;
                patrolSpawnDelay = data.patrolSpawnDelay;

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
        ConfigData data = new ConfigData(patrolSpawning, patrolSpawnDelay);
        try (FileWriter writer = new FileWriter(configFile)) {
            GSON.toJson(data, writer);
        } catch (IOException e) {
            System.err.println("Failed to save config file: " + e.getMessage());
        }
    }

    private static class ConfigData {
        Boolean patrolSpawning;
        Integer patrolSpawnDelay;

        ConfigData(boolean patrolSpawning, int patrolSpawnDelay) {
            this.patrolSpawning = patrolSpawning;
            this.patrolSpawnDelay = patrolSpawnDelay;
        }
    }
}
