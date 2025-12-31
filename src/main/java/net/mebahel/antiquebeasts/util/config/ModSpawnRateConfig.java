package net.mebahel.antiquebeasts.util.config;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

public class ModSpawnRateConfig {
    private static final String CONFIG_FILE_NAME = "antiquebeasts_spawn_rate_config.json";
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();

    // Configuration properties with defaults
    public static int eliteHopliteSpawnRate = 5;
    public static int championHopliteSpawnRate = 5;
    public static int heroHopliteSpawnRate = 5;
    public static int hadesChosenSpawnRate = 3;
    public static int chimeraSpawnRate = 2;
    public static int cyclopsSpawnRate = 5;
    public static int frostCyclopsSpawnRate = 5;
    public static int pegasusSpawnRate = 5;
    public static int centaurSpawnRate = 5;
    public static int hadesShadeSpawnRate = 4;
    public static int throwingAxemanSpawnRate = 4;
    public static int hersirSpawnRate = 5;
    public static int huskarlSpawnRate = 5;
    public static int einherjarSpawnRate = 5;
    public static int valkyrieSpawnRate = 5;
    public static int wadjetSpawnRate = 3;
    public static int mummySpawnRate = 3;
    public static int servantSpawnRate = 3;
    public static int egyptianCaravanSpawnRate = 2;
    public static int harpySpawnRate = 3;

