package net.mebahel.antiquebeasts.config.draugr;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

/**
 * Génère un README texte expliquant les différents champs de configuration
 * du mod Mebahel Creatures - Draugr.
 *
 * Le fichier sera créé dans le même répertoire que les fichiers JSON :
 *   - bonus_health_config.json
 *   - combat_balancing_config.json
 *   - spawn_rate_config.json
 */
public class DraugrConfigReadmeGenerator {

    private static final String README_FILE_NAME = "README.txt";

    /**
     * Génère (ou écrase) le fichier README_MebahelDraugrConfigs.txt
     * dans le dossier de config fourni.
     */
    public static void generate(File configDir) {
        if (!configDir.exists() && !configDir.mkdirs()) {
            System.err.println("[MebahelDraugr] Failed to create config directory for README");
            return;
        }

        File readmeFile = new File(configDir, README_FILE_NAME);

        try (FileWriter writer = new FileWriter(readmeFile)) {
            StringBuilder sb = new StringBuilder();

            sb.append("Mebahel's Draugr Creatures - Configuration Guide\n");
            sb.append("================================================\n\n");
            sb.append("This file explains what each field in the configuration JSON files does.\n");
            sb.append("All configs are located in this folder.\n\n\n");

            // -------- bonus_health_config.json --------
            sb.append("1) bonus_health_config.json\n");
            sb.append("--------------------------------\n");
            sb.append("Controls extra health (in HP/health points) added on top of each mob's base health.\n");
            sb.append("Valid values: 0 to 100 (anything outside this range will be reset to 0).\n\n");

            sb.append("  - draugrBonusHealth\n");
            sb.append("    Extra health added to basic Draugr melee units.\n");
            sb.append("    Example: 10 => base HP + 10.\n\n");

            sb.append("  - draugrArcherBonusHealth\n");
            sb.append("    Extra health added to Draugr Archers.\n\n");

            sb.append("  - draugrWightBonusHealth\n");
            sb.append("    Extra health added to Draugr Wights (elite melee + magic).\n\n");

            sb.append("  - draugrScourgeBonusHealth\n");
            sb.append("    Extra health added to Draugr Scourges (stronger spellcaster).\n\n");

            sb.append("  - draugrOverlordBonusHealth\n");
            sb.append("    Extra health added to the Draugr Overlord boss.\n");
            sb.append("    Useful if you want a longer, more epic fight.\n\n");

            sb.append("  - skeletonWarriorBonusHealth\n");
            sb.append("    Extra health added to Skeleton Warriors.\n\n");

            sb.append("  - skeletonWarriorHeadBonusHealth\n");
            sb.append("    Extra health added to the floating Skeleton Warrior Head phase.\n\n\n");

            // -------- combat_balancing_config.json --------
            sb.append("2) combat_balancing_config.json\n");
            sb.append("--------------------------------------\n");
            sb.append("Controls combat-related behaviours and probabilities for Draugr entities.\n\n");

            sb.append("  - draugrMinBlockProbability (float, default 8.0)\n");
            sb.append("    Minimum chance (in %) for a Draugr to decide to block after an attack\n");
            sb.append("    when its health is relatively high.\n");
            sb.append("    Example: 8.0 => at high HP, ~8% chance to block.\n\n");

            sb.append("  - draugrMaxBlockProbability (float, default 16.0)\n");
            sb.append("    Maximum chance (in %) for a Draugr to block when its HP is low.\n");
            sb.append("    The actual block chance interpolates between min and max based on current HP.\n");
            sb.append("    Higher values => more defensive behaviour at low HP.\n\n");

            sb.append("  - draugrSpawnWithPotionProbability (float, default 5.0)\n");
            sb.append("    Chance (in %) for a Draugr to spawn with a potion (heal/strength/resistance/etc.).\n");
            sb.append("    Valid range: 0 < value <= 100 (outside this range => reset to 5).\n");
            sb.append("    Example: 5.0 => roughly 1 Draugr out of 20 spawns with a potion.\n\n");

            sb.append("  - draugrRaidScalingDifficulty (boolean, default true)\n");
            sb.append("    If true, Draugr raids scale with difficulty / wave progression.\n");
            sb.append("    Typically used to make later waves stronger (more mobs).\n");
            sb.append("    true  => raids become harder as if a player as entered the nether in your world.\n");
            sb.append("    false => raids stay at a flat difficulty.\n\n\n");

            // -------- spawn_rate_config.json --------
            sb.append("3) spawn_rate_config.json\n");
            sb.append("---------------------------------\n");
            sb.append("Controls the relative rarity / spawn frequency of each custom mob.\n");
            sb.append("All values must be between 0 and 10.\n");
            sb.append("These are NOT direct percentages but weights used in spawn checks.\n");
            sb.append("Higher value => more likely to spawn when conditions are met.\n\n");

            sb.append("  - draugrSpawnRate (int, default 10)\n");
            sb.append("    Spawn weight for basic Draugr melee mobs.\n");
            sb.append("    0 => effectively never spawns.\n");
            sb.append("    10 => maximum spawn frequency for this mob type.\n\n");

            sb.append("  - draugrArcherSpawnRate (int, default 9)\n");
            sb.append("    Spawn weight for Draugr Archers.\n\n");

            sb.append("  - draugrWightSpawnRate (int, default 7)\n");
            sb.append("    Spawn weight for Draugr Wights (stronger unit).\n\n");

            sb.append("  - draugrScourgeSpawnRate (int, default 5)\n");
            sb.append("    Spawn weight for Draugr Scourges (rare/elite unit).\n\n");

            sb.append("  - skeletonWarriorSpawnRate (int, default 8)\n");
            sb.append("    Spawn weight for Skeleton Warriors.\n\n");

            sb.append("Notes on spawn rates:\n");
            sb.append("  - All these values are combined with vanilla spawn conditions (light level, biome, etc.).\n");
            sb.append("  - Setting a value to 0 is a simple way to completely disable a mob type.\n");
            sb.append("  - Values > 10 are automatically reset to defaults when the config loads.\n\n");

            writer.write(sb.toString());
            System.out.println("[MebahelDraugr] Generated config README at: " + readmeFile.getAbsolutePath());
        } catch (IOException e) {
            System.err.println("[MebahelDraugr] Failed to write config README: " + e.getMessage());
        }
    }
}
