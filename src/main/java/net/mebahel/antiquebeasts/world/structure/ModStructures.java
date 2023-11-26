package net.mebahel.antiquebeasts.world.structure;

import net.mebahel.antiquebeasts.mixin.StructureFeatureAccessor;
import net.mebahel.antiquebeasts.AntiqueBeasts;
import net.minecraft.world.gen.GenerationStep;
import net.minecraft.world.gen.feature.StructureFeature;

public class ModStructures {
    public static StructureFeature<?> HADES_SHRINE_STRUCTURE = new HadesShrineStructure();
    public static StructureFeature<?> TINY_CYCLOPS_CAVE_STRUCTURE = new HadesShrineStructure();

    public static void registerStructureFeatures() {
        StructureFeatureAccessor.callRegister(AntiqueBeasts.MOD_ID + ":hades_shrine_structure",
                HADES_SHRINE_STRUCTURE, GenerationStep.Feature.SURFACE_STRUCTURES);
        StructureFeatureAccessor.callRegister(AntiqueBeasts.MOD_ID + ":tiny_cyclops_cave_structure",
                TINY_CYCLOPS_CAVE_STRUCTURE, GenerationStep.Feature.SURFACE_STRUCTURES);
    }
}