    public static void loadConfig(File configDir) {
        if (!configDir.exists()) {
            configDir.mkdirs();
        }

        File configFile = new File(configDir, CONFIG_FILE_NAME);
        if (configFile.exists()) {
            try (FileReader reader = new FileReader(configFile)) {
                ConfigData data = GSON.fromJson(reader, ConfigData.class);
                boolean updated = false;

                if (data.eliteHopliteSpawnRate == null || data.eliteHopliteSpawnRate > 10 || data.eliteHopliteSpawnRate < 0) {
                    data.eliteHopliteSpawnRate = 5;
                    updated = true;
                }
                if (data.championHopliteSpawnRate == null || data.championHopliteSpawnRate > 10 || data.championHopliteSpawnRate < 0) {
                    data.championHopliteSpawnRate = 5;
                    updated = true;
                }
                if (data.heroHopliteSpawnRate == null || data.heroHopliteSpawnRate > 10 || data.heroHopliteSpawnRate < 0) {
                    data.heroHopliteSpawnRate = 5;
                    updated = true;
                }
                if (data.hadesChosenSpawnRate == null || data.hadesChosenSpawnRate > 10 || data.hadesChosenSpawnRate < 0) {
                    data.hadesChosenSpawnRate = 3;
                    updated = true;
                }
                if (data.chimeraSpawnRate == null || data.chimeraSpawnRate > 10 || data.chimeraSpawnRate < 0) {
                    data.chimeraSpawnRate = 2;
                    updated = true;
                }
                if (data.cyclopsSpawnRate == null || data.cyclopsSpawnRate > 10 || data.cyclopsSpawnRate < 0) {
                    data.cyclopsSpawnRate = 5;
                    updated = true;
                }
                if (data.frostCyclopsSpawnRate == null || data.frostCyclopsSpawnRate > 10 || data.frostCyclopsSpawnRate < 0) {
                    data.frostCyclopsSpawnRate = 5;
                    updated = true;
                }
                if (data.pegasusSpawnRate == null || data.pegasusSpawnRate > 10 || data.pegasusSpawnRate < 0) {
                    data.pegasusSpawnRate = 5;
                    updated = true;
                }
                if (data.centaurSpawnRate == null || data.centaurSpawnRate > 10 || data.centaurSpawnRate < 0) {
                    data.centaurSpawnRate = 5;
                    updated = true;
                }

                if (data.hadesShadeSpawnRate == null || data.hadesShadeSpawnRate > 10 || data.hadesShadeSpawnRate < 0) {
                    data.hadesShadeSpawnRate = 4;
                    updated = true;
                }

                if (data.throwingAxemanSpawnRate == null || data.throwingAxemanSpawnRate > 10 || data.throwingAxemanSpawnRate < 0) {
                    data.throwingAxemanSpawnRate = 5;
                    updated = true;
                }

                if (data.hersirSpawnRate == null || data.hersirSpawnRate > 10 || data.hersirSpawnRate < 0) {
                    data.hersirSpawnRate = 5;
                    updated = true;
                }

                if (data.huskarlSpawnRate == null || data.huskarlSpawnRate > 10 || data.huskarlSpawnRate < 0) {
                    data.huskarlSpawnRate = 5;
                    updated = true;
                }

                if (data.einherjarSpawnRate == null || data.einherjarSpawnRate > 10 || data.einherjarSpawnRate < 0) {
                    data.einherjarSpawnRate = 5;
                    updated = true;
                }

                if (data.valkyrieSpawnRate == null || data.valkyrieSpawnRate > 10 || data.valkyrieSpawnRate < 0) {
                    data.valkyrieSpawnRate = 5;
                    updated = true;
                }

                if (data.wadjetSpawnRate == null || data.wadjetSpawnRate > 10 || data.wadjetSpawnRate < 0) {
                    data.wadjetSpawnRate = 3;
                    updated = true;
                }

                if (data.mummySpawnRate == null || data.mummySpawnRate > 10 || data.mummySpawnRate < 0) {
                    data.mummySpawnRate = 3;
                    updated = true;
                }

                if (data.servantSpawnRate == null || data.servantSpawnRate > 10 || data.servantSpawnRate < 0) {
                    data.servantSpawnRate = 3;
                    updated = true;
                }
                if (data.egyptianCaravanSpawnRate == null || data.egyptianCaravanSpawnRate > 10 || data.egyptianCaravanSpawnRate < 0) {
                    data.egyptianCaravanSpawnRate = 2;
                    updated = true;
                }
                if (data.harpySpawnRate == null || data.harpySpawnRate > 10 || data.harpySpawnRate < 0) {
                    data.harpySpawnRate = 3;
                    updated = true;
                }

                eliteHopliteSpawnRate = data.eliteHopliteSpawnRate;
                championHopliteSpawnRate = data.championHopliteSpawnRate;
                heroHopliteSpawnRate = data.heroHopliteSpawnRate;
                hadesChosenSpawnRate = data.hadesChosenSpawnRate;
                chimeraSpawnRate = data.chimeraSpawnRate;
                cyclopsSpawnRate = data.cyclopsSpawnRate;
                frostCyclopsSpawnRate = data.frostCyclopsSpawnRate;
                pegasusSpawnRate = data.pegasusSpawnRate;
                centaurSpawnRate = data.centaurSpawnRate;
                hadesShadeSpawnRate = data.hadesShadeSpawnRate;
                throwingAxemanSpawnRate = data.throwingAxemanSpawnRate;
                hersirSpawnRate = data.hersirSpawnRate;
                huskarlSpawnRate = data.huskarlSpawnRate;
                einherjarSpawnRate = data.einherjarSpawnRate;
                valkyrieSpawnRate = data.valkyrieSpawnRate;
                wadjetSpawnRate = data.wadjetSpawnRate;
                mummySpawnRate = data.mummySpawnRate;
                servantSpawnRate = data.servantSpawnRate;
                egyptianCaravanSpawnRate = data.egyptianCaravanSpawnRate;
                harpySpawnRate = data.harpySpawnRate;

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
        ConfigData data = new ConfigData(eliteHopliteSpawnRate, championHopliteSpawnRate, heroHopliteSpawnRate, hadesChosenSpawnRate, chimeraSpawnRate,
                cyclopsSpawnRate, frostCyclopsSpawnRate, pegasusSpawnRate, centaurSpawnRate, hadesShadeSpawnRate,
                throwingAxemanSpawnRate, hersirSpawnRate, huskarlSpawnRate, einherjarSpawnRate, valkyrieSpawnRate,
                wadjetSpawnRate, mummySpawnRate, servantSpawnRate, egyptianCaravanSpawnRate, harpySpawnRate);
        try (FileWriter writer = new FileWriter(configFile)) {
            GSON.toJson(data, writer);
        } catch (IOException e) {
            System.err.println("Failed to save config file: " + e.getMessage());
        }
    }

    private static class ConfigData {
        Integer eliteHopliteSpawnRate;
        Integer championHopliteSpawnRate;
        Integer heroHopliteSpawnRate;
        Integer hadesChosenSpawnRate;
        Integer chimeraSpawnRate;
        Integer cyclopsSpawnRate;
        Integer frostCyclopsSpawnRate;
        Integer pegasusSpawnRate;
        Integer centaurSpawnRate;
        Integer hadesShadeSpawnRate;
        Integer throwingAxemanSpawnRate;
        Integer hersirSpawnRate;
        Integer huskarlSpawnRate;
        Integer einherjarSpawnRate;
        Integer valkyrieSpawnRate;
        Integer wadjetSpawnRate;
        Integer mummySpawnRate;
        Integer servantSpawnRate;
        Integer egyptianCaravanSpawnRate;
        Integer harpySpawnRate;

        ConfigData(int eliteHopliteSpawnRate, int championHopliteSpawnRate, int heroHopliteSpawnRate, int hadesChosenSpawnRate,
                   int chimeraSpawnRate, int cyclopsSpawnRate, int frostCyclopsSpawnRate, int pegasusSpawnRate, int centaurSpawnRate,
                   int hadesShadeSpawnRate, int throwingAxemanSpawnRate, int hersirSpawnRate, int huskarlSpawnRate, int einherjarSpawnRate,
                   int valkyrieSpawnRate, int wadjetSpawnRate, int mummySpawnRate, int servantSpawnRate, int egyptianCaravanSpawnRate,
                   int harpySpawnRate) {

            this.eliteHopliteSpawnRate = eliteHopliteSpawnRate;
            this.championHopliteSpawnRate = championHopliteSpawnRate;
            this.heroHopliteSpawnRate = heroHopliteSpawnRate;
            this.hadesChosenSpawnRate = hadesChosenSpawnRate;
            this.chimeraSpawnRate = chimeraSpawnRate;
            this.cyclopsSpawnRate = cyclopsSpawnRate;
            this.frostCyclopsSpawnRate = frostCyclopsSpawnRate;
            this.pegasusSpawnRate = pegasusSpawnRate;
            this.centaurSpawnRate = centaurSpawnRate;
            this.hadesShadeSpawnRate = hadesShadeSpawnRate;
            this.throwingAxemanSpawnRate = throwingAxemanSpawnRate;
            this.hersirSpawnRate = hersirSpawnRate;
            this.huskarlSpawnRate = huskarlSpawnRate;
            this.einherjarSpawnRate = einherjarSpawnRate;
            this.valkyrieSpawnRate = valkyrieSpawnRate;
            this.wadjetSpawnRate = wadjetSpawnRate;
            this.mummySpawnRate = mummySpawnRate;
            this.servantSpawnRate = servantSpawnRate;
            this.egyptianCaravanSpawnRate = egyptianCaravanSpawnRate;
            this.harpySpawnRate = harpySpawnRate;
        }
    }
}
