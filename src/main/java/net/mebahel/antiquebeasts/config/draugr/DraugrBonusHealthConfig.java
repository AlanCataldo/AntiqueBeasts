package net.mebahel.antiquebeasts.config.draugr;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

public class DraugrBonusHealthConfig {
    private static final String CONFIG_FILE_NAME = "bonus_health_config.json";
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();

    // Configuration properties with defaults

    public static int draugrBonusHealth = 0;
    public static int draugrArcherBonusHealth = 0;
    public static int draugrWightBonusHealth = 0;
    public static int draugrScourgeBonusHealth = 0;
    public static int draugrOverlordBonusHealth = 0;
    public static int skeletonWarriorBonusHealth = 0;
    public static int skeletonWarriorHeadBonusHealth = 0;

    public static void loadConfig(File configDir) {
        if (!configDir.exists()) {
            configDir.mkdirs();
        }

        File configFile = new File(configDir, CONFIG_FILE_NAME);
        if (configFile.exists()) {
            try (FileReader reader = new FileReader(configFile)) {
                ConfigData data = GSON.fromJson(reader, ConfigData.class);
                boolean updated = false;

                if (data.draugrBonusHealth == null || data.draugrBonusHealth > 100 || data.draugrBonusHealth < 0) {
                    data.draugrBonusHealth = 0;
                    updated = true;
                }
                if (data.draugrArcherBonusHealth == null || data.draugrArcherBonusHealth > 100 || data.draugrArcherBonusHealth < 0) {
                    data.draugrArcherBonusHealth = 0;
                    updated = true;
                }
                if (data.draugrWightBonusHealth == null || data.draugrWightBonusHealth > 100 || data.draugrWightBonusHealth < 0) {
                    data.draugrWightBonusHealth = 0;
                    updated = true;
                }
                if (data.draugrScourgeBonusHealth == null || data.draugrScourgeBonusHealth > 100 || data.draugrScourgeBonusHealth < 0) {
                    data.draugrScourgeBonusHealth = 0;
                    updated = true;
                }
                if (data.draugrOverlordBonusHealth == null || data.draugrOverlordBonusHealth > 100 || data.draugrOverlordBonusHealth < 0) {
                    data.draugrOverlordBonusHealth = 0;
                    updated = true;
                }
                if (data.skeletonWarriorBonusHealth == null || data.skeletonWarriorBonusHealth > 100 || data.skeletonWarriorBonusHealth < 0) {
                    data.skeletonWarriorBonusHealth = 0;
                    updated = true;
                }
                if (data.skeletonWarriorHeadBonusHealth == null || data.skeletonWarriorHeadBonusHealth > 100 || data.skeletonWarriorHeadBonusHealth < 0) {
                    data.skeletonWarriorHeadBonusHealth = 0;
                    updated = true;
                }

                draugrBonusHealth = data.draugrBonusHealth;
                draugrArcherBonusHealth = data.draugrArcherBonusHealth;
                draugrWightBonusHealth = data.draugrWightBonusHealth;
                draugrScourgeBonusHealth = data.draugrScourgeBonusHealth;
                draugrOverlordBonusHealth = data.draugrOverlordBonusHealth;
                skeletonWarriorBonusHealth = data.skeletonWarriorBonusHealth;
                skeletonWarriorHeadBonusHealth = data.skeletonWarriorHeadBonusHealth;

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
        ConfigData data = new ConfigData(draugrBonusHealth, draugrArcherBonusHealth, draugrWightBonusHealth,
                draugrScourgeBonusHealth, draugrOverlordBonusHealth, skeletonWarriorBonusHealth, skeletonWarriorHeadBonusHealth);
        try (FileWriter writer = new FileWriter(configFile)) {
            GSON.toJson(data, writer);
        } catch (IOException e) {
            System.err.println("Failed to save config file: " + e.getMessage());
        }

    }

    private static class ConfigData {
        Integer draugrBonusHealth;
        Integer draugrArcherBonusHealth;
        Integer draugrWightBonusHealth;
        Integer draugrScourgeBonusHealth;
        Integer draugrOverlordBonusHealth;
        Integer skeletonWarriorBonusHealth;
        Integer skeletonWarriorHeadBonusHealth;

        ConfigData(int draugrBonusHealth, int draugrArcherBonusHealth, int draugrWightBonusHealth,
                   int draugrScourgeBonusHealth, int draugrOverlordBonusHealth, int skeletonWarriorBonusHealth,
                   int skeletonWarriorHeadBonusHealth) {

            this.draugrBonusHealth = draugrBonusHealth;
            this.draugrArcherBonusHealth = draugrArcherBonusHealth;
            this.draugrWightBonusHealth = draugrWightBonusHealth;
            this.draugrScourgeBonusHealth = draugrScourgeBonusHealth;
            this.draugrOverlordBonusHealth = draugrOverlordBonusHealth;
            this.skeletonWarriorBonusHealth = skeletonWarriorBonusHealth;
            this.skeletonWarriorHeadBonusHealth = skeletonWarriorHeadBonusHealth;
        }
    }
}
