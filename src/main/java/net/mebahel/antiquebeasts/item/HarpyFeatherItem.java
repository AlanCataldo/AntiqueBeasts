package net.mebahel.antiquebeasts.item;

import net.mebahel.antiquebeasts.entity.projectiles.HarpyFeatherEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.stat.Stats;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

public class HarpyFeatherItem extends Item {

    public HarpyFeatherItem(Settings settings) {
        super(settings);
    }

    // Méthode pour lancer l'objet
    @Override
    public TypedActionResult<ItemStack> use(World world, PlayerEntity player, Hand hand) {
        ItemStack itemStack = player.getStackInHand(hand);

        // Jouer le son de lancer
        world.playSound(null, player.getX(), player.getY(), player.getZ(), SoundEvents.ENTITY_SNOWBALL_THROW, SoundCategory.NEUTRAL, 0.5F, 1.0F);

        if (!world.isClient) {
            // Créer une nouvelle HarpyFeatherEntity
            HarpyFeatherEntity featherEntity = new HarpyFeatherEntity(world, player, 7);  // 7 points de dégâts

            // Position et direction de la plume
            Vec3d direction = player.getRotationVec(1.0F);
            featherEntity.setVelocity(direction.x, direction.y, direction.z, 1.5F, 1.0F); // Vitesse de lancement

            // Définir la position de départ (devant le joueur)
            featherEntity.setPosition(player.getX(), player.getY() + 0.75, player.getZ());

            // Lancer l'entité dans le monde
            world.spawnEntity(featherEntity);
        }
        player.getItemCooldownManager().set(this, 10);
        // Réduire le nombre d'items dans le stack (comme une boule de neige)
        if (!player.getAbilities().creativeMode) {
            itemStack.decrement(1);
        }

        player.incrementStat(Stats.USED.getOrCreateStat(this));
        return TypedActionResult.success(itemStack, world.isClient());
    }
}
