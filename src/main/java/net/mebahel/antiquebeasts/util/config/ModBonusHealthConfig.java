package net.mebahel.antiquebeasts.util.config;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

public class ModBonusHealthConfig {
    private static final String CONFIG_FILE_NAME = "antiquebeasts_bonus_health_config.json";
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();

    // Configuration properties with defaults
    public static int eliteHopliteBonusHealth = 0;
    public static int championHopliteBonusHealth = 0;
    public static int heroHopliteBonusHealth = 0;
    public static int hadesChosenBonusHealth = 0;
    public static int chimeraBonusHealth = 0;
    public static int cyclopsBonusHealth = 0;
    public static int frostCyclopsBonusHealth = 0;
    public static int pegasusBonusHealth = 0;
    public static int centaurBonusHealth = 0;
    public static int hadesShadeBonusHealth = 0;
    public static int throwingAxemanBonusHealth = 0;
    public static int hersirBonusHealth = 0;
    public static int huskarlBonusHealth = 0;
    public static int einherjarBonusHealth = 0;
    public static int valkyrieBonusHealth = 0;
    public static int wadjetBonusHealth = 0;
    public static int mummyBonusHealth = 0;
    public static int servantBonusHealth = 0;
    public static int egyptianCaravanBonusHealth = 0;
    public static int draugrBonusHealth = 0;
    public static int draugrArcherBonusHealth = 0;
    public static int draugrWightBonusHealth = 0;
    public static int draugrScourgeBonusHealth = 0;
    public static int harpyBonusHealth = 0;
    public static int mummifiedPharaohBonusHealth = 0;
    public static int axemanBonusHealth = 0;
    public static int camleryBonusHealth = 0;
    public static int elephantRiderBonusHealth = 0;

