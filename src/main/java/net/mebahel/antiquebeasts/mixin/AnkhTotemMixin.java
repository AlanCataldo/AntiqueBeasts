package net.mebahel.antiquebeasts.mixin;

import net.mebahel.antiquebeasts.item.AnkhItem;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Items;
import net.minecraft.registry.tag.DamageTypeTags;
import net.minecraft.server.world.ServerWorld;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(LivingEntity.class)
public abstract class AnkhTotemMixin {

    @Inject(method = "damage", at = @At("HEAD"), cancellable = true)
    public void onDamage(DamageSource source, float amount, CallbackInfoReturnable<Boolean> info) {
        LivingEntity entity = (LivingEntity) (Object) this;

        if (entity instanceof PlayerEntity player) {
            // Vérifie si le joueur va mourir après avoir reçu ce dégât
            if (player.getHealth() <= amount && !source.isIn(DamageTypeTags.BYPASSES_INVULNERABILITY)) {
                // Vérifier si le joueur a un Ankh dans la main (main ou off-hand)
                if (player.getOffHandStack().getItem() instanceof AnkhItem || player.getMainHandStack().getItem() instanceof AnkhItem) {
                    // Applique l'effet du totem (annule la mort et applique les effets de régénération, etc.)
                    player.setHealth(1.0F); // Revient à 1 point de vie

                    // Supprime d'abord les effets négatifs avant d'ajouter la régénération
                    player.clearStatusEffects();

                    // Applique la régénération après avoir supprimé les effets négatifs
                    player.addStatusEffect(new StatusEffectInstance(StatusEffects.REGENERATION, 800, 2));
                    player.addStatusEffect(new StatusEffectInstance(StatusEffects.FIRE_RESISTANCE, 800, 0));
                    player.addStatusEffect(new StatusEffectInstance(StatusEffects.ABSORPTION, 100, 1));

                    // Applique la protection du totem de la non-mort
                    player.getItemCooldownManager().set(Items.TOTEM_OF_UNDYING, 100);

                    if (!player.getWorld().isClient()) {
                        // On s'assure que c'est bien un serveur (ServerWorld)
                        if (player.getWorld() instanceof ServerWorld serverWorld) {
                            serverWorld.sendEntityStatus(player, (byte) 35); // Envoie l'événement de totem activé au client
                        }
                    }

                    // Supprimer l'Ankh de l'inventaire après l'activation
                    if (!player.getAbilities().creativeMode) {
                        if (player.getOffHandStack().getItem() instanceof AnkhItem) {
                            player.getOffHandStack().decrement(1);
                        } else if (player.getMainHandStack().getItem() instanceof AnkhItem) {
                            player.getMainHandStack().decrement(1);
                        }
                    }

                    // Empêche la mort
                    info.setReturnValue(false);
                }
            }
        }
    }
}

