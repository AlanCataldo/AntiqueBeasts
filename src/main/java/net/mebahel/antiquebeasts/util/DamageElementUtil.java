package net.mebahel.antiquebeasts.util;

import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.projectile.ProjectileEntity;
import net.minecraft.entity.projectile.thrown.ThrownItemEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.Registries;
import net.minecraft.util.Identifier;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.Nullable;

import java.util.Locale;

public final class DamageElementUtil {
    private static final Logger LOGGER = LogManager.getLogger("mebahelcreaturesdraugr/DamageElementUtil");

    /** Sécurité perf: limite la taille de la signature (évite un log/concat infinis si bug). */
    private static final int MAX_SIGNATURE_CHARS = 512;

    private DamageElementUtil() {}

    // =========================
    // Result & Config
    // =========================

    public static final class ElementResult {
        public final boolean projectile;
        public final boolean fireLike;
        public final boolean frostLike;
        public final boolean waterLike;
        public final float multiplier;
        public final String chosen;     // "fire" | "frost" | "water" | "none"
        public final String signature;  // debug stable

        public ElementResult(boolean projectile, boolean fireLike, boolean frostLike, boolean waterLike,
                             float multiplier, String chosen, String signature) {
            this.projectile = projectile;
            this.fireLike = fireLike;
            this.frostLike = frostLike;
            this.waterLike = waterLike;
            this.multiplier = multiplier;
            this.chosen = chosen;
            this.signature = signature;
        }
    }

    public static final class ElementConfig {
        public final float fireMult;
        public final float frostMult;
        public final float waterMult;

        /** Mots-clés basés sur ids/translation keys (stable, pas de texte utilisateur). */
        public final String[] fireNeedles;
        public final String[] frostNeedles;
        public final String[] waterNeedles;

        public ElementConfig(float fireMult, float frostMult, float waterMult,
                             @Nullable String[] fireNeedles, @Nullable String[] frostNeedles, @Nullable String[] waterNeedles) {
            this.fireMult = saneMultiplier(fireMult);
            this.frostMult = saneMultiplier(frostMult);
            this.waterMult = saneMultiplier(waterMult);

            // Anti-NPE: jamais null
            this.fireNeedles = (fireNeedles == null) ? new String[0] : fireNeedles;
            this.frostNeedles = (frostNeedles == null) ? new String[0] : frostNeedles;
            this.waterNeedles = (waterNeedles == null) ? new String[0] : waterNeedles;
        }

        public static ElementConfig defaultPriority(float fireMult, float frostMult, float waterMult) {
            return new ElementConfig(
                    fireMult, frostMult, waterMult,
                    new String[]{"fire", "flame", "burn", "lava", "inferno"},
                    new String[]{"frost", "ice", "freeze", "froze", "snow", "stalhrim"},
                    new String[]{"water", "wet", "drown"}
            );
        }
    }

    // =========================
    // Public API
    // =========================

