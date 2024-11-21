package net.mebahel.antiquebeasts.util.config;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import net.minecraft.client.gui.tab.Tab;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Map;

public class ModArmorValueConfig {
    private static final String CONFIG_FILE_NAME = "antiquebeasts_armor_value_config.json";
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    public static int[] valkyrieArmor = {4, 6, 8, 4};
    public static int[] ironPlateArmor = {3, 5, 6, 2};
    public static int[] goldPlateArmor = {3, 5, 6, 3};
    public static int[] diamondPlateArmor = {4, 6, 8, 4};
    public static int[] netheritePlateArmor = {5, 7, 9, 5};
    public static int[] ironScaleArmor = {3, 5, 6, 2};
    public static int[] goldScaleArmor = {3, 5, 6, 3};
    public static int[] diamondScaleArmor = {4, 6, 8, 4};

    public static void loadConfig(File configDir) {
        if (!configDir.exists()) {
            configDir.mkdirs();
        }

        File configFile = new File(configDir, CONFIG_FILE_NAME);
        if (configFile.exists()) {
            try (FileReader reader = new FileReader(configFile)) {
                ConfigData data = GSON.fromJson(reader, ConfigData.class);

                boolean updated = false;

                if (data.valkyrieArmor == null) {
                    data.valkyrieArmor = new int[]{4, 6, 8, 4};
                    updated = true;
                }
                if (data.ironPlateArmor == null) {
                    data.ironPlateArmor = new int[]{3, 5, 6, 2};
                    updated = true;
                }
                if (data.goldPlateArmor == null) {
                    data.goldPlateArmor = new int[]{3, 5, 6, 3};
                    updated = true;
                }
                if (data.diamondPlateArmor == null) {
                    data.diamondPlateArmor = new int[]{4, 6, 8, 4};
                    updated = true;
                }
                if (data.netheritePlateArmor == null) {
                    data.netheritePlateArmor = new int[]{5, 7, 9, 5};
                    updated = true;
                }
                if (data.ironScaleArmor == null) {
                    data.ironScaleArmor = new int[]{3, 5, 6, 2};
                    updated = true;
                }
                if (data.goldScaleArmor == null) {
                    data.goldScaleArmor = new int[]{3, 5, 6, 3};
                    updated = true;
                }
                if (data.diamondScaleArmor == null) {
                    data.diamondScaleArmor = new int[]{4, 6, 8, 4};
                    updated = true;
                }
                valkyrieArmor = data.valkyrieArmor;
                ironPlateArmor = data.ironPlateArmor;
                goldPlateArmor = data.goldPlateArmor;
                diamondPlateArmor = data.diamondPlateArmor;
                netheritePlateArmor = data.netheritePlateArmor;
                ironScaleArmor = data.ironScaleArmor;
                goldScaleArmor = data.goldScaleArmor;
                diamondScaleArmor = data.diamondScaleArmor;

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
        ConfigData data = new ConfigData(valkyrieArmor, ironPlateArmor, goldPlateArmor, diamondPlateArmor, netheritePlateArmor,
                ironScaleArmor, goldScaleArmor, diamondScaleArmor);
        try (FileWriter writer = new FileWriter(configFile)) {
            GSON.toJson(data, writer);
        } catch (IOException e) {
            System.err.println("Failed to save config file: " + e.getMessage());
        }
    }

    private static class ConfigData {
        int[] valkyrieArmor;
        int[] ironPlateArmor;
        int[] goldPlateArmor;
        int[] diamondPlateArmor;
        int[] netheritePlateArmor;
        int[] ironScaleArmor;
        int[] goldScaleArmor;
        int[] diamondScaleArmor;

        ConfigData(int[] valkyrieArmor, int[] ironPlateArmor, int[] goldPlateArmor, int[] diamondPlateArmor, int[] netheritePlateArmor,
                   int[] ironScaleArmor, int[] goldScaleArmor, int[] diamondScaleArmor) {
            this.valkyrieArmor = valkyrieArmor;
            this.ironPlateArmor = ironPlateArmor;
            this.goldPlateArmor = goldPlateArmor;
            this.diamondPlateArmor = diamondPlateArmor;
            this.netheritePlateArmor = netheritePlateArmor;
            this.ironScaleArmor = ironScaleArmor;
            this.goldScaleArmor = goldScaleArmor;
            this.diamondScaleArmor = diamondScaleArmor;
        }
    }
}
