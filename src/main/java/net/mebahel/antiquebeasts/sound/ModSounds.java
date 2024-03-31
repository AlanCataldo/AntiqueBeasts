package net.mebahel.antiquebeasts.sound;

import net.mebahel.antiquebeasts.AntiqueBeasts;
import net.minecraft.sound.SoundEvent;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;


public class ModSounds {
    public static SoundEvent CYCLOPS_STEP1 = registerSoundEvent("cyclops_walk");
    public static SoundEvent CYCLOPS_HURT1 = registerSoundEvent("cyclops_hurt1");
    public static SoundEvent CYCLOPS_HURT2 = registerSoundEvent("cyclops_hurt2");
    public static SoundEvent CYCLOPS_HIT1 = registerSoundEvent("cyclops_hit1");
    public static SoundEvent CYCLOPS_DEATH1 = registerSoundEvent("cyclops_death1");
    public static SoundEvent CYCLOPS_DEATH2 = registerSoundEvent("cyclops_death2");
    public static SoundEvent CYCLOPS_FLESHCRUSH3 = registerSoundEvent("cyclops_fleshcrush3");
    public static SoundEvent CYCLOPS_ROCKCRUSH1 = registerSoundEvent("cyclops_rockcrush1");
    public static SoundEvent HOPLITE_DEATH1 = registerSoundEvent("hoplite_death1");
    public static SoundEvent HOPLITE_DEATH2 = registerSoundEvent("hoplite_death2");
    public static SoundEvent HOPLITE_HURT1 = registerSoundEvent("hoplite_hurt1");
    public static SoundEvent HOPLITE_HURT2 = registerSoundEvent("hoplite_hurt2");
    public static SoundEvent HOPLITE_AMBIENT1 = registerSoundEvent("hoplite_ambient1");
    public static SoundEvent HOPLITE_AMBIENT2 = registerSoundEvent("hoplite_ambient2");
    public static SoundEvent HOPLITE_AMBIENT3 = registerSoundEvent("hoplite_ambient3");
    public static SoundEvent CYCLOPS_AMBIENT1 = registerSoundEvent("cyclops_ambient1");
    public static SoundEvent CYCLOPS_AMBIENT2 = registerSoundEvent("cyclops_ambient2");
    public static SoundEvent CYCLOPS_AMBIENT3 = registerSoundEvent("cyclops_ambient3");
    public static SoundEvent CYCLOPS_ATTACKING1 = registerSoundEvent("cyclops_attacking1");
    public static SoundEvent CYCLOPS_ATTACKING2 = registerSoundEvent("cyclops_attacking2");
    public static SoundEvent HOPLITE_ATTACKING1 = registerSoundEvent("hoplite_attacking1");
    public static SoundEvent HOPLITE_ATTACKING2 = registerSoundEvent("hoplite_attacking2");
    public static SoundEvent HERSIR_ATTACKING1 = registerSoundEvent("hersir_attacking1");
    public static SoundEvent HERSIR_ATTACKING2 = registerSoundEvent("hersir_attacking2");
    public static SoundEvent HERSIR_AMBIENT1 = registerSoundEvent("hersir_ambient1");
    public static SoundEvent HERSIR_AMBIENT2 = registerSoundEvent("hersir_ambient2");
    public static SoundEvent HERSIR_AMBIENT3 = registerSoundEvent("hersir_ambient3");
    public static SoundEvent THROWING_AXEMAN_AMBIENT1 = registerSoundEvent("throwing_axeman_ambient1");
    public static SoundEvent THROWING_AXEMAN_AMBIENT2 = registerSoundEvent("throwing_axeman_ambient2");
    public static SoundEvent THROWING_AXEMAN_AMBIENT3 = registerSoundEvent("throwing_axeman_ambient3");
    public static SoundEvent THROWING_AXEMAN_ATTACKING1 = registerSoundEvent("throwing_axeman_attacking1");
    public static SoundEvent THROWING_AXEMAN_ATTACKING2 = registerSoundEvent("throwing_axeman_attacking2");
    public static SoundEvent HADES_SHADE_HURT1 = registerSoundEvent("hades_shade_hurt1");
    public static SoundEvent HADES_SHADE_HURT2 = registerSoundEvent("hades_shade_hurt2");
    public static SoundEvent HADES_SHADE_AMBIENT1 = registerSoundEvent("hades_shade_ambient1");
    public static SoundEvent HADES_SHADE_AMBIENT2 = registerSoundEvent("hades_shade_ambient2");
    public static SoundEvent HADES_SHADE_DEATH = registerSoundEvent("hades_shade_death");
    public static SoundEvent HADES_SHADE_SPAWN = registerSoundEvent("hades_shade_spawn");
    public static SoundEvent SPEAR_HIT = registerSoundEvent("spear_hit");
    public static SoundEvent SHIELD_BLOCK = registerSoundEvent("shield_block");

