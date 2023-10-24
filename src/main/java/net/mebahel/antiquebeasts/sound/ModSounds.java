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
    private static SoundEvent registerSoundEvent(String name) {
        Identifier id = new Identifier(AntiqueBeasts.MOD_ID, name);
        return Registry.register(Registry.SOUND_EVENT, id, new SoundEvent(id));
    }

    public static void registerSounds() {
        System.out.println("Registering ModSounds for " + AntiqueBeasts.MOD_ID);
    }
}
