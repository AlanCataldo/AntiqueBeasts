package net.mebahel.antiquebeasts.util;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

public class ModConfig {
    private static final String CONFIG_FILE_NAME = "antiquebeasts_config.json";
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();

    // Configuration properties with defaults
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

                // Vérifier et mettre à jour les champs manquants
                boolean updated = false;

                if (data.patrolSpawning == null) {
                    data.patrolSpawning = true;  // Valeur par défaut
                    updated = true;
                }
                if (data.patrolSpawnDelay == null || data.patrolSpawnDelay < 1 || data.patrolSpawnDelay > 60) {
                    data.patrolSpawnDelay = 15;  // Valeur par défaut
                    updated = true;
                }

                // Mettre à jour les valeurs de la classe
                patrolSpawning = data.patrolSpawning;
                patrolSpawnDelay = data.patrolSpawnDelay;

                // Sauvegarder la configuration si elle a été mise à jour
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
        Boolean patrolSpawning;  // Utilisation de Boolean pour permettre la vérification de null
        Integer patrolSpawnDelay;  // Utilisation de Integer pour permettre la vérification de null

        ConfigData(boolean patrolSpawning, int patrolSpawnDelay) {
            this.patrolSpawning = patrolSpawning;
            this.patrolSpawnDelay = patrolSpawnDelay;
        }
    }
}