    public static SoundEvent SWING = registerSoundEvent("swing1");
    public static SoundEvent FLESHCRUSH1 = registerSoundEvent("fleshcrush1");
    public static SoundEvent EINHERJAR_AMBIENT1 = registerSoundEvent("einherjar_ambient1");
    public static SoundEvent EINHERJAR_AMBIENT2 = registerSoundEvent("einherjar_ambient2");
    public static SoundEvent EINHERJAR_AMBIENT3 = registerSoundEvent("einherjar_ambient3");
    public static SoundEvent EINHERJAR_AMBIENT4 = registerSoundEvent("einherjar_ambient4");
    public static SoundEvent EINHERJAR_AMBIENT5 = registerSoundEvent("einherjar_ambient5");
    public static SoundEvent EINHERJAR_AMBIENT6 = registerSoundEvent("einherjar_ambient6");
    public static SoundEvent EINHERJAR_HURT1 = registerSoundEvent("einherjar_hurt1");
    public static SoundEvent EINHERJAR_HURT2 = registerSoundEvent("einherjar_hurt2");
    public static SoundEvent EINHERJAR_DEATH1 = registerSoundEvent("einherjar_death1");
    public static SoundEvent EINHERJAR_HORN = registerSoundEvent("einherjar_horn");
    public static SoundEvent VALKYRIE_ATTACK1 = registerSoundEvent("valkyrie_attack1");
    public static SoundEvent VALKYRIE_ATTACK2 = registerSoundEvent("valkyrie_attack2");
    public static SoundEvent VALKYRIE_ATTACK3 = registerSoundEvent("valkyrie_attack3");
    public static SoundEvent VALKYRIE_AMBIENT1 = registerSoundEvent("valkyrie_ambient1");
    public static SoundEvent VALKYRIE_AMBIENT2 = registerSoundEvent("valkyrie_ambient2");
    public static SoundEvent VALKYRIE_AMBIENT3 = registerSoundEvent("valkyrie_ambient3");
    public static SoundEvent VALKYRIE_DEATH1 = registerSoundEvent("valkyrie_death1");
    public static SoundEvent VALKYRIE_DEATH2 = registerSoundEvent("valkyrie_death2");
    public static SoundEvent VALKYRIE_HURT1 = registerSoundEvent("valkyrie_hurt1");
    public static SoundEvent VALKYRIE_HURT2 = registerSoundEvent("valkyrie_hurt2");
    public static SoundEvent HEAL = registerSoundEvent("heal");
    private static SoundEvent registerSoundEvent(String name) {
        Identifier id = new Identifier(AntiqueBeasts.MOD_ID, name);
        return Registry.register(Registry.SOUND_EVENT, id, new SoundEvent(id));
    }

    public static void registerSounds() {
        System.out.println("Registering ModSounds for " + AntiqueBeasts.MOD_ID);
    }
}