    public static void loadConfig(File configDir) {
        if (!configDir.exists()) {
            configDir.mkdirs();
        }

        File configFile = new File(configDir, CONFIG_FILE_NAME);
        if (configFile.exists()) {
            try (FileReader reader = new FileReader(configFile)) {
                ConfigData data = GSON.fromJson(reader, ConfigData.class);
                boolean updated = false;

                if (data.eliteHopliteBonusHealth == null || data.eliteHopliteBonusHealth > 100 || data.eliteHopliteBonusHealth < 0) {
                    data.eliteHopliteBonusHealth = 0;
                    updated = true;
                }
                if (data.championHopliteBonusHealth == null || data.championHopliteBonusHealth > 100 || data.championHopliteBonusHealth < 0) {
                    data.championHopliteBonusHealth = 0;
                    updated = true;
                }
                if (data.heroHopliteBonusHealth == null || data.heroHopliteBonusHealth > 100 || data.heroHopliteBonusHealth < 0) {
                    data.heroHopliteBonusHealth = 0;
                    updated = true;
                }
                if (data.hadesChosenBonusHealth == null || data.hadesChosenBonusHealth > 100 || data.hadesChosenBonusHealth < 0) {
                    data.hadesChosenBonusHealth = 3;
                    updated = true;
                }
                if (data.chimeraBonusHealth == null || data.chimeraBonusHealth > 100 || data.chimeraBonusHealth < 0) {
                    data.chimeraBonusHealth = 0;
                    updated = true;
                }
                if (data.cyclopsBonusHealth == null || data.cyclopsBonusHealth > 100 || data.cyclopsBonusHealth < 0) {
                    data.cyclopsBonusHealth = 0;
                    updated = true;
                }
                if (data.frostCyclopsBonusHealth == null || data.frostCyclopsBonusHealth > 100 || data.frostCyclopsBonusHealth < 0) {
                    data.frostCyclopsBonusHealth = 0;
                    updated = true;
                }
                if (data.pegasusBonusHealth == null || data.pegasusBonusHealth > 100 || data.pegasusBonusHealth < 0) {
                    data.pegasusBonusHealth = 0;
                    updated = true;
                }
                if (data.centaurBonusHealth == null || data.centaurBonusHealth > 100 || data.centaurBonusHealth < 0) {
                    data.centaurBonusHealth = 0;
                    updated = true;
                }

                if (data.hadesShadeBonusHealth == null || data.hadesShadeBonusHealth > 100 || data.hadesShadeBonusHealth < 0) {
                    data.hadesShadeBonusHealth = 0;
                    updated = true;
                }

                if (data.throwingAxemanBonusHealth == null || data.throwingAxemanBonusHealth > 100 || data.throwingAxemanBonusHealth < 0) {
                    data.throwingAxemanBonusHealth = 0;
                    updated = true;
                }

                if (data.hersirBonusHealth == null || data.hersirBonusHealth > 100 || data.hersirBonusHealth < 0) {
                    data.hersirBonusHealth = 0;
                    updated = true;
                }

                if (data.huskarlBonusHealth == null || data.huskarlBonusHealth > 100 || data.huskarlBonusHealth < 0) {
                    data.huskarlBonusHealth = 0;
                    updated = true;
                }

                if (data.einherjarBonusHealth == null || data.einherjarBonusHealth > 100 || data.einherjarBonusHealth < 0) {
                    data.einherjarBonusHealth = 0;
                    updated = true;
                }

                if (data.valkyrieBonusHealth == null || data.valkyrieBonusHealth > 100 || data.valkyrieBonusHealth < 0) {
                    data.valkyrieBonusHealth = 0;
                    updated = true;
                }

                if (data.wadjetBonusHealth == null || data.wadjetBonusHealth > 100 || data.wadjetBonusHealth < 0) {
                    data.wadjetBonusHealth = 0;
                    updated = true;
                }

                if (data.mummyBonusHealth == null || data.mummyBonusHealth > 100 || data.mummyBonusHealth < 0) {
                    data.mummyBonusHealth = 0;
                    updated = true;
                }

                if (data.servantBonusHealth == null || data.servantBonusHealth > 100 || data.servantBonusHealth < 0) {
                    data.servantBonusHealth = 0;
                    updated = true;
                }
                if (data.egyptianCaravanBonusHealth == null || data.egyptianCaravanBonusHealth > 100 || data.egyptianCaravanBonusHealth < 0) {
                    data.egyptianCaravanBonusHealth = 0;
                    updated = true;
                }
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
                if (data.harpyBonusHealth == null || data.harpyBonusHealth > 100 || data.harpyBonusHealth < 0) {
                    data.harpyBonusHealth = 0;
                    updated = true;
                }
                if (data.mummifiedPharaohBonusHealth == null || data.mummifiedPharaohBonusHealth > 100 || data.mummifiedPharaohBonusHealth < 0) {
                    data.mummifiedPharaohBonusHealth = 0;
                    updated = true;
                }
                if (data.axemanBonusHealth == null || data.axemanBonusHealth > 100 || data.axemanBonusHealth < 0) {
                    data.axemanBonusHealth = 0;
                    updated = true;
                }
                if (data.camleryBonusHealth == null || data.camleryBonusHealth > 100 || data.camleryBonusHealth < 0) {
                    data.camleryBonusHealth = 0;
                    updated = true;
                }
                if (data.elephantRiderBonusHealth == null || data.elephantRiderBonusHealth > 100 || data.elephantRiderBonusHealth < 0) {
                    data.elephantRiderBonusHealth = 0;
                    updated = true;
                }

                eliteHopliteBonusHealth = data.eliteHopliteBonusHealth;
                championHopliteBonusHealth = data.championHopliteBonusHealth;
                heroHopliteBonusHealth = data.heroHopliteBonusHealth;
                hadesChosenBonusHealth = data.hadesChosenBonusHealth;
                chimeraBonusHealth = data.chimeraBonusHealth;
                cyclopsBonusHealth = data.cyclopsBonusHealth;
                frostCyclopsBonusHealth = data.frostCyclopsBonusHealth;
                pegasusBonusHealth = data.pegasusBonusHealth;
                centaurBonusHealth = data.centaurBonusHealth;
                hadesShadeBonusHealth = data.hadesShadeBonusHealth;
                throwingAxemanBonusHealth = data.throwingAxemanBonusHealth;
                hersirBonusHealth = data.hersirBonusHealth;
                huskarlBonusHealth = data.huskarlBonusHealth;
                einherjarBonusHealth = data.einherjarBonusHealth;
                valkyrieBonusHealth = data.valkyrieBonusHealth;
                wadjetBonusHealth = data.wadjetBonusHealth;
                mummyBonusHealth = data.mummyBonusHealth;
                servantBonusHealth = data.servantBonusHealth;
                egyptianCaravanBonusHealth = data.egyptianCaravanBonusHealth;
                draugrBonusHealth = data.draugrBonusHealth;
                draugrArcherBonusHealth = data.draugrArcherBonusHealth;
                draugrWightBonusHealth = data.draugrWightBonusHealth;
                draugrScourgeBonusHealth = data.draugrScourgeBonusHealth;
                harpyBonusHealth = data.harpyBonusHealth;
                mummifiedPharaohBonusHealth= data.mummifiedPharaohBonusHealth;
                axemanBonusHealth = data.axemanBonusHealth;
                camleryBonusHealth = data.camleryBonusHealth;
                elephantRiderBonusHealth = data.elephantRiderBonusHealth;

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
        ConfigData data = new ConfigData(eliteHopliteBonusHealth, championHopliteBonusHealth, heroHopliteBonusHealth, hadesChosenBonusHealth, chimeraBonusHealth,
                cyclopsBonusHealth, frostCyclopsBonusHealth, pegasusBonusHealth, centaurBonusHealth, hadesShadeBonusHealth,
                throwingAxemanBonusHealth, hersirBonusHealth, huskarlBonusHealth, einherjarBonusHealth, valkyrieBonusHealth,
                wadjetBonusHealth, mummyBonusHealth, servantBonusHealth, egyptianCaravanBonusHealth, draugrBonusHealth, draugrArcherBonusHealth, draugrWightBonusHealth,
                draugrScourgeBonusHealth,
                harpyBonusHealth, mummifiedPharaohBonusHealth, axemanBonusHealth, camleryBonusHealth, elephantRiderBonusHealth);
        try (FileWriter writer = new FileWriter(configFile)) {
            GSON.toJson(data, writer);
        } catch (IOException e) {
            System.err.println("Failed to save config file: " + e.getMessage());
        }
    }

    private static class ConfigData {
        Integer eliteHopliteBonusHealth;
        Integer championHopliteBonusHealth;
        Integer heroHopliteBonusHealth;
        Integer hadesChosenBonusHealth;
        Integer chimeraBonusHealth;
        Integer cyclopsBonusHealth;
        Integer frostCyclopsBonusHealth;
        Integer pegasusBonusHealth;
        Integer centaurBonusHealth;
        Integer hadesShadeBonusHealth;
        Integer throwingAxemanBonusHealth;
        Integer hersirBonusHealth;
        Integer huskarlBonusHealth;
        Integer einherjarBonusHealth;
        Integer valkyrieBonusHealth;
        Integer wadjetBonusHealth;
        Integer mummyBonusHealth;
        Integer servantBonusHealth;
        Integer egyptianCaravanBonusHealth;
        Integer draugrBonusHealth;
        Integer draugrArcherBonusHealth;
        Integer draugrWightBonusHealth;
        Integer draugrScourgeBonusHealth;
        Integer harpyBonusHealth;
        Integer mummifiedPharaohBonusHealth;
        Integer axemanBonusHealth;
        Integer camleryBonusHealth;
        Integer elephantRiderBonusHealth;
        ConfigData(int eliteHopliteBonusHealth, int championHopliteBonusHealth, int heroHopliteBonusHealth, int hadesChosenBonusHealth,
                   int chimeraBonusHealth, int cyclopsBonusHealth, int frostCyclopsBonusHealth, int pegasusBonusHealth, int centaurBonusHealth,
                   int hadesShadeBonusHealth, int throwingAxemanBonusHealth, int hersirBonusHealth, int huskarlBonusHealth, int einherjarBonusHealth,
                   int valkyrieBonusHealth, int wadjetBonusHealth, int mummyBonusHealth, int servantBonusHealth, int egyptianCaravanBonusHealth,
                   int draugrBonusHealth, int draugrArcherBonusHealth, int draugrWightBonusHealth,
                   int draugrScourgeBonusHealth, int harpyBonusHealth, int mummifiedPharaohBonusHealth,int axemanBonusHealth,
                   int camleryBonusHealth, int elephantRiderBonusHealth) {

            this.eliteHopliteBonusHealth = eliteHopliteBonusHealth;
            this.championHopliteBonusHealth = championHopliteBonusHealth;
            this.heroHopliteBonusHealth = heroHopliteBonusHealth;
            this.hadesChosenBonusHealth = hadesChosenBonusHealth;
            this.chimeraBonusHealth = chimeraBonusHealth;
            this.cyclopsBonusHealth = cyclopsBonusHealth;
            this.frostCyclopsBonusHealth = frostCyclopsBonusHealth;
            this.pegasusBonusHealth = pegasusBonusHealth;
            this.centaurBonusHealth = centaurBonusHealth;
            this.hadesShadeBonusHealth = hadesShadeBonusHealth;
            this.throwingAxemanBonusHealth = throwingAxemanBonusHealth;
            this.hersirBonusHealth = hersirBonusHealth;
            this.huskarlBonusHealth = huskarlBonusHealth;
            this.einherjarBonusHealth = einherjarBonusHealth;
            this.valkyrieBonusHealth = valkyrieBonusHealth;
            this.wadjetBonusHealth = wadjetBonusHealth;
            this.mummyBonusHealth = mummyBonusHealth;
            this.servantBonusHealth = servantBonusHealth;
            this.egyptianCaravanBonusHealth = egyptianCaravanBonusHealth;
            this.draugrBonusHealth = draugrBonusHealth;
            this.draugrArcherBonusHealth = draugrArcherBonusHealth;
            this.draugrWightBonusHealth = draugrWightBonusHealth;
            this.draugrScourgeBonusHealth = draugrScourgeBonusHealth;
            this.harpyBonusHealth = harpyBonusHealth;
            this.mummifiedPharaohBonusHealth = mummifiedPharaohBonusHealth;
            this.axemanBonusHealth = axemanBonusHealth;
            this.camleryBonusHealth = camleryBonusHealth;
            this.elephantRiderBonusHealth = elephantRiderBonusHealth;
        }
    }
}