    /**
     * Détermine l'élément à partir d'une signature STABLE:
     * - projectile: entity registry id + translation key + (si ThrownItemEntity) item registry id + item translation key
     * - mêlée: main hand item registry id + translation key
     *
     * Ne dépend jamais de customName / texte affiché / traduction.
     */
    private static final boolean DEBUG_LOG = false;
    public static ElementResult compute(@Nullable DamageSource source, @Nullable Entity direct, @Nullable ElementConfig cfg) {
        // Anti-crash : param null-safe
        if (cfg == null) cfg = ElementConfig.defaultPriority(1.0f, 1.0f, 1.0f);

        boolean projectile = direct instanceof ProjectileEntity;

        String sig = "";
        try {
            sig = projectile ? buildStableProjectileSignature(direct) : buildStableWeaponSignature(source);
        } catch (Throwable t) {
            // compute ne doit jamais casser un tick
            safeWarn("compute() failed -> fallback to empty signature. err={}", t);
            sig = "";
        }

        boolean fireLike = containsAny(sig, cfg.fireNeedles);
        boolean frostLike = containsAny(sig, cfg.frostNeedles);
        boolean waterLike = containsAny(sig, cfg.waterNeedles);

        // Priorité: FROST > WATER > FIRE
        float mult = 1.0f;
        String chosen = "none";

        if (frostLike) {
            mult = cfg.frostMult;
            chosen = "frost";
        } else if (waterLike) {
            mult = cfg.waterMult;
            chosen = "water";
        } else if (fireLike) {
            mult = cfg.fireMult;
            chosen = "fire";
        }

        // Anti valeurs aberrantes (NaN/Inf)
        mult = saneMultiplier(mult);
        if (DEBUG_LOG) {
            try {
                String srcType = projectile ? "PROJECTILE" : "MELEE";
                String fire = fireLike ? "YES" : "no";
                String frost = frostLike ? "YES" : "no";
                String water = waterLike ? "YES" : "no";

                LOGGER.info(
                        "[DamageElement] src={} chosen={} mult={} | fire={} frost={} water={} | sig=\"{}\"",
                        srcType,
                        chosen,
                        mult,
                        fire,
                        frost,
                        water,
                        sig
                );
            } catch (Throwable t) {
                // le log ne doit JAMAIS casser un hit
                LOGGER.warn("[DamageElement] logging failed: {}", t.toString());
            }
        }


        return new ElementResult(projectile, fireLike, frostLike, waterLike, mult, chosen, sig);
    }

    public static float apply(float amount, @Nullable ElementResult r) {
        if (r == null) return amount;
        float m = saneMultiplier(r.multiplier);
        return amount * m;
    }

    // =========================
    // Signatures (stable + safe)
    // =========================

    /**
     * Signature projectile stable: ids/keys uniquement.
     * IMPORTANT: pas de getName(), pas de customName, pas de texte.
     */
    public static String buildStableProjectileSignature(@Nullable Entity e) {
        if (e == null) return "";

        StringBuilder sb = new StringBuilder(256);

        // Entity type id + translation key
        tryAppendEntityType(sb, e);

        // ThrownItemEntity: getStack() peut throw si pas d'item par défaut
        if (e instanceof ThrownItemEntity tie) {
            ItemStack stack = ItemStack.EMPTY;
            try {
                stack = tie.getStack();
            } catch (NullPointerException ignored) {
                // Projectile ThrownItemEntity sans default item.
                // Pas grave : l'entity id/translation key est déjà dans la signature.
                stack = ItemStack.EMPTY;
            } catch (Throwable t) {
                safeWarn("ThrownItemEntity.getStack() failed for entityType={} entityId={} err={}",
                        safeEntityTypeId(e), safeEntityId(e), t);
                stack = ItemStack.EMPTY;
            }

            if (stack != null && !stack.isEmpty()) {
                tryAppendItemStack(sb, stack, "thrown");
            }
        }

        return normalizeSignature(sb);
    }

    /**
     * Signature mêlée stable (Fabric-safe):
     * - attaquant -> LivingEntity -> main hand stack -> item registry id + translation key
     */
    public static String buildStableWeaponSignature(@Nullable DamageSource source) {
        if (source == null) return "";

        Entity attacker;
        try {
            attacker = source.getAttacker();
        } catch (Throwable t) {
            safeWarn("DamageSource.getAttacker() failed err={}", t);
            return "";
        }

        if (!(attacker instanceof LivingEntity le)) return "";

        ItemStack weapon;
        try {
            weapon = le.getMainHandStack();
        } catch (Throwable t) {
            safeWarn("LivingEntity.getMainHandStack() failed attackerType={} attackerId={} err={}",
                    safeEntityTypeId(attacker), safeEntityId(attacker), t);
            return "";
        }

        if (weapon == null || weapon.isEmpty()) return "";

        StringBuilder sb = new StringBuilder(128);
        tryAppendItemStack(sb, weapon, "weapon");

        return normalizeSignature(sb);
    }

    // =========================
    // Helpers (anti-crash)
    // =========================

    private static void tryAppendEntityType(StringBuilder sb, Entity e) {
        // Registry id
        try {
            Identifier entId = Registries.ENTITY_TYPE.getId(e.getType());
            if (entId != null) appendToken(sb, entId.toString());
        } catch (Throwable t) {
            safeWarn("Could not append entity registry id. entityId={} err={}", safeEntityId(e), t);
        }

        // Translation key
        try {
            String key = e.getType().getTranslationKey();
            if (key != null && !key.isEmpty()) appendToken(sb, key);
        } catch (Throwable t) {
            safeWarn("Could not append entity translation key. entityType={} entityId={} err={}",
                    safeEntityTypeId(e), safeEntityId(e), t);
        }
    }

    private static void tryAppendItemStack(StringBuilder sb, ItemStack stack, String context) {
        // Registry id
        try {
            Identifier itemId = Registries.ITEM.getId(stack.getItem());
            if (itemId != null) appendToken(sb, itemId.toString());
        } catch (Throwable t) {
            safeWarn("Could not append {} item registry id. err={}", context, t);
        }

        // Translation key
        try {
            String key = stack.getItem().getTranslationKey();
            if (key != null && !key.isEmpty()) appendToken(sb, key);
        } catch (Throwable t) {
            safeWarn("Could not append {} item translation key. err={}", context, t);
        }
    }

    private static void appendToken(StringBuilder sb, String token) {
        if (token == null || token.isEmpty()) return;

        // évite de dépasser la limite (robustesse + perf)
        if (sb.length() >= MAX_SIGNATURE_CHARS) return;

        int remain = MAX_SIGNATURE_CHARS - sb.length();
        if (remain <= 1) return;

        if (sb.length() > 0) sb.append(' ');

        // tronque le token si besoin
        if (token.length() > remain - 1) {
            sb.append(token, 0, Math.max(0, remain - 1));
        } else {
            sb.append(token);
        }
    }

    private static String normalizeSignature(StringBuilder sb) {
        if (sb == null || sb.length() == 0) return "";
        String s = sb.toString();
        // lower-case stable, trim, limite hard (au cas où)
        s = s.trim().toLowerCase(Locale.ROOT);
        if (s.length() > MAX_SIGNATURE_CHARS) s = s.substring(0, MAX_SIGNATURE_CHARS);
        return s;
    }

    public static boolean containsAny(@Nullable String haystack, @Nullable String... needles) {
        if (haystack == null || haystack.isEmpty()) return false;
        if (needles == null || needles.length == 0) return false;

        for (String n : needles) {
            if (n == null || n.isEmpty()) continue;
            // On lower-case le needle une fois (petit coût ok)
            String needle = n.toLowerCase(Locale.ROOT);
            if (!needle.isEmpty() && haystack.contains(needle)) return true;
        }
        return false;
    }

    private static float saneMultiplier(float v) {
        // évite NaN/Inf et valeurs négatives absurdes
        if (Float.isNaN(v) || Float.isInfinite(v)) return 1.0f;
        // autorise 0 si tu veux "immunité"
        if (v < 0f) return 1.0f;
        return v;
    }

    private static String safeEntityTypeId(@Nullable Entity e) {
        if (e == null) return "null";
        try {
            Identifier id = Registries.ENTITY_TYPE.getId(e.getType());
            return (id == null) ? e.getType().toString() : id.toString();
        } catch (Throwable t) {
            return "unknown";
        }
    }

    private static int safeEntityId(@Nullable Entity e) {
        if (e == null) return -1;
        try {
            return e.getId();
        } catch (Throwable t) {
            return -1;
        }
    }

    private static void safeWarn(String msg, Object... args) {
        try {
            // garde tes logs mais ne crash jamais à cause d'un logger/format foireux
            LOGGER.warn("[DamageElementUtil] " + msg, args);
        } catch (Throwable ignored) {
            // no-op
        }
    }
}
